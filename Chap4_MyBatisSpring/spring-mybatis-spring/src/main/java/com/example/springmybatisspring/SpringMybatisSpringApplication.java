package com.example.springmybatisspring;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.springmybatisspring.entity.Employee;
import com.example.springmybatisspring.mapper.EmployeeMapper;

@SpringBootApplication
public class SpringMybatisSpringApplication {

	public static void main(String[] args) {
		// DIコンテナ作成
		ApplicationContext context = SpringApplication.run(SpringMybatisSpringApplication.class, args);
		
		// EmployeeMapperのBean取得
		EmployeeMapper employeeMapper = context.getBean(EmployeeMapper.class);
		
		
		// 1. 全検索
		System.out.println("===================全検索===================");
		List<Employee> allEmpList = employeeMapper.findALL();
		for (Employee employee : allEmpList) {
			System.out.println("全検索結果 => " + employee);
		}
		
		
		// 2. 主キー検索
		System.out.println("===================主キー検索===================");
		int key = 1; // 検索キー
		Employee foundEmp = employeeMapper.findById(key);
		System.out.println("主キー検索(key=" + key + ") => " + foundEmp);
		
		
		// 3. 社員追加
		System.out.println("===================社員追加===================");
		Employee newEmployee = new Employee();
		newEmployee.setName("森川悠人");
		newEmployee.setJoinedDate(LocalDate.of(2022,4,1));
		newEmployee.setDepartmentName("お遊び部");
		newEmployee.setEmail("m.y@example.com");
		
		employeeMapper.insert(newEmployee);
		System.out.println("社員追加 => " + newEmployee);
		
		
		// 4. 主キーで指定した社員情報の更新
		System.out.println("===================社員更新===================");
		Employee updateEmp = employeeMapper.findById(key);
		updateEmp.setName("吉田佑希");
		updateEmp.setJoinedDate(LocalDate.of(2022,4,1));
		newEmployee.setDepartmentName("お遊び部");
		newEmployee.setEmail("y.y@example.com");
		
		int updatedCount = employeeMapper.update(updateEmp);
		System.out.println("更新された要員 => " + updateEmp);
		
		
		// 5. 主キーで指定した社員の削除
		System.out.println("===================社員削除===================");
		System.out.println("削除する要員 => " + employeeMapper.findById(newEmployee.getId()));
		int deletedCount = employeeMapper.delete(newEmployee.getId());

	}

}
