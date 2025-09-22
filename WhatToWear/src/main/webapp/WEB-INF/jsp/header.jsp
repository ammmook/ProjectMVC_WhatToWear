<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>What To Wear</title>
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+Thai:wght@100..900&display=swap" rel="stylesheet">
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.6.0/uicons-regular-straight/css/uicons-regular-straight.css'>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/11.7.32/sweetalert2.min.css">
	<link rel="stylesheet" href="assets/css/header_styles.css">
	<script src="https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/11.7.32/sweetalert2.min.js"></script>
</head>
<body>
	<nav class="nav">
		<a href="/Project_WhatToWear/" class="nav-logo"> 
			<img src="assets/img/what-to-wear-logo.png"> 
			WhatToWear
		</a>

		<div class="nav-menu">
			<div class="nav-center">
				<a href="/WhatToWear/" class="nav-link"> 
					<i class="fi fi-rs-home"></i> 
					หน้าหลัก
				</a> 
					<a href="matchstyles" class="nav-link"> 
					<i class="fi fi-rs-shirt"></i>
					ค้นหาสไตล์ของคุณ
				</a>
				<c:if test="${not empty user}">
					<a href="listclothes" class="nav-link"> 
						<i class="fi fi-rs-clothes-hanger"></i>
						เสื้อผ้าของคุณ
					</a>
					<a href="favoritestyles" class="nav-link"> 
						<i class="fi fi-rs-heart"></i>
						สไตล์ที่บันทึก
					</a>
				</c:if>
			</div>

			<c:choose>
				<c:when test="${not empty user}">
					<div class="nav-right">
						<div class="user-profile">
							<span class="username">${user.username}</span>
							<div class="user-avatar">
								<i class="fi fi-rs-circle-user"></i>
							</div>
							<div class="dropdown-menu">
								<a href="yourprofile" class="dropdown-item"> <i
									class="fi fi-rs-user"></i> 
									โปรไฟล์
								</a>

								<a href="addcloth" class="dropdown-item">
									<i class="fi fi-rs-add"></i>
									เพิ่มเสื้อผ้า
								</a>
								<div class="dropdown-divider"></div>
								<a href="logout" class="dropdown-item logout"> 
									<i class="fi fi-rs-sign-out-alt"></i> 
									ออกจากระบบ
								</a>
							</div>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="nav-right">
						<a href="login" class="nav-btn login">เข้าสู่ระบบ</a> 
						<a href="register" class="nav-btn signup">ลงทะเบียน</a>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</nav>
	<script src="assets/js/header.js"></script>
</body>
</html>