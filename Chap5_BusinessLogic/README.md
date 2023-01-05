# ビジネスロジック層
  
- Javaで開発するアプリケーションは3つの層に分けられる  
  - 永続化層：DBアクセスを担当する。DBアクセスの結果をビジネスロジック層に返す。
  - **ビジネスロジック層：永続化層の結果を元に処理を行う。トランザクション管理などを行う。**
  - プレゼンテーション層：画面などのUIを提供する。ビジネスロジック層の呼び出しを行う。

  
本章では、ビジネスロジック層の構築について学ぶ。

# Dependency Injection

ビジネスロジック層は永続化層の処理を呼び出し、呼び出した結果を元に処理を行う。  
言い換えると、ビジネスロジック層は永続化層のインスタンスが無いと処理を行うことができない。  
  
  
そこで利用するのが「**Dependency Injection（DI）**」である。
DIとは必要なインスタンスをDIコンテナが自動的に代入することを言う。
  
  
以下の様に、Sample1がSample2のを使用（依存）している例を考える。  
この例の場合、DIによって以下の処理が行われる。  

1. Sample2のインスタンスが生成される（Samplw2 sample2）
2. @Autowiredが付与されたコンストラクタ内でSample1インスタンスを生成 （Sample1 sample1）
3. Sample1インスタンスにSample2インスタンスを代入する（sample1 = sample2）
  
上記のように必要なインスタンスをDIコンテナが自動的に代入する
```java
public class Sample1 {

  // Sample1はSample2を使用しているため依存していると言える
	private final Sample2 sample2; 	

  @Autowired
	public Sample1(Sample2 sample2) {
		this.sample2 = sample2;
	}

}
```

```java
public class Sample2 {
  // フィールド・メソッド
}
```


# ビジネスロジック層の構築
本章では、[永続化層](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Chap4_MyBatisSpring)の処理を呼び出すビジネスロジック層を構築する。　　
作成手順は以下のとおりである。  

- ビジネスロジックインタフェースの作成
- ビジネスロジッククラスの作成

実現する機能は以下のとおりである。
1. 社員を全件検索
2. 社員を主キー検索
3. 社員の追加
4. 主キーで指定した社員情報の更新
5. 主キーで指定した社員の削除

## ビジネスロジックインタフェースの作成
ビジネスロジッククラスはインタフェースを作成し、メソッドの定義後に実装クラスを作成する。

- EmployeeService.java：「src/main/java/com/example/service/EmployeeService.java」

```java
package com.example.springbusinesslogic.service;

import java.util.List;

import com.example.springbusinesslogic.entity.Employee;

public interface EmployeeService {
	
	/**
	 *  1. 社員を全件検索
	 * SELECT文で複数リスト分のEmployeeを返す
	 */
	List<Employee> findALL();
	
	/**
	 *  2. 社員を主キー検索	
	 * SELECT文で主キー検索を行い1人分のEmployeeを返す
	 */
	Employee findById(Integer id);
	
	/**
	 *  3. 社員の追加
	 * INSERT文でEmployeeを指定し挿入するためvoid型
	 */
	void insert(Employee employee);
	
	/**
	 *  4. 主キーで指定した社員情報の更新
	 * UPDATE文でEmployeeを指定し更新するためvoid型
	 */
	void update(Employee employee);
	
	/**
	 *  5. 主キーで指定した社員の削除
	 * 主キー検索を行いDELETE文で要員を削除するためvoid型
	 */
	void delete(Integer id);

}
```

## ビジネスロジッククラスの作成

- EmployeeServiceImpl.java：「src/main/java/com/example/service/EmployeeService.java」
```java
package com.example.springbusinesslogic.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.springbusinesslogic.entity.Employee;
import com.example.springbusinesslogic.mapper.EmployeeMapper;

@Service
public class EmployeeServiceImpl implements EmployeeService {
	
	private final EmployeeMapper employeeMapper;
	
	// EmployeeMapperをDIする
	@Autowired
	public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
		this.employeeMapper = employeeMapper;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Employee> findAll(){
		List<Employee> employeeList = employeeMapper.findALL();
		return employeeList;
	}

	@Override
	@Transactional(readOnly = true)
	public Employee findById(Integer id) {
		Employee employee = employeeMapper.findById(id);
		return employee;
	}

	@Override
	public void insert(Employee employee) {
		employeeMapper.insert(employee);
	}

	@Override
	public void update(Employee employee) {
		employeeMapper.update(employee);
	}

	@Override
	public void delete(Integer id) {
		employeeMapper.delete(id);
	}

}
```

|アノテーション|説明|
|---|---|
|@Service|ビジネスロジッククラスに付与するアノテーション|
|@Transactional|トランザクション管理を行うアノテーション <br> 「readOnly」はSELECT文のみの場合に「true」にする|

