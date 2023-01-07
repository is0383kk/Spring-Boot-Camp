package com.example.springpresentation.service;

import java.util.List;

import com.example.springpresentation.entity.Employee;

public interface EmployeeService {
	
	List<Employee> findAll();
	
	Employee findById(Integer id);
	
	void insert(Employee employee);
	
	void update(Employee employee);
	
	void delete(Integer id);

}
