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
