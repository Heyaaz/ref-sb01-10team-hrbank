//package com.sprint.example.sb01part2hrbankteam10ref.repository;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import com.sprint.example.sb01part2hrbankteam10ref.entity.BinaryContent;
//import jakarta.transaction.Transactional;
//import java.math.BigInteger;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 지금 설정된 실제 데이터 베이스를 사용하도록 설정
//@Transactional // 테스트 후 자동으로 롤백되도록 설정
//class BinaryContentRepositoryTest {
//
//  @Autowired
//  private BinaryContentRepository binaryContentRepository;
//
//  @Test // 생성 테스트
//  void testSaveFile() {
//
//    // given
//    BinaryContent binaryContent = BinaryContent.builder()
//        .name("backup1")
//        .contentType("application/zip")
//        .size(new BigInteger("1024"))
//        .build();
//    binaryContentRepository.save(binaryContent);
//
//    // when
//    BinaryContent resultCreate = binaryContentRepository.findById(binaryContent.getId()).orElse(null);
//
//    // then
//    assertNotNull(resultCreate);
//    assertEquals(binaryContent.getName(), resultCreate.getName());
//    assertEquals(binaryContent.getContentType(), resultCreate.getContentType());
//    assertEquals(binaryContent.getSize(), resultCreate.getSize());
//  }
//
//  @Test // 삭제 테스트
//  void testDeleteFile() {
//
//    // given
//    BinaryContent binaryContent = BinaryContent.builder()
//        .name("backup1")
//        .contentType("application/zip")
//        .size(new BigInteger("1024"))
//        .build();
//    binaryContentRepository.save(binaryContent);
//
//    // when
//    binaryContentRepository.deleteById(binaryContent.getId());
//    BinaryContent getBinaryContent = binaryContentRepository.findById(binaryContent.getId()).orElse(null);
//
//    // then
//    assertNull(getBinaryContent);
//  }
//}