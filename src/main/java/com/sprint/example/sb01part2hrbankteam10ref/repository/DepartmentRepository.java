package com.sprint.example.sb01part2hrbankteam10ref.repository;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import com.sprint.example.sb01part2hrbankteam10ref.repository.custom.DepartmentRepositoryCustom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer>, DepartmentRepositoryCustom {
    
    boolean existsByName(String name);
    
    Optional<Department> findByNameEquals(String name);

}
