<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>รายการเสื้อผ้าของฉัน - WhatToWear</title>
	<link rel="icon" type="image/png" href="assets/img/what-to-wear-logo.png">
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+Thai:wght@100..900&display=swap" rel="stylesheet">
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.0.0/uicons-regular-straight/css/uicons-regular-straight.css'>
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.0.0/uicons-regular-solid/css/uicons-regular-solid.css'>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<link rel="stylesheet" href="assets/css/listclothes_styles.css"> <%-- Changed version to 1.1 --%>
</head>
<body>
	<%@ include file="header.jsp"%>

	<main class="clothes-container">
		<h1 class="page-title">รายการเสื้อผ้าของฉัน</h1>

		<div class="filter-container">
			<form action="filterclothes" method="post">
				<div class="filter-section">
					<div class="filter-dropdown">
						<label for="category-filter">หมวดหมู่หลัก</label>
						<div class="select-wrapper">
							<select name="category" id="category-filter" onchange="this.form.submit()">
								<option value="allcategories" ${selectedCategory == null || selectedCategory == 'allcategories' ? 'selected' : ''}>เสื้อผ้าทั้งหมด</option>
								<c:if test="${not empty categories}">
									<c:forEach var="category" items="${categories}">
										<option value="${category.categoryId}" ${selectedCategory == category.categoryId ? 'selected' : ''}>${category.categoryName}</option>
									</c:forEach>
								</c:if>
							</select>
						</div>
					</div>

					<div class="filter-dropdown">
						<label for="subcategory-filter">หมวดหมู่ย่อย</label>
						<div class="select-wrapper">
							<select name="subcategory" id="subcategory-filter" onchange="this.form.submit()">
								<option value="allsubcategories" ${selectedSubcate == null || selectedSubcate == 'allsubcategories' ? 'selected' : ''}>เสื้อผ้าทั้งหมด</option>
								<c:if test="${not empty subcates}">
									<c:forEach var="item" items="${subcates}">
										<option value="${item.subCategoryId}" ${selectedSubcate == item.subCategoryId ? 'selected' : ''}>${item.subCategoryName}</option>
									</c:forEach>
								</c:if>
							</select>
						</div>
					</div>
				</div>
			</form>

			<button id="delete-btn" class="delete-btn">
				<i class="fi fi-rs-trash"></i> ลบเสื้อผ้า
			</button>
			<div id="selection-buttons" class="selection-buttons" style="display: none;">
				<button id="cancel-btn" class="cancel-btn">ยกเลิก</button>
				<button id="confirm-delete-btn" class="confirm-delete-btn">
					<i class="fi fi-rs-trash"></i> ลบรายการที่เลือก
				</button>
			</div>
		</div>

		<%-- ▼▼▼ SECTION EDITED / ส่วนที่แก้ไข ▼▼▼ --%>
		<c:if test="${not empty err_msg}">
			<div class="empty-state">
				<div class="empty-state-content">
					<p>${err_msg}</p>
					<a href="addcloth" class="add-cloth-btn">
						<i class="fi fi-rs-add"></i> เพิ่มเสื้อผ้า
					</a>
				</div>
			</div>
		</c:if>
		<%-- ▲▲▲ END OF EDITED SECTION / สิ้นสุดส่วนที่แก้ไข ▲▲▲ --%>

		<c:if test="${empty err_msg}">
			<form id="delete-form" action="deleteclothes" method="post">
				<input type="hidden" name="category" value="${selectedCategory != null ? selectedCategory : 'allcategories'}">
				<input type="hidden" name="subcategory" value="${selectedSubcategory != null ? selectedSubcategory : 'allsubcategories'}">
				<div class="clothes-grid">
					<c:forEach var="item" items="${clothes}" varStatus="status">
						<div class="clothes-item">
							<img src="assets/img/clothes/${item.imgPath}" alt="${item.subCategory.subCategoryName}">
							<div class="item-checkbox">
								<input type="checkbox" id="item${status.index + 1}" name="selectedItems" class="clothes-checkbox" value="${item.clothid}"> 
								<label for="item${status.index + 1}"></label>
							</div>
						</div>
					</c:forEach>
				</div>
			</form>
		</c:if>
	</main>

	<%@ include file="footer.jsp"%>
    
    <%-- (JavaScript and other scripts remain the same) --%>
	<script>
        //JavaScript สำหรับการเลือกเสื้อผ้าเพื่อลบโดยใช้ SweetAlert
        document.addEventListener('DOMContentLoaded', function() {
            // อ้างอิงไปยังปุ่มต่างๆ
            const deleteBtn = document.getElementById('delete-btn');
            const selectionButtons = document.getElementById('selection-buttons');
            const cancelBtn = document.getElementById('cancel-btn');
            const confirmDeleteBtn = document.getElementById('confirm-delete-btn');
            const clothesItems = document.querySelectorAll('.clothes-item');
            const checkboxes = document.querySelectorAll('.clothes-checkbox');
            const deleteForm = document.getElementById('delete-form');
            
            // สร้างฟังก์ชันเพื่อเปิดโหมดการเลือกรายการ
            function enableSelectionMode() {
                // ซ่อนปุ่มลบและแสดงปุ่มตัวเลือก
                deleteBtn.style.display = 'none';
                selectionButtons.style.display = 'flex';
                
                // ทำให้ทุกรายการเสื้อผ้าสามารถคลิกเพื่อเลือกได้
                clothesItems.forEach(function(item, index) {
                    // เพิ่มคลาส selectable ให้กับรายการ
                    item.classList.add('selectable');
                    
                    // ลบ event listener เดิมที่อาจมีอยู่ก่อน (ไม่ต้องใช้ clone)
                    item.removeEventListener('click', itemClickHandler);
                    
            
                    // เพิ่ม event listener ใหม่
                    item.addEventListener('click', itemClickHandler);
                });
            }
            
            // ฟังก์ชันจัดการการคลิกที่รายการเสื้อผ้า
            function itemClickHandler(event) {
                // ป้องกันการเลือกซ้ำซ้อนหากคลิกที่ checkbox โดยตรง
                if (event.target.tagName === 'INPUT' || event.target.tagName === 'LABEL') {
                    return;
                }
                
                // หา index ของรายการที่ถูกคลิก
                const clickedItem = this;
                const index = Array.from(clothesItems).indexOf(clickedItem);
                
                // สลับสถานะ checkbox
                const checkbox = checkboxes[index];
                checkbox.checked = !checkbox.checked;
                
                // สลับคลาส selected
                if (checkbox.checked) {
                    clickedItem.classList.add('selected');
                } else {
                    clickedItem.classList.remove('selected');
                }
            }
            
            // สร้างฟังก์ชันเพื่อยกเลิกการเลือกรายการ
            function disableSelectionMode() {
                // แสดงปุ่มลบและซ่อนปุ่มตัวเลือก
                deleteBtn.style.display = 'flex';
                selectionButtons.style.display = 'none';
                
                // ยกเลิกการเลือก checkbox ทั้งหมด
                checkboxes.forEach(function(checkbox) {
                    checkbox.checked = false;
                });
                // ลบคลาส selectable และ selected จากทุกรายการเสื้อผ้า
                clothesItems.forEach(function(item) {
                    item.classList.remove('selectable');
                    item.classList.remove('selected');
                    
                    // ลบ event listener การคลิกรายการ (สำคัญ)
                    item.removeEventListener('click', itemClickHandler);
                });
            }
            
            // เมื่อคลิกปุ่มลบเสื้อผ้า
            deleteBtn.addEventListener('click', function(e) {
                e.preventDefault();
                enableSelectionMode();
            });
            // เมื่อคลิกปุ่มยกเลิก
            cancelBtn.addEventListener('click', function(e) {
                e.preventDefault();
                disableSelectionMode();
            });
            // เมื่อคลิกปุ่มยืนยันการลบ
            confirmDeleteBtn.addEventListener('click', function(e) {
                e.preventDefault();
                
                // เก็บรวบรวม IDs ของเสื้อผ้าที่ถูกเลือก
                const selectedIds = [];
                checkboxes.forEach(function(checkbox) {
                    if (checkbox.checked) {
                        selectedIds.push(checkbox.value);
                    }
            
                });
                
                // ตรวจสอบว่ามีรายการที่ถูกเลือกหรือไม่
                if (selectedIds.length === 0) {
                    Swal.fire({
                        title: 'แจ้งเตือน',
                        text: 'กรุณาเลือกรายการที่ต้องการลบ',
                        icon: 'warning',
            
                        confirmButtonText: 'ตกลง',
                    });
                    return;
                }
                
                // แสดง SweetAlert เพื่อยืนยันการลบ
                Swal.fire({
                    title: 'ยืนยันการลบรายการ',
                    text: 'คุณต้องการลบรายการที่เลือกจำนวน ' + selectedIds.length + ' รายการใช่หรือไม่?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#ff3b30',
                    cancelButtonColor: '#6b7280',
                    confirmButtonText: 'ยืนยันการลบ',
                    cancelButtonText: 'ยกเลิก'
                }).then((result) => {
                
                    if (result.isConfirmed) {
                        // ถ้าผู้ใช้ยืนยัน ส่งฟอร์มไปยัง server
                        deleteForm.submit();
                    } else {
                        // ถ้าผู้ใช้ยกเลิกการลบ ให้ยกเลิกการเลือกรายการทั้งหมด
                        disableSelectionMode();
                    }
                });
            });
        });
    </script>

	<c:if test="${param.add == 'success'}">
		<script>
			document.addEventListener('DOMContentLoaded', function() {
			    Swal.fire({
			        icon: 'success',
			        title: 'สำเร็จ!',
			        text: 'เพิ่มเสื้อผ้าเรียบร้อยแล้ว',
			        showConfirmButton: false,
			        allowOutsideClick: true,
			        timer: 2000,
			        timerProgressBar: true
			    });
			});
		</script>
	</c:if>
	
	<c:if test="${param.delete == 'success'}">
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            Swal.fire({
                icon: 'success',
                title: 'สำเร็จ!',
                text: 'ลบเสื้อผ้าที่เลือกเรียบร้อยแล้ว',
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