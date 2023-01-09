package com.example.springsession.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.springsession.model.Account;

@Controller
public class Page2Controller {
	
	private Account account;
	
	@Autowired
	public Page2Controller(Account account) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.account = account;
	}
	
	@GetMapping("page2")
	public String page2(Model model) {
		model.addAttribute("account", account);
		return "page2";
	}

}
