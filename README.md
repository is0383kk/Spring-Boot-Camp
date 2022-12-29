# Spring Boot勉強用リポジトリ


# Spring Framework
Spring FrameworkはJavaで実装されたオープンソースのフレームワーク。
Dependency Injection（DIコンテナ）が機能の中心。  
一方でSpring Frameworkには問題点がある。
- 依存ライブラリが多数
- Bean（Javaのインスタンス）の定義が多数  

Spring Frameworkの問題点を解決するためにSpring Bootが存在する。

---

目次
- [DIコンテナ](https://github.com/is0383kk/Spring-Boot-Camp/blob/main/Chap1_DI/README.md)
- [Spring MVC×ThymeleafでHelloWorld](#chap2)
	- [静的リソースWEBアプリ作成手順](#chap2-1)
	- [動的リソースWEBアプリ作成手順](#chap2-2)
	- [リクエストパラメータを受け取る動的リソースWEBアプリ作成手順](#chap2-3)
	- [リダイレクト機能の作成手順](#chap2-4)
- [Bean Varidation](#chap3)
	- [Bean Varidationを使った入力値チェックを実装する](#chap3-1)
	- [@RequestParamと@Varidatedの違いについて](#chap3-4-1)
	- [相関バリデーションチェックを実装する](#chap3-5)
---

<a id="chap1"></a>

---

<a id="chap2"></a>

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


<a id="chap3-2"></a>
## エラーメッセージの定義
入力値チェック時のエラーメッセージを定義する。  

- エラーメッセージ定義手順
	- 「src/main/resources」に「messages.properties」というファイルを作成する。
		- ファイル記述のルール
			- アノテーション名.フォーム名.プロパティ名（NotBlank.helloForm.userName）
			- アノテーション名.プロパティ名（NotBlank.userName）
			- 「{0}」には同じファイル上で定義しているプロパティ名（helloForm.userName=名前）
			- 「{1}」以降はアノテーションの属性値（アルファベット順）※@Rangeだと「max」「min」の順番で入るので「{1}」に「max」が入る
		- messages.properties
		
			```
			NotBlank.helloForm.userName={0}は必須入力です
			NotNull.helloForm.age={0}は必須入力です
			Length.helloForm.userName={0}は{2}文字以上{1}文字以下で入力してください
			Range.helloForm.age={0}は{2}以上{1}以下で入力してください
			typeMismatch.java.lang.Integer={0}は整数で入力してください

			helloForm.userName=名前
			helloForm.age=年齢
			```
		
	- 「src/main/resources」の「application.properties」に「messages.properties」のパスを通す
		- ```spring.messages.basename=messages```



<a id="chap3-3"></a>
## ビューの作成

- index.html
	- 入力値チェック時にエラーがあった場合に、テキストボックスに値を残す
	- 入力値チェック時にエラーがあった場合に、エラーメッセージを表示する

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>入力画面</title>
<link rel="stylesheet" href="../../static/css/style.css"
	th:href="@{/css/style.css}">
</head>

<body>
	<h1>名前と年齢を入力してください</h1>
	<form action="result.html" th:action="@{result}"
		th:object="${helloForm}">
		
		名前：<input type="text" name="userName" placeholder="名前を1〜20文字以内で入力してください" th:field="*{userName}"><br>
		<div th:errors="*{userName}" class="error">ダミー</div>
		
		年齢：<input type="text" name="age" placeholder="年齢を1〜100までの数値で入力してください" th:field="*{age}"><br>
		<div th:errors="*{age}" class="error">ダミー</div>
		
		<button>送信</button>
	
	</form>
	<a href="../index.html" th:href="@{/}">トップ画面へ</a>
</body>

</html>
```

|属性|説明|
|---|---|
|th:object="&{helloForm}"|この属性で囲まれた部分ではオブジェクト名を省略いて記述することができる。<br> \*{プロパティ名}（アウタリスク構文）|
|th:field|入力値チェック時にエラーがあった際にテキストボックスの値を残しておく|
|th:errors|入力値チェック時にエラーがあった際にエラー文を表示する|


<a id="chap3-4"></a>
## コントローラの作成
入力値チェックを行うコントローラメソッドを作成する。  

- HelloController.java
	- メソッドの引数に「@Validated」が付与されていると入力値チェックを行うことができる。
	- 入力値チェックの結果を「BindingResult」に渡す。

```java
package com.example.springmvcpractice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.springmvcpractice.form.HelloForm;

@Controller
@RequestMapping("/hello")
public class HelloController {

	@GetMapping("/index")
	public String index(Model model) {
		
		model.addAttribute("helloForm", new HelloForm());
		
		return "hello/index";
	}

	@GetMapping("/result")
	public String result(@Validated HelloForm helloForm,
			BindingResult bindingResult, Model model) {
		
		if (bindingResult.hasErrors()) {
			System.out.println("入力値チェック");
			return "hello/index";
		}
		
		model.addAttribute("userName", helloForm.getUserName());
		model.addAttribute("age", helloForm.getAge());
		
		return "hello/result";
	}

}
```

||説明|
|@Validated|フォームクラスの引数に付与することでコントローラメソッド実行前に入力値チェックを行う。 <br> @Validated HelloForm helloForm|
|BindingResult|入力値チェックの結果を保持するインタフェース。<br> エラーがあった場合、「bindingResult.hasErrors()」が「true」を返す。|
※「BindingResult」の引数はフォームクラスの引数の直後にする。（@Validated AForm aForm,BindingResult bindingResult）


<a id="chap3-4-1"></a>
### 「@RequestParam」を使う場合と「@Validated」を使う場合の違い
リクエストパラメータ：「?userName="吉田"&age=30」であると考える。
- @RequestParam
	- リクエストパラメータ名と@RequestParamの引数を合わせるとリクエストパラメータが格納される
	- String userName = "吉田"
	- Integer age = 30
	```java
	public String result(@RequestParam String userName, 
				@RequestParam Integer age, ...)
	```
- @Validated
	- フォームクラスに同名のフィールドとsetter・getterがあるとリクエストパラメータが格納される
	- HelloForm型 helloForm.userName = "吉田"
	- HelloForm型 helloForm.age = 30
	```java
	public String result(@Validated HelloForm helloForm,
			     BindingResult bindingResult, ...) 
	```
	
	```java
	public class HelloForm {
	
	// フィールド
	@NotBlank
	@Length(min = 1, max = 20)
	private String userName;
	
	@NotNull
	@Range(min = 1, max = 100)
	private Integer age;
	```


<a id="chap3-5"></a>
## 結果画面の作成
入力された名前と年齢を表示する画面を作成する。
コントローラクラスのModelの属性値に設定された値がビューに送られる。
```java
model.addAttribute("userName", helloForm.getUserName());
model.addAttribute("age", helloForm.getAge());
```

--- 

- result.index
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>結果を出力</title>
</head>
<body>
    <p>ようこそ <span th:text="${userName}">ダミー</span>さん</p>
    <p>年齢：<span th:text="${age}">20</span></p>
    <a href="index.html" th:href="@{index}">入力画面に戻る</a>
</body>
</html>

```

「Spring Bootアプリケーション」として実行し、入力値チェックが行われ結果画面に遷移すればOK。


<a id="chap3-6"></a>
## 相関バリデーションチェックを実装する
相関バリデーションとは「メールアドレスを２回入力して比較する入力値チェック」「２つの項目の大小を比較する入力値チェック」など複数の項目にまたがった入力値チェックのことをいう。  
今回は、メールアドレスを2回入力させる相関バリデーションチェックを実装する。









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
