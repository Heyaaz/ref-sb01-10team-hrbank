package com.sprint.example.sb01part2hrbankteam10ref.service.impl;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.CursorPageResponseChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.DiffDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.EmployeeHistoryResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory.ChangeType;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode.EmployeeHistoryErrorCode;
import com.sprint.example.sb01part2hrbankteam10ref.mapper.EmployeeHistoryMapper;
import com.sprint.example.sb01part2hrbankteam10ref.repository.EmployeeHistoryRepository;
import com.sprint.example.sb01part2hrbankteam10ref.service.EmployeeHistoryService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Base64;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeHistoryServiceImpl implements EmployeeHistoryService {

    private final EmployeeHistoryRepository employeeHistoryRepository;

    @Transactional
    @Override
    public ChangeLogDto create(String employeeNumber, EmployeeHistory.ChangeType type,
        String memo, EmployeeDto beforeDate, EmployeeDto afterData, String clientIp) {

        EmployeeHistory history = EmployeeHistoryMapper.toEntity(employeeNumber, type, memo, beforeDate, afterData, clientIp);

        EmployeeHistory savedHistory = employeeHistoryRepository.save(history);
        return EmployeeHistoryMapper.toChangeLogDto(savedHistory);
    }

    @Override
    public CursorPageResponseDto getEmployeeHistoriesWithCursor(String employeeNumber,
        ChangeType type, String memo, String ipAddress, LocalDateTime atFrom, LocalDateTime atTo,
        Integer idAfter, String cursor, int size, String sortField, String sortDirection) {

        try {
            // 1. 정렬 방향 설정
            boolean isAscending = "asc".equalsIgnoreCase(sortDirection);

            // 2. 커서 처리
            Integer cursorId = null;
            if (idAfter != null) {
                cursorId = idAfter;
            } else if (StringUtils.hasText(cursor)) {
                try {
                    cursorId = Integer.parseInt(cursor);
                } catch (NumberFormatException e) {
                    log.warn("유효하지 않은 커서 값 : {}",cursor);
                }
            }

            // 3. 전체 레코드 수 조회
            Long totalCount = employeeHistoryRepository.countEmployeeHistories(
                employeeNumber, type, memo, ipAddress, atFrom, atTo
            );

            // 4. QueryDSL을 사용하여 쿼리 생성
            List<EmployeeHistory> employeeHistories = employeeHistoryRepository.findEmployeeHistoryWithCursor(
                    employeeNumber, type, memo, ipAddress, atFrom, atTo, cursorId, cursor, size,
                    sortField, sortDirection
            );

            // 5. Dto 변환
            List<EmployeeHistoryResponseDto> responseDtos = employeeHistories.stream()
                .map(employeeHistory -> {
                    return EmployeeHistoryResponseDto.builder()
                        .id(employeeHistory.getId())
                        .employeeNumber(employeeHistory.getEmployeeNumber())
                        .type(employeeHistory.getType())
                        .memo(employeeHistory.getMemo())
                        .ipAddress(employeeHistory.getIpAddress())
                        .at(employeeHistory.getLoggedAt())
                        .build();
                }).collect(Collectors.toList());

            // 6. 다음 커서 값 설정
            String nextCursor = null;
            Long nextIdAfterValue = null;
            boolean hasNextValue = false;

            if(!employeeHistories.isEmpty() && employeeHistories.size() >= size) {
                EmployeeHistory lastHistory = employeeHistories.get(employeeHistories.size() -1);
                nextCursor = String.valueOf(lastHistory.getId());
                nextIdAfterValue = Long.valueOf(lastHistory.getId());
                hasNextValue = true;
            }

            // 7. 응답 생성 - totalElements를 실제 전체 개수로 설정
            CursorPageResponseDto<EmployeeHistoryResponseDto> response = CursorPageResponseDto.<EmployeeHistoryResponseDto>builder()
                .content(responseDtos)
                .nextCursor(nextCursor)
                .nextIdAfter(nextIdAfterValue)
                .size(responseDtos.size())
                .totalElements(totalCount)
                .hasNext(hasNextValue)
                .build();

            return response;

        } catch (Exception e) {
            throw e;
        }

    }


    @Override
    @Transactional(readOnly = true)
    public List<DiffDto> getChangeDiffs(Integer id) {
        EmployeeHistory history = employeeHistoryRepository.findById(id)
                .orElseThrow(() -> new RestApiException(
                        EmployeeHistoryErrorCode.EMPLOYEE_HISTORY_NOT_FOUND,
                        "해당 ID를 가진 직원이 없습니다."));
        return EmployeeHistoryMapper.toDiffList(history);
    }

@Override
@Transactional(readOnly = true)
public Long countEmployeeHistories(LocalDateTime fromDate, LocalDateTime toDate) {
    return employeeHistoryRepository.countByModifiedAtBetween(fromDate, toDate);
}

}
