# DIコンテナとBean
Spring Frameworkは**DIコンテナ**というJavaのインスタンスを格納する箱を持っている。  
Spring Frameworkにおいては、アプリケーション実行時にインスタンスを生成してDIコンテナに保持する。
  
  
- DIコンテナ：Spring Frameworkの動作に必要なインスタンスを保持する入れ物
  - あるBeanを別のBeanに代入する（Dependency Injection）
  - トランザクション管理などの割り込み処理を織り込む（Aspected Oriented Programming）
  - Beanインスタンスのライフサイクルを管理する
- Bean：DIコンテナで管理されているインスタンス
  - Bean定義の方法
    1. コンポーネントスキャンによる定義
    2. Java Configクラスによる定義 

本章では、「コンポーネントスキャン」、「Java Config」を使ってBeanの定義を行い、DIコンテナから取り出す一連の流れを学ぶ。  

**結局DIってなんなの？**  
- DIとは「依存性の注入」
	- 依存性：必要なインスタンス
	- 注入：自動的代入
つまり、DIとは **「newしないで代入してもらう」** こと


# Bean定義方法①：コンポーネントスキャンによるBeanの定義
- コンポーネントスキャン：指定されたパッケージから「@Component」が付与されたクラスを探す
  - 「@Component」が付与されたクラスがあればインスタンス化しDIコンテナに格納する

## 設定クラスの作成
DIコンテナ・Beanに必要な設定クラスを作成する。この設定クラスをJava Configという。

- AppConfig.java
```java
package com.example.springdicontainer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.example.springdicontainer.data"})
public class AppConfig {
	
}
```

|アノテーション|説明|
|---|---|
|@Configuration|Java Configであることを示す|
|@ComponentScan|Java Configに付与。basePackages属性のパッケージ配下から@Componentが付与されたクラスを探してインスタンス化しDIコンテナに保持|

※@Component が付与されていても、コンポーネントスキャンの対象ではない場合、Beanにはならない。

## Beanとしたいクラスの作成
Java Configファイル内にてコンポネントスキャンの対象に
```java
@ComponentScan(basePackages = {"com.example.springdicontainer.data"})
```
と指定されているため「com.example.springdicontainer.data」を作成し、パッケージ配下にBeanにしたいクラスを作成する。

- Logic.java：「src/main/java/com/example/springdicontainer/data/Logic.java」
  - 「@Component」アノテーションを付与することでコンポーネントスキャンの対象となる
```java
package com.example.springdicontainer.data;

import org.springframework.stereotype.Component;

@Component
public class Logic {

	public void logicMethod() {
		System.out.println("logicMethodを実行します");
	}
	
}
```

## DIコンテナの作成
DIコンテナは「SpringApplication.run()」メソッドで作成される。  
Java Configクラスを引数に与え、戻り値である「ApplicationContext」がDIコンテナとなる。

DIコンテナの作成は
```java
// DIコンテナの作成
ApplicationContext context = SpringApplication.run(AppConfig.class, args);
```
の一文で実行され、次の処理が行われる。
- Java Configクラスの読み込み 
- コンポーネントスキャン
- Beanインスタンスの生成とBeanインスタンスをDIコンテナ内に保存
  
  
これを実行後、AppConfig.classの「@ComponentScan」内にある「com/example/data/Logic.java」クラスのインスタンスがDIコンテナ内にある状態となる。

## DIコンテナからBeanを取り出してみる
Beanの取り出しには「ApplicationContextのgetBean()」メソッドを使用する。  

- SpringDiContainerApplication：「src/main/java/com/example/springdicontainer」

|アノテーション|説明|
|---|---|
|@SpringBootApplication | @Configuration・@ComponentScan・@EnableAutoConfigurationを組み合わせたアノテーション <br> @ComponentScanでパッケージが指定されていないため、このクラスのパッケージがベースパッケージとなる|

```java
package com.example.springdicontainer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.springdicontainer.config.AppConfig;
import com.example.springdicontainer.data.Logic;

@SpringBootApplication
public class SpringDiContainerApplication {

	public static void main(String[] args) {
		// DIコンテナの作成
		ApplicationContext context = SpringApplication.run(AppConfig.class, args);
		
		// Beanの呼び出し
		Logic logic = context.getBean(Logic.class);
		logic.logicMethod();
	}

}
```

### 実行結果
「SpringDiContainerApplication」を右クリックし、「Java アプリケーション」として実行する。すると以下のように、DIコンテナからBeanの取り出しができていることがわかる。
```
2023-01-05T15:26:52.026+09:00  INFO 49854 --- [           main] c.e.s.SpringDiContainerApplication       : Starting SpringDiContainerApplication using Java 17.0.5 with PID 49854 (/home/is0383kk/workspace/workspace_sp/spring-di-container/bin/main started by is0383kk in /home/is0383kk/workspace/workspace_sp/spring-di-container)
2023-01-05T15:26:52.029+09:00  INFO 49854 --- [           main] c.e.s.SpringDiContainerApplication       : No active profile set, falling back to 1 default profile: "default"
2023-01-05T15:26:52.164+09:00  INFO 49854 --- [           main] c.e.s.SpringDiContainerApplication       : Started SpringDiContainerApplication in 0.367 seconds (process running for 0.634)
logicMethodを実行します
```

