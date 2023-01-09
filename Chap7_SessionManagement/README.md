# セッション管理

## SpringにおけるModelの振る舞い
Spring FrameworkにおけるBeanは、原則１クラスに１インスタンスがDIコンテナ内で管理されている。  
したがって、複数のブラウザからリクエスト要求があった場合でも１つのコントローラが使い回されることとなる。  
言い換えると、コントローラクラスのフィールドは、アクセスされるブラウザ毎に個別のデータを保持することはできない。  
Modelはクライアントからリクエストがある度に生成され、ブラウザにレスポンスを返した時点で消えてしまう。したがって、Modelに格納したデータは複数のブラウザ間で共有されない。

## セッション管理  
セッション管理とは、クライアント毎に固有のデータを複数のリクエストを跨いで保持、共有することができる機能である。  
- セッション管理の流れ
  - リクエスト時
    - リクエストを受けた際、Session IDをリクエストパラメータ・Cookieから取得できない場合、アプリケーションサーバは、アプリケーション内で一意なSession IDを付けた「HttpSession」オブジェクトを生成する
    - Session IDがリクエストパラメータ・Cookieに入っている場合、そのSession IDに対した「HttpSession」オブジェクトを生成する
    - WEBアプリケーションは、「HttpSession」オブジェクトに属性名と値をセットで格納できる。また、属性名を指定し対応する値を取り出すことができる
  - レスポンス時
    - クライアントにレスポンスを返す際、アプリケーションサーバはSession IDが入ったCookieを返す
    - レスポンスのHTMLデータにURLがあると、Session IDをリクエストパラメータとして追加して返す
    - セッションの有効期限が切れるとそのSession IDは無効化される

### セッション管理時の設定
- セッション管理の有効期限
  - Spring bootではデフォルトでセッションの有効期限が30分に指定されており、「application.properties」で有効期限を変更可能である。  
  - パフォーマンス低下に繋がるためセッション管理の有効期限は長くしすぎないようにする。
  - 以下は20分の例（mで分、何も付けないと秒）
    ```server.servlet.session.timeout=20m```
    セッションの有効期限は長くしすぎないようにする。
- URL Rewriting：レスポンスのHTMLデータにURLがあると、Session IDをリクエストパラメータとして追加して返す機能
  - デフォルトで有効化されている（Sessionハイジャックの危険性がやや高まるので注意）
  - 無効化する場合
    ```server.servlet.session.tracking-modes=COOKIE```

# セッション管理の実装
本章では、ページ遷移する複数のページ間をまたがってデータを共有するアプリケーションを作成する。
今回は、「top.thml」「page1.html」「page2.html」の３つのページ間で「名前」と「時間」を共有するアプリケーションを作成する。

- 実装の流れ
  - セッション管理したいクラスに「@Component」を作りBean定義する
  - セッション管理したいクラス(Bean定義済み)に「@SessionScope」を付与する
  - SessionScopeで管理されたBeanをコントローラクラス上でDIする

## セッションスコープを使ったクラスの作成
複数のページ間で「名前」と「時間」をセッションスコープで管理するためのクラスを作成する。
- Account.java：「src/main/java/ベースパッケージ/model/Account.java」

||説明|
|---|---|
|@Component|Bean定義|
|@SessionScope|セッション管理させたいBeanに対して付与する <br> クライアント毎に個別のインスタンスが形成されるようになる|
|implements Serializable|メモリーが圧迫されるとセッション管理されているオブジェクトおシリアライズしてファイルに退避させる <br> マーカーインタフェースと呼ばれ、オーバーライドすべき抽象メソッドを持たない|
|serialVersionID|ファイルにシリアライズされた内容からオブジェクトを再生させる間に、フィールド・メソッドに変更があれば再生したオブジェクトが正しく機能しない <br> 「serialVersionUID」は元のクラスから変更されているか判断するための値である|

```java
package com.example.springsession.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class Account implements Serializable {
	// Sessionに格納するクラスはSerializableで実装する
	private static final long serialVersionUID = 8760625261281613617L;
	
	// フィールド
	private String name;
	private LocalDateTime dateTime;
	
	// コンストラクタ
	public Account(){
		System.out.println("Accountインスタンスの生成");
	}

	// getter・setter
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	@Override
	public String toString() {
		return "Account [name=" + name + ", dateTime=" + dateTime + "]";
	}
}
```

