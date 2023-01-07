package com.example.springpresentation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.springpresentation.entity.Employee;

@Mapper
public interface EmployeeMapper {
	
	@Select({"SELECT * FROM employee"})
	List<Employee> findAll();
	
	@Select({"SELECT * FROM employee WHERE id = #{id}"})
	Employee findById(Integer id);
	
	@Insert({"INSERT INTO employee(name, joined_date, department_name, email)", 
			  "VALUES(#{name}, #{joinedDate}, #{departmentName}, #{email})"})
	void insert(Employee employee);
	
	@Update({"UPDATE employee", 
			  "SET name = #{name}, joined_date = #{joinedDate}, department_name = #{departmentName}, email = #{email}",
			  "WHERE id = #{id}"})
	void update(Employee employee);
	
	@Delete({"DELETE FROM employee WHERE id = #{id}"})
	void delete(Integer id);

}
