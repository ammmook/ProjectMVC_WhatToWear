document.addEventListener('DOMContentLoaded', function() {
	// ========== ELEMENT SELECTORS ==========
	const form = document.querySelector('form');
	const usernameInput = document.querySelector('input[name="username"]');
	const passwordInput = document.querySelector('input[name="pwd"]');
	const confirmPasswordInput = document.querySelector('input[name="confirmpwd"]');
	const genderInputs = document.querySelectorAll('input[name="gender"]');
	const passwordToggles = document.querySelectorAll('.password-toggle');
	const genderOptions = document.querySelectorAll('.gender-option');

	// แสดงข้อความผิดพลาด
	const showError = (element, message) => {
		// ลบข้อความผิดพลาดเดิม (ถ้ามี)
		const parent = element.closest('.form-group');
		const existingError = parent.querySelector('.error-message');
		if (existingError) {
			existingError.remove();
		}

		// เพิ่ม class invalid ให้ input
		element.classList.add('invalid');

		// สร้างข้อความผิดพลาด
		const errorElement = document.createElement('small');
		errorElement.className = 'error-message';
		errorElement.innerHTML = `<i class="fi fi-rr-exclamation error-icon"></i> ${message}`;

		// หาตำแหน่งเพื่อแสดงข้อความ
		const inputContainer = element.closest('.input-with-icon') || element.closest('.password-field');
		if (inputContainer) {
			inputContainer.after(errorElement);
		} else {
			parent.appendChild(errorElement);
		}
	};

	// ลบข้อความผิดพลาด
	const clearError = (element) => {
		const parent = element.closest('.form-group');
		const errorMsg = parent.querySelector('.error-message');
		if (errorMsg) {
			errorMsg.remove();
		}
		element.classList.remove('invalid');
	};

	// ตรวจสอบชื่อผู้ใช้
	const validateUsername = (username) => {
		const usernameRegex = /^[A-Za-z0-9 ]{5,20}$/;
		return usernameRegex.test(username);
	};

	// ตรวจสอบรหัสผ่าน
	const validatePassword = (pwd) => {
		const pwdRegex = /^(?=(.*[A-Za-z0-9]))[A-Za-z0-9!#_.]{8,20}$/;
		return pwdRegex.test(pwd);
	};

	// ========== PASSWORD VISIBILITY TOGGLE ==========
	if (passwordToggles.length > 0) {
		passwordToggles.forEach(toggle => {
			toggle.addEventListener('click', function() {
				const input = this.previousElementSibling;
				const isPassword = input.type === 'password';

				// สลับประเภท input
				input.type = isPassword ? 'text' : 'password';

				// เปลี่ยนไอคอน
				this.classList.toggle('fi-rr-eye', !isPassword);
				this.classList.toggle('fi-rr-eye-crossed', isPassword);
			});
		});
	}

	// ========== GENDER SELECTION ==========
	if (genderOptions.length > 0) {
		genderOptions.forEach(option => {
			option.addEventListener('click', function() {
				// ลบ class selected จากทั้งหมด
				genderOptions.forEach(opt => opt.classList.remove('selected'));

				// เพิ่ม class ให้ตัวที่เลือก
				this.classList.add('selected');
				this.querySelector('input').checked = true;

				// ลบข้อความผิดพลาด
				const genderGroup = document.querySelector('.gender-group');
				const errorMsg = genderGroup.parentElement.querySelector('.error-message');
				if (errorMsg) {
					errorMsg.remove();
				}

				// ลบ class invalid จากทุกตัวเลือก
				genderOptions.forEach(opt => opt.classList.remove('invalid'));
			});
		});
	}

	// ========== INPUT VALIDATION ==========

	// ตรวจสอบชื่อผู้ใช้
	if (usernameInput) {
		usernameInput.addEventListener('input', function() {
			if (this.value.trim() !== '' && validateUsername(this.value)) {
				clearError(this);
			}
		});

		usernameInput.addEventListener('blur', function() {
			if (this.value.trim() !== '' && !validateUsername(this.value)) {
				showError(this, 'กรุณากรอกชื่อผู้ใช้ให้ถูกต้อง (ความยาว 5-20 ตัวอักษร)');
			}
		});
	}

	// ตรวจสอบรหัสผ่าน (ถ้ามีการกรอก)
	if (passwordInput) {
		passwordInput.addEventListener('input', function() {
			// ถ้าช่องรหัสผ่านว่างเปล่า ให้ล้างข้อความผิดพลาด เพราะเป็นฟิลด์ optional
			if (this.value.trim() === '') {
				clearError(this);
				// ล้างข้อความผิดพลาดจากช่องยืนยันรหัสผ่านด้วย
				if (confirmPasswordInput) {
					clearError(confirmPasswordInput);
				}
				return;
			}
			
			// ถ้ามีการกรอกรหัสผ่าน ให้ตรวจสอบความถูกต้อง
			if (validatePassword(this.value)) {
				clearError(this);

				// ตรวจสอบการยืนยันรหัสผ่านหากมีการกรอกแล้ว
				if (confirmPasswordInput && confirmPasswordInput.value.trim() !== '') {
					if (confirmPasswordInput.value === this.value) {
						clearError(confirmPasswordInput);
					} else {
						showError(confirmPasswordInput, 'รหัสผ่านไม่ตรงกัน');
					}
				}
			}
		});

		passwordInput.addEventListener('blur', function() {
			// ถ้าช่องรหัสผ่านไม่ว่างเปล่า ให้ตรวจสอบรูปแบบ
			if (this.value.trim() !== '' && !validatePassword(this.value)) {
				showError(this, 'รหัสผ่านต้องประกอบด้วยตัวอักษรอังกฤษหรือตัวเลข (ความยาว 8-20 ตัวอักษร)');
			}
		});
	}

	// ตรวจสอบการยืนยันรหัสผ่าน (ถ้ามีการกรอก)
	if (confirmPasswordInput) {
		confirmPasswordInput.addEventListener('input', function() {
			// ถ้าช่องยืนยันรหัสผ่านว่างเปล่า และช่องรหัสผ่านก็ว่างเปล่า
			if (this.value.trim() === '' && passwordInput.value.trim() === '') {
				clearError(this);
				return;
			}
			
			// ถ้าช่องยืนยันรหัสผ่านไม่ว่างเปล่า แต่ช่องรหัสผ่านว่างเปล่า
			if (this.value.trim() !== '' && passwordInput.value.trim() === '') {
				showError(this, 'กรุณากรอกรหัสผ่านใหม่ก่อน');
				return;
			}
			
			// ตรวจสอบว่ารหัสผ่านตรงกันหรือไม่
			if (this.value === passwordInput.value) {
				clearError(this);
			} else {
				showError(this, 'รหัสผ่านไม่ตรงกัน');
			}
		});

		confirmPasswordInput.addEventListener('blur', function() {
			// ถ้าทั้งสองช่องว่างเปล่า ไม่ต้องแสดงข้อความผิดพลาด
			if (this.value.trim() === '' && passwordInput.value.trim() === '') {
				clearError(this);
				return;
			}
			
			// ถ้าช่องยืนยันรหัสผ่านไม่ว่างเปล่า และช่องรหัสผ่านก็ไม่ว่างเปล่า แต่ไม่ตรงกัน
			if (this.value.trim() !== '' && passwordInput.value.trim() !== '' && this.value !== passwordInput.value) {
				showError(this, 'รหัสผ่านไม่ตรงกัน');
			}
		});
	}

	// ========== FORM SUBMISSION ==========
	if (form) {
		form.addEventListener('submit', function(event) {
			let isValid = true;

			// ตรวจสอบชื่อผู้ใช้
			if (usernameInput) {
				if (usernameInput.value.trim() === '') {
					showError(usernameInput, 'กรุณากรอกชื่อผู้ใช้');
					isValid = false;
				} else if (!validateUsername(usernameInput.value)) {
					showError(usernameInput, 'กรุณากรอกชื่อผู้ใช้ให้ถูกต้อง (ความยาว 5-20 ตัวอักษร)');
					isValid = false;
				}
			}

			// ตรวจสอบเพศ
			if (genderInputs.length > 0) {
				let genderSelected = Array.from(genderInputs).some(input => input.checked);
				if (!genderSelected) {
					// ทำให้กรอบเป็นสีแดง
					genderOptions.forEach(option => option.classList.add('invalid'));

					// แสดงข้อความผิดพลาด
					const genderGroup = document.querySelector('.gender-group');
					const errorElement = document.createElement('small');
					errorElement.className = 'error-message';
					errorElement.innerHTML = '<i class="fi fi-rr-exclamation error-icon"></i> กรุณาเลือกเพศ';
					genderGroup.parentElement.appendChild(errorElement);

					isValid = false;
				}
			}

			// ตรวจสอบรหัสผ่าน (เฉพาะเมื่อมีการกรอก)
			const passwordEntered = passwordInput && passwordInput.value.trim() !== '';
			
			if (passwordEntered) {
				if (!validatePassword(passwordInput.value)) {
					showError(passwordInput, 'รหัสผ่านต้องประกอบด้วยตัวอักษรอังกฤษหรือตัวเลข (ความยาว 8-20 ตัวอักษร)');
					isValid = false;
				}
				
				// ตรวจสอบยืนยันรหัสผ่าน (เฉพาะเมื่อมีการกรอกรหัสผ่าน)
				if (confirmPasswordInput) {
					if (confirmPasswordInput.value.trim() === '') {
						showError(confirmPasswordInput, 'กรุณายืนยันรหัสผ่าน');
						isValid = false;
					} else if (confirmPasswordInput.value !== passwordInput.value) {
						showError(confirmPasswordInput, 'รหัสผ่านไม่ตรงกัน');
						isValid = false;
					}
				}
			}

			// ถ้าฟอร์มไม่ถูกต้อง ยกเลิกการส่งฟอร์ม
			if (!isValid) {
				event.preventDefault();

				// เลื่อนไปยังข้อผิดพลาดแรก
				const firstError = document.querySelector('.error-message');
				if (firstError) {
					firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
				}
			}
		});
	}

	// ========== SWEET ALERT NOTIFICATIONS ==========
	// ตรวจสอบ URL parameters สำหรับข้อความแจ้งเตือน
	const urlParams = new URLSearchParams(window.location.search);

	// แจ้งเตือนการอัปเดตโปรไฟล์สำเร็จ
	if (urlParams.get('update') === 'success') {
		Swal.fire({
			icon: 'success',
			title: 'สำเร็จ!',
			text: 'อัปเดตข้อมูลส่วนตัวเรียบร้อย',
			showConfirmButton: false,
			allowOutsideClick: true,
			timer: 2000,
			timerProgressBar: true
		});
	}
	
	// ตั้งค่าเพศที่เลือกไว้
	const setSelectedGender = () => {
		genderInputs.forEach(input => {
			if (input.checked) {
				const option = input.closest('.gender-option');
				if (option) {
					option.classList.add('selected');
				}
			}
		});
	};
	
	// เรียกใช้งานฟังก์ชั่นตั้งค่าเพศที่เลือกไว้
	setSelectedGender();
});