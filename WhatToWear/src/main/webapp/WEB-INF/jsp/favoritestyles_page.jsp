<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>รายการโปรดของฉัน - WhatToWear</title>
	<link rel="icon" type="image/png" href="assets/img/what-to-wear-logo.png">
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+Thai:wght@100..900&display=swap" rel="stylesheet">
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.0.0/uicons-regular-straight/css/uicons-regular-straight.css'>
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.1.0/uicons-solid-straight/css/uicons-solid-straight.css'>
	<link rel="stylesheet" href="assets/css/favoritestyles_styles.css?v=1.1">
</head>
<body>
	<%@ include file="header.jsp"%>
	
	<main class="favorite-container">
	<h1 class="favorite-title">รายการโปรดของฉัน</h1>
	
	<form action="filterType" method="post">
	    <div class="filter-container">
	        <div class="filter-group">
	            <span class="filter-label">แสดงรายการ:</span>
	            <div class="filter-dropdown">
	                <select id="sortType" name="sortType" class="sort-select" onchange="this.form.submit()">
	                    <option value="alltype" ${selectedType == 'alltype' ? 'selected' : ''}>ชุดทั้งหมด</option>
	                    <c:if test="${not empty type}">
	                        <c:forEach var="item" items="${type}">
	                            <option value="${item.typeId}" ${selectedType == item.typeId ? 'selected' : ''}>${item.typeName}</option>
	                        </c:forEach>
	                   	</c:if>
	                </select>
	                <i class="fi fi-rs-angle-small-down dropdown-icon"></i>
	            </div>
	        </div>
	        
	        <div class="filter-checkbox-group">
	            <input type="checkbox" id="showOuterwear" name="showOuterwear" onchange="this.form.submit()" ${showOuterwear ? 'checked' : ''}>
	            <label for="showOuterwear">แสดงเสื้อคลุม</label>
	        </div>
	    </div>
	</form>
	
	<%-- ▼▼▼ SECTION EDITED / ส่วนที่แก้ไข ▼▼▼ --%>
	<c:if test="${not empty err_msg}">
	    <div class="empty-state">
	        <div class="empty-state-content">
				<p>${err_msg}</p>
				<a href="matchstyles" class="search-style-btn">
					<i class="fi fi-rs-search"></i> ค้นหาสไตล์
				</a>
			</div>
	    </div>
	</c:if>
	<%-- ▲▲▲ END SECTION EDITED / สิ้นสุดส่วนที่แก้ไข ▲▲▲ --%>
	
	<div class="favorites-grid">
	    <c:if test="${empty err_msg}">
	        <c:forEach var="style" items="${styles}" varStatus="status">
	            <div class="style-card">
	                <div class="card-header">
	                    <h2 class="style-title">สไตล์</h2>
	                    <span class="style-tag">${style.formalityType.typeName}</span>
	                </div>
	                <div class="style-items">
	                    <c:forEach var="item" items="${style.clothingItems}">
	                        <div class="item-container">
	                            <img src="assets/img/clothes/${item.imgPath}" alt="${item.subCategory.subCategoryName}" class="item-image">
	                        </div>
	                    </c:forEach>
	                </div>
	
	                <a href="deletefavorite?id=${style.styleId}" class="favorite-button">
	                    <i class="fi fi-ss-heart"></i>
	                    เพิ่มในรายการโปรดแล้ว
	                </a>
	            </div>
	        </c:forEach>
	    </c:if>
	</div>
	</main>
	<%@ include file="footer.jsp"%>
</body>
</html>