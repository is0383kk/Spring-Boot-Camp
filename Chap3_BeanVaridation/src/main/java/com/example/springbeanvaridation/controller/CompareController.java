package com.example.springbeanvaridation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.springbeanvaridation.form.EmailForm;

@Controller
@RequestMapping("/compare")
public class CompareController {
	
	@GetMapping("/index")
	public String index(EmailForm emailForm, Model model) {
		return "compare/index";
	}
	
	@PostMapping("/result")
	public String result(@Validated EmailForm emailForm,
			BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			return "compare/index";
		}
		
		model.addAttribute("email", emailForm.getEmail());
		return "compare/result";
	}

}
