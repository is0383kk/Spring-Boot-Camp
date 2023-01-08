package com.example.springbusinesslogic;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.springbusinesslogic.entity.Employee;
import com.example.springbusinesslogic.service.EmployeeService;

@SpringBootApplication
public class SpringBusinessLogicApplication {

	public static void main(String[] args) {
		// DIコンテナ作成
		ApplicationContext context = SpringApplication.run(SpringBusinessLogicApplication.class, args);
		
		// employeeServiceのBean取得
		EmployeeService employeeService = context.getBean(EmployeeService.class);
		
		
		// 1. 全検索
		System.out.println("===================全検索===================");
		List<Employee> allEmpList = employeeService.findAll();
		for (Employee employee : allEmpList) {
			System.out.println("全検索結果 => " + employee);
		}
		
		
		// 2. 主キー検索
		System.out.println("===================主キー検索===================");
		int key = 1; // 検索キー
		Employee foundEmp = employeeService.findById(key);
		System.out.println("主キー検索(key=" + key + ") => " + foundEmp);
		
		
		// 3. 社員追加
		System.out.println("===================社員追加===================");
		Employee newEmployee = new Employee();
		newEmployee.setName("森川悠人");
		newEmployee.setJoinedDate(LocalDate.of(2022,4,1));
		newEmployee.setDepartmentName("お遊び部");
		newEmployee.setEmail("m.y@example.com");
		
		employeeService.insert(newEmployee);
		System.out.println("社員追加 => " + newEmployee);
		
		
		// 4. 主キーで指定した社員情報の更新
		System.out.println("===================社員更新===================");
		Employee updateEmp = employeeService.findById(key);
		updateEmp.setName("吉田佑希");
		updateEmp.setJoinedDate(LocalDate.of(2022,4,1));
		newEmployee.setDepartmentName("お遊び部");
		newEmployee.setEmail("y.y@example.com");
		
		employeeService.update(updateEmp);
		System.out.println("更新された要員 => " + updateEmp);
		
		
		// 5. 主キーで指定した社員の削除
		System.out.println("===================社員削除===================");
		System.out.println("削除する要員 => " + employeeService.findById(newEmployee.getId()));
		employeeService.delete(newEmployee.getId());
	}

}
