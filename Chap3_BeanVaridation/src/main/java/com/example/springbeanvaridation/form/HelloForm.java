package com.example.springbeanvaridation.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HelloForm {
	
	// フィールド
	@NotBlank
	@Length(min = 1, max = 20)
	private String userName;
	
	@NotNull
	@Range(min = 1, max = 100)
	private Integer age;
	
	// Getter・setter
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
