# プレゼンテーション層
- Javaで開発するアプリケーションは3つの層に分けられる  
  - 永続化層：DBアクセスを担当する。DBアクセスの結果をビジネスロジック層に返す。
  - ビジネスロジック層：永続化層の結果を元に処理を行う。トランザクション管理などを行う。
  - **プレゼンテーション層：画面などのUIを提供する。ビジネスロジック層の呼び出しを行う。**  

本章では、プレゼンテーション層の構築について学ぶ。  
そして、永続化層・ビジネスロジック層・プレゼンテーション層を組み合わせたアプリケーションの作成方法を学ぶ。  

---

- 事前準備
  - プロジェクトの作成
    - Gradle-Groovy
    - 依存ライブラリ
      - Thymeleaf・Spring Web・MySQL Driver・MyBatis Framework
    - application.properties
      ```
      # データベース接続先の定義
      spring.datasource.url=jdbc:mysql://localhost/DB名
      spring.datasource.username=root
      spring.datasource.password=パスワード
      spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
      spring.sql.init.mode=never
      spring.sql.init.encoding=utf-8

      # MyBatis
      mybatis.configuration.map-underscore-to-camel-case=true

      # ログレベル
      logging.level.com.example.springpresentation.mapper=debug

      ```
    
# WEBアプリを作成の流れ
本章では以下の流れでアプリケーションを作成する。
1. 永続化層の構築
  - エンティティクラスの作成
  - Mapperインタフェースの作成
2. ビジネスロジック層の構築  
  - サービスインタフェースの作成
  - サービスクラスの作成
3. プレゼンテーション層の作成
  - フォームクラスの作成
  - 画面の作成



