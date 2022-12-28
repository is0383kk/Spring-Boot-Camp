# Spring Boot勉強用リポジトリ


# Spring Framework
Spring FrameworkはJavaで実装されたオープンソースのフレームワーク。***Dependency Injection（DIコンテナ）***が機能の中心。  
一方でSpring Frameworkには問題点がある。
- 依存ライブラリが多数
- Bean（Javaのインスタンス）の定義が多数
Spring Frameworkの問題点を解決するためにSpring Bootが存在する。

- 目次
- [DIコンテナ](#chap1)
- [Spring MVC×ThymeleafでHelloWorld](#chap2)
	- [静的リソースWEBアプリ作成手順](#chap2-1)
	- [動的リソースWEBアプリ作成手順](#chap2-2)



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
