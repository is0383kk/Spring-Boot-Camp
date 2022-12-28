# Spring Boot勉強用リポジトリ


# Spring Framework
Spring FrameworkはJavaで実装されたオープンソースのフレームワーク。***Dependency Injection（DIコンテナ）***が機能の中心。  
一方でSpring Frameworkには問題点がある。
- 依存ライブラリが多数
- Bean（Javaのインスタンス）の定義が多数  

Spring Frameworkの問題点を解決するためにSpring Bootが存在する。

---

目次
- [DIコンテナ](#chap1)
- [Spring MVC×ThymeleafでHelloWorld](#chap2)
	- [静的リソースWEBアプリ作成手順](#chap2-1)
	- [動的リソースWEBアプリ作成手順](#chap2-2)
	- [リクエストパラメータを受け取る動的リソースWEBアプリ作成手順](#chap2-3)
	- [リダイレクト機能の作成手順](#chap2-4)

---

<a id="chap1"></a>
# DIコンテナ
## DIコンテナとBean
- DIコンテナ：Spring Frameworkの動作に必要なインスタンスを保持
- Bean：DIコンテナで管理されているJavaインスタンス

## アノテーションによるBeanの定義
クラスにアノテーションを付けることでBeanを定義する。

### 設定クラスの作成
DIコンテナ・Beanに必要な設定クラスを作成する。この設定クラスをJava Configという。

- AppConfig.java
```java
package com.example.sample.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.example.sample.A", "com.example.sample.B"})
public class AppConfig {
}
```

|アノテーション|説明|
|---|---|
|@Configuration|Java Configであることを示す|
|@ComponentScan|Java Configに付与。basePackages属性のパッケージ配下から@Componentが付与されたクラスを探してインスタンス化しDIコンテナに保持|

※@Component が付与されていても、コンポーネントスキャンの対象ではない場合、Beanにはならない。

### DIコンテナの作成  
- DIコンテナの作成：SpringApplication.run()メソッドで作成
  - Java Configクラスの読み込み  
  - コンポーネントスキャン
  - BeanインスタンスをDIコンテナ内に保存

引数はJava Configクラス  
**戻り値であるApplicationContextがDIコンテナ**となる  

```java
// DIコンテナの作成
ApplicationContext context = SpringApplication.run(AppConfig.class, args);
```
これを実行後、AppConfig.classの@ComponentScan内にある「A」と「B」インスタンスがDIコンテナ内にある状態となる。

---

- Beanの取り出し：ApplicationContextのgetBean()メソッドを利用  
```java
DataLogic logicA = context.getBean(ALogic.class);
logicA.method();
```
---

<a id="chap2"></a>
# Spring MVC×ThymeleafでHelloWorld

- [Spring MVC](https://spring.pleiades.io/spring-framework/docs/current/reference/html/web.html)：Spring BootでWEBアプリを作成する際に使用するフレームワーク 
- [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf_ja.html)：Spring MVCのビューとして使用できる
  - HTML5として記述可能
  - JSPのようにJavaソースがビュー側に入り込まない
  - クロスサイトスクリプティング脆弱性を引き起こしにくい

## Spring Bootプロジェクトを作ってみる
- EclipseからSpringスタータプロジェクトを作成する
  - Eclipseから「新規」->「Springスタータプロジェクト」
  - パッケージングは「jar」
  - パッケージネーム・成果物・プロジェクトの名前に合わせる
    - スプリングスタータプロジェクト・依存関係：「Spring Web」と「Thymeleaf」を選択 
    - build.gradleに選択した機能情報が記述

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
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
	useJUnitPlatform()
}

```


続いて、静的コンテンツ・mainメソッドの作成を行う

<a id="chap2-1"></a>
## 静的コンテンツを返す機能の作成
- 静的コンテンツ：常に同じ結果を返すコンテンツ（CSS・JavaScriptなど）※検索エンジンなどで検索した結果で変化するコンテンツを動的コンテンツ  
- 作成するもの
  - hello.html：静的コンテンツ
  - Application.java（Spring Boot Applicationクラス）
- 静的リソースWEBアプリケーションの動作の流れ
  - HTTPリクエストを受け取る
  - URLに対応した静的リソースがないか確認
  - 対応したリソースがあればHTTPレスポンスに格納
  - 作成したHTTPレスポンスをクライアントに返す
  
- 作成するもの
	- hello.html
	- Application.java

### 静的コンテンツの作成手順
- hello.index
  - 配置場所：「src/main/resource/static」に配置する
  - staic直下に配置したHTMLファイルは[http://localhost:8080/hello.index](http://localhost:8080/hello.index)でアクセスできる

---

```html
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Spring bootで静的コンテンツを表示</title>
</head>
<body>
	<p>Hello World</p>
	<a href="hello/index">入力ページへ行く </a>
</body>
</html>
```

### main()メソッドの作成手順
- Application.java
  - 配置場所：「com.example.プロジェクト名」に配置する
  - @SpringBootApplicationを付与
  - Spring Bootアプリケーションはmain()メソッドから起動する。  
  - main()メソッド内には、DIコンテナを作成する処理のみを記述する。DIコンテナが作成されるとWEBアプリケーションとして動作する。  
  
- @SpringBootApplication：@Configration・@ComponentScan・EnableAutoConfigrationを組み合わせたアノテーション
  - **@ComponentScanにbasepakagesを指定しない場合、付与したクラスのパッケージ配下がコンポーネントスキャン対象となる**
    - 今回の場合は「com.example.プロジェクト名」配下がスキャン対象となる
  - Java Configクラスの役割も兼ねるので、@Beanを付与したメソッドをクラス内に宣言可能

---

```java
package com.example.springmvcpractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringMvcPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMvcPracticeApplication.class, args);
	}

}
```

プロジェクトを「Spring Bootアプリケーション」として実行し、[http://localhost:8080/hello.index](http://localhost:8080/hello.index)にアクセス。

<a id="chap2-2"></a>
## 動的コンテンツを返す機能の作成

次に動的コンテンツを返す機能を作成する。  
動的コンテンツを返すWEBアプリケーションはプログラムの機能をModel・View・Controllerの３つに分割して作成する。

- Controller
	- リクエスト内容に応じた処理を行い遷移させたいView（ページ）を決める
		- クライアントからのリクエストはDispatherServletが受け取る。
		- DispatherServletが処理をコントローラに割り振る
	- Viewが生成するレスポンスを構築するために必要な動的データがある場合、Modelに格納するオブジェクトを生成する
	- 作成のルール
		- コントローラクラス宣言の前に@Controllerを付与
		- 処理したいURLを設定することでリクエストに対応したコントローラクラスのメソッドが呼び出される
- Model
	- Viewに受け渡したいオブジェクトを格納する入れ物
	- 作成のルール
		- Viewに受け渡したいオブジェクトをModelに格納
- View
	- Modelに格納されたデータを使い、クライアントに格納するHTMLコンテンツを作成する
	- 作成のルール
		- Viewテンプレートレスポンスとして返したいHTMLの雛形をViewテンプレートとして作成（src/main/resorce/templates）
		- Modelに格納されたオブジェクトデータを埋め込む指定を行う

  
- 作成するもの
	- HelloController.java
	- index.html
	
### コントローラクラスの作成手順
[http://localhost:8080/hello/index](http://localhost:8080/hello/index)にアクセスされた際に動作するコントローラクラスを作成する。

- HelloController.java：「src/main/java/com/example/web/controller/HelloController.java」
	- コントローラクラスに@Controllerが付与される
	- @RequestMapping・@GetMappingを付与したコントローラメソッドを作成

```java
package com.example.springmvcpractice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController {
	
	@GetMapping("/index")
	public String index() {
		return "hello/index";
	}
	
}
```

|アノテーション|説明|
|---|---|
|@Controller|コントローラクラスに付与するアノテーション。@Componentが含まれているためコンポーネントスキャンの対象となる。|
|@RequestMapping|URLとクラス・メソッドを対応付ける。「http://サーバ名/hello」と対応している。|
|@GetMapping|HTTPリクエストのGET及びURLとクラス・メソッドを対応付ける。**@RequestMappingと@GetMappingの両方がある場合、URLが結合される。index()メソッドの場合、Requestの「/hello」、Getの「/index」が結合し、「/hello/index」となる。**|

※@RequestMapingはクラス・メソッドに付与できる。@GetMappingはメソッドのみに付与できる

---

- コントローラメソッド
	- 戻り値は**ビューの論理名（拡張子を除いたもの）**を指定。

```java
@GetMapping("/index")
public String index() {
	return "hello/index";
}
```

### Viewテンプレートの作成手順
画面はHTML5で記述できるTymeleafで作成する。  

- index.html：「src/main/resource/templates/hello/index.html」
	- サーバで実行された際に格納される値を「th:」で始まる属性を使って記述
	- 動的リンクを埋め込む場合は「@{リンク}」のようにリンクURL記法を用いる

```html
<<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>名前を入力してください</h1>
	<form action="result.html" th:action="@{result}">
		<input type="text" name="userName">
		<button>送信</button>
	</form>
</body>
</html>
```

「Spring Bootアプリケーション」で実行し[http://localhost:8080/hello.html](http://localhost:8080/hello.html)にアクセスする。  
直接[http://localhost:8080/hello/index](http://localhost:8080/hello/index)にアクセスできることを確認。


<a id="chap2-3"></a>
## リクエストパラメータを受け取る動的コンテンツの作成
リクエストパラメータを受け取り、その内容を反映させた動的コンテンツを作成する。
  
- 作成するもの
	- HelloControllerにメソッドを追加
	- /hello/result.html


### コントローラクラスにメソッドを追加


- HelloController.java
	- result()メソッドを追加
```java
package com.example.springmvcpractice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/hello")
public class HelloController {

	@GetMapping("/index")
	public String index() {
		return "hello/index";
	}

	@GetMapping("/result")
	public String result(@RequestParam String userName, Model model) {
		model.addAttribute("userNameAttribute", userName);
		return "hello/result";
	}

}
```

||説明|
|---|---|
|@RequestParam|リクエストパラメータを引数で受け取るためのアノテーション。引数名はリクエストパラメータに合わせる。|
|Model|コントローラからビューに値を渡すためのインタフェース。addAttribute()メソッドで属性名と値を格納する。第一引数の「"userNameAttribute"」はビューに渡す際の属性名で、ビュー側で「${userNameAttribute}」で受け取る。第2引数は値そのもの。|

### 結果を出力するビューの作成

- result.html
	- コントローラからModelを通して渡された値には「${属性名}」という変数式を用いる
	- タグに「th:text」属性を指定する


```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>結果を出力</title>
</head>
<body>
    <p>ようこそ <span th:text="${userNameAttribute}">ダミーネーム</span>さん</p>
    <a href="index.html" th:href="@{index}">入力画面に戻る</a>
</body>
</html>
```

「Spring Bootアプリケーション」で実行し[http://localhost:8080/hello.html](http://localhost:8080/hello.html)にアクセスする。  
結果出力画面に入力した値が表示されていればOK。



<a id="chap2-4"></a>
## リダイレクト機能の作成
リダイレクト機能を使うことで、ページにアクセスした際自動的に別ページに遷移させられる。  
[http://localhost:8080/](http://localhost:8080/)にアクセスすると、[http://localhost:8080/hello/index.html](http://localhost:8080/hello/index.html)にリダイレクトする機能を作成する。

- RootController.java
	- コンテキストルートにアクセスされた際に「hello/index」にリダイレクトする

```java
package com.example.springmvcpractice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
	
	@GetMapping("/")
	public String root() {
		// "redirect:"を戻り値の先頭につけることでリダイレクトできる
		return "redirect:hello/index";
	}
}
```

---

<a id="chap3"></a>
# Bean Validation
- Bean Validation
	- 入力検証のための仕様・フィールドやメソッドの引数にアノテーションを付与し制約条件を指定できる
	- 入力値チェックなどの複雑なロジックによる検証を行う必要がなくなる

Bean Validationを使用するため、Eclipse上からプロジェクトを右クリックし「スターターの追加」を選択し、「Validation」にチェックを入れて導入する。※他の依存ライブラリにもチェックを入れる。

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
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
	useJUnitPlatform()
}


```

<a id="chap3-1"></a>
## フォームクラスの作成
- フォームクラス：HTMLフォームからの入力を受け取るクラス
	- フィールド・getter・setterの名称は画面のform要素のname属性と一致させる
	- フィールドにBean Validationのアノテーションを付与することで制約

- HelloForm.java：「src/main/java/com/example/web/formHelloForm.java」

```java
package com.example.springmvcpractice.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class HelloForm {
	
	// フィールド
	@NotBlank
	@Length(min = 1, max = 20)
	private String userName;
	
	@NotNull
	@Range(min = 1, max = 100)
	private Integer age;
	
	// Getter・setter
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}
}
```

|アノテーション|説明|
|---|---|
|@NotBlank|null・空文字・半角スペースを許可しない|
|@Length(min = 1, max = 20)|文字列長が１文字以上２０文字以下|
|@NotNull|nullを許可しない|
|@Range(min = 1, max = 100)|数値が１以上１００以下|



# 付録
## トラブルシューティング

### コントローラクラスの「@GetMapping」などのパスが不適切な場合
- バグ原因の例：コントローラの「@GetMapping」のパスが誤っている
- 事象：ステータスが404でWhitelabel Error Pageが表示される。 (type=Not Found, status=404).
※コンソールにエラー出力は行われない

### 「templates」内のHTMLに問題がある場合 
- バグ原因の例：「templates/hello/index.html」にて、コントローラから受け取るModelの属性値が誤っている
- 事象：ステータスが500でWhitelabel Error Pageが表示される。 (type=Internal Server Error, status=500).

例外
```bash
ERROR 40914 org.thymeleaf.TemplateEngine [THYMELEAF][http-nio-8080-exec-1] Exception processing template "hello/index": Exception evaluating SpringEL expression: "userNameA" (template: "hello/index" - line 14, col 8)

org.thymeleaf.exceptions.TemplateProcessingException: Exception evaluating SpringEL expression: "userNameA" (template: "hello/index" - line 14, col 8)
~~~
~~~
Caused by: org.springframework.expression.spel.SpelEvaluationException: EL1008E: Property or field 'userNameA' cannot be found on object of type 'com.example.springmvcpractice.form.HelloForm' - maybe not public or not valid?
```