## 永続化層①：エンティティクラスの作成
エンティティクラスを作成します。  
実装自体は[ここで作ったもの](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Chap4_MyBatisSpring#%E3%82%A8%E3%83%B3%E3%83%86%E3%82%A3%E3%83%86%E3%82%A3%E3%82%AF%E3%83%A9%E3%82%B9%E3%81%AE%E4%BD%9C%E6%88%90%E6%89%8B%E9%A0%86)と同じです。  
異なる点は、後に実装するフォームクラスからエンティティクラスに変換する際に使用するコンストラクタがある点です。  

- Employee.java：「src/main/java/com/example/ベースパッケージ/entity/employee.java」
  - **DBのカラムに対応する**
    - ※「DB側:joined_data」の場合、「Java側:joinedData」とする
    - 「mybatis.configuration.map-underscore-to-camel-case=true」とすることで以上の変換が可能
  - getter・setterを持つ
```java
package com.example.springpresentation.entity;

import java.time.LocalDate;

public class Employee {
	
	/**
	 * フィールド
	 * DBのカラムに対応する
	 */
	private Integer id;
	
	private String name;
	
	private LocalDate joinedDate;
	
	private String departmentName;
	
	private String email;
	
	// コンストラクタ
	public Employee() {
		super();
	}
	
	// FormクラスからEntityクラスに変換する際に使用するコンストラクタ
	public Employee(String name, LocalDate joinedDate, String departmentName, String email) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.name = name;
		this.joinedDate = joinedDate;
		this.departmentName = departmentName;
		this.email = email;
	}
	
	/**
	 * getter・setter
	 */
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getJoinedDate() {
		return joinedDate;
	}
	public void setJoinedDate(LocalDate joinedDate) {
		this.joinedDate = joinedDate;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String toString() {
		return "id：" + this.id +
				" ,name：" + this.name +
				" ,joinedDate：" + this.joinedDate +
				" ,departmentName：" + this.departmentName +
				" ,email：" + this.email;
	}
	
}
```


## 永続化層②：Mapperインタフェースの作成
DBへのアクセスを行うMapperインタフェースを作成します。  
実装自体は[ここで実装したもの](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Chap4_MyBatisSpring#mapper)と同じです。  
  
  
- EmployeeMapper.java：「src/main/java/com/example/ベースパッケージ/mapper/EmployeeMapper.java」
  - Mapperを付与
  - 機能
    - 全検索
    - 主キー検索
    - 追加
    - 更新
    - 削除

```java
package com.example.springpresentation.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.springpresentation.entity.Employee;

@Mapper
public interface EmployeeMapper {
	
	@Select({"SELECT * FROM employee"})
	List<Employee> findAll();
	
	@Select({"SELECT * FROM employee WHERE id = #{id}"})
	Employee findById(Integer id);
	
	@Insert({"INSERT INTO employee(name, joinedDate, departmentName, emial)", 
			  "VALUES(#{name}, #{joinedDate}, #{departmentName}, #{email})"})
	void insert(Employee employee);
	
	@Update({"UPDATE employee", 
			  "SET name = #{name}, joined_date = #{joinedDate}, department_name = #{departmentName}, email = #{email}",
			  "WHERE id = #{id"})
	void update(Employee employee);
	
	@Delete({"DELETE FROM employee WHERE id = #{id}"})
	void delete(Integer id);

}
```

## ビジネスロジック層①：サービスインタフェースの作成
ビジネスロジック層では、まずインタフェースを作成し、メソッドの定義後に実装クラスを作成する。  

実装自体は[ここで実装したもの](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Chap5_BusinessLogic#%E3%83%93%E3%82%B8%E3%83%8D%E3%82%B9%E3%83%AD%E3%82%B8%E3%83%83%E3%82%AF%E3%82%A4%E3%83%B3%E3%82%BF%E3%83%95%E3%82%A7%E3%83%BC%E3%82%B9%E3%81%AE%E4%BD%9C%E6%88%90)と同じです。  
  
- EmployeeService.java：「src/main/java/com/example/ベースパッケージ/service/EmployeeService.java」
  - Mapperインタフェースで定義した各処理に沿って記述します
```java
package com.example.springpresentation.service;

import java.util.List;

import com.example.springpresentation.entity.Employee;

public interface EmployeeService {
	
	List<Employee> findAll();
	
	Employee findById(Integer id);
	
	void insert(Employee employee);
	
	void update(Employee employee);
	
	void delete(Integer id);

}
```

## ビジネスロジック層②：サービスクラスの作成
サービスインタフェースを元にサービスクラスの作成を行います。  
実装自体は[ここで実装したもの](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Chap5_BusinessLogic#%E3%83%93%E3%82%B8%E3%83%8D%E3%82%B9%E3%83%AD%E3%82%B8%E3%83%83%E3%82%AF%E3%82%AF%E3%83%A9%E3%82%B9%E3%81%AE%E4%BD%9C%E6%88%90)と同じです。  
  
- EmployeeServiceImpl.java：「src/main/java/com/example/ベースパッケージ/service/EmployeeServiceImpl.java」
  - 「@Service」を付与
  - SELECT文には「@Transactional(readOnly = true)」を付与し、それ以外は「@Transactional」を付与
```java
ppackage com.example.springpresentation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springpresentation.entity.Employee;
import com.example.springpresentation.mapper.EmployeeMapper;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	private final EmployeeMapper employeeMapper;
	
	// コンストラクタインジェクションによるDI化（@Autowiredはあってもなくても良い）
	@Autowired
	public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.employeeMapper = employeeMapper;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Employee> findAll() {
		// TODO 自動生成されたメソッド・スタブ
		List<Employee> employeeList = employeeMapper.findAll();
		return employeeList;
	}

	@Override
	@Transactional(readOnly = true)
	public Employee findById(Integer id) {
		// TODO 自動生成されたメソッド・スタブ
		Employee employee = employeeMapper.findById(id);
		return employee;
	}

	@Override
	@Transactional
	public void insert(Employee employee) {
		// TODO 自動生成されたメソッド・スタブ
		employeeMapper.insert(employee);
	}

	@Override
	@Transactional
	public void update(Employee employee) {
		// TODO 自動生成されたメソッド・スタブ
		employeeMapper.update(employee);
	}

	@Override
	@Transactional
	public void delete(Integer id) {
		// TODO 自動生成されたメソッド・スタブ
		employeeMapper.delete(id);
	}
	
}
```

## プレゼンテーション層①：フォームクラスの作成
入力フォームの入力を受け取るクラスであるフォームクラスを作成する。  
※フォームクラスはエンティティクラスと似た構造になるが、クラスは別々で作成する。

- EmployeeForm.java：「src/main/java/com/example/ベースパッケージ/form/EmployeeForm.java」
  - **入力フォームの各要素に対応する**
  - フォームクラスをエンティティクラスに変換するためのメソッドを用意する
  ```java
  // FormクラスをEntityクラスに変換するメソッド
	public Employee convertToEntity() {
		return new Employee(name, joinedDate, departmentName, email);
	}
  ```
```java
package com.example.springpresentation.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.springpresentation.entity.Employee;

public class EmployeeForm {
	
	// フィールド
	private String name;
	
	@DateTimeFormat(pattern = "uuuu-MM-dd")
	private LocalDate joinedDate;
	
	private String departmentName;
	
	private String email;
	
	// FormクラスをEntityクラスに変換するメソッド
	public Employee convertToEntity() {
		return new Employee(name, joinedDate, departmentName, email);
	}
	
	// getter・setter
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDate getJoinedDate() {
		return joinedDate;
	}

	public void setJoinedDate(LocalDate joinedDate) {
		this.joinedDate = joinedDate;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "EmployeeForm [name=" + name + 
				", joinedDate=" + joinedDate + 
				", departmentName=" + departmentName + 
				", email=" + email + "]";
	}
}
```
|アノテーション|説明|
|---|---|
|@DateTimeFormat|日付のフォーマットを指定する<br>※アノテーションを使わなくとも「application.properties」に日付フォーマットを指定できる。<br>「spring.mvc.format.date=iso」を指定するとアノテーションを付与しなくても文字列からLocalDate型に「uuuu-MM-dd」形式で型変換される|

## プレゼンテーション層②：コントローラクラスの作成
ビジネスロジック層の呼び出しを行うコントローラクラスの作成を行います。  
- EmployeeController.java：「src/main/java/com/example/ベースパッケージ/controller/EmployeeController.java」
  - 「@Controller」を付与
```java
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
@RequestMapping("/employee")
public class EmployeeController {
	
	private final EmployeeService employeeService;
	
	// コンストラクタインジェクションによるDI
	public EmployeeController(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}
	
	// 全検索 → 一覧画面に遷移
	@GetMapping("/index")
	public String index(Model model) {
		List<Employee> employeeList = employeeService.findAll();
		model.addAttribute("employeeList", employeeList);
		return "index";
	}
	
	// 主キー検索 → 詳細画面に遷移
	@GetMapping("/findById")
	public String findById(@RequestParam Integer id, Model model) {
		Employee employee = employeeService.findById(id);
		model.addAttribute("employee", employee);
		return "findById";
	}
	
	// 入力画面に遷移
	@GetMapping("/insertForm")
	public String moveInsert() {
		return "insertForm";
	}
	
	// フォームに入力されたリクエストパラメータを受け取りDBへの挿入処理を行う
	// 処理後、一覧画面にリダイレクト
	@PostMapping("/insertComplete")
	public String insert(EmployeeForm employeeForm) {
		Employee employee = employeeForm.convertToEntity();
		employeeService.insert(employee);
		return "redirect:index";
	}

}
```

