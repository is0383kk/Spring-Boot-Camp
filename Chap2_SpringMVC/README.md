# Spring MVC×ThymeleafでHelloWorld
本章ではSpring MVCとTymeleafを用いて簡単なWEBアプリケーションを作成する。  
- [Spring MVC](https://spring.pleiades.io/spring-framework/docs/current/reference/html/web.html)：Spring BootでWEBアプリを作成する際に使用するフレームワーク 
- [Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf_ja.html)：Spring MVCのビューとして使用できる
  - HTML5として記述可能
  - JSPのようにJavaソースがビュー側に入り込まない
  - クロスサイトスクリプティング脆弱性を引き起こしにくい

# Spring Bootプロジェクトを作ってみる
まずは、EclipseでSpring bootのプロジェクトを作成する。  
- Eclipseから「新規」->「Springスタータプロジェクト」
- パッケージングは「jar」
- パッケージネーム・成果物・プロジェクトの名前に合わせる
	- スプリングスタータプロジェクト・依存関係：「Spring Web」と「Thymeleaf」を選択 
	- build.gradleに選択した機能情報が記述

|機能|説明|
|---|---|
|spring-boot-starter-web|Web関連ライブラリをまとめたStarterライブラリ。Spring MVC本体及び組み込みTomcatなどが含まれる。|
|spring-boot-starter-thymeleaf|Thymeleaf関連ライブラリをまとめたStarterライブラリ。Thymeleaf及びSpring MVCとの連携ライブラリが含まれる。|

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

# 静的コンテンツを返す機能の作成
まずは、Spring Bootを使って静的コンテンツを作成する。
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

## 静的コンテンツの作成
- hello.index：「src/main/resource/static/hello.html」
  - 「src/main/resource/static」直下に配置した静的コンテンツはアプリケーションとして実行せずとも「http://localhost:8080/ファイル名」でアクセスできる

※今回の場合は、[http://localhost:8080/hello.html](http://localhost:8080/hello.html)から直接アクセスできる。  

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

## main()メソッドの作成手順
Spring bootで作成されたWEBアプリケーションは「main」メソッドから起動する。  
- Application.java：「src/main/java/com/example/ベースパッケージ」に配置する（※Spring Bootでプロジェクトを作成すると自動で作成される）
  - 「@SpringBootApplication」アノテーションを付与
  - Spring Bootアプリケーションはmain()メソッドから起動する。  
  - main()メソッド内には、DIコンテナを作成する処理のみを記述する。DIコンテナが作成されるとWEBアプリケーションとして動作する。  

|アノテーション|説明|
|@SpringBootApplication|@Configration・@ComponentScan・EnableAutoConfigrationを組み合わせたアノテーション <br>
「@ComponentScan」にbasepakagesを指定しない場合、付与したクラスのパッケージ配下がコンポーネントスキャン対象となる <br>
今回の場合は「src/main/java/com/example/ベースパッケージ」配下がコンポーネントスキャンのスキャン対象となる <br>
Java Configクラスの役割も兼ねるので、@Beanを付与したメソッドをクラス内に宣言可能|

```java
package com.example.springmvchello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringMvcHelloApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringMvcHelloApplication.class, args);
	}

}

```

プロジェクトを右クリックし「実行」から「Spring Bootアプリケーション」として実行し、[http://localhost:8080/hello.index](http://localhost:8080/hello.index)にアクセス。
  
画面が表示されればOK。

# 動的コンテンツを返す機能の作成

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
	
## コントローラクラスの作成手順
[http://localhost:8080/hello/index](http://localhost:8080/hello/index)にアクセスされた際に動作するコントローラクラスを作成する。

- HelloController.java：「src/main/java/com/example/web/controller/HelloController.java」
	- コントローラクラスに「@Controller」が付与される
	- 「@RequestMapping・@GetMapping」を付与したコントローラメソッドを作成
	- コントローラメソッド
		- 「GetMapping」でURLを指定する。
		- 戻り値は「String」を指定するのが一般的である。**ビューの論理名（拡張子を除いたもの）**を指定。
			```java
			@GetMapping("/index")
			public String index() {
				return "hello/index";
			}
			```

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

## Viewテンプレートの作成手順
画面はHTML5で記述できるTymeleafで作成する。  

- index.html：「src/main/resource/templates/hello/index.html」
	- サーバで実行された際に格納される値を「th:」で始まる属性を使って記述
	- 動的リンクを埋め込む場合は「@{リンク}」のようにリンクURL記法を用いる

```html
<!DOCTYPE html>
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



# リクエストパラメータを受け取る動的コンテンツの作成
リクエストパラメータを受け取り、その内容を反映させた動的コンテンツを作成する。
  
- 作成するもの
	- HelloControllerにメソッドを追加
	- /hello/result.html


## コントローラクラスにメソッドを追加


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

## 結果を出力するビューの作成

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
# リダイレクト機能の作成
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