# Bean定義方法②：Java ConfigによるBean定義
1つ目のアノテーションによる定義は便利である一方で、**ライブラリ内のクラスなどは「@Component」アノテーションを付与できない場合がある**。  
そこで、本章ではJava Configクラスにメソッドを作成しBeanを定義する方法を学ぶ。 

## Beanとしたいクラスの作成
今回は、あえてBeanとしてクラスに「@Component」アノテーションを付与しない
- Logic.java：「src/main/java/com/example/springdicontainer2/data/Logic.java」
  - 「@Component」アノテーションを付与しないため、このままではコンポーネントスキャンの対象外
```java
package com.example.springdicontainer2.data;

// @Componentは付与しない
public class Logic {
	
	public void logicMethod() {
		System.out.println("logicMethodを実行します");
	}
}
```

## 設定クラスの作成
Java Configを作成する。  
今回はコンポーネントスキャンを行わないため、「@ComponentScan」アノテーションは付与しない。  

- AppConfig.java：「com.example.springdicontainer2.config.AppConfig.java」
  - Java Configクラス内で「@Bean」アノテーションを付与することで戻り値のインスタンスがBeanとなりDIコンテナに格納される

```java
package com.example.springdicontainer2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.springdicontainer2.data.Logic;

@Configuration
public class AppConfig {

    @Bean
    Logic logicMethod() {
        return new Logic();
    }

}
```

## DIコンテナからBeanを取り出してみる
Beanの取り出しには「ApplicationContextのgetBean()」メソッドを使用する。  

- SpringDiContainerApplication：「src/main/java/com/example/springdicontainer」
```java
package com.example.springdicontainer2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.springdicontainer2.config.AppConfig;
import com.example.springdicontainer2.data.Logic;

@SpringBootApplication
public class SpringDiContainer2Application {

	public static void main(String[] args) {
		// DIコンテナの作成
		ApplicationContext context = SpringApplication.run(AppConfig.class, args);
		
		// Beanの呼び出し
		Logic logic = context.getBean(Logic.class);
		logic.logicMethod();
	}

}
```

### 実行結果
「SpringDiContainerApplication」を右クリックし、「Java アプリケーション」として実行する。すると以下のように、DIコンテナからBeanの取り出しができていることがわかる。
```
2023-01-05T15:54:37.177+09:00  INFO 51688 --- [           main] c.e.s.SpringDiContainer2Application      : Starting SpringDiContainer2Application using Java 17.0.5 with PID 51688 (/home/is0383kk/workspace/workspace_sp/spring-di-container-2/bin/main started by is0383kk in /home/is0383kk/workspace/workspace_sp/spring-di-container-2)
2023-01-05T15:54:37.180+09:00  INFO 51688 --- [           main] c.e.s.SpringDiContainer2Application      : No active profile set, falling back to 1 default profile: "default"
2023-01-05T15:54:37.301+09:00  INFO 51688 --- [           main] c.e.s.SpringDiContainer2Application      : Started SpringDiContainer2Application in 0.33 seconds (process running for 0.579)
logicMethodを実行します
```

<!--
# スコープ  
- スコープ：コンテナが管理しているBeanの有効範囲
	- Beanがいつ生成され、いつ破棄されるか
|スコープの種類|説明|
|---|---|
|singleton|インスタンスは１つのみ <br> singletonでは１つのインスタンスが使いまわされる <br> フィールドで値を保持するのは厳禁|
|prototype|必要なときにインスタンスが毎回作られる|
|request|リクエストと同じ|
|session|セッションと同じ|

※singletonとsession以外ほぼ使わない

## スコープの指定方法
- 「@scope」をBeanに付与する
	- Beanが「@Component」を使って定義されている場合
		```java
		@Scope("session")
		@Component
		public class Hoge {
			...
		}
		```
	- Beanが「@Bean」で定義されている場合
		```java
		@Configuration
		public class AppConfig {
			@Scope("request")
			@Bean
			public Hoge hoge() {
				...
			}
		}
		```

# プロキシ
- プロキシ：本来BeanとなるはずだったインスタンスをラップしたBean
	- インタフェース・継承を利用して作られる

具体例：
こちらの例の場合、一見「HogeImpl」がBean定義されるようにみえるが
```java
@Component
public class HogeImpl implements Hoge {
	public void hoge() {...}
}
```
実際にBeanとなるものは「HogeProxy」となる
```java
public class HogeProxy implements Hoge {
	@Override
	public void hoge() {...}
}
```

# AOP
- AOP：本来の処理の前後に割り込み処理を行う
	- トランザクションの開始・終了・権限チェックなど
-->
