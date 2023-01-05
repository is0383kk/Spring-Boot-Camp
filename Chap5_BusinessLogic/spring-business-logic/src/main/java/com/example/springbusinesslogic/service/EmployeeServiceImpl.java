package com.example.springbusinesslogic.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springbusinesslogic.entity.Employee;
import com.example.springbusinesslogic.mapper.EmployeeMapper;

@Service
public class EmployeeServiceImpl implements EmployeeService {
	
	private final EmployeeMapper employeeMapper;
	
	// EmployeeMapperをDIする
	@Autowired
	public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
		this.employeeMapper = employeeMapper;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Employee> findAll(){
		List<Employee> employeeList = employeeMapper.findALL();
		return employeeList;
	}

	@Override
	@Transactional(readOnly = true)
	public Employee findById(Integer id) {
		Employee employee = employeeMapper.findById(id);
		return employee;
	}

	@Override
	public void insert(Employee employee) {
		employeeMapper.insert(employee);
	}

	@Override
	public void update(Employee employee) {
		employeeMapper.update(employee);
	}

	@Override
	public void delete(Integer id) {
		employeeMapper.delete(id);
	}

}
