package com.sprint.example.sb01part2hrbankteam10ref.controller.docs;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeCreateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDistributionDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeTrendDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.dto.page.CursorPageResponseDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Employee;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Employee", description = "직원 관리 API")
public interface EmployeeDocs {

    @Operation(summary = "직원 등록", description = "새로운 직원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "등록 성공",
            content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복 이메일",
            content = @Content(examples = @ExampleObject(value = "{ 'error': 'email={email}' }"))),
            @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "{ 'error': 'departmentId={departmentId}' }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @PostMapping
    ResponseEntity<EmployeeDto> createEmployee(
            HttpServletRequest httpServletRequest,
            @Parameter(description = "직원 등록 요청")
            @RequestPart(name = "employee") EmployeeCreateRequest request,

            @Parameter(description = "프로필 이미지")
            @RequestPart(name = "profile", required = false) MultipartFile profile
            );

    @Operation(summary = "직원 수정", description = "직원 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
            content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 중복된 이메일",
            content = @Content(examples = @ExampleObject(value = "{ 'error': 'email={email}' }"))),
            @ApiResponse(responseCode = "404", description = "직원 또는 부서를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': 'departmentId={departmentId}' }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @PatchMapping("/{id}")
    ResponseEntity<EmployeeDto> updateEmployee(
            HttpServletRequest httpServletRequest,

            @Parameter(description = "직원 ID")
            @PathVariable Integer id,

            @Parameter(description = "직원 수정 요청")
            @RequestPart(name = "employee") EmployeeUpdateRequest request,

            @Parameter(description = "프로필 이미지")
            @RequestPart(name = "profile", required = false) MultipartFile profile
            );

    @Operation(summary = "직원 상세 조회", description = "직원 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "404", description = "직원을 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "{ 'error': 'id={id}' }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @GetMapping("/{id}")
    ResponseEntity<EmployeeDto> getEmployee(
            @Parameter(description = "직원 ID") @PathVariable Integer id
    );

    @Operation(summary = "직원 삭제", description = "직원을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "직원을 찾을 수 없음",
            content = @Content(examples = @ExampleObject(value = "{ 'error': 'id={id}' }"))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteEmployee(
            HttpServletRequest httpServletRequest,
            @Parameter(description = "직원 ID") @PathVariable Integer id
    );

//    @Operation(summary = "직원 목록 조회", description = "직원 목록을 조회합니다.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "조회 성공",
//            content = @Content(schema = @Schema(implementation = CursorPageResponseDto.class))),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청",
//                    content = @Content(examples = @ExampleObject(value = ""))),
//            @ApiResponse(responseCode = "500", description = "서버 오류",
//                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
//    })
//    @GetMapping
//    ResponseEntity<CursorPageResponseDto<EmployeeDto>> getListEmployee(
//            @Parameter(description = "직원 이름 또는 이메일", example = "홍길동")
//            @RequestParam(required = false) String nameOrEmail,
//
//            @Parameter(description = "사원 번호", example = "EMP-2023-001")
//            @RequestParam(required = false) String employeeNumber,
//
//            @Parameter(description = "부서 이름", example = "개발팀")
//            @RequestParam(required = false) String departmentName,
//
//            @Parameter(description = "직함", example = "선임 개발자")
//            @RequestParam(required = false) String position,
//
//            @Parameter(description = "입사일 시작", example = "2023-01-01")
//            @RequestParam(required = false) LocalDate hireDateFrom,
//
//            @Parameter(description = "입사일 종료", example = "2023-01-01")
//            @RequestParam(required = false) LocalDate hireDateTo,
//
//            @Parameter(description = "상태", example = "ACTIVE, ON_LEAVE, RESIGNED")
//            @RequestParam(required = false)Employee.EmployeeStatus status,
//
//            @Parameter(description = "이전 페이지의 마지막 요소 ID")
//            @RequestParam(required = false) Integer idAfter,
//
//            @Parameter(description = "커서")
//            @RequestParam(required = false) String cursor,
//
//            @Parameter(description = "페이지 크기", example = "10")
//            @RequestParam(required = false, defaultValue = "10") Integer size,
//
//            @Parameter(description = "정렬 필드 (name, employeeNumber, hireDate)", example = "name")
//            @RequestParam(required = false, defaultValue = "name") String sortField,
//
//            @Parameter(description = "정렬 방향 (asc 또는 desc, 기본값: asc)", example = "asc")
//            @RequestParam(required = false, defaultValue = "asc") String sortDirection
//            );

    @Operation(summary = "직원 분포 조회", description = "지정된 기준으로 그룹화된 직원 분포를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDistributionDto.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(examples = @ExampleObject(value = ""))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @GetMapping("/stats/distribution")
    ResponseEntity<List<EmployeeDistributionDto>> getDistribution(
            @Parameter(description = "그룹화 기준(department: 부서별, position: 직무별, 기본값: department)", example = "department")
            @RequestParam(defaultValue = "department") String groupBy,

            @Parameter(description = "직원 상태(재직중, 휴직중, 퇴사, 기본값: 재직중)", example = "ACTIVE")
            @RequestParam
                    (defaultValue = "ACTIVE") Employee.EmployeeStatus status
    );

    @Operation(summary = "직원 수 추이 조회", description = "지정된 기간 및 시간 단위로 그룹화된 직원 수 추이를 조회합니다. 파라미터를 제공하지 않으면 최근 12개월 데이터를 월 단위로 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeTrendDto.class)))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 시간 단위",
                    content = @Content(examples = @ExampleObject(value = ""))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @GetMapping("/status/trend")
    ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrend(
        @Parameter(description = "시작 일시", example = "from") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime from,
        @Parameter(description = "종료 일시", example = "to") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime to,
        @Parameter(description = "시간 단위 (day, week, month, quarter, year)", example = "month")
        @RequestParam(defaultValue = "month") String unit
    );

    @Operation(summary = "직원 수 조회", description = "지정된 조건에 맞는 직원 수를 조회합니다. 상태 필터링 및 입사일 기간 필터링이 가능합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(examples = @ExampleObject(value = ""))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @GetMapping("/count")
    ResponseEntity<Long> getEmployeeCount(
            @Parameter(description = "직원 상태", example = "ACTIVE")
            @RequestParam(required = false) Employee.EmployeeStatus status,

            @Parameter(description = "입사일 시작(지정 시 해당 기간 내 입사한 직원 수 조회, 미지정 시 전체 직원 수 조회)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

            @Parameter(description = "입사일 종료(fromDate와 함께 사용)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    );
}