## @Transactionalによるトランザクション管理
@Transactionalアノテーションを付与すると、メソッドの開始時にトランザクションが開始され、メソッドの終了時にトランザクションがコミットされる。  
仮にメソッド内で非チェック例外が発生いた際は、自動的にロールバックが行われる。また、チェック例外の場合はロールバックされずに例外発生直前までの処理はコミットされる。
  
  
  
@Transactionalには「rollbackFor」「noRollbackFor」要素があり、これを利用すると非チェック例外でのコミット・チェック例外でのロールバックを可能とする。

```java
// SQLExceptionではロールバック・NullPointerExceptionではコミット
@Transactional(rollbackFor = DWLEception.class, noRollbackFor = NullPointerException.class)
public void hogeMethod {
...
}
```

## 実行
SpringBusinessLogicApplication.javaをJavaアプリケーションとして実行する。  
  
- SpringBusinessLogicApplication.java：「src/main/java/com/example/SpringBusinessLogicApplication.java」
```java
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
```
### 実行結果
DIコンテナで保持されているEmployeeService型のオブジェクトを取得しメソッドを呼び出すことで、オブジェクトの処理を呼び出していることがわかる。  
```
===================全検索===================
2023-01-05T11:37:47.872+09:00  INFO 45770 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2023-01-05T11:37:48.158+09:00  INFO 45770 --- [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@4ed4a7e4
2023-01-05T11:37:48.159+09:00  INFO 45770 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2023-01-05T11:37:48.175+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findALL      : ==>  Preparing: SELECT * FROM employee
2023-01-05T11:37:48.196+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findALL      : ==> Parameters: 
2023-01-05T11:37:48.221+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findALL      : <==      Total: 4
全検索結果 => id：1 ,name：吉田佑希 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：tanaka.taro@example.com
全検索結果 => id：2 ,name：佐藤花子 ,joinedDate：2022-04-01 ,departmentName：営業部 ,email：sato.hanako@example.com
全検索結果 => id：3 ,name：山田次郎 ,joinedDate：2022-04-01 ,departmentName：人事部 ,email：yamada.jiro@example.com
全検索結果 => id：4 ,name：鈴木一郎 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：suzuki.ichirou@example.com
===================主キー検索===================
2023-01-05T11:37:48.229+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==>  Preparing: SELECT * FROM employee WHERE id = ?
2023-01-05T11:37:48.229+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==> Parameters: 1(Integer)
2023-01-05T11:37:48.231+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : <==      Total: 1
主キー検索(key=1) => id：1 ,name：吉田佑希 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：tanaka.taro@example.com
===================社員追加===================
2023-01-05T11:37:48.233+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.insert       : ==>  Preparing: INSERT INTO employee(name, joined_date, department_name, email) VALUES(?, ?, ?, ?)
2023-01-05T11:37:48.234+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.insert       : ==> Parameters: 森川悠人(String), 2022-04-01(LocalDate), お遊び部(String), m.y@example.com(String)
2023-01-05T11:37:48.241+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.insert       : <==    Updates: 1
社員追加 => id：18 ,name：森川悠人 ,joinedDate：2022-04-01 ,departmentName：お遊び部 ,email：m.y@example.com
===================社員更新===================
2023-01-05T11:37:48.244+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==>  Preparing: SELECT * FROM employee WHERE id = ?
2023-01-05T11:37:48.244+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==> Parameters: 1(Integer)
2023-01-05T11:37:48.246+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : <==      Total: 1
2023-01-05T11:37:48.247+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.update       : ==>  Preparing: UPDATE employee SET name = ?, joined_date = ?, department_name = ?, email = ? WHERE id = ?
2023-01-05T11:37:48.248+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.update       : ==> Parameters: 吉田佑希(String), 2022-04-01(LocalDate), 開発部(String), tanaka.taro@example.com(String), 1(Integer)
2023-01-05T11:37:48.249+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.update       : <==    Updates: 1
更新された要員 => id：1 ,name：吉田佑希 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：tanaka.taro@example.com
===================社員削除===================
2023-01-05T11:37:48.250+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==>  Preparing: SELECT * FROM employee WHERE id = ?
2023-01-05T11:37:48.251+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==> Parameters: 18(Integer)
2023-01-05T11:37:48.252+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : <==      Total: 1
削除する要員 => id：18 ,name：森川悠人 ,joinedDate：2022-04-01 ,departmentName：お遊び部 ,email：m.y@example.com
2023-01-05T11:37:48.254+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.delete       : ==>  Preparing: DELETE FROM employee WHERE id = ?
2023-01-05T11:37:48.254+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.delete       : ==> Parameters: 18(Integer)
2023-01-05T11:37:48.257+09:00 DEBUG 45770 --- [           main] c.e.s.mapper.EmployeeMapper.delete       : <==    Updates: 1
2023-01-05T11:37:48.259+09:00  INFO 45770 --- [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2023-01-05T11:37:48.262+09:00  INFO 45770 --- [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
```
