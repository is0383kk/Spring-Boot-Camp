package com.example.springbusinesslogic.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;
import com.example.springbusinesslogic.entity.Employee;

@Mapper
public interface EmployeeMapper {
	
	// 1. 全検索
	@Select("SELECT * FROM employee")
	List<Employee> findALL();
	
	// 2. 主キー検索
	@Select({"SELECT * FROM employee",
			  "WHERE id = #{id}"})
	Employee findById(Integer id);
	
	// 3. 社員追加
	@Insert({"INSERT INTO employee(name, joined_date, department_name, email)",
			  "VALUES(#{name}, #{joinedDate}, #{departmentName}, #{email})"})
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	void insert(Employee employee);
	
	// 4. 主キーで指定した社員情報の更新
	@Update({"UPDATE employee", 
			  "SET name = #{name}, joined_date = #{joinedDate}, department_name = #{departmentName}, email = #{email}", 
			  "WHERE id = #{id}"})

	int update(Employee employee);
	
	// 5. 主キーで指定した社員の削除
	@Delete({"DELETE FROM employee WHERE id = #{id}"})
	int delete(Integer id);

}
