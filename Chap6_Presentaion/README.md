# プレゼンテーション層
- Javaで開発するアプリケーションは3つの層に分けられる  
  - 永続化層：DBアクセスを担当する。DBアクセスの結果をビジネスロジック層に返す。
  - ビジネスロジック層：永続化層の結果を元に処理を行う。トランザクション管理などを行う。
  - **プレゼンテーション層：画面などのUIを提供する。ビジネスロジック層の呼び出しを行う。**  

本章では、プレゼンテーション層の構築について学ぶ。  
そして、永続化層・ビジネスロジック層・プレゼンテーション層を組み合わせたアプリケーションの作成方法を学ぶ。  

---

- 事前準備
  - プロジェクトの作成
    - Gradle-Groovy
    - 依存ライブラリ
      - Thymeleaf・Spring Web・MySQL Driver・MyBatis Framework
    - application.properties
      ```
      # データベース接続先の定義
      spring.datasource.url=jdbc:mysql://localhost/DB名
      spring.datasource.username=root
      spring.datasource.password=パスワード
      spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
      spring.sql.init.mode=never
      spring.sql.init.encoding=utf-8

      # MyBatis
      mybatis.configuration.map-underscore-to-camel-case=true

      # ログレベル
      logging.level.com.example.springpresentation.mapper=debug

      ```
    
# WEBアプリを作成する
本章では以下の流れでアプリケーションを作成する。
- フォームクラスの作成
- コントローラクラスの作成  
- 画面の作成
  
