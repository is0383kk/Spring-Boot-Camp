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
	@Select("SELECT * FROM employee WHERE id = #{id}")
	Employee findById(Integer id);
	
	// 3. 社員追加
	@Insert("INSERT INTO employee(name, joined_data, department_name, email)" + 
			 " VALUES(#{name}, #{joinedDate}, #{departmentName}, #{email})")
	@Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
	void insert(Employee employee);
	
	// 4. 主キーで指定した社員情報の更新
	@Update("UPDATE employee SET name = #{name}, joined_date = #{joindDate}," + 
			 " department_name = #{departmentDate}, email = #{email} WHERE id = #{id}")
	int update(Employee employee);
	
	// 5. 主キーで指定した社員の削除
	@Delete("DELETE FROM employee WHERE id = #{id}")
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

