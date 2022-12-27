# Spring Boot勉強用リポジトリ


# Spring Framework
Spring FrameworkはJavaで実装されたオープンソースのフレームワーク。***Dependency Injection（DIコンテナ）***が機能の中心。  
一方でSpring Frameworkには問題点がある。
- 依存ライブラリが多数
- Bean（Javaのインスタンス）の定義が多数
Spring Frameworkの問題点を解決するためにSpring Bootが存在する。

- 目次
  1. [DIコンテナ](#chap1)
  2. [Spring MVC×ThymeleafでHelloWorld](#chap2)



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

## 静的コンテンツを返す機能の作成
- 静的コンテンツ：常に同じ結果を返すコンテンツ（CSS・JavaScriptなど）※検索エンジンなどで検索した結果で変化するコンテンツを動的コンテンツ  
- 作成するもの
 - hello.html：静的コンテンツ
 - Application.java（Spring Boot Applicationクラス）