## コントローラの作成
- RootController：「src/main/java/ベースパッケージ/controller/RootController.java」
  - 以下３つにアクセスされた際に動作するコントローラメソッドを作成する
    - [http://localhost:8080/](http://localhost:8080/)
    - [http://localhost:8080/](http://localhost:8080/login)
    - [http://localhost:8080/](http://localhost:8080/top)

```java
package com.example.springsession.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.springsession.model.Account;

@Controller
public class RootController {
	private Account account;
	
	@Autowired
	public RootController(Account account) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.account = account;
	}
	
	@GetMapping("/")
	public String index() {
		account.setDateTime(LocalDateTime.now());
		return "index";
	}
	
	@GetMapping("/login")
	public String login(@RequestParam String name, Model model) {
		account.setName(name);
		account.setDateTime(LocalDateTime.now());
		model.addAttribute("account", account);
		return "top";
	}
	
	@GetMapping("/top")
	public String top(Model model) {
		model.addAttribute("account", account);
		return "top";
	}

}
```

---

「page1」「page2」が呼び出された際に動作するコントローラクラスを作成する。  

- Page1Controller.java：「src/main/java/ベースパッケージ/controller/Page1Controller.java」

```java
package com.example.springsession.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.springsession.model.Account;

@Controller
public class Page1Controller {
	
	private Account account;
	
	@Autowired
	public Page1Controller(Account account) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.account = account;
	}
	
	@GetMapping("page1")
	public String page1(Model model) {
		model.addAttribute("account", account);
		return "page1";
	}

}
```

---

- Page1Controller.java：「src/main/java/ベースパッケージ/controller/Page1Controller.java」

```java
package com.example.springsession.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.springsession.model.Account;

@Controller
public class Page2Controller {
	
	private Account account;
	
	@Autowired
	public Page2Controller(Account account) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.account = account;
	}
	
	@GetMapping("page2")
	public String page2(Model model) {
		model.addAttribute("account", account);
		return "page2";
	}

}
```

## 画面の実装
名前を入力する画面の実装
- index.html：「src/main/resources/templates/index.html」
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>入力画面</title>
</head>
<body>
    <h1>名前を入力してください</h1>
    <form action="login.html" th:action="@{login}">
        <input type="text" name="name">
        <button>送信</button>
    </form>
</body>
</html>
```

---

名前を入力後に遷移する画面の実装
- top.html：「src/main/resources/templates/top.html」
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>トップ画面</title>
</head>
<body>
    <p>セッションスコープの利用</p>
    <table>
    <tr>
        <td>名前</td>
        <td th:text="${account.name}"></td>
    </tr>
    <tr>
        <td>時刻</td>
        <td th:text="${#temporals.format(account.dateTime, 'MM/dd HH:ss')}"></td>
    </tr>    
    </table>
    <a href="page1.html" th:href="@{page1}">Page1へ</a>
    <a href="page2.html" th:href="@{page2}">Page2へ</a>
</body>
</html>
```

---

画面間で「名前」と「時間」を共有する画面の実装
- page1.html：「src/main/resources/templates/page1.html」
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Page1</title>
</head>
<body>
    <p>セッションスコープの利用</p>
    <table>
    <tr>
        <td>名前</td>
        <td th:text="${account.name}"></td>
    </tr>
    <tr>
        <td>時刻</td>
        <td th:text="${#temporals.format(account.dateTime, 'MM/dd HH:ss')}"></td>
    </tr>    
    </table>
    <a href="top.html" th:href="@{top}">Topへ</a>
    <a href="page2.html" th:href="@{page2}">Page2へ</a>
</body>
</html>
```

---
画面間で「名前」と「時間」を共有する画面の実装
- page2.html：「src/main/resources/templates/page2.html」
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Page2</title>
</head>
<body>
    <p>セッションスコープの利用</p>
    <table>
    <tr>
        <td>名前</td>
        <td th:text="${account.name}"></td>
    </tr>
    <tr>
        <td>時刻</td>
        <td th:text="${#temporals.format(account.dateTime, 'MM/dd HH:ss')}"></td>
    </tr>    
    </table>
    <a href="top.html" th:href="@{top}">Topへ</a>
    <a href="page1.html" th:href="@{page1}">Page1へ</a>
</body>
</html>
```

# 実行
アプリケーションを実行し[http://localhost:8080/](http://localhost:8080/)で名前を入力し画面遷移する。  
その後、「[page1](http://localhost:8080/page1)」と「[page2](http://localhost:8080/page2)」を異なるタブで開く。  
「名前」と「時刻」が共有されていればOK。
