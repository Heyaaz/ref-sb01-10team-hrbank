package com.sprint.example.sb01part2hrbankteam10ref.mapper;


import com.sprint.example.sb01part2hrbankteam10ref.dto.department.DepartmentUpdateRequest;
import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DepartmentUpdateMapper {
  void updateDepartmentFromRequest(DepartmentUpdateRequest request, @MappingTarget Department department);
}
