<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="th">
<head>
	<meta charset="UTF-8">
	<title>แก้ไขข้อมูลส่วนตัว - WhatToWear</title>
	<link rel="icon" type="image/png" href="assets/img/what-to-wear-logo.png">
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Noto+Sans+Thai:wght@100..900&display=swap">
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.0.0/uicons-regular-rounded/css/uicons-regular-rounded.css'>
	<link rel="stylesheet" href="assets/css/profile_styles.css?v=1.0">
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
	<%@ include file="header.jsp"%>

	<div class="profile-container">
		<h2 class="profile-title">แก้ไขข้อมูลส่วนตัว</h2>

		<div class="avatar-container">
			<i class="fi fi-rr-pencil"></i>
		</div>

		<div class="profile-form-container">
			<form action="update_profile" method="post">
				<div class="form-group">
					<label class="form-label">ชื่อผู้ใช้</label>
					<div class="input-with-icon">
						<i class="fi fi-rr-user input-icon"></i> 
						<input type="text" name="username" value="${user.username}" class="form-control" placeholder="กรุณากรอกชื่อผู้ใช้" required>
					</div>
				</div>

				<div class="form-group">
					<label class="form-label">อีเมล</label>
					<div class="input-with-icon">
						<i class="fi fi-rr-envelope input-icon"></i> 
						<input type="email" name="email" value="${user.email}" class="form-control" placeholder="อีเมล" readonly="readonly"> 
						<i class="fi fi-rr-lock email-lock"></i>
					</div>
				</div>

				<div class="form-group">
					<label class="form-label">เพศ</label>
					<div class="gender-group">
						<div class="gender-option">
							<input type="radio" name="gender" id="male" value="1" ${user.gender == '1' ? 'checked' : ''}> 
							<label for="male">ชาย</label>
						</div>
						<div class="gender-option">
							<input type="radio" name="gender" id="female" value="2" ${user.gender == '2' ? 'checked' : ''}> 
							<label for="female">หญิง</label>
						</div>
						<div class="gender-option">
							<input type="radio" name="gender" id="other" value="3" ${user.gender == '3' ? 'checked' : ''}> 
							<label for="other">ไม่ระบุ</label>
						</div>
					</div>
				</div>

				<div class="form-group">
					<label class="form-label">เปลี่ยนรหัสผ่าน (ไม่จำเป็น)</label>
					<div class="input-with-icon">
						<i class="fi fi-rr-key input-icon"></i> 
						<input type="password" name="pwd" class="form-control" placeholder="รหัสผ่านใหม่">
						<i class="fi fi-rr-eye password-toggle"></i>
					</div>
				</div>

				<div class="form-group">
					<div class="input-with-icon">
						<i class="fi fi-rr-key input-icon"></i> 
						<input type="password" name="confirmpwd" class="form-control" placeholder="ยืนยันรหัสผ่านใหม่"> 
						<i class="fi fi-rr-eye password-toggle"></i>
					</div>
				</div>

				<button type="submit" class="submit-btn">บันทึกการเปลี่ยนแปลง</button>
			</form>
		</div>
	</div>

	<%@ include file="footer.jsp"%>
	<script src="assets/js/edit_profile.js"></script>
	
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