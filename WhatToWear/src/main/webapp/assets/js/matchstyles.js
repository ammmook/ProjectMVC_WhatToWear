document.addEventListener('DOMContentLoaded', function() {

	// ===== การจัดการการเลือกเสื้อผ้า =====
	function initializeClothingSelection() {
		const clothingItems = document.querySelectorAll('.clothing-item');

		// เพิ่ม event listener ให้กับแต่ละชิ้นเสื้อผ้า
		clothingItems.forEach(function(item) {
			item.addEventListener('click', function() {
				// หา checkbox ที่อยู่ภายในชิ้นเสื้อผ้านี้
				const checkbox = this.querySelector('.clothing-checkbox');

				// ดึงข้อมูลที่จำเป็นสำหรับการนำทาง
				const clothid = checkbox.value;
				const selectedCates = getSelectedCategory();

				// นำทางไปยัง controller พร้อมส่งข้อมูล
				selectCloth(clothid, selectedCates);
			});
		});
	}

	// ฟังก์ชันสำหรับการนำทางไปยัง controller
	function selectCloth(clothid, cateid) {
		window.location = "matchstyles?clothid=" + clothid + "&id=" + cateid;
	}

	// ฟังก์ชันสำหรับดึงหมวดหมู่ที่เลือกในปัจจุบัน
	function getSelectedCategory() {
		// หาแท็บที่มีคลาส 'selected'
		const selectedTab = document.querySelector('.tab.selected .tab-category');
		if (selectedTab) {
			// ดึง category id จาก href
			const href = selectedTab.getAttribute('href');
			const urlParams = new URLSearchParams(href.split('?')[1]);
			return urlParams.get('id');
		}

		// ถ้าไม่เจอให้ใช้ค่าเริ่มต้น
		return 'CG001';
	}

	// ===== การจัดการการเลือกรูปแบบการแต่งกาย =====
	function initializeStyleSelection() {
		const styleLabels = document.querySelectorAll('.style-label');

		// เพิ่ม event listener ให้กับแต่ละ label
		styleLabels.forEach(function(label) {
			label.addEventListener('click', function() {
				// ลบคลาส 'selected' จากตัวเลือกทั้งหมด
				const allStyleOptions = document.querySelectorAll('.style-option');
				allStyleOptions.forEach(function(option) {
					option.classList.remove('selected');
				});

				// เพิ่มคลาส 'selected' ให้ตัวเลือกที่คลิก
				const parentOption = this.closest('.style-option');
				parentOption.classList.add('selected');

				// หา radio button ที่เกี่ยวข้อง
				const radioId = this.getAttribute('for');
				const radioButton = document.getElementById(radioId);

				if (radioButton) {
					radioButton.checked = true;
					console.log('เลือกรูปแบบ: ' + radioButton.value);
				}
			});
		});
	}

	// ===== การตรวจสอบข้อมูลก่อนส่งฟอร์ม =====
	function validateForm() {
		// ตรวจสอบการเลือกรูปแบบการแต่งกาย
		const selectedStyle = document.querySelector('input[name="formality_type"]:checked');

		if (!selectedStyle) {
			// แสดงข้อความเตือนด้วย SweetAlert
			Swal.fire({
				icon: 'warning',
				title: 'กรุณาเลือกรูปแบบการแต่งกาย',
				text: 'กรุณาเลือกรูปแบบการแต่งกายก่อนดูการแนะนำ',
				confirmButtonText: 'ตกลง',
				confirmButtonColor: '#111827'
			});
			return false;
		}

		// ผ่านการตรวจสอบ
		console.log('ส่งข้อมูล - รูปแบบที่เลือก: ' + selectedStyle.value);
		return true;
	}

	// ===== การจัดการการส่งฟอร์ม =====
	function initializeFormSubmission() {
		const clothingForm = document.getElementById('clothing-form');

		if (clothingForm) {
			clothingForm.addEventListener('submit', function(event) {
				// ตรวจสอบข้อมูลก่อนส่ง
				if (!validateForm()) {
					event.preventDefault(); // หยุดการส่งฟอร์มถ้าข้อมูลไม่ครบ
					return;
				}

				// ถ้าผ่านการตรวจสอบให้ส่งฟอร์มไปยัง server
				console.log('ส่งฟอร์มไปยัง server');
			});
		}
	}

	// ===== การจัดการปุ่มล้างข้อมูล =====
	function initializeFormReset() {
		const resetButton = document.querySelector('button[type="reset"]');

		if (resetButton) {
			resetButton.addEventListener('click', function(event) {
				event.preventDefault(); // ป้องกันการ reset แบบปกติ

				// ยืนยันก่อนล้างข้อมูลด้วย SweetAlert
				Swal.fire({
					icon: 'question',
					title: 'ยืนยันการล้างข้อมูล?',
					text: 'คุณต้องการล้างการเลือกทั้งหมดหรือไม่?',
					showCancelButton: true,
					confirmButtonText: 'ยืนยัน',
					cancelButtonText: 'ยกเลิก',
					confirmButtonColor: '#111827',
					cancelButtonColor: '#6b7280'
				}).then(function(result) {
					if (result.isConfirmed) {
						resetAllSelections();
					}
				});
			});
		}
	}

	// ฟังก์ชันสำหรับล้างการเลือกทั้งหมด
	function resetAllSelections() {
		// ล้างการเลือกเสื้อผ้า
		const checkboxes = document.querySelectorAll('.clothing-checkbox');
		checkboxes.forEach(function(checkbox) {
			checkbox.checked = false;
			checkbox.closest('.clothing-item').classList.remove('selected');
		});

		// ล้างการเลือกรูปแบบการแต่งกาย
		const styleRadios = document.querySelectorAll('input[name="formality_type"]');
		styleRadios.forEach(function(radio) {
			radio.checked = false;
		});

		// ลบคลาส 'selected' จากตัวเลือกรูปแบบทั้งหมด
		const styleOptions = document.querySelectorAll('.style-option');
		styleOptions.forEach(function(option) {
			option.classList.remove('selected');
		});

		Swal.fire({
			icon: 'success',
			title: 'ล้างข้อมูลสำเร็จ',
			text: 'ล้างการเลือกทั้งหมดเรียบร้อยแล้ว',
			showConfirmButton: false,
			timer: 1500,
			timerProgressBar: true
		}).then(function() {
			window.location = "matchstyles?clear=all";
		});
	}

	// ===== เรียกใช้ฟังก์ชันทั้งหมดเมื่อเว็บไซต์โหลดเสร็จ =====
	initializeClothingSelection();
	initializeStyleSelection();
	initializeFormSubmission();
	initializeFormReset();

	console.log('โหลดระบบเสร็จสิ้น');
});