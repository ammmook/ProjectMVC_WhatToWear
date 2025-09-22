package com.springmvc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "formality_types")
public class FormalityType {
	@Id
	@Column(name = "type_id", length = 3, nullable = false)
	private String typeId;
	
	@Column(name = "type_name", length = 50, nullable = false)
	private String typeName;

	public FormalityType() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FormalityType(String typeId, String typeName) {
		super();
		this.typeId = typeId;
		this.typeName = typeName;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}
