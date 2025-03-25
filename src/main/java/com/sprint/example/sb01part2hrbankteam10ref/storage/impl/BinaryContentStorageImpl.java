package com.sprint.example.sb01part2hrbankteam10ref.storage.impl;

import com.sprint.example.sb01part2hrbankteam10ref.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode.BinaryContentErrorCode;
import com.sprint.example.sb01part2hrbankteam10ref.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;


@Component
public class BinaryContentStorageImpl implements BinaryContentStorage {

  // 저장 디렉토리 경로 선언
  private final Path profileStorage;
  private final Path backupStorage;

  // 경로 의존성 주입 생성자
  public BinaryContentStorageImpl(@Value("${user.dir}/savedFiles/profiles") String profileStorage, @Value("${user.dir}/savedFiles/backups") String backupStorage) {
    this.profileStorage = Path.of(profileStorage);
    this.backupStorage = Path.of(backupStorage);
    init();
  }

  // 해당 디렉토리가 없을 시 만들어주는 초기화 메서드
  private void init(){
    try {
      Files.createDirectories(profileStorage);
      Files.createDirectories(backupStorage);
    } catch(IOException e){
      throw new RestApiException(BinaryContentErrorCode.DIRECTORY_CREATION_FAILED, e.getMessage());
    }
  }

  // 프로필 파일 저장
  @Override
  public Integer saveProfile(Integer fileId, MultipartFile file){
    // 파일 확장자 검증
    validateFileExtension(file);

    // fileId로 경로 생성 -> resolvePath
    Path path = profileResolvePath(fileId);

    // byte[] data를 로컬저장소에 넣기
    try{
      byte[] data = file.getBytes();
      Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }catch(IOException e){
      throw new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_WRITE_ERROR, e.getMessage());
    }
    return fileId;
  }

  // 허용된 프로필 이미지 확장자인지 검증
  private void validateFileExtension(MultipartFile file) {
    // 허용된 확장자 목록
    List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

    String originalFilename = file.getOriginalFilename();
    if (originalFilename == null || originalFilename.isEmpty()) {
      throw new RestApiException(BinaryContentErrorCode.INVALID_FILE_TYPE, "파일명이 유효하지 않습니다.");
    }

    String extension = "";
    int lastDotIndex = originalFilename.lastIndexOf('.');
    if (lastDotIndex > 0) {
      extension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
    }

    if (!allowedExtensions.contains(extension)) {
      throw new RestApiException(BinaryContentErrorCode.INVALID_FILE_TYPE,
              "지원하지 않는 파일 형식입니다. 지원되는 형식: " + String.join(", ", allowedExtensions));
    }
  }

  // 백업 파일 저장
  @Override
  public Integer saveBackup(Integer fileId, MultipartFile file) {
    // fileId로 경로 생성

    Path path = backupResolvePath(fileId);

    // 파일을 스트림을 통해 저장
    try (InputStream inputStream = file.getInputStream();
        OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

      byte[] buffer = new byte[32768]; // 32KB씩 저장
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        outputStream.write(buffer, 0, bytesRead);
      }
    } catch (IOException e) {
      throw new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_STREAM_ERROR, e.getMessage());
    }

    return fileId;
  }

  // 프로필 파일 다운로드
  @Override
  public Resource downloadProfile(Integer fileId){
    // 다운받을 파일의 경로 생성
    Path path = profileResolvePath(fileId);

    if (!Files.exists(path)) {
      throw new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_NOT_FOUND, profileResolvePath(fileId).toString());
    }

    // 파일 시스템 리소스 생성
    FileSystemResource resource = new FileSystemResource(path);

    // 리소스가 존재하고 읽을 수 있는지 확인
    if (!resource.exists() || !resource.isReadable()) {
      throw new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_READ_ERROR, profileResolvePath(fileId).toString());
    }

    // Resource 반환
    return resource;
  }

  // 백업 파일 다운로드
  @Override
  public Resource downloadBackup(Integer fileId){
    // 다운받을 파일의 경로 생성
    Path path = backupResolvePath(fileId);

    if (!Files.exists(path)) {
      throw new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_NOT_FOUND, backupResolvePath(fileId).toString());
    }

    // 파일 시스템 리소스 생성
    FileSystemResource resource = new FileSystemResource(path);

    // 리소스가 존재하고 읽을 수 있는지 확인
    if (!resource.exists() || !resource.isReadable()) {
      throw new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_READ_ERROR, backupResolvePath(fileId).toString());
    }

    // Resource 반환
    return resource;
  }


  // 아이디로 경로를 설정하는 메서드 resolvePath
  @Override
  public Path profileResolvePath(Integer fileId){
    return profileStorage.resolve(fileId.toString());
  }


  @Override
  public Path backupResolvePath(Integer fileId){
    return backupStorage.resolve(fileId.toString());
  }

  // 프로필 파일 삭제
  @Override
  public void deleteProfile(Integer fileId) {
    Path path = profileResolvePath(fileId);

    try {
      if (Files.deleteIfExists(path)) {
        System.out.println("삭제된 파일 " + path);
      } else {
        throw new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_NOT_FOUND, path.toString());
      }
    } catch (IOException e) {
      throw new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_DELETE_ERROR, e.getMessage());
    }
  }

  // 백업 파일 삭제
  @Override
  public void deleteBackup(Integer fileId) {
    Path path = backupResolvePath(fileId);

    try {
      if (Files.deleteIfExists(path)) {
        System.out.println("삭제된 파일 " + path);
      } else {
        throw new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_NOT_FOUND, path.toString());
      }
    } catch (IOException e) {
      throw new RestApiException(BinaryContentErrorCode.BINARY_CONTENT_DELETE_ERROR, e.getMessage());
    }
  }
}

