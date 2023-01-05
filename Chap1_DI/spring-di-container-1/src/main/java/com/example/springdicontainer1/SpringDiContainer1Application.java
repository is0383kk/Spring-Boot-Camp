package com.example.springdicontainer1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.example.springdicontainer1.config.AppConfig;
import com.example.springdicontainer1.data.Logic;

@SpringBootApplication
public class SpringDiContainer1Application {

	public static void main(String[] args) {
		// DIコンテナの作成
		ApplicationContext context = SpringApplication.run(AppConfig.class, args);
		
		// Beanの呼び出し
		Logic logic = context.getBean(Logic.class);
		logic.logicMethod();
	}

}
