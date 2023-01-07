package com.example.springpresentation.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.springpresentation.entity.Employee;
import com.example.springpresentation.form.EmployeeForm;
import com.example.springpresentation.service.EmployeeService;

@Controller
public class EmployeeController {
	
	private final EmployeeService employeeService;
	
	// コンストラクタインジェクションによるDI
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	
	@GetMapping("/")
	public String root() {
		return "redirect:index";
	}
	
	// 全検索 → 一覧画面に遷移
	@GetMapping("/index")
	public String index(Model model) {
		List<Employee> employeeList = employeeService.findAll();
		model.addAttribute("employeeList", employeeList);
		System.out.println("===============全検索===============");
		System.out.println("employeeList : " + employeeList);
		return "index";
	}
	
	// 主キー検索 → 詳細画面に遷移
	@GetMapping("/detail")
	public String findById(@RequestParam Integer id, Model model) {
		Employee employee = employeeService.findById(id);
		model.addAttribute("employee", employee);
		return "detail";
	}
	
	// 入力画面に遷移
	@GetMapping("/regist")
	public String regist() {
		return "regist";
	}
	
	// フォームに入力されたリクエストパラメータを受け取りDBへの挿入処理を行う
	// 処理後、一覧画面にリダイレクト
	@PostMapping("/insertComplete")
	public String insert(EmployeeForm employeeForm) {
		Employee employee = employeeForm.convertToEntity();
		employeeService.insert(employee);
		System.out.println("===============挿入===============");
		System.out.println("employee : " + employee);
		return "redirect:index";
	}
	
	@GetMapping("/edit")
	public String edit(@RequestParam Integer id, Model model) {
		model.addAttribute("id", id);
		return "edit";
	}
	
	@PostMapping("/updateComplete")
	public String edit(@RequestParam Integer id, EmployeeForm employeeForm) {
		Employee employee = employeeForm.convertToEntity();
		employee.setId(id);
		employeeService.update(employee);
		System.out.println("===============更新===============");
		System.out.println("employee : " + employee);
		return "redirect:index";
	}
	
	@GetMapping("/deleteComplete")
	public String edit(@RequestParam Integer id) {
		employeeService.delete(id);
		System.out.println("===============削除===============");
		return "redirect:index";
	}

}
