package com.sprint.example.sb01part2hrbankteam10ref.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.sprint.example.sb01part2hrbankteam10ref.dto.binary_content.BinaryContentUploadResponse;
import com.sprint.example.sb01part2hrbankteam10ref.service.BinaryContentService;
import jakarta.transaction.Transactional;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
class BinaryContentServiceImplTest {
  @Autowired
  private BinaryContentService binaryContentService;

  @Test
  void testUploadFile(){

    // given
    // MultipartFile 목 객체 생성
    String fileName = "testfile";
    String contentType = "testfile/zip";
    byte[] content = "테스트 이미지 내용".getBytes();
    int size = content.length;

    MultipartFile testFile = new MockMultipartFile(
        "file",
        fileName,
        contentType,
        content
    );

    // when
    BinaryContentUploadResponse responseProfile = binaryContentService.uploadProfile(testFile);
    BinaryContentUploadResponse responseBackup = binaryContentService.uploadBackup(testFile);

    // then
    assertNotNull(responseProfile.getId());  // ID가 null이 아니고 테스트 돌릴 때마다 값이 다르면 ok
    assertEquals("testfile", responseProfile.getName());
    assertEquals("testfile/zip", responseProfile.getContentType());
    assertEquals(BigInteger.valueOf(size), responseProfile.getSize());

    assertNotNull(responseBackup.getId());
    assertEquals("testfile", responseBackup.getName());
    assertEquals("testfile/zip", responseBackup.getContentType());
    assertEquals(BigInteger.valueOf(size), responseBackup.getSize());
  }

}