# 永続化層  
- Javaで開発するアプリケーションは3つの層に分けられる  
  - 永続化層：DBアクセスを担当する。DBアクセスの結果をビジネスロジック層に返す。
  - ビジネスロジック層：永続化層の結果を元に処理を行う。トランザクション管理などを行う。
  - プレゼンテーション層：画面などのUIを提供する。ビジネスロジック層の呼び出しを行う。

  
本章では、MybBatis-Springを用いた永続化層の構築について学ぶ。

# My-Batis-Springのための設定
- EclipseからSpringスタータプロジェクトを作成する
  - Eclipseから「新規」->「Springスタータプロジェクト」
  - パッケージングは「jar」
  - パッケージネーム・成果物・プロジェクトの名前に合わせる
    - スプリングスタータプロジェクト・依存関係：「MyBatis Framework」と「MySQL Driver」を選択 
    - build.gradleに選択した機能情報が記述

|機能|説明|
|---|---|
|mybatis-spring-boot-starter|MyBatis及びSpringと連携するMyBatis-Springライブラリが含まれる。|
|mysql-connector-java|MySQLのJDBCドライバー。実行時のみの利用のため「runtimeOnly」が指定されている。|

---

依存ライブラリを導入後のBuild.gradleは以下のようになる
```
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.0.1'
	id 'io.spring.dependency-management' version '1.1.0'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.0'
	runtimeOnly 'com.mysql:mysql-connector-j'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
	useJUnitPlatform()
}
```

---

- application.properties：JDBC・MyBatisの設定を記述

```
# データベース接続先の定義
spring.datasource.url=jdbc:mysql://localhost/sampledb
spring.datasource.username=root
spring.datasource.password=ぱすわーど
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.sql.init.mode=never
spring.sql.init.encoding=utf-8

# MyBatis
mybatis.configuration.map-underscore-to-camel-case=true
```

||説明|
|---|---|
|spring.datasource.url|JDBCのURLを指定する。「spring.datasource.url=jdbc:mysql://localhost/DB名」とする。|
|spring.datasource.username|DBのユーザ名を指定する。|
|spring.datasource.password|DBのパスワードを指定する。|
|mybatis.configuration.map-underscore-to-camel-case=true|「hoge_hoge」から「hogeHoge」への変換を有効化する|

- テーブルの作成
```sql
CREATE TABLE employee
(
	id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(10),
    joined_date DATE,
    department_name VARCHAR(20),
    email VARCHAR(256)
);
```

```sql
INSERT INTO employee VALUES(1,'田中太郎','2022-04-01','開発部','tanaka.taro@example.com');
INSERT INTO employee VALUES(2,'佐藤花子','2022-04-01','営業部','sato.hanako@example.com');
INSERT INTO employee VALUES(3,'山田次郎','2022-04-01','人事部','yamada.jiro@example.com');
INSERT INTO employee VALUES(4,'鈴木一郎','2022-04-01','開発部','suzuki.ichirou@example.com');
```
# MyBatis-Springを使ったDBアクセスの手順
MyBatis-springを使ってDBアクセスを行うアプリケーションの開発を行う。作成の手順は以下の通り行う。
- エンティティクラス
  - DBからの検索結果を保持するクラス
- Mapperインタフェース
  - DBアクセスを行うインタフェース
  - メソッドを定義し、アノテーションの付与・XML内にSQL文を記述する
  - インタフェースを実装したクラスとインスタンスは、MyBatis-Springによって生成され、インスタンスはDIコンテナでBeanとして管理される

## エンティティクラスの作成手順
- エンティティクラス：DBからの検索結果を保持する。クラス＝テーブル・フィールド＝カラムに対応する
	- クラス名は任意に設定可能
	- 引数なしコンストラクタを必ず持つ
	- フィールド名はSELECTした列名と同名にする
		- ※「DB側:joined_data」の場合、「Java側:joinedData」とする
		- 「mybatis.configuration.map-underscore-to-camel-case=true」とすることで以上の変換が可能
	- getter・setterを持つ

---

- Employee.java：「src/main/java/com/example/entity/employee.java」

```java
package com.example.springmybatisspring.entity;

import java.time.LocalDate;

public class Employee {
	
	/**
	 * フィールド
	 * DBのカラムに対応する
	 */
	private Integer id;
	
	private String name;
	
	private LocalDate joinedData;
	
	private String departmentName;
	
	private String email;
	
	
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
	public LocalDate getJoinedData() {
		return joinedData;
	}
	public void setJoinedData(LocalDate joinedData) {
		this.joinedData = joinedData;
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

}
```

|DBカラム名|Employeeクラスのフィールド名|
|---|---|
|INTEGER id|Integer id|
|VARCHAR(10) name|String name|
|DATE joined_data|LocalDate joinedData|
|VARCHAR(20) department_name|String departmentName|
|VARCHAR(256) email|String email|
  
