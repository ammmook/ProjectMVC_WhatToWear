package com.springmvc.model;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "match_styles")
public class MatchStyle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "style_id", nullable = false)
	private Long styleId;

    @ManyToMany
    @JoinTable(
        name = "favorite_styles",
        joinColumns = @JoinColumn(name = "style_id"),
        inverseJoinColumns = @JoinColumn(name = "clothing_id")
    )
	private List<ClothingItem> clothingItems = new ArrayList<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id", nullable = false)
	private FormalityType formalityType;

	public MatchStyle() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getStyleId() {
		return styleId;
	}

	public void setStyleId(Long styleId) {
		this.styleId = styleId;
	}

	public List<ClothingItem> getClothingItems() {
		return clothingItems;
	}

	public void setClothingItems(List<ClothingItem> clothingItems) {
		this.clothingItems = clothingItems;
	}

	public FormalityType getFormalityType() {
		return formalityType;
	}

	public void setFormalityType(FormalityType formalityType) {
		this.formalityType = formalityType;
	}


}
