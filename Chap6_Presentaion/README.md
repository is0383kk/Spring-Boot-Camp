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
    
# 作成の流れ
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


# 永続化層の構築
## エンティティクラスの作成
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


## Mapperインタフェースの作成
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

# ビジネスロジック層の構築
## サービスインタフェースの作成
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

## サービスクラスの作成
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

# プレゼンテーション層の構築①：フォームクラス・コントローラクラスの作成
## フォームクラスの作成
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
  

## コントローラクラスの作成
ビジネスロジック層の呼び出しを行うコントローラクラスの作成を行います。  
- EmployeeController.java：「src/main/java/com/example/ベースパッケージ/controller/EmployeeController.java」
  - 「@Controller」を付与

※追加処理後、リダイレクト「return "redirect:index";」により一覧画面に遷移している。これは「PRGパターン」と呼ばれ、画面の再読込による2重登録を防ぐための工夫である。
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
@RequestMapping("/")
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

```
# プレゼンテーション層の構築②：画面の作成
ここでは、画面作成を行う。  
作成する画面は以下の３つである。  
- index.html：社員情報を一覧表示する画面
- regist.html：新規登録（テーブルに挿入）する社員情報を入力する画面
- detail.html：主キー検索された社員情報を表示する画面
- edit.html：修正（レコードを更新）する社員情報を入力する画面

## index.html：社員情報を一覧表示する画面
- index.html：「src/main/resources/index.html」
	- 機能：DB上の社員一覧を表示する
		- Controllerから「employeeList」を受け取り、「employee」に格納している
		- 「th:each」を使って「List<Employee> employeeList」の中身を取り出している
		- 「@{detail(id=*{id})}」：1つ目の「id」はControllerに送るリクエストパラメータの名称、「*{id}」は「employee.id」
		```html
		<tr th:each="employee : ${employeeList}" th:object="${employee}">
			<td th:text="*{id}"></td>
			<td th:text="*{name}"></td>
			<td th:text="*{joinedDate}"></td>
			<td th:text="*{departmentName}"></td>
			<td th:text="*{email}"></td>
			<td><a href="detail.html" th:href="@{detail(id=*{id})}">詳細</a></td>
		</tr>
		```

||説明|
|---|---|
|th:each|繰り返しを記述する属性<br>th:each="変数名":${コレクション名}<br>コレクション名はList型のものをModelに格納する|

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>一覧画面</title>
</head>

<body>
	<h1>社員一覧</h1>
	<form action="index.html" th:action="@{detail}">
		ID：<input type="number" name="id">
		<button>主キー検索を行い詳細画面に遷移</button>
	</form>

	<a href="regist.html" th:href="@{regist}">社員情報の挿入を行う画面に遷移</a>

	<table>
		<tr>
			<th>ID</th>
			<th>氏名</th>
			<th>所属年月日</th>
			<th>所属部署</th>
			<th>メールアドレス</th>
		</tr>
		<tr th:each="employee : ${employeeList}" th:object="${employee}">
			<td th:text="*{id}"></td>
			<td th:text="*{name}"></td>
			<td th:text="*{joinedDate}"></td>
			<td th:text="*{departmentName}"></td>
			<td th:text="*{email}"></td>
			<td><a href="detail.html" th:href="@{detail(id=*{id})}">詳細</a></td>
		</tr>

	</table>
</body>

</html>
```
## regist.html：新規登録（テーブルに挿入）するための社員情報を入力する画面
- regist.html：「src/main/resources/regist.html」
	- 機能：入力フォームに入力された社員情報をControllerに送る
	- 入力フォームとなるので「method="post"」を指定する
	```html
	<form method="post" action="index.html" th:action="@{insertComplete}">
	```
	

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>一覧画面</title>
</head>

<body>
	<h1>社員情報をDBに挿入</h1>

	<a href="index.html" th:href="@{index}">一覧画面に遷移</a>

	<form method="post" action="index.html" th:action="@{insertComplete}">
		<table>
			<tr>
				<th>ID</th>
				<td>自動採番</td>
			</tr>
			<tr>
				<th>氏名</th>
				<td><input type="text" name="name"></td>
			</tr>
			<tr>
				<th>所属年月日</th>
				<td><input type="date" name="joinedDate"></td>
			</tr>
			<tr>
				<th>部署名</th>
				<td><input type="text" name="departmentName"></td>
			</tr>
			<tr>
				<th>メールアドレス</th>
				<td><input type="email" name="email"></td>
			</tr>
		</table>
		<button>登録する</button>
	</form>

</body>
</html>
```
		
## detail.html：主キー検索された社員情報を表示する画面
- detail.html：「src/main/resources/detail.html」
	- 機能：Controllerから「employee」でModelを受け取り、受け取った社員情報を表示する
	- 「${employee.フィールド名}」で値を表示している
	- 表示されている主キーの社員の削除するために「${employee.id}」をリクエストパラメータとしてControllerに送っている
		```html
		<a href="index.html" th:href="@{deleteComplete(id=${employee.id})}">削除する</a>
		```

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>詳細画面</title>
</head>

<body>
	<h1>社員詳細</h1>

	<a href="index.html" th:href="@{index}">一覧画面に遷移</a>
	<a href="edit.html" th:href="@{edit(id=${employee.id})}">修正画面に遷移</a>
	<table>
		<tr>
			<th>ID</th>
			<td th:text="${employee.id}"></td>
		</tr>
		<tr>
			<th>氏名</th>
			<td th:text="${employee.name}"></td>
		</tr>
		<tr>
			<th>所属年月日</th>
			<td th:text="${employee.joinedDate}"></td>
		</tr>
		<tr>
			<th>所属部署</th>
			<td th:text="${employee.departmentName}"></td>
		</tr>
		<tr>
			<th>メールアドレス</th>
			<td th:text="${employee.email}"></td>
		</tr>
	</table>
   <a href="index.html" th:href="@{deleteComplete(id=${employee.id})}">削除する</a>
</body>
</html>
```
## edit.html：修正（レコードを更新）する社員情報を入力する画面
- edit.html：「src/main/resources/edit.html」
	- 機能：「form」に入力された値をControllerに送る
	- 入力フォームなので「method="post"」を指定する
	- Controllerは主キーをリクエストパラメータとして受け取るので「th:action="@{updateComplete(id=${id})}"」としている

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>修正画面</title>
</head>

<body>
	<h1>レコードを修正</h1>

	<a href="index.html" th:href="@{index}">一覧画面に遷移</a>

	<form method="post" action="index.html" th:action="@{updateComplete(id=${id})}">
		<table>
			<tr>
				<th>ID</th>
				<td th:text="${id}"></td>
			</tr>
			<tr>
				<th>氏名</th>
				<td><input type="text" name="name"></td>
			</tr>
			<tr>
				<th>所属年月日</th>
				<td><input type="date" name="joinedDate"></td>
			</tr>
			<tr>
				<th>部署名</th>
				<td><input type="text" name="departmentName"></td>
			</tr>
			<tr>
				<th>メールアドレス</th>
				<td><input type="email" name="email"></td>
			</tr>
		</table>
		<button>修正する</button>
	</form>

</body>
</html>
```
