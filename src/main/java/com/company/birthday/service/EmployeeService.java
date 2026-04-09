package com.company.birthday.service;

import com.company.birthday.dto.request.EmployeeFormRequest;
import com.company.birthday.dto.response.EmployeeListResponse;
import com.company.birthday.entity.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

	Page<EmployeeListResponse> getActiveEmployees(Pageable pageable);

	List<Department> getAllDepartments();

	EmployeeListResponse createEmployee(EmployeeFormRequest request);

	EmployeeListResponse updateEmployee(Integer employeeId, EmployeeFormRequest request);
}
