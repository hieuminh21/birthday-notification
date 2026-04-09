package com.company.birthday.service.impl;

import com.company.birthday.dto.request.EmployeeFormRequest;
import com.company.birthday.dto.response.EmployeeListResponse;
import com.company.birthday.entity.Department;
import com.company.birthday.entity.Employee;
import com.company.birthday.repository.DepartmentRepository;
import com.company.birthday.repository.EmployeeRepository;
import com.company.birthday.service.EmployeeService;
import com.company.birthday.service.exception.DuplicateFieldException;
import com.company.birthday.service.mapper.EmployeeMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	private final EmployeeRepository employeeRepository;
	private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

	public EmployeeServiceImpl(
			EmployeeRepository employeeRepository,
			DepartmentRepository departmentRepository,
			EmployeeMapper employeeMapper
	) {
		this.employeeRepository = employeeRepository;
		this.departmentRepository = departmentRepository;
		this.employeeMapper = employeeMapper;
	}

	@Override
	public Page<EmployeeListResponse> getActiveEmployees(Pageable pageable) {
		Page<Employee> employeePage = employeeRepository.findByIsActiveTrue(pageable);
		AtomicInteger rowNumber = new AtomicInteger((int) pageable.getOffset() + 1);

		return employeePage.map(employee -> employeeMapper.toListResponse(employee, rowNumber.getAndIncrement()));
	}

	@Override
	public List<Department> getAllDepartments() {
		return departmentRepository.findAllDepartment();
	}

	@Override
	public EmployeeListResponse createEmployee(EmployeeFormRequest request) {
		validateUniqueFields(request, null);
		Employee employee = new Employee();
		updateEntity(employee, request);
		Employee savedEmployee = employeeRepository.save(employee);
		return employeeMapper.toListResponse(savedEmployee, 0);
	}

	@Override
	public EmployeeListResponse updateEmployee(Integer employeeId, EmployeeFormRequest request) {
		validateUniqueFields(request, employeeId);
		Employee employee = employeeRepository.findByEmployeeIdAndIsActiveTrue(employeeId)
				.orElseThrow(() -> new EntityNotFoundException("Nhan vien khong ton tai hoac da bi khoa."));
		updateEntity(employee, request);
		Employee savedEmployee = employeeRepository.save(employee);
		return employeeMapper.toListResponse(savedEmployee, 0);
	}

	private void updateEntity(Employee employee, EmployeeFormRequest request) {
		Department department = departmentRepository.findById(request.getDepartmentId())
				.orElseThrow(() -> new EntityNotFoundException("Phong ban khong ton tai."));

		employee.setDepartment(department);
		employee.setJobTitle(request.getJobTitle().trim());
		employee.setEmployeeCode(request.getEmployeeCode().trim());
		employee.setFullName(request.getFullName().trim());
		employee.setDateOfBirth(request.getDateOfBirth());
		employee.setPhoneNumber(request.getPhoneNumber().trim());
		employee.setEmail(request.getEmail().trim());
		employee.setIsActive(true);
	}

	private void validateUniqueFields(EmployeeFormRequest request, Integer excludedEmployeeId) {
		String employeeCode = request.getEmployeeCode() == null ? null : request.getEmployeeCode().trim();
		String email = request.getEmail() == null ? null : request.getEmail().trim();

		validateEmployeeCode(employeeCode, excludedEmployeeId);
		validateEmail(email, excludedEmployeeId);
	}

	private void validateEmployeeCode(String employeeCode, Integer excludedEmployeeId) {
		if (employeeCode == null || employeeCode.isBlank()) {
			return;
		}

		boolean duplicateEmployeeCode = excludedEmployeeId == null
				? employeeRepository.existsByEmployeeCodeIgnoreCase(employeeCode)
				: employeeRepository.existsByEmployeeCodeIgnoreCaseAndEmployeeIdNot(employeeCode, excludedEmployeeId);
		if (duplicateEmployeeCode) {
			throw new DuplicateFieldException("employeeCode", "Ma nhan vien da ton tai.");
		}
	}

	private void validateEmail(String email, Integer excludedEmployeeId) {
		if (email == null || email.isBlank()) {
			return;
		}

		boolean duplicateEmail = excludedEmployeeId == null
				? employeeRepository.existsByEmailIgnoreCase(email)
				: employeeRepository.existsByEmailIgnoreCaseAndEmployeeIdNot(email, excludedEmployeeId);
		if (duplicateEmail) {
			throw new DuplicateFieldException("email", "Email da ton tai.");
		}
	}
}

