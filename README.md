# Spring Boot勉強用リポジトリ

# Spring Framework
Spring FrameworkはJavaで実装されたオープンソースのフレームワーク。
Dependency Injection（DI）が機能の中心。  
一方でSpring Frameworkには問題点がある。
- 依存ライブラリが多数
- 記述すべきJava Configが大量
- Bean（Javaのインスタンス）の定義が多数  

こうしたSpring Frameworkの問題点を解決するためにSpring Bootが存在する。  
  
Spring Frameworkの問題点に対してSpring Bootには大きく2つの利点が存在する。
- Starterライブラリ
	- Spring Frameworkの大量にある依存ライブラリをコンパクトにしたライブラリ
	- 開発者が指定するライブラリが少なくて済む
- Auto Configrationクラス
	- 記述済みのJava Configクラスを大量に提供している
	- Java Configファイルを記述する量が減る


Spring BootはSpringStarterライブラリやAutoConfigrationクラスを提供しているのみで純粋なSpring Frameworkである

---

目次
- [DIコンテナ](https://github.com/is0383kk/Spring-Boot-Camp/blob/main/Chap1_DI/README.md)
	- Bean定義方法①：コンポーネントスキャンによるBeanの定義
	- Bean定義方法②：Java ConfigによるBean定義
- [Spring MVC×ThymeleafでHelloWorld](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Chap2_SpringMVC)
	- 静的リソースWEBアプリ作成手順
	- 動的リソースWEBアプリ作成手順
	- リクエストパラメータを受け取る動的リソースWEBアプリ作成手順
	- リダイレクト機能の作成手順
- [Bean Varidation](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Chap3_BeanVaridation)
	- Bean Varidationを使った入力値チェックを実装する
	- @RequestParamと@Varidatedの違いについて
	- 相関バリデーションチェックを実装する
- [永続化層：My Batis Spring](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Chap4_MyBatisSpring)
	- 永続化層
	- My-Batis-Springのための設定
	- MyBatis-Springを使ったDBアクセスの手順
	- エンティティクラスの作成手順
	- Mapper
	- バインド変数
	- 実行結果に詳細ログを出力する
- [ビジネスロジック層](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Chap5_BusinessLogic)
	- Dependency Injection
	- ビジネスロジック層の構築
	- ビジネスロジックインタフェースの作成
	- ビジネスロジッククラスの作成
	- @Transactionalによるトランザクション管理
- [プレゼンテーション層](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Chap6_Presentaion)
	- アプリケーション作成の流れ
	- 永続化層の構築
		- エンティティクラスの作成
		- Mapperインタフェースの作成
	- ビジネスロジック層の構築
		- サービスインタフェースの
		- 作成サービスクラスの作成
	- プレゼンテーション層の構築
		- フォームクラスの作成
		- コントローラクラスの作成
		- 画面の作成
- 付録
	- [Eclipseの導入](https://github.com/is0383kk/Spring-Boot-Camp/tree/main/Appendix/EclipseSetting)
	- 整備中
	- [公式リファレンス](https://spring.pleiades.io/spring-boot/docs/current/reference/html/)
		- [公式入門ガイド](https://spring.pleiades.io/guides#getting-started-guides)
		- [application.propertiesで設定できる項目](https://spring.pleiades.io/spring-boot/docs/current/reference/html/application-properties.html)
  	- [thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf_ja.html)
