package com.example.springbeanvaridation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
	
	@GetMapping("/")
	public String root() {
		// "redirect:"を戻り値の先頭につけることでリダイレクトできる
		return "redirect:hello/index";
	}

}
