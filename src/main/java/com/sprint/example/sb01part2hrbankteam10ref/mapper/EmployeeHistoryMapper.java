package com.sprint.example.sb01part2hrbankteam10ref.mapper;

import com.sprint.example.sb01part2hrbankteam10ref.dto.employee.EmployeeDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.ChangeLogDto;
import com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history.DiffDto;
import com.sprint.example.sb01part2hrbankteam10ref.entity.EmployeeHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EmployeeHistoryMapper {

    public static EmployeeHistory toEntity(String employeeNumber, EmployeeHistory.ChangeType type, String memo,
        EmployeeDto beforeData, EmployeeDto afterData, String clientIp) {

        List<DiffDto> diffs = compareChanges(beforeData, afterData);

        Map<String, Object> changedFields = diffs.stream()
                .collect(Collectors.toMap(DiffDto::getPropertyName, EmployeeHistoryMapper::toDetailMap));

        return EmployeeHistory.builder()
                .employeeNumber(employeeNumber)
                .type(type)
                .memo(memo)
                .modifiedAt(LocalDateTime.now())
                .ipAddress(clientIp)
                .changedFields(changedFields)
                .build();
    }

    public static List<DiffDto> compareChanges(EmployeeDto beforeData, EmployeeDto afterData) {
        List<DiffDto> changes = new ArrayList<>();
        if (beforeData == null) {
            beforeData = EmployeeDto.builder().build();
        } else if (afterData == null) {
            afterData = EmployeeDto.builder().build();
        }

        addDiffIfChanged(changes, "name", beforeData.getName(), afterData.getName());
        addDiffIfChanged(changes, "email", beforeData.getEmail(), afterData.getEmail());
        addDiffIfChanged(changes, "position", beforeData.getPosition(), afterData.getPosition());
//        addDiffIfChanged(changes, "departmentId", beforeData.getDepartmentId(), afterData.getDepartmentId());
        addDiffIfChanged(changes, "department", beforeData.getDepartmentName(), afterData.getDepartmentName());
        addDiffIfChanged(changes, "hireDate", beforeData.getHireDate(), afterData.getHireDate());
        addDiffIfChanged(changes, "status", beforeData.getStatus(), afterData.getStatus());
        // 프로필 이미지 비교는 주석 처리되어 있으므로 그대로 유지합니다.
//        if (!Objects.equals(beforeData.getProfileImageId(), afterData.getProfileImageId())) {
//            changes.add(new DiffDto("profileImageId", String.valueOf(beforeData.getProfileImageId()), String.valueOf(afterData.getProfileImageId())));
//        }

        return changes;
    }

    private static void addDiffIfChanged(List<DiffDto> changes, String propertyName, Object beforeValue, Object afterValue) {
        String beforeStr = beforeValue != null ? beforeValue.toString() : null;
        String afterStr = afterValue != null ? afterValue.toString() : null;
        if (!Objects.equals(beforeStr, afterStr)) {
            changes.add(new DiffDto(propertyName, beforeStr, afterStr));
        }
    }

    public static Map<String, Object> toDetailMap(DiffDto diff) {
        Map<String, Object> detailMap = new HashMap<>();
        detailMap.put("before", diff.getBefore());
        detailMap.put("after", diff.getAfter());
        return detailMap;
    }





    public static ChangeLogDto toChangeLogDto(EmployeeHistory employeeHistory) {
        return ChangeLogDto.fromEntity(employeeHistory);
    }

    public static List<DiffDto> toDiffList(EmployeeHistory history) {
        if (history == null || history.getChangedFields() == null) {
            return Collections.emptyList();
        }
        return history.getChangedFields().entrySet().stream()
                .filter(entry -> entry.getValue() instanceof Map)
                .map(entry -> {
                    Map<?, ?> detailMap = (Map<?, ?>) entry.getValue();
                    String before = detailMap.get("before") != null ? detailMap.get("before").toString() : "";
                    String after = detailMap.get("after") != null ? detailMap.get("after").toString() : "";
                    return new DiffDto(entry.getKey(), before, after);
                })
                .collect(Collectors.toList());
    }

}
