package com.springmvc.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	@Id
	@Column(name = "user_email", length = 100, nullable = false)
	private String email;

	@Column(name = "user_name", length = 60, nullable = false)
	private String username;

	@Column(name = "user_gender", nullable = false)
	private int gender;

	@Column(name = "user_password", length = 200, nullable = false)
	private String password;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ClothingItem> clothingItems = new ArrayList<>();

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String email, String username, int gender, String password) {
		super();
		this.email = email;
		this.username = username;
		this.gender = gender;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<ClothingItem> getClothingItems() {
		return clothingItems;
	}

	public void setClothingItems(List<ClothingItem> clothingItems) {
		this.clothingItems = clothingItems;
	}

}
