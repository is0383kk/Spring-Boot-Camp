# トラブルシューティング

## コントローラクラスの「@GetMapping」などのパスが不適切な場合（status=404）
- バグ原因の例：コントローラの「@GetMapping」のパスが誤っている
- 事象：ステータスが404でWhitelabel Error Pageが表示される。 (type=Not Found, status=404).
※コンソールにエラー出力は行われない

## 「templates」内のHTMLに問題がある場合（status=500） 
- バグ原因の例：「templates/hello/index.html」にて、コントローラから受け取るModelの属性値が誤っている
  - 「@Getmapping」で正しいURLが指定されている（status=500が出る）が、Modelの属性値が誤っているため画面が表示されない
- 事象：ステータスが500でWhitelabel Error Pageが表示される。 (type=Internal Server Error, status=500).

例外
```bash
ERROR 40914 org.thymeleaf.TemplateEngine [THYMELEAF][http-nio-8080-exec-1] Exception processing template "hello/index": Exception evaluating SpringEL expression: "userNameA" (template: "hello/index" - line 14, col 8)

org.thymeleaf.exceptions.TemplateProcessingException: Exception evaluating SpringEL expression: "userNameA" (template: "hello/index" - line 14, col 8)
~~~
~~~
Caused by: org.springframework.expression.spel.SpelEvaluationException: EL1008E: Property or field 'userNameA' cannot be found on object of type 'com.example.springmvcpractice.form.HelloForm' - maybe not public or not valid?
```

## DBへの挿入処理時にSQL文が誤っている場合（status=500）
- バグ原因の例：MapperインタフェースのSQL文が誤っている
- 事象：画面上に「There was an unexpected error (type=Internal Server Error, status=500)」が表示され、テーブルへの挿入処理が行われない
  
例外
```bash
2023-01-07T19:33:16.944+09:00[0;39m [31mERROR[0;39m [35m50161[0;39m [2m---[0;39m [2m[nio-8080-exec-4][0;39m [36mo.a.c.c.C.[.[.[/].[dispatcherServlet]   [0;39m [2m:[0;39m Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed: org.springframework.jdbc.BadSqlGrammarException: 
### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column 'departmentName' in 'field list'
### The error may exist in com/example/springpresentation/mapper/EmployeeMapper.java (best guess)
### The error may involve com.example.springpresentation.mapper.EmployeeMapper.insert-Inline
### The error occurred while setting parameters
### SQL: INSERT INTO employee(name, joined_date, departmentName, email) VALUES(?, ?, ?, ?)
### Cause: java.sql.SQLSyntaxErrorException: Unknown column 'departmentName' in 'field list'
; bad SQL grammar []] with root cause
```

テーブルのカラム名が「department_name」、Entityクラスのフィールドが「departmentName」であり、SQL文で誤って「departmentName」を指定している。  
```bash
### SQL: INSERT INTO employee(name, joined_date, departmentName, email) VALUES(?, ?, ?, ?)
### Cause: java.sql.SQLSyntaxErrorException: Unknown column 'departmentName' in 'field list'
```
