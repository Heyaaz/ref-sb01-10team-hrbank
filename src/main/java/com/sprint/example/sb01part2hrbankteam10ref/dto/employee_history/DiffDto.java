package com.sprint.example.sb01part2hrbankteam10ref.dto.employee_history;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiffDto {

    private String propertyName;
    private String before;
    private String after;
}
