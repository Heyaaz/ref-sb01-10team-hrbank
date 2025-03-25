package com.sprint.example.sb01part2hrbankteam10ref.service;

import com.sprint.example.sb01part2hrbankteam10ref.dto.binary_content.BinaryContentUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BinaryContentService {
  BinaryContentUploadResponse uploadProfile(MultipartFile file);
  BinaryContentUploadResponse uploadBackup(MultipartFile file);
}