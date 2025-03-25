package com.sprint.example.sb01part2hrbankteam10ref.repository;

import com.sprint.example.sb01part2hrbankteam10ref.entity.Department;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

  Optional<Department> findByName(String name);

  Optional<Department> findByNameEquals(String name);

  boolean existsByName(String name);

  List<Department> findByNameAndDescription(String name, String description);


  @Query("SELECT d FROM Department d " +
      "WHERE (:nameOrDesc IS NULL OR d.name LIKE %:nameOrDesc% OR d.description LIKE %:nameOrDesc%) " +
      "AND (:idAfter IS NULL OR d.id > :idAfter) " +
      "ORDER BY " +
      "CASE WHEN :sortField = 'name' AND :sortDirection = 'asc' THEN d.name END ASC, " +
      "CASE WHEN :sortField = 'name' AND :sortDirection = 'desc' THEN d.name END DESC, " +
      "CASE WHEN :sortField = 'establishedDate' AND :sortDirection = 'asc' THEN d.establishedDate END ASC, " +
      "CASE WHEN :sortField = 'establishedDate' AND :sortDirection = 'desc' THEN d.establishedDate END DESC")
  List<Department> findDepartmentsWithCursor(
      @Param("nameOrDesc") String nameOrDesc,
      @Param("idAfter") Integer idAfter,
      @Param("sortField") String sortField,
      @Param("sortDirection") String sortDirection);

  long countByNameContainingOrDescriptionContaining(String name, String description);

  Optional<Object> findFirstByEstablishedDateAfterOrderById(LocalDateTime date);

}