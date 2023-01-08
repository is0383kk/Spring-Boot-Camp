package com.example.springbeanvaridation.form;

import java.util.Objects;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public class EmailForm {
	
	// フィールド
	@NotBlank
	@Length(min = 10, max = 100)
	private String email;
	
	@NotBlank
	@Length(min = 10, max = 100)
	private String confirmEmail;

	// getter・setter
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirmEmail() {
		return confirmEmail;
	}

	public void setConfirmEmail(String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}
	
	// true以外を不可
	@AssertTrue 
	public boolean isSameEmail() {
		return Objects.equals(email, confirmEmail);
	}

}
