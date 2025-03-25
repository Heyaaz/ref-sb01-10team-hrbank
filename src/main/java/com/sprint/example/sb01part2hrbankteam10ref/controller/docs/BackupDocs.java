package com.sprint.example.sb01part2hrbankteam10ref.controller.docs;

import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.BackupDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.backup.CursorPageResponseBackupDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Backup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Tag(name = "Backup", description = "백업 관리 API")
public interface BackupDocs {

    @Operation(summary = "데이터 백업 생성", description = "데이터 백업을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "백업 성공",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(examples = @ExampleObject(value = ""))),
            @ApiResponse(responseCode = "409", description = "이미 진행 중인 백업이 있음",
                    content = @Content(examples = @ExampleObject(value = ""))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @PostMapping
    ResponseEntity<Integer> backup(HttpServletRequest request);

    @Operation(summary = "최근 백업 조회", description = "지정된 상태의 가장 최근 백업 정보를 조회합니다. 상태를 지정하지 않으면 성공적으로 완료된(COMPLETED) 백업을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = BackupDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 상태값",
                    content = @Content(examples = @ExampleObject(value = ""))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @GetMapping("/latest")
    ResponseEntity<BackupDto> getLastBackup(
            @Parameter(description = "백업 상태 (COMPLETED, FAILED, IN_PROGRESS, 기본값: COMPLETED)", example = "COMPLETED")
            @RequestParam Backup.BackupStatus status
    );

    @Operation(summary = "데이터 백업 목록 조회", description = "데이터 백업 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 지원하지 않는 정렬 필드",
                    content = @Content(examples = @ExampleObject(value = ""))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(examples = @ExampleObject(value = "{ 'error': '담당자에게 문의해주세요.' }")))
    })
    @GetMapping
    ResponseEntity<CursorPageResponseBackupDto> getBackupList(
            @Parameter(description = "작업자", example = "192.168.0.1")
            @RequestParam(required = false) String worker,

            @Parameter(description = "상태 (IN_PROGRESS, COMPLETED, FAILED)", example = "완료")
            @RequestParam(required = false) Backup.BackupStatus status,

            @Parameter(description = "시작 시간(부터)", example = "2023-01-01T12:00:00Z")
            @RequestParam(required = false) String startedAtFrom,

            @Parameter(description = "시작 시간(까지)", example = "2024-12-31T23:59:59")
            @RequestParam(required = false) String startedAtTo,

            @Parameter(description = "백업 파일", example = "40")
            @RequestParam(required = false) Integer binaryContentId,

            @Parameter(description = "이전 페이지 마지막 요소 ID")
            @RequestParam(required = false) Integer idAfter,

            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "정렬 필드 (startedAt, endedAt, status)", example = "startedAt")
            @RequestParam(defaultValue = "startedAt") String sortField,

            @Parameter(description = "정렬 방향 (ASC, DESC)", example = "DESC")
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDirection
    );
}
