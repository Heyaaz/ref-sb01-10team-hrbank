package com.sprint.example.sb01part2hrbankteam10ref.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "BinaryContent", description = "파일 관리 API")
public interface BinaryContentDocs {
    @Operation(summary = "파일 다운로드", description = "파일을 다운로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "다운로드 성공",
                    content = @Content(schema = @Schema(implementation = Resource.class))),
            @ApiResponse(responseCode = "404", description = "파일을 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = ""))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @GetMapping("/{id}/download")
    ResponseEntity<Resource> download(
            @Parameter(description = "파일 ID")
            @PathVariable(value = "id") Integer binaryContentId
    );
}
