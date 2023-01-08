package com.example.springpresentation.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.springpresentation.entity.Employee;

public class EmployeeForm {
	
	// フィールド
	private String name;
	
	@DateTimeFormat(pattern = "uuuu-MM-dd")
	private LocalDate joinedDate;
	
	private String departmentName;
	
	private String email;
	
	// FormクラスをEntityクラスに変換するメソッド
	public Employee convertToEntity() {
		return new Employee(name, joinedDate, departmentName, email);
	}
	
	// getter・setter
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getJoinedDate() {
		return joinedDate;
	}

	public void setJoinedDate(LocalDate joinedDate) {
		this.joinedDate = joinedDate;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "EmployeeForm [name=" + name + 
				", joinedDate=" + joinedDate + 
				", departmentName=" + departmentName + 
				", email=" + email + "]";
	}
	
	

}
