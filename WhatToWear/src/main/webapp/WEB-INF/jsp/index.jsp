<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>WhatToWear - แนะนำการแต่งกายตามรูปแบบ</title>
	<link rel="icon" type="image/png" href="assets/img/what-to-wear-logo.png">
	<link rel="preconnect" href="https://fonts.googleapis.com">
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
	<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+Thai:wght@100..900&display=swap" rel="stylesheet">
	<link rel='stylesheet' href='https://cdn-uicons.flaticon.com/2.0.0/uicons-regular-straight/css/uicons-regular-straight.css'>
	<link rel="stylesheet" href="assets/css/index_styles.css">
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>
	<%@ include file="header.jsp"%>
	
	<!-- Hero Section -->
	<section class="hero">
		<div class="hero-content">
			<h1>แต่งตัวให้เหมาะสม<br>ในทุกโอกาส</h1>
			<p>WhatToWear ระบบแนะนำการแต่งกายสำหรับทุกรูปแบบ<br>ที่จะช่วยให้คุณค้นพบสไตล์ที่เหมาะกับคุณที่สุด</p>
			<div class="hero-buttons">
				<a href="matchstyles" class="primary-btn">ค้นหาสไตล์ของคุณ</a>
				<a href="#how-it-works" class="secondary-btn">เรียนรู้เพิ่มเติม</a>
			</div>
		</div>
		<div class="hero-image">
			<img src="https://images.unsplash.com/photo-1620799140188-3b2a02fd9a77?q=80&w=1972&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="WhatToWear Hero Image">
		</div>
	</section>
	
	<!-- Features Section -->
	<section class="features" id="how-it-works">
		<div class="section-header">
			<h2>เราช่วยคุณแต่งตัวได้อย่างไร</h2>
			<p>ด้วยระบบอัจฉริยะที่จะช่วยวิเคราะห์และจับคู่การแต่งกายให้เหมาะสมกับโอกาสต่างๆ</p>
		</div>
		
		<div class="features-grid">
			<div class="feature-card">
				<div class="feature-icon">
					<i class="fi fi-rs-camera"></i>
				</div>
				<h3>อัพโหลดเสื้อผ้าของคุณ</h3>
				<p>เพียงถ่ายรูปหรืออัพโหลดรูปเสื้อผ้าในตู้เสื้อผ้าของคุณเข้าสู่ระบบ</p>
			</div>
			
			<div class="feature-card">
				<div class="feature-icon">
					<i class="fi fi-rs-brain"></i>
				</div>
				<h3>ระบบวิเคราะห์ด้วย AI</h3>
				<p>ระบบ AI ช่วยวิเคราะห์ประเภทและรูปแบบของเสื้อผ้าแต่ละชิ้น</p>
			</div>
			
			<div class="feature-card">
				<div class="feature-icon">
					<i class="fi fi-rs-magic-wand"></i>
				</div>
				<h3>รับคำแนะนำ</h3>
				<p>รับคำแนะนำการแต่งกายที่เหมาะสมกับโอกาสและสไตล์ที่คุณต้องการ</p>
			</div>
		</div>
	</section>
	
	<!-- Style Types Section -->
	<section class="style-types">
		<div class="section-header">
			<h2>รูปแบบการแต่งกาย</h2>
			<p>ระบบของเราช่วยแนะนำการแต่งกายใน 3 รูปแบบหลัก</p>
		</div>
		
		<div class="style-cards">
			<div class="style-card">
				<div class="style-image">
					<img src="https://images.unsplash.com/photo-1561052967-61fc91e48d79?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="Formal Style">
				</div>
				<div class="style-content">
					<h3>ทางการ (Formal)</h3>
					<p>สำหรับโอกาสสำคัญที่ต้องการความเป็นทางการสูง เช่น การสัมภาษณ์งาน งานประชุมสำคัญ หรืองานพิธีการต่างๆ</p>
					<ul class="style-tips">
						<li><i class="fi fi-rs-check"></i> สูท เสื้อเชิ้ต กางเกงสแล็ค</li>
						<li><i class="fi fi-rs-check"></i> ชุดกระโปรงทางการ</li>
						<li><i class="fi fi-rs-check"></i> โทนสีสุภาพ เรียบหรู</li>
					</ul>
				</div>
			</div>
			
			<div class="style-card">
				<div class="style-image">
					<img src="https://images.unsplash.com/photo-1612128267833-18707de58c92?q=80&w=1973&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="Semi-Formal Style">
				</div>
				<div class="style-content">
					<h3>กึ่งทางการ (Semi-Formal)</h3>
					<p>เหมาะสำหรับโอกาสที่ต้องการความสุภาพแต่ไม่เป็นทางการมากเกินไป เช่น งานเลี้ยงบริษัท งานสังสรรค์ หรือการพบปะทางธุรกิจแบบไม่เป็นทางการ</p>
					<ul class="style-tips">
						<li><i class="fi fi-rs-check"></i> เสื้อเชิ้ตคู่กับกางเกงสแล็ค</li>
						<li><i class="fi fi-rs-check"></i> ชุดเดรสสั้น</li>
						<li><i class="fi fi-rs-check"></i> สามารถเพิ่มสีสันได้มากขึ้น</li>
					</ul>
				</div>
			</div>
			
			<div class="style-card">
				<div class="style-image">
					<img src="https://images.unsplash.com/photo-1524275406383-49f669cf763a?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D" alt="Casual Style">
				</div>
				<div class="style-content">
					<h3>ลำลอง (Casual)</h3>
					<p>สำหรับกิจกรรมประจำวัน การพบปะเพื่อนฝูง หรือการพักผ่อนหย่อนใจ ที่ไม่ต้องการความเป็นทางการ</p>
					<ul class="style-tips">
						<li><i class="fi fi-rs-check"></i> เสื้อยืด กางเกงยีนส์</li>
						<li><i class="fi fi-rs-check"></i> เสื้อเชิ้ตลายสก็อต</li>
						<li><i class="fi fi-rs-check"></i> อิสระในการเลือกสีและลวดลาย</li>
					</ul>
				</div>
			</div>
		</div>
	</section>
	
	<!-- AI Technology Section -->
	<section class="ai-tech">
		<div class="tech-content">
			<h2>เทคโนโลยี AI ช่วยวิเคราะห์</h2>
			<p>เราใช้เทคโนโลยี AI เป็นส่วนช่วยในการวิเคราะห์เสื้อผ้าที่คุณอัพโหลดเข้าสู่ระบบ ทำให้การจัดหมวดหมู่เสื้อผ้าของคุณเป็นไปอย่างแม่นยำ</p>
			
			<div class="tech-features">
				<div class="tech-feature">
					<i class="fi fi-rs-eye"></i>
					<h4>การรู้จำภาพ</h4>
					<p>ระบุประเภทเสื้อผ้า เช่น เสื้อเชิ้ต กางเกง กระโปรง ฯลฯ</p>
				</div>
				
				<div class="tech-feature">
					<i class="fi fi-rs-palette"></i>
					<h4>การวิเคราะห์ลวดลาย</h4>
					<p>ตรวจสอบและระบุว่าเสื้อผ้าแต่ละชิ้นมีลวดลายหรือไม่</p>
				</div>
				
				<div class="tech-feature">
					<i class="fi fi-rs-clothes-hanger"></i>
					<h4>การจัดหมวดหมู่</h4>
					<p>จัดประเภทเสื้อผ้าตามความเหมาะสมกับรูปแบบการแต่งกาย</p>
				</div>
			</div>
			
			<a href="addcloth" class="primary-btn">ลองใช้งานเลย</a>
		</div>
		<div class="tech-image">
			<img src="https://images.pexels.com/photos/17483869/pexels-photo-17483869.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2" alt="AI Technology">
		</div>
	</section>
	
	<%@ include file="footer.jsp"%>
	<script src="assets/js/index.js?v=1.0"></script>
	
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