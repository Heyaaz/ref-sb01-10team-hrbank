package com.sprint.example.sb01part2hrbankteam10ref.dto.binary_content;

import com.sprint.example.sb01part2hrbankteam10ref.entity.BinaryContent;
import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinaryContentUploadResponse {
  Integer id;
  String name;
  String contentType;
  BigInteger size;

  // 정적 팩토리 메서드
  public static BinaryContentUploadResponse from(BinaryContent binaryContent) {
    BinaryContentUploadResponse response = new BinaryContentUploadResponse();
    response.setId(binaryContent.getId());
    response.setName(binaryContent.getName());
    response.setContentType(binaryContent.getContentType());
    response.setSize(binaryContent.getSize());
    return response;
  }
}