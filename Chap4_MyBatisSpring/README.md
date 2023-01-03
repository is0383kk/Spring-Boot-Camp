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
