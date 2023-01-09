package com.example.springsession.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.springsession.model.Account;

@Controller
public class RootController {
	private Account account;
	
	@Autowired
	public RootController(Account account) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.account = account;
	}
	
	@GetMapping("/")
	public String index() {
		account.setDateTime(LocalDateTime.now());
		return "index";
	}
	
	@GetMapping("/login")
	public String login(@RequestParam String name, Model model) {
		account.setName(name);
		account.setDateTime(LocalDateTime.now());
		model.addAttribute("account", account);
		return "top";
	}
	
	@GetMapping("/top")
	public String top(Model model) {
		model.addAttribute("account", account);
		return "top";
	}

}
