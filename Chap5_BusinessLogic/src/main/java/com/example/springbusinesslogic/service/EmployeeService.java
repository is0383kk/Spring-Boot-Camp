package com.example.springbusinesslogic.service;

import java.util.List;

import com.example.springbusinesslogic.entity.Employee;

public interface EmployeeService {
	
	/**
	 *  1. 社員を全件検索
	 * SELECT文で複数リスト分のEmployeeを返す
	 */
	List<Employee> findAll();
	
	/**
	 *  2. 社員を主キー検索	
	 * SELECT文で主キー検索を行い1人分のEmployeeを返す
	 */
	Employee findById(Integer id);
	
	/**
	 *  3. 社員の追加
	 * INSERT文でEmployeeを指定し挿入するためvoid型
	 */
	void insert(Employee employee);
	
	/**
	 *  4. 主キーで指定した社員情報の更新
	 * UPDATE文でEmployeeを指定し更新するためvoid型
	 */
	void update(Employee employee);
	
	/**
	 *  5. 主キーで指定した社員の削除
	 * 主キー検索を行いDELETE文で要員を削除するためvoid型
	 */
	void delete(Integer id);

}