DBカラムの型とJavaの型の対応関係は[ここ](https://mybatis.org/mybatis-3/ja/configuration.html#typeHandlers)を参照

## Mapper
- Mapperインタフェース：DBへのアクセスを行う
	- アノテーション・XMLでSQL文を記述する

---

- EmployeeMapper.java：「src/main/java/com/example/mapper/EmployeeMapper.java」
	1. 社員を全件検索
		- @Select("SELECT * FROM employee")
	2. 社員を主キー検索
		- @Select("SELECT * FROM employee WHERE id = #{id}")
	3. 社員の追加：
		- @Insert("INSERT INTO employee(name, joined_data, department_name, email)" + " VALUES(#{name}, #{joinedDate}, #{departmentName}, #{email})")
		- @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	4. 主キーで指定した社員情報の更新
		- @Update("UPDATE employee SET name = #{name}, joined_date = #{joindDate}," + " department_name = #{departmentDate}, email = #{email} WHERE id = #{id}")
	5. 主キーで指定した社員の削除
		- @Delete("DELETE FROM employee WHERE id = #{id}")
  
```java
package com.example.springmybatisspring.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;
import com.example.springmybatisspring.entity.Employee;

@Mapper
public interface EmployeeMapper {
	
	// 1. 全検索
	@Select("SELECT * FROM employee")
	List<Employee> findALL();
	
	// 2. 主キー検索
	@Select({"SELECT * FROM employee",
		 "WHERE id = #{id}"})
	Employee findById(Integer id);
	
	// 3. 社員追加
	@Insert({"INSERT INTO employee(name, joined_date, department_name, email)",
		 "VALUES(#{name}, #{joinedDate}, #{departmentName}, #{email})"})
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	void insert(Employee employee);
	
	// 4. 主キーで指定した社員情報の更新
	@Update({"UPDATE employee", 
		 "SET name = #{name}, joined_date = #{joinedDate}, department_name = #{departmentName}, email = #{email}", 
		 "WHERE id = #{id}"})

	int update(Employee employee);
	
	// 5. 主キーで指定した社員の削除
	@Delete({"DELETE FROM employee WHERE id = #{id}"})
	int delete(Integer id);

}
```

|アノテーション|説明|
|---|---|
|@Mapper|Mapperインタフェースを示すアノテーション|
|@Select|SELECT文を記述するアノテーション <br> ※戻り値は全検索の様に複数データある場合は「List<Employee>」にし、0件または1件の場合は「Employee」とする|
|@Insert|INSERT文を記述するアノテーション|
|@Options|検索や追加の際のオプションを指定するアノテーション <br> 社員追加時にDBで採番されたIDをemployeeのidフィールドに代入するように指定している <br> 「useGeneratedKeys」：採番された主キー値を代入する際は「true」にする <br> 「keyColumn」：テーブル側で主キーを表すカラム名 <br> エンティティクラス側で主キー表すフィールド名|
|@Update|UPDATE文を記述するアノテーション　<br> ※戻り値に整数型を指定すると更新したレコード数が返却される|
|@Delete|DELETE文を記述するアノテーション|

### バインド変数
メソッドに引数がある場合、その引数をバインド変数：「#{引数名}」として埋め込められる。  

- 「findById(Integer id)」の様にIntegerやStringが引数の場合は、バインド変数に「#{id}」を指定している
```java
@Select("SELECT * FROM employee WHERE id = #{id}")
Employee findById(Integer id);
```

---

- 「update(Employee employee)」の様にクラスが引数の場合は、バインド変数にフィールド変数名「#id」「#{name}」「#{joindDate}」「#{departmentDate}」「#{email}」を指定している
```java
@Update("UPDATE employee SET name = #{name}, joined_date = #{joindDate}," + " department_name = #{departmentDate}, email = #{email} WHERE id = #{id}")
int update(Employee employee);
```



## 実行
実際に実行して動作確認を行う。  
「SpringMybatisSpringApplication.java」を選択し右クリック、「実行」から「Javaアプリケーション」として実行する。

- SpringMybatisSpringApplication.java：「src/main/java/com.example.springmybatisspring」

```java
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
```

### 実行結果
実行結果は以下のようになる。
```
===================全検索===================
2023-01-04T16:07:39.335+09:00  INFO 32024 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2023-01-04T16:07:39.580+09:00  INFO 32024 --- [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@10db6131
2023-01-04T16:07:39.581+09:00  INFO 32024 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
全検索結果 => id：1 ,name：吉田佑希 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：tanaka.taro@example.com
全検索結果 => id：2 ,name：佐藤花子 ,joinedDate：2022-04-01 ,departmentName：営業部 ,email：sato.hanako@example.com
全検索結果 => id：3 ,name：山田次郎 ,joinedDate：2022-04-01 ,departmentName：人事部 ,email：yamada.jiro@example.com
全検索結果 => id：4 ,name：鈴木一郎 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：suzuki.ichirou@example.com
===================主キー検索===================
主キー検索(key=1) => id：1 ,name：吉田佑希 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：tanaka.taro@example.com
===================社員追加===================
社員追加 => id：12 ,name：森川悠人 ,joinedDate：2022-04-01 ,departmentName：お遊び部 ,email：m.y@example.com
===================社員更新===================
更新された要員 => id：1 ,name：吉田佑希 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：tanaka.taro@example.com
===================社員削除===================
削除する要員 => id：12 ,name：森川悠人 ,joinedDate：2022-04-01 ,departmentName：お遊び部 ,email：m.y@example.com
2023-01-04T16:07:39.634+09:00  INFO 32024 --- [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2023-01-04T16:07:39.637+09:00  INFO 32024 --- [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
```

### 実行結果に詳細ログを出力する
application.propertiesに  
```logging.level.com.example.springmybatisspring.mapper.EmployeeMapper=debug```
を記述する。  
  
そうして実行した結果は以下のように実行したSQL文が可視化される。  
また、「Preparing: SELECT * FROM employee WHERE id = ?」の次の行には「==> Parameters: 1(Integer)」のように「？」の部分に対応するパラメータが表示される。
```java
===================全検索===================
2023-01-04T16:38:39.868+09:00  INFO 32503 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2023-01-04T16:38:40.120+09:00  INFO 32503 --- [           main] com.zaxxer.hikari.pool.HikariPool        : HikariPool-1 - Added connection com.mysql.cj.jdbc.ConnectionImpl@43a09ce2
2023-01-04T16:38:40.122+09:00  INFO 32503 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2023-01-04T16:38:40.128+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findALL      : ==>  Preparing: SELECT * FROM employee
2023-01-04T16:38:40.145+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findALL      : ==> Parameters: 
2023-01-04T16:38:40.166+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findALL      : <==      Total: 4
全検索結果 => id：1 ,name：吉田佑希 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：tanaka.taro@example.com
全検索結果 => id：2 ,name：佐藤花子 ,joinedDate：2022-04-01 ,departmentName：営業部 ,email：sato.hanako@example.com
全検索結果 => id：3 ,name：山田次郎 ,joinedDate：2022-04-01 ,departmentName：人事部 ,email：yamada.jiro@example.com
全検索結果 => id：4 ,name：鈴木一郎 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：suzuki.ichirou@example.com
===================主キー検索===================
2023-01-04T16:38:40.170+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==>  Preparing: SELECT * FROM employee WHERE id = ?
2023-01-04T16:38:40.170+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==> Parameters: 1(Integer)
2023-01-04T16:38:40.172+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : <==      Total: 1
主キー検索(key=1) => id：1 ,name：吉田佑希 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：tanaka.taro@example.com
===================社員追加===================
2023-01-04T16:38:40.172+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.insert       : ==>  Preparing: INSERT INTO employee(name, joined_date, department_name, email) VALUES(?, ?, ?, ?)
2023-01-04T16:38:40.173+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.insert       : ==> Parameters: 森川悠人(String), 2022-04-01(LocalDate), お遊び部(String), m.y@example.com(String)
2023-01-04T16:38:40.180+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.insert       : <==    Updates: 1
社員追加 => id：14 ,name：森川悠人 ,joinedDate：2022-04-01 ,departmentName：お遊び部 ,email：m.y@example.com
===================社員更新===================
2023-01-04T16:38:40.181+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==>  Preparing: SELECT * FROM employee WHERE id = ?
2023-01-04T16:38:40.182+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==> Parameters: 1(Integer)
2023-01-04T16:38:40.183+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : <==      Total: 1
2023-01-04T16:38:40.183+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.update       : ==>  Preparing: UPDATE employee SET name = ?, joined_date = ?, department_name = ?, email = ? WHERE id = ?
2023-01-04T16:38:40.184+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.update       : ==> Parameters: 吉田佑希(String), 2022-04-01(LocalDate), 開発部(String), tanaka.taro@example.com(String), 1(Integer)
2023-01-04T16:38:40.185+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.update       : <==    Updates: 1
更新された要員 => id：1 ,name：吉田佑希 ,joinedDate：2022-04-01 ,departmentName：開発部 ,email：tanaka.taro@example.com
===================社員削除===================
2023-01-04T16:38:40.185+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==>  Preparing: SELECT * FROM employee WHERE id = ?
2023-01-04T16:38:40.185+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : ==> Parameters: 14(Integer)
2023-01-04T16:38:40.186+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.findById     : <==      Total: 1
削除する要員 => id：14 ,name：森川悠人 ,joinedDate：2022-04-01 ,departmentName：お遊び部 ,email：m.y@example.com
2023-01-04T16:38:40.187+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.delete       : ==>  Preparing: DELETE FROM employee WHERE id = ?
2023-01-04T16:38:40.187+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.delete       : ==> Parameters: 14(Integer)
2023-01-04T16:38:40.191+09:00 DEBUG 32503 --- [           main] c.e.s.mapper.EmployeeMapper.delete       : <==    Updates: 1
2023-01-04T16:38:40.194+09:00  INFO 32503 --- [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2023-01-04T16:38:40.196+09:00  INFO 32503 --- [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
```
