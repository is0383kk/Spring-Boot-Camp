# Spring Boot勉強用リポジトリ


# Spring Framework
Spring FrameworkはJavaで実装されたオープンソースのフレームワーク。***Dependency Injection（DIコンテナ）***が機能の中心。  
一方でSpring Frameworkには問題点がある。
- 依存ライブラリが多数
- Bean（Javaのインスタンス）の定義が多数
Spring Frameworkの問題点を解決するためにSpring Bootが存在する。

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
