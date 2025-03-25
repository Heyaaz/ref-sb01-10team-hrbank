package com.sprint.example.sb01part2hrbankteam10ref.storage;

import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public interface BinaryContentStorage {
  // 프로필 파일 저장
  Integer saveProfile(Integer fileId, MultipartFile file);

  // 백업 파일 저장
  Integer saveBackup(Integer fileId, MultipartFile file);

  // 프로필 피일 다운로드
  Resource downloadProfile(Integer fileId);

  // 백업 파일 다운로드
  Resource downloadBackup(Integer fileId);

  // 프로필 파일 경로 생성
  Path profileResolvePath(Integer fileId);

  // 백업 파일 경로 생성
  Path backupResolvePath(Integer fileId);

  // 프로필 파일 삭제
  void deleteProfile(Integer fileId);

  // 백업 파일 삭제
  void deleteBackup(Integer fileId);
}
