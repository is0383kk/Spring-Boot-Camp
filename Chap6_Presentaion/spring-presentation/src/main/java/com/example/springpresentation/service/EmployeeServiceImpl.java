package com.example.springpresentation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springpresentation.entity.Employee;
import com.example.springpresentation.mapper.EmployeeMapper;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	private final EmployeeMapper employeeMapper;
	
	// コンストラクタインジェクションによるDI化（@Autowiredはあってもなくても良い）
	@Autowired
	public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.employeeMapper = employeeMapper;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Employee> findAll() {
		// TODO 自動生成されたメソッド・スタブ
		List<Employee> employeeList = employeeMapper.findAll();
		return employeeList;
	}

	@Override
	@Transactional(readOnly = true)
	public Employee findById(Integer id) {
		// TODO 自動生成されたメソッド・スタブ
		Employee employee = employeeMapper.findById(id);
		return employee;
	}

	@Override
	@Transactional
	public void insert(Employee employee) {
		// TODO 自動生成されたメソッド・スタブ
		employeeMapper.insert(employee);
	}

	@Override
	@Transactional
	public void update(Employee employee) {
		// TODO 自動生成されたメソッド・スタブ
		employeeMapper.update(employee);
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		// TODO 自動生成されたメソッド・スタブ
		employeeMapper.delete(id);
	}
	
}
