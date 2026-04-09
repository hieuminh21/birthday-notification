package com.company.birthday.repository;

import com.company.birthday.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

	@EntityGraph(attributePaths = "department")
	Page<Employee> findByIsActiveTrue(Pageable pageable);

	boolean existsByEmployeeCodeIgnoreCase(String employeeCode);

	boolean existsByEmployeeCodeIgnoreCaseAndEmployeeIdNot(String employeeCode, Integer employeeId);

	boolean existsByEmailIgnoreCase(String email);

	boolean existsByEmailIgnoreCaseAndEmployeeIdNot(String email, Integer employeeId);

	Optional<Employee> findByEmployeeIdAndIsActiveTrue(Integer employeeId);
}
