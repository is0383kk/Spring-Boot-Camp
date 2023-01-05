# Spring Boot勉強用リポジトリ

# Spring Framework
Spring FrameworkはJavaで実装されたオープンソースのフレームワーク。
Dependency Injection（DIコンテナ）が機能の中心。  
一方でSpring Frameworkには問題点がある。
- 依存ライブラリが多数
- 記述すべきJava Configが大量
- Bean（Javaのインスタンス）の定義が多数  

Spring Frameworkの問題点を解決するためにSpring Bootが存在する。
Spring BootはSpringStarterライブラリやAutoConfigrationクラスを提供しているのみで純粋なSpringである。    
---

Spring Bootには大きく2つの利点が存在する。
- Starterライブラリ
	- Spring Frameworkの大量にある依存ライブラリをコンパクトにしたライブラリ
	- 開発者が指定するライブラリが少なくて済む
- Auto Configrationクラス
	- 記述済みのJava Configクラスを大量に提供している
	- Java Configファイルを記述する量が減る

---

目次
- [DIコンテナ](https://github.com/is0383kk/Spring-Boot-Camp/blob/main/Chap1_DI/README.md)
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
- 付録
	- 整備中
