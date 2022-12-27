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
@ComponentScan(basePackages = {"com.example.sample.data.mybatis", "com.example.sample.view.thymeleaf"})
public class AppConfig {
}
```

| アノテーション|説明|
|@Configuration|Java Configであることを示す|
|@ComponentScan|Java Configに付与|
