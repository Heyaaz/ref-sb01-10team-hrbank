//package com.sprint.example.sb01part2hrbankteam10ref.controller.docs;
//
//import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentCreateRequest;
//import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentDto;
//import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentResponseDto;
//import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentUpdateRequest;
//import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
//import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.ExampleObject;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import java.util.List;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@Tag(name = "Department", description = "부서 관리 API")
//public interface DepartmentDocs {
//    @Operation(summary = "부서 등록", description = "새로운 부서를 등록합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "등록 성공",
//                    content = @Content(schema = @Schema(implementation = DepartmentDto.class))),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이름",
//                    content = @Content(examples = @ExampleObject(value = "{ 'error': '이미 존재하는 부서입니다.' }"))),
//            @ApiResponse(responseCode = "500", description = "서버 오류",
//                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
//    })
//    @PostMapping
//    ResponseEntity<DepartmentDto> createDepartment(
//            @Parameter(description = "부서 생성 요청 정보")
//            @RequestBody DepartmentCreateRequest request
//    );
//
//    @Operation(summary = "부서 수정", description = "부서 정보를 수정합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "수정 성공",
//                    content = @Content(schema = @Schema(implementation = DepartmentDto.class))),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이름",
//                    content = @Content(examples = @ExampleObject(value = ""))),
//            @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음",
//                    content = @Content(examples = @ExampleObject(value = ""))),
//            @ApiResponse(responseCode = "500", description = "서버 오류",
//                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
//    })
//    @PatchMapping("/{id}")
//    ResponseEntity<DepartmentDto> updateDepartment(
//            @Parameter(description = "부서 ID")
//            @PathVariable Integer id,
//
//            @Parameter(description = "부서 수정 요청 정보")
//            @RequestBody DepartmentUpdateRequest request
//    );
//
//    @Operation(summary = "부서 삭제", description = "부서를 삭제합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "204", description = "삭제 성공"),
//            @ApiResponse(responseCode = "400", description = "소속 직원이 있는 부서는 삭제할 수 없음",
//                    content = @Content(examples = @ExampleObject(value = ""))),
//            @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음",
//                    content = @Content(examples = @ExampleObject(value = ""))),
//            @ApiResponse(responseCode = "500", description = "서버 오류",
//                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
//    })
//    @DeleteMapping("/{id}")
//    ResponseEntity<String> deleteDepartment(
//            @Parameter(description = "부서 ID")
//            @PathVariable Integer id
//    );
//
//    @Operation(summary = "부서 상세 조회", description = "부서 상세 정보를 조회합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "조회 성공",
//                    content = @Content(schema = @Schema(implementation = DepartmentDto.class))),
//            @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음",
//                    content = @Content(examples = @ExampleObject(value = ""))),
//            @ApiResponse(responseCode = "500", description = "서버 오류",
//                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
//    })
//    @GetMapping("/{id}")
//    ResponseEntity<DepartmentDto> getDepartment(
//            @Parameter(description = "부서 ID")
//            @PathVariable Integer id
//    );
//
//}
