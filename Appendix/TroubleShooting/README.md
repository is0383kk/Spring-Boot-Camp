# トラブルシューティング

## コントローラクラスの「@GetMapping」などのパスが不適切な場合
- バグ原因の例：コントローラの「@GetMapping」のパスが誤っている
- 事象：ステータスが404でWhitelabel Error Pageが表示される。 (type=Not Found, status=404).
※コンソールにエラー出力は行われない

## 「templates」内のHTMLに問題がある場合 
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
