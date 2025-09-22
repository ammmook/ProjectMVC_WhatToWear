<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>เข้าสู่ระบบ - WhatToWear</title>
	<link rel="icon" type="image/png" href="assets/img/what-to-wear-logo.png">
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Noto+Sans+Thai:wght@100..900&display=swap">
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.0.0/uicons-regular-rounded/css/uicons-regular-rounded.css'>
	<link rel="stylesheet" href="assets/css/register_login_styles.css?v=1.0">
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
	<%@ include file="header.jsp"%>
	<div class="form-container">
		<h2 class="form-title">เข้าสู่ระบบ</h2>
		<p class="form-subtitle">กรุณากรอกข้อมูลของคุณ</p>

		<form action="login_form" method="post">
			<div class="form-group">
				<label class="form-label">อีเมล</label>
				<div class="input-with-icon">
					<i class="fi fi-rr-envelope input-icon"></i> 
					<input type="email" name="email" class="form-control" placeholder="example@email.com" required>
				</div>
			</div>

			<div class="form-group">
				<label class="form-label">รหัสผ่าน</label>
				<div class="password-field">
					<i class="fi fi-rr-key input-icon"></i> 
					<input type="password" name="pwd" class="form-control" placeholder="กรุณากรอกรหัสผ่าน" required> 
					<i class="fi fi-rr-eye password-toggle"></i>
				</div>
			</div>

			<button type="submit" class="submit-btn">เข้าสู่ระบบ</button>
		</form>

		<a href="register" class="go-link">ยังไม่มีบัญชี? ลงทะเบียน</a>
	</div>

	<%@ include file="footer.jsp"%>
	<script src="assets/js/register_login.js"></script>
	
	<c:if test="${showAlert}">
		<script>
			document.addEventListener('DOMContentLoaded', function() {
				Swal.fire({
					icon: '${alertType}',
					title: '${alertTitle}',
					text: '${alertMessage}',
					showConfirmButton: false,
					allowOutsideClick: true,
					timer: 2000,
					timerProgressBar: true
				});
			});
		</script>
	</c:if>
</body>
</html>