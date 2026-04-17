package com.company.birthday.repository;

import com.company.birthday.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
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

	List<Employee> findByBirthMonthAndBirthDayAndIsActiveTrue(Integer birthMonth, Integer birthDay);

	default List<Employee> findByTodayBirthday() {
		// TODO: Adjust business rules (timezone/leap day/filters) for "birthday today".
		LocalDate today = LocalDate.now();
		return findByBirthMonthAndBirthDayAndIsActiveTrue(today.getMonthValue(), today.getDayOfMonth());
	}
}
