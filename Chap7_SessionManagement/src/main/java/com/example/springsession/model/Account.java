package com.example.springsession.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class Account implements Serializable {
	// Sessionに格納するクラスはSerializableで実装する
	private static final long serialVersionUID = 8760625261281613617L;
	
	// フィールド
	private String name;
	private LocalDateTime dateTime;
	
	// コンストラクタ
	public Account(){
		System.out.println("Accountインスタンスの生成");
	}

	// getter・setter
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	@Override
	public String toString() {
		return "Account [name=" + name + ", dateTime=" + dateTime + "]";
	}
	
	

}
