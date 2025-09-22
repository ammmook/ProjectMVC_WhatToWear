package com.springmvc.model;

import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "clothing_items")
public class ClothingItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "clothing_id", nullable = false)
	private Long clothid;

	@Column(name = "have_pattern", nullable = false)
	private boolean havePattern;

	@Column(name = "clothing_imgpath", length = 250, nullable = false)
	private String imgPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_email", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcate_id", nullable = false)
    private SubCategory subCategory;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private FormalityType formalityType;

	public ClothingItem() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ClothingItem(Long clothid, boolean havePattern, String imgPath, User user, SubCategory subCategory,
			FormalityType formalityType) {
		super();
		this.clothid = clothid;
		this.havePattern = havePattern;
		this.imgPath = imgPath;
		this.user = user;
		this.subCategory = subCategory;
		this.formalityType = formalityType;
	}

	public Long getClothid() {
		return clothid;
	}

	public void setClothid(Long clothid) {
		this.clothid = clothid;
	}

	public boolean isHavePattern() {
		return havePattern;
	}

	public void setHavePattern(boolean havePattern) {
		this.havePattern = havePattern;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SubCategory getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
	}

	public FormalityType getFormalityType() {
		return formalityType;
	}

	public void setFormalityType(FormalityType formalityType) {
		this.formalityType = formalityType;
	}
}
