package com.sprint.example.sb01part2hrbankteam10ref.controller.api;

import com.sprint.example.sb01part2hrbankteam10ref.entity.BinaryContent;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.RestApiException;
import com.sprint.example.sb01part2hrbankteam10ref.global.exception.errorcode.BinaryContentErrorCode;
import com.sprint.example.sb01part2hrbankteam10ref.repository.BinaryContentRepository;
import com.sprint.example.sb01part2hrbankteam10ref.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/files")
@RequiredArgsConstructor
public class BinaryContentController {

  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentRepository binaryContentRepository;

  @GetMapping("/{id}/download")
  public ResponseEntity<Resource> download(@PathVariable(value = "id") Integer binaryContentId) {
    String contentType = binaryContentRepository.findById(binaryContentId)
        .map(BinaryContent::getContentType)
        .orElseThrow(() -> new RestApiException(BinaryContentErrorCode
            .BINARY_CONTENT_NOT_FOUND,"파일을 찾을 수 없습니다."));

    Resource resource = null;
    if ("text/csv".equals(contentType)){
      resource = binaryContentStorage.downloadBackup(binaryContentId);
    }else {
      resource = binaryContentStorage.downloadProfile(binaryContentId);
    }
    return ResponseEntity.status(HttpStatus.OK).body(resource);
  }
}
