package com.sprint.example.sb01part2hrbankteam10ref.repository.custom;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import java.util.List;

public interface DepartmentRepositoryCustom {
    
    List<Department> findDepartmentsWithCursor(
        String nameOrDescription, 
        Integer cursorId, 
        int size, 
        String sortField, 
        boolean isAscending);
}
