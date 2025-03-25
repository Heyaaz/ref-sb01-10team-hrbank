package com.sprint.example.sb01part2hrbankteam10ref.service.impl;

import com.sprint.example.sb01part2hrbankteam10ref.entity.BinaryContent;
import com.sprint.example.sb01part2hrbankteam10ref.repository.BinaryContentRepository;
import com.sprint.example.sb01part2hrbankteam10ref.service.BinaryContentService;
import com.sprint.example.sb01part2hrbankteam10ref.storage.BinaryContentStorage;
import com.sprint.example.sb01part2hrbankteam10ref.dto.binary_content.BinaryContentUploadResponse;
import jakarta.transaction.Transactional;
import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  // 프로필 파일 업로드
  @Override
  @Transactional
  public BinaryContentUploadResponse uploadProfile(MultipartFile multipartFile) {
    // 메타 정보 저장
    BinaryContent binaryContent = BinaryContent.builder()
        .name(multipartFile.getOriginalFilename())
        .contentType(multipartFile.getContentType())
        .size(BigInteger.valueOf(multipartFile.getSize()))
        .build();

    BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

    // 실제 파일을 로컬 저장소에 저장
    binaryContentStorage.saveProfile(savedBinaryContent.getId(), multipartFile);

    return BinaryContentUploadResponse.from(savedBinaryContent);
  }

  // 백업 파일 업로드
  @Override
  @Transactional
  public BinaryContentUploadResponse uploadBackup(MultipartFile multipartFile) {
    // 메타 정보 저장
    BinaryContent binaryContent = BinaryContent.builder()
        .name(multipartFile.getOriginalFilename())
        .contentType(multipartFile.getContentType())
        .size(BigInteger.valueOf(multipartFile.getSize()))
        .build();

    BinaryContent savedBinaryContent = binaryContentRepository.save(binaryContent);

    // 실제 파일을 로컬 저장소에 저장
    binaryContentStorage.saveBackup(savedBinaryContent.getId(), multipartFile);

    return BinaryContentUploadResponse.from(savedBinaryContent);
  }
}