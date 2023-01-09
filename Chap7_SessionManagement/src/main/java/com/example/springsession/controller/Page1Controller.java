package com.example.springsession.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.springsession.model.Account;

@Controller
public class Page1Controller {
	
	private Account account;
	
	@Autowired
	public Page1Controller(Account account) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.account = account;
	}
	
	@GetMapping("page1")
	public String page1(Model model) {
		model.addAttribute("account", account);
		return "page1";
	}

}
