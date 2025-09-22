package com.springmvc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sub_categories")
public class SubCategory {
	@Id
	@Column(name = "subcate_id", length = 5, nullable = false)
	private String subCategoryId;
	
	@Column(name = "subcate_name", length = 60, nullable = false)
	private String subCategoryName;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;


	public SubCategory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SubCategory(String subCategoryId, String subCategoryName) {
		super();
		this.subCategoryId = subCategoryId;
		this.subCategoryName = subCategoryName;
	}

	public String getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(String subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public String getSubCategoryName() {
		return subCategoryName;
	}

	public void setSubCategoryName(String subCategoryName) {
		this.subCategoryName = subCategoryName;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

}
