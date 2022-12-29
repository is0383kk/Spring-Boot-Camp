package com.example.springbeanvaridation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.springbeanvaridation.form.HelloForm;

@Controller
@RequestMapping("/hello")
public class HelloController {

	@GetMapping("/index")
	public String index(HelloForm helloForm, Model model) {
		
		return "hello/index";
	}

	@GetMapping("/result")
	public String result(@Validated HelloForm helloForm,
			BindingResult bindingResult, Model model) {

		if (bindingResult.hasErrors()) {
			return "hello/index";
		}
		
		model.addAttribute("userName", helloForm.getUserName());
		model.addAttribute("age", helloForm.getAge());
		
		return "hello/result";
	}

}
