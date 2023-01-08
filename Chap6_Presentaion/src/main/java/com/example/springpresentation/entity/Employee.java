package com.example.springpresentation.entity;

import java.time.LocalDate;

public class Employee {
	
	/**
	 * フィールド
	 * DBのカラムに対応する
	 */
	private Integer id;
	
	private String name;
	
	private LocalDate joinedDate;
	
	private String departmentName;
	
	private String email;
	
	// コンストラクタ
	public Employee() {
		super();
	}
	
	// FormクラスからEntityクラスに変換する際に使用するコンストラクタ
	public Employee(String name, LocalDate joinedDate, String departmentName, String email) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.name = name;
		this.joinedDate = joinedDate;
		this.departmentName = departmentName;
		this.email = email;
	}
	
	/**
	 * getter・setter
	 */
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public LocalDate getJoinedDate() {
		return joinedDate;
	}
	public void setJoinedDate(LocalDate joinedDate) {
		this.joinedDate = joinedDate;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String toString() {
		return "id：" + this.id +
				" ,name：" + this.name +
				" ,joinedDate：" + this.joinedDate +
				" ,departmentName：" + this.departmentName +
				" ,email：" + this.email;
	}
	

}
