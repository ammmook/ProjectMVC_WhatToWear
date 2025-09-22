<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ค้นหาสไตล์ของคุณ - WhatToWear</title>
    <link rel="icon" type="image/png" href="assets/img/what-to-wear-logo.png">
    <link href="https://fonts.googleapis.com/css2?family=Noto+Sans+Thai:wght@100..900&display=swap" rel="stylesheet">
    <link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.0.0/uicons-regular-straight/css/uicons-regular-straight.css'>
    <link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.6.0/uicons-solid-straight/css/uicons-solid-straight.css'>
	<link rel="stylesheet" href="sweetalert2.min.css">
	<link rel="stylesheet" href="assets/css/matchstyles_styles.css?v=1.0">
</head>
<body>
    <%@ include file="header.jsp"%>

    <main class="container">
        <h1 class="page-title">แนะนำการแต่งตัววันนี้</h1>
        
        <section class="clothing-selection">          
			<form action="matchstyles" method="post" id="clothing-form">
                <%-- ▼▼▼ SECTION EDITED / ส่วนที่แก้ไข ▼▼▼ --%>
			    <div class="layout-container">
			        <div class="left-column">
			            <div class="style-selection">
			                <h2 class="selection-title">เลือกรูปแบบการแต่งกาย</h2>
			                <div class="style-options">
			                    <div class="style-option">
			                         <input type="radio" name="formality_type" id="formal" value="T01">
			                        <label for="formal" class="style-label">
			                            <div class="style-icon">
			                                <i class="fi fi-ss-shirt"></i>
			                            </div>
			                            <span>ทางการ</span>
			                         </label>
			                    </div>
			                    <div class="style-option">
			                        <input type="radio" name="formality_type" id="semiformal" value="T02">
			                         <label for="semiformal" class="style-label">
			                            <div class="style-icon">
			                                <i class="fi fi-ss-shirt-long-sleeve"></i>
			                            </div>
			                            <span>กึ่งทางการ</span>
			                        </label>
			                    </div>
			                    <div class="style-option selected">
			                        <input type="radio" name="formality_type" id="casual" value="T03">
			                        <label for="casual" class="style-label">
			                            <div class="style-icon">
			                                <i class="fi fi-ss-tshirt"></i>
			                            </div>
			                            <span>ลำลอง</span>
			                        </label>
			                    </div>
			                </div>
			            </div>
			            
			            <div class="form-actions">
			                <button type="submit" class="btn btn-primary">ดูการแนะนำ</button>
			                <button type="reset" class="btn btn-secondary">ล้างการเลือก</button>
			            </div>
			        </div>
			
			        <div class="right-column">
			            <h2 class="selection-title">เลือกเสื้อผ้าของคุณ</h2>
			            <div class="category-tabs">
			                 <div class="tab ${selectedCates == 'CG001' ? 'selected' : ''}">
						        <a href="matchstyles?id=CG001" class="tab-category">
						            <img src="assets/img/icon/tshirt.png"> เสื้อท่อนบน
						        </a>
						    </div>
						    <div class="tab ${selectedCates == 'CG002' ? 'selected' : ''}">
						        <a href="matchstyles?id=CG002" class="tab-category">
						            <img src="assets/img/icon/pants.png"> เสื้อท่อนล่าง
						        </a>
						    </div>
						    <div class="tab ${selectedCates == 'CG003' ? 'selected' : ''}">
						        <a href="matchstyles?id=CG003" class="tab-category">
						            <img src="assets/img/icon/dress.png"> เดรส
						        </a>
						    </div>
						    <div class="tab ${selectedCates == 'CG004' ? 'selected' : ''}">
						        <a href="matchstyles?id=CG004" class="tab-category">
						            <img src="assets/img/icon/jacket.png"> เสื้อคลุมนอก
						        </a>
						    </div>
			            </div>
			
			            <div class="clothing-scroll-container">
			                 <c:if test="${not empty clothes}">
					            <div class="clothing-grid">
									<c:forEach var="item" items="${clothes}">
									    <c:set var="isSelected" value="false" />
									    <c:if test="${not empty selectedClothes}">
									        <c:forEach var="selectedId" items="${selectedClothes}">
									            <c:if test="${selectedId eq item.clothid.toString()}">
									                <c:set var="isSelected" value="true" />
									            </c:if>
									        </c:forEach>
									    </c:if>
									    
									    <div class="clothing-item ${isSelected ? 'selected' : ''}">
									        <img src="assets/img/clothes/${item.imgPath}" alt="${item.subCategory.subCategoryName}">
									        <label for="cloth${item.clothid}" class="clothing-label"></label>
									        <input type="checkbox" name="selectedClothes" value="${item.clothid}" class="clothing-checkbox" ${isSelected ? 'checked' : ''}>
									    </div>
									</c:forEach>
					            </div>
					        </c:if>
					        
							<c:if test="${empty clothes}">
							    <div class="empty-state">
							        <div class="empty-state-content">
							            <p>${err_msg}</p>
							            <a href="addcloth_page" class="add-cloth-btn">
							                <i class="fi fi-rs-add"></i> เพิ่มเสื้อผ้า
							            </a>
							        </div>
							    </div>
							</c:if>
			            </div>
			        </div>
			    </div>
			    <%-- ▲▲▲ END OF EDITED SECTION / สิ้นสุดส่วนที่แก้ไข ▲▲▲ --%>
            </form>
        </section>
    </main>

    <%@ include file="footer.jsp"%>
   
    <script src="assets/js/matchstyles.js"> </script>
</body>
</html>