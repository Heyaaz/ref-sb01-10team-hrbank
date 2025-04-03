package com.sprint.example.sb01part2hrbankteam10ref.service.impl;

import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.BackupDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.BackupResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.EmployeeForBackupDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup.BackupStatus;
import com.sprint.example.sb01part2hrbankteam10ref.entity.BinaryContent;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode.BackupErrorCode;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode.BinaryContentErrorCode;
import com.sprint.example.sb01part2hrbankteam10ref.mapper.BackupMapper;
import com.sprint.example.sb01part2hrbankteam10ref.mapper.EmployeeMapper;
import com.sprint.example.sb01part2hrbankteam10ref.repository.BackupRepository;
import com.sprint.example.sb01part2hrbankteam10ref.repository.BinaryContentRepository;
import com.sprint.example.sb01part2hrbankteam10ref.repository.EmployeeHistoryRepository;
import com.sprint.example.sb01part2hrbankteam10ref.repository.EmployeeRepository;
import com.sprint.example.sb01part2hrbankteam10ref.service.BackupService;
import com.sprint.example.sb01part2hrbankteam10ref.storage.BinaryContentStorage;
import com.sprint.example.sb01part2hrbankteam10ref.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.lang3.StringEscapeUtils.escapeCsv;

// ID,직원번호,이름,이메일,부서,직급,입사일,상태

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BackupServiceImpl implements BackupService {

  private final BackupRepository backupRepository;
  private final BinaryContentRepository binaryContentRepository;
  private final BinaryContentStorage binaryContentStorage;
  private final EmployeeRepository employeeRepository;
  private final EmployeeHistoryRepository employeeHistoryRepository;

  // 백업 서비스에서 두 가지 다른 메서드로 분리
  @Override
  public Integer performBackupByHand(HttpServletRequest request) {
    // 수동 백업 - 요청자의 IP 주소 사용
    String workerIpAddress = IpUtil.getClientIp(request);
    return executeBackup(workerIpAddress);
  }

  @Override
  public Integer performBackupByBatch() {
    // batch에 의한 백업 - system으로 표시
    String workerIpAddress = "system";
    return executeBackup(workerIpAddress);
  }


  private Integer executeBackup(String workerIpAddress) {
    // 로직: if 백업 불필요 -> 건너뜀 상태로 배치이력 저장하고 프로세스 종료
    if (!isBackupNeeded()) {
      Backup backupHistory = createBackupHistory(workerIpAddress, BackupStatus.SKIPPED, LocalDateTime.now(), LocalDateTime.now(), null);
      return backupRepository.save(backupHistory).getId();
    }

    // 진행중 상태로 배치이력 저장, 작업자는 요청자의 ip 주소
    Backup atStartBackupHistory = createBackupHistory(workerIpAddress, BackupStatus.IN_PROGRESS,
            LocalDateTime.now(), null, null);
    backupRepository.save(atStartBackupHistory);

    File backupFile = null;
    // 백업
    try {
      // 백업 로직 수행 (직원 정보 가져오기)
      List<EmployeeForBackupDto> backupContent = fetchEmployeeData();

      // 전체 직원 정보를 CSV 파일로 저장 = 스트리밍/버퍼
      ResponseEntity<Resource> response = convertBackupToCsvFile(backupContent, atStartBackupHistory.getId());

      // 파일을 MultipartFile로 변환 (파일이 생성되었을 경우만)
      if (response.getBody() instanceof FileSystemResource) {
        backupFile = ((FileSystemResource) response.getBody()).getFile();
      }

      // 백업 파일이 생성되지 않으면 에러 처리
      if (backupFile == null || !backupFile.exists()) {
        throw new RestApiException(BackupErrorCode.BACKUP_FILE_CREATION_FAILED, "파일 생성에 실패했습니다.");
      }

      // 파일을 MultipartFile로 변환
      MultipartFile backUpMultipartFile = convertFileToMultipartFile(backupFile);

      // 파일 정보를 먼저 DB에 저장 후 ID를 가져옴
      BigInteger fileSize = BigInteger.valueOf(backupFile.length());
      BinaryContent binaryContentEntity = BinaryContent.builder()
              .name(backupFile.getName())
              .contentType("text/csv")
              .size(fileSize)
              .build();

      // 저장하고 ID 반환받기
      Integer fileId = binaryContentRepository.save(binaryContentEntity).getId();
      BinaryContent savedBinaryContent = binaryContentRepository.findById(fileId)
              .orElseThrow(() -> new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_NOT_FOUND, "파일을 찾을 수 없습니다."));

      // 확실히 ID가 있는지 확인
      if (fileId == null) {
        throw new RestApiException(BackupErrorCode.BACKUP_FILE_CREATION_FAILED, "파일 ID를 생성할 수 없습니다.");
      }

      // 그 후에 파일 저장
      binaryContentStorage.saveBackup(fileId, backUpMultipartFile);

      // 백업 성공 -> 백업이력 완료로 수정
      atStartBackupHistory.updateStatus(BackupStatus.COMPLETED, LocalDateTime.now(), savedBinaryContent);
      return backupRepository.save(atStartBackupHistory).getId();

    } catch (Exception e) {
      // 저장하던 파일 삭제, 에러로그 .log 파일로 저장, 백업이력 실패로 수정
      MultipartFile file = logError(e);
      BinaryContent savedLogBinaryContent = BinaryContent.builder()
              .name(file.getName())
              .contentType(file.getContentType())
              .size(BigInteger.valueOf(file.getSize()))
              .build();

      // 백업 실패 시 이력
      atStartBackupHistory.updateStatus(BackupStatus.FAILED, LocalDateTime.now(), savedLogBinaryContent);

      // 파일이 생성되었을 경우에만 삭제
      if (backupFile != null) {
        Integer fileId = binaryContentRepository.findByName(backupFile.getName());
        if (fileId != null) {
          binaryContentRepository.deleteById(fileId);
          binaryContentStorage.deleteBackup(fileId);
        }
      }
      throw new RestApiException(BackupErrorCode.BACKUP_ERROR, atStartBackupHistory.getId().toString());
    }
  }

  // 백업 여부 결정 로직: 가장 최근 완료된 배치 작업시간 < 직원 데이터 변경 시간 -> 백업 필요
  private boolean isBackupNeeded() {

    LocalDateTime lastBackupTime = backupRepository.findLastCompletedBackupAt();

    if (lastBackupTime == null) {
      lastBackupTime = LocalDateTime.MIN;
    }

    LocalDateTime lastEmployeeUpdate = employeeHistoryRepository.findLastModifiedAt();
    if (lastEmployeeUpdate == null) {
      lastEmployeeUpdate = LocalDateTime.MIN;
    }

    return lastBackupTime.isBefore(lastEmployeeUpdate);
  }

  private Backup createBackupHistory(String workerIpAddress, BackupStatus status, LocalDateTime startedAt, LocalDateTime endedAt, BinaryContent binaryContent) {
    return Backup.builder()
            .workerIpAddress(workerIpAddress)
            .status(status)
            .startedAt(startedAt)
            .endedAt(endedAt)
            .binaryContent(binaryContent)
            .build();
  }


  private ResponseEntity<Resource> convertBackupToCsvFile(List<EmployeeForBackupDto> backupContent, Integer backupId) {
    File csvFile = null;
    File zipFile = null;

    try {
      // 임시 디렉토리에 파일 생성
      csvFile = File.createTempFile("backup_", ".csv");

      try (BufferedWriter writer = new BufferedWriter(
              new OutputStreamWriter(new FileOutputStream(csvFile), StandardCharsets.UTF_8))) {

        // BOM 마커 추가 (UTF-8 인코딩 명시)
        writer.write('\uFEFF');

        // 헤더 작성
        writer.write("ID,직원번호,이름,이메일,부서,직급,입사일,상태");
        writer.newLine();

        // ID 목록 추출
        List<Integer> employeeIds = backupContent.stream()
                .map(EmployeeForBackupDto::getId)
                .collect(Collectors.toList());

        // 한 번의 쿼리로 모든 직원 정보 가져오기
        Map<Integer, Employee> employeeMap = employeeRepository.findAllById(employeeIds)
                .stream()
                .collect(Collectors.toMap(Employee::getId, employee -> employee));

        // CSV 데이터 작성
        for (EmployeeForBackupDto dto : backupContent) {
          Employee employee = employeeMap.get(dto.getId());
          if (employee != null) {
            writer.write(
                    escapeCsv(String.valueOf(employee.getId())) + "," +
                            escapeCsv(employee.getEmployeeNumber()) + "," +
                            escapeCsv(employee.getName()) + "," +
                            escapeCsv(employee.getEmail()) + "," +
                            escapeCsv(String.valueOf(employee.getDepartment())) + "," +
                            escapeCsv(employee.getPosition()) + "," +
                            escapeCsv(employee.getHireDate() != null ? employee.getHireDate().toString() : "") + "," +
                            escapeCsv(String.valueOf(employee.getStatus()))
            );
            writer.newLine();
          }
        }
        writer.flush();

        // ZIP 파일 생성
        zipFile = File.createTempFile("backup_", ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

          // ZIP 엔트리 생성 및 CSV 파일 추가
          ZipEntry zipEntry = new ZipEntry("backup_" + backupId + ".csv");
          zos.putNextEntry(zipEntry);

          // 버퍼를 사용해 파일 내용 복사
          byte[] buffer = new byte[1024];
          try (FileInputStream fis = new FileInputStream(csvFile)) {
            int length;
            while ((length = fis.read(buffer)) > 0) {
              zos.write(buffer, 0, length);
            }
          }
          zos.closeEntry();
        }

        // 임시 CSV 파일 삭제
        csvFile.delete();

        // ZIP 파일 반환
        FileSystemResource resource = new FileSystemResource(zipFile);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"backup_" + backupId + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
      }
    } catch (IOException e) {
      // 오류 발생 시 임시 파일 정리
      if (csvFile != null && csvFile.exists()) csvFile.delete();
      if (zipFile != null && zipFile.exists()) zipFile.delete();
      throw new RestApiException(BackupErrorCode.BACKUP_FILE_CREATION_FAILED, e.getMessage());
    }
  }

  public MultipartFile convertFileToMultipartFile(File file) throws IOException {
    FileInputStream inputStream = new FileInputStream(file);
    return new MockMultipartFile(
            "file", // 파라미터 이름 (폼에서 사용하는 이름)
            file.getName(), // 파일 이름
            "text/csv", // CSV 파일 MIME 타입
            inputStream); // 파일의 InputStream
  }


  private MultipartFile logError(Exception e) {
    File logFile = new File("backup_error.log");
    File zipFile = new File("backup_error.zip");

    try (FileWriter fileWriter = new FileWriter(logFile, true);
         BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

      bufferedWriter.write(LocalDateTime.now() + " : " + e.getMessage());
      bufferedWriter.newLine();
    } catch (IOException ex) {
      ex.printStackTrace();
    }

    if (logFile.exists()) {
      try (FileOutputStream fos = new FileOutputStream(zipFile);
           ZipOutputStream zos = new ZipOutputStream(fos);
           FileInputStream fis = new FileInputStream(logFile)) {

        ZipEntry zipEntry = new ZipEntry(logFile.getName());
        zos.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
          zos.write(buffer, 0, length);
        }

        zos.closeEntry();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }

      // ZIP 파일을 MultipartFile로 변환
      try {
        MultipartFile multipartFile = new MockMultipartFile(
                zipFile.getName(),
                zipFile.getName(),
                "application/zip",
                new FileInputStream(zipFile)
        );


        // 파일 DB에 저장
        BigInteger fileSize = BigInteger.valueOf(logFile.length());
        BinaryContent binaryContent = BinaryContent.builder()
                .name(logFile.getName())
                .contentType("text/plain")
                .size(fileSize)
                .build();

        Integer fileId = binaryContentRepository.save(binaryContent).getId();

        // 변환된 MultipartFile을 사용하여 저장
        binaryContentStorage.saveBackup(fileId, multipartFile);

        // 로그 파일 삭제
        logFile.delete();

        return multipartFile;

      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  return null;
  }


  private List<EmployeeForBackupDto> fetchEmployeeData() {

    List<EmployeeDto> employeeDtoList = employeeRepository.findAll()
            .stream()
            .map(EmployeeMapper::toDto)
            .toList();

    List<EmployeeForBackupDto> employeeForBackupDtoList = new ArrayList<>();
    for(EmployeeDto dto : employeeDtoList){
      Integer id = dto.getId();
      String name = dto.getName();
      String email = dto.getEmail();
      String employeeNumber = dto.getEmployeeNumber();
      Employee.EmployeeStatus status = dto.getStatus();
      String position = dto.getPosition();
      String departmentName = dto.getDepartmentName();
      LocalDate hireDate = dto.getHireDate();
      EmployeeForBackupDto employeeForBackupDto = EmployeeForBackupDto.builder()
              .id(id)
              .name(name)
              .email(email)
              .employeeNumber(employeeNumber)
              .status(status)
              .position(position)
              .departmentName(departmentName)
              .hireDate(hireDate)
              .build();
      employeeForBackupDtoList.add(employeeForBackupDto);
    }
    return employeeForBackupDtoList;
  }

  @Override
  public CursorPageResponseDto<BackupResponseDto> getBackupList(String workerIpAddress,
      BackupStatus status, LocalDateTime startedAtFrom, LocalDateTime startedAtTo, Integer fileId,
      Integer idAfter, String cursor, int size, String sortField, String sortDirection) {

    try {
      // 1. 정렬 방향 설정
      boolean isAscending = "asc".equalsIgnoreCase(sortDirection);

      // 2. 커서 처리
      Integer cursorId = null;
      if (idAfter != null) {
        cursorId = idAfter;
      } else if (cursor != null && !cursor.isEmpty()) {
        try {
          cursorId = Integer.parseInt(cursor);
        } catch (NumberFormatException e) {
          log.warn("유효하지 않은 커서 값: {}", cursor);
        }
      }

      // 3. QueryDSL을 사용하여 쿼리 생성
      List<Backup> backups = backupRepository.findBackupsWithCursor(
              startedAtFrom, startedAtTo, workerIpAddress, status, cursorId, cursor, size,
              sortField, sortDirection
      );

      // 4. Dto 변환
      List<BackupResponseDto> responseDtos = backups.stream()
              .map(backup -> {
                BackupDto dto = BackupMapper.toDto(backup);
                return BackupResponseDto.builder()
                    .id(dto.getId())
                    .status(dto.getStatus())
                    .startedAt(dto.getStartedAt())
                    .endedAt(dto.getEndedAt())
                    .worker(dto.getWorker())
                    .fileId(dto.getFileId())
                    .build();
              }).collect(Collectors.toList());

      // 5. 다음 커서 값 설정
      String nextCursor = null;
      Long nextIdAfterValue = null;
      boolean hasNextValue = false;

      if (!backups.isEmpty() && backups.size() >= size) {
        Backup lastBackup = backups.get(backups.size() - 1);
        nextCursor = String.valueOf(lastBackup.getId());
        nextIdAfterValue = Long.valueOf(lastBackup.getId());
        hasNextValue = true;
      }

      // 6. 응답 생성
      CursorPageResponseDto<BackupResponseDto> response = CursorPageResponseDto.<BackupResponseDto>builder()
          .content(responseDtos)
          .nextCursor(nextCursor)
          .nextIdAfter(nextIdAfterValue)
          .hasNext(hasNextValue)
          .size(responseDtos.size())
          .totalElements((long) backupRepository.count())
          .build();
      return response;
    } catch (Exception e) {
      throw e;
    }

  }
}