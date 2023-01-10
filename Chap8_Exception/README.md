# 例外処理
本章では、Spring bootにおける例外処理について学ぶ。  
今回は、以下の特定の文字列が入力されるとエラー画面に遷移するアプリケーションを作成する。  

- 「ex」：Exceptionを発生させ例外処理を行う
- 「run」：RuntimeExceptionを発生させ例外処理を行う
- 「io」：IOExceptionを発生させ例外処理を行う
- 「null」：NullPointerExceptionを発生させ例外処理を行う

# 例外ハンドラーの作成
例外ハンドラーでは、発生した例外を処理してエラー画面に遷移させる処理を記述する。  

- GlobalExceptionHandler.java：「src/main/java/ベースパッケージ/handler/GlobalExceptionHandler.java」
  - 処理対象の例外
    - java.lang.Exception
    - java.lang.RuntimeException
 
 |アノテーション|説明|
 |---|---|
 |@ControllerAdvice|「@Component」を持つ <br> 「@ExceptionHandler」を付与した例外処理メソッドを定義することができる|
 |@ExceptionHandler|付与したメソッドが例外処理を行うことを示すアノテーション <br> 例外処理の種類はメソッドの引数の型で定義される|
 |@ResponseStatus|HTTPレスポンスのステータスコードを指定するアノテーション <br> HttpStatus列挙型でステータスコードを指定する|


```java
package com.example.springexception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handException(Exception e, Model model) {
		String message = e.getMessage();
		model.addAttribute("message", message);
		return "exception";
	}
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleRuntimeException(RuntimeException e, Model model) {
		String message = e.getMessage();
		model.addAttribute("message", message);
		return "runtimeException";
	}

}
```

# エラー画面の作成
例外ハンドラーからModelを介して受け取ったメッセージを表示する

- exception.html：「src/main/resources/templates/exception/exception.html」
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>Exception画面</title>
</head>
<body>
    <h1>Exception画面</h1>
    <p th:text="${message}"></p>
    <a href="index.html" th:href="@{/}">入力画面へ</a>
</body>
</html>
```

---

- runtimeException.html：「src/main/resources/templates/exception/runtimeException.html」

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>RuntimeException画面</title>
</head>
<body>
    <h1>RuntimeException画面</h1>
    <p th:text="${message}"></p>
    <a href="index.html" th:href="@{/}">入力画面へ</a>
</body>
</html>
```
# コントローラクラスの作成
- HelloController.java：「src/main/java/ベースパッケージ/controller/HelloController.java」

```java
package com.example.springexception.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;

@Controller
public class HelloController {
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("result")
	public String result(@RequestParam String userName, Model model) throws Exception {
		throwException(userName);
		model.addAttribute("userName", userName);
		return "result";
	
	}

	private void throwException(String value) throws Exception {
		if ("ex".equals(value)) {
			throw new Exception("Exception");
		} else if ("run".equals(value)) {
			throw new RuntimeException("RuntimeException");
		} else if ("io".equals(value)) {
			throw new IOException("IOException");
		} else if ("null".equals(value)) {
			throw new NullPointerException("NullPointerException");
		}
		
	}

}
```

# 実行
Spring bootアプリケーションとして実行し、以下の文字列が入力されるとエラー画面に遷移すればOK  
- 「ex」：Exceptionを発生させ例外処理を行う
- 「run」：RuntimeExceptionを発生させ例外処理を行う
- 「io」：IOExceptionを発生させ例外処理を行う
- 「null」：NullPointerExceptionを発生させ例外処理を行う
