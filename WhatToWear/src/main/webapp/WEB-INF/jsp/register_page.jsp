<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>สมัครสมาชิก - WhatToWear</title>
	<link rel="icon" type="image/png" href="assets/img/what-to-wear-logo.png">
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Noto+Sans+Thai:wght@100..900&display=swap">
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.0.0/uicons-regular-rounded/css/uicons-regular-rounded.css'>
	<link rel="stylesheet" href="assets/css/register_login_styles.css">
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
	<%@ include file="header.jsp"%>
	<div class="form-container">
		<h2 class="form-title">ลงทะเบียน</h2>
		<p class="form-subtitle">สร้างบัญชีใหม่เพื่อเริ่มต้นใช้งาน</p>

		<form action="register_form" method="post">
			<div class="form-group">
				<label class="form-label">ชื่อผู้ใช้</label>
				<div class="input-with-icon">
					<i class="fi fi-rr-user input-icon"></i> 
					<input type="text" name="username" class="form-control" placeholder="กรุณากรอกชื่อผู้ใช้" required>
				</div>
			</div>

			<div class="form-group">
				<label class="form-label">อีเมล</label>
				<div class="input-with-icon">
					<i class="fi fi-rr-envelope input-icon"></i> 
					<input type="email" name="email" class="form-control" placeholder="example@email.com" required>
				</div>
			</div>

			<div class="form-group">
				<label class="form-label">เพศ</label>
				<div class="gender-group">
					<div class="gender-option">
						<input type="radio" name="gender" id="male" value="1"> 
						<label for="male">ชาย</label>
					</div>
					<div class="gender-option">
						<input type="radio" name="gender" id="female" value="2"> 
						<label for="female">หญิง</label>
					</div>
					<div class="gender-option">
						<input type="radio" name="gender" id="other" value="3"> <label for="other">ไม่ระบุ</label>
					</div>
				</div>
			</div>

			<div class="form-group">
				<label class="form-label">รหัสผ่าน</label>
				<div class="password-field">
					<i class="fi fi-rr-key input-icon"></i> <input type="password"
						name="pwd" class="form-control" placeholder="กรุณากรอกรหัสผ่าน"
						required> <i class="fi fi-rr-eye password-toggle"></i>
				</div>
			</div>

			<div class="form-group">
				<label class="form-label">ยืนยันรหัสผ่าน</label>
				<div class="password-field">
					<i class="fi fi-rr-key input-icon"></i> 
					<input type="password" name="confirmpwd" class="form-control" placeholder="กรุณากรอกรหัสผ่านอีกครั้ง" required> 
					<i class="fi fi-rr-eye password-toggle"></i>
				</div>
			</div>

			<button type="submit" class="submit-btn">ลงทะเบียน</button>
		</form>

		<a href="login" class="go-link">มีบัญชีอยู่แล้ว? เข้าสู่ระบบ</a>
	</div>

	<%@ include file="footer.jsp"%>
	<script src="assets/js/register_login.js?v=1.0"></script>

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