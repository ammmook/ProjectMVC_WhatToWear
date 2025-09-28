<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>เพิ่มเสื้อผ้าใหม่ - WhatToWear</title>
	<link rel="icon" type="image/png" href="assets/img/what-to-wear-logo.png">
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+Thai:wght@100..900&display=swap" rel="stylesheet">
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.4.2/uicons-regular-straight/css/uicons-regular-straight.css'>
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.4.2/uicons-regular-solid/css/uicons-regular-solid.css'>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<link rel="stylesheet" href="assets/css/addcloth_styles.css" />
</head>
<body>
	<%@ include file="header.jsp"%>
	<main class="main-content">
		<h1 class="page-title">เพิ่มเสื้อผ้าใหม่</h1>

		<div class="add-clothing-form">
			<form action="addcloth" method="post" enctype="multipart/form-data"
				id="clothingForm" novalidate>
				<div class="form-layout">
					<div class="image-section">
						<div class="form-group">
							<label class="form-label required">รูปภาพเสื้อผ้า</label>
							<p class="form-hint">กรุณาถ่ายรูปเสื้อผ้าให้ชัดเจน โดยวางบนพื้นหลังเรียบเพื่อให้เห็นรายละเอียดของเสื้อผ้าทั้งตัว</p>

							<div class="image-upload-area" id="imageUploadArea">
								<div class="upload-placeholder" id="uploadPlaceholder">
									<i class="fi fi-rs-cloud-upload-alt"></i>
									<div>คลิก หรือลากไฟล์มาวางที่นี่</div>
									<span class="upload-info">ขนาดไฟล์สูงสุด: 5MB (PNG, JPG)</span>
								</div>
								<img id="uploadedImage" class="uploaded-image" alt="รูปภาพที่อัพโหลด"> 
								<input type="file" id="clothing-image" name="imagefile" accept="image/*">
							</div>
							<div class="error-message" id="imageError"></div>
						</div>
					</div>

					<div class="form-section">
						<div class="form-group">
							<label class="form-label required">หมวดหมู่เสื้อผ้า</label> 
							<select name="subcategory" class="form-select" id="subcategory">
								<option value="" selected disabled>-- เลือกหมวดหมู่ --</option>
								<c:forEach var="item" items="${subcates}">
									<option value="${item.subCategoryId}">${item.subCategoryName}</option>
								</c:forEach>
							</select>
							<div class="error-message" id="subcategoryError"></div>
						</div>

						<div class="form-group">
							<label class="form-label required">ลวดลาย</label>
							<div class="radio-group" id="patternGroup">
								<label class="radio-option"> 
									<input type="radio" name="pattern" value="1"> มี
								</label> 
								<label class="radio-option"> 
									<input type="radio" name="pattern" value="0"> ไม่มี
								</label>
							</div>
							<div class="error-message" id="patternError"></div>
						</div>

						<div class="form-group">
							<label class="form-label required">ความเป็นทางการเสื้อผ้า</label>
							<select name="formalitytype" class="form-select" id="formalitytype">
								<option value="" selected disabled>-- เลือกประเภทความเป็นทางการ --</option>
								<c:forEach var="item" items="${types}">
									<option value="${item.typeId}">${item.typeName}</option>
								</c:forEach>
							</select>
							<div class="error-message" id="formalitytypeError"></div>
						</div>
					</div>
				</div>

				<div class="form-button-group">
					<button type="submit" class="submit-button" id="submitBtn">บันทึก</button>
					<button type="button" class="cancel-button" id="cancelBtn">ยกเลิก</button>
				</div>
			</form>
		</div>
	</main>

	<%@ include file="footer.jsp"%>

	<script type="text/javascript">
        const userGender = '${userGender}'; 
    </script>
	<script type="text/javascript" src="assets/js/addcloth.js?v=1.0"></script>

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
                }).then(() => {
                    <c:if test="${alertType eq 'error'}">
                        document.getElementById('submitBtn').disabled = false;
                        document.getElementById('submitBtn').textContent = 'บันทึก';
                    </c:if>
                });
            });
        </script>
	</c:if>
</body>
</html>