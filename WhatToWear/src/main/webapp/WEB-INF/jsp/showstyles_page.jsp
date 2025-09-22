<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>แนะนำการแต่งกายสำหรับคุณ - WhatToWear</title>
	<link rel="icon" type="image/png" href="assets/img/what-to-wear-logo.png">
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+Thai:wght@100..900&display=swap" rel="stylesheet">
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.0.0/uicons-regular-straight/css/uicons-regular-straight.css'>
	<link rel="stylesheet" href="assets/css/showstyles_styles.css?v=1.0">
</head>
<body>
	<%@ include file="header.jsp"%>

	<div class="main-container">
		<!-- Sidebar แสดงเสื้อผ้าที่เลือก -->
		<div class="sidebar">
		    <h3 class="sidebar-title">เสื้อผ้าที่เลือก</h3>
		    
		    <!-- ปุ่มแสดงทั้งหมด -->
		    <div class="sidebar-item ${empty filterClothingId ? 'active' : ''}">
		        <a href="showstyles" class="show-all-link">
		            <div class="show-all-button">
		                <i class="fi fi-rs-apps"></i>
		                <span>แสดงทั้งหมด</span>
		            </div>
		        </a>
		    </div>
		    
		    <div class="sidebar-items">
		        <c:forEach var="item" items="${selectedClothes}">
		            <div class="sidebar-item ${filterClothingId == item.clothid ? 'selected' : ''}">
		                <a href="showstyles?id=${item.clothid}"> 
		                    <img src="assets/img/clothes/${item.imgPath}" alt="${item.subCategory.subCategoryName}"/>
		                </a>
		            </div>
		        </c:forEach>
		    </div>
		</div>

		<div class="content">
		    <h1 class="page-title">แนะนำการแต่งกายสำหรับคุณ</h1>
		    <p class="page-subtitle">ค้นพบสไตล์ที่ใช่สำหรับคุณ</p>
		    
		    <c:if test="${not empty styles}">
		        <div class="results-info">
		            <span>พบ ${totalStyles} สไตล์ที่เหมาะสม</span>
		        </div>
		    </c:if>
		
		    <c:if test="${not empty err_msg}">
		        <div class="error-message">${err_msg}</div>
		    </c:if>
		
		    <!-- แสดงผลการจับคู่ -->
		    <c:if test="${not empty styles}">
		        <div class="outfit-grid">
		            <c:forEach var="style" items="${styles}" varStatus="status">
		                <div class="outfit-card">
		                    <div class="outfit-header">
		                        <span class="outfit-type">สไตล์ ${status.index + 1}</span>
		                        <button class="view-button">${style.formalityType.typeName}</button>
		                    </div>
		                    <div class="outfit-images">
		                        <c:forEach var="item" items="${style.clothingItems}">
		                            <div class="outfit-image">
		                                <img src="assets/img/clothes/${item.imgPath}" alt="${item.subCategory.subCategoryName}"/>
		                                <span class="item-label">${item.subCategory.category.categoryName}</span>
		                            </div>
		                        </c:forEach>
		                    </div>
		                 	<a href="addfavoritestyle?index=${status.index}" class="like-button">
		                        <i class="fi fi-rs-heart"></i> เพิ่มในรายการโปรด
		                	</a>
		                </div>
		            </c:forEach>
		        </div>
		    </c:if>
		
		    <c:if test="${empty err_msg and empty styles}">
		        <div class="no-results">
		            <h3>ไม่พบการจับคู่ที่เหมาะสม</h3>
		            <p>กรุณาลองเลือกเสื้อผ้าหรือความเป็นทางการแบบอื่น</p>
		            <a href="matchstyles" class="back-button">กลับไปเลือกใหม่</a>
		        </div>
		    </c:if>
		</div>
	</div>

	<%@ include file="footer.jsp"%>
</body>
</html>