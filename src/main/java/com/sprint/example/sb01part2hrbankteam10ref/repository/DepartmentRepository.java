package com.sprint.example.sb01part2hrbankteam10ref.repository;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import com.sprint.example.sb01part2hrbankteam10ref.repository.custom.DepartmentRepositoryCustom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DepartmentRepository extends JpaRepository<Department, Integer>, DepartmentRepositoryCustom {
    
    boolean existsByName(String name);
    
    Optional<Department> findByNameEquals(String name);
    
    @Query("SELECT d FROM Department d WHERE " +
           "(d.name LIKE %:name% OR :name = '') AND " +
           "(d.description LIKE %:description% OR :description = '')")
    List<Department> findByNameAndDescription(@Param("name") String name, 
                                             @Param("description") String description);
}
