document.addEventListener('DOMContentLoaded', function() {
	// ========== ELEMENT SELECTORS ==========
	const form = document.querySelector('form');
	const usernameInput = document.querySelector('input[name="username"]');
	const emailInput = document.querySelector('input[name="email"]');
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

	// ตรวจสอบรูปแบบอีเมล
	const validateEmail = (email) => {
		const emailRegex = /^(?=.{5,60}$)[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;
		return emailRegex.test(email);
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

	// ตรวจสอบอีเมล
	if (emailInput) {
		emailInput.addEventListener('input', function() {
			if (this.value.trim() !== '' && validateEmail(this.value)) {
				clearError(this);
			}
		});

		emailInput.addEventListener('blur', function() {
			if (this.value.trim() !== '' && !validateEmail(this.value)) {
				showError(this, 'กรุณาป้อนอีเมลที่ถูกต้อง (ความยาว 5-60 ตัวอักษร)');
			}
		});
	}

	// ตรวจสอบรหัสผ่าน
	if (passwordInput) {
		passwordInput.addEventListener('input', function() {
			if (this.value.trim() !== '' && validatePassword(this.value)) {
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
			if (this.value.trim() !== '' && !validatePassword(this.value)) {
				showError(this, 'รหัสผ่านต้องประกอบด้วยตัวอักษรอังกฤษหรือตัวเลข (ความยาว 8-20 ตัวอักษร)');
			}
		});
	}

	// ตรวจสอบการยืนยันรหัสผ่าน
	if (confirmPasswordInput) {
		confirmPasswordInput.addEventListener('input', function() {
			if (this.value === passwordInput.value) {
				clearError(this);
			}
		});

		confirmPasswordInput.addEventListener('blur', function() {
			if (this.value.trim() !== '' && this.value !== passwordInput.value) {
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

			// ตรวจสอบอีเมล
			if (emailInput) {
				if (emailInput.value.trim() === '') {
					showError(emailInput, 'กรุณากรอกอีเมล');
					isValid = false;
				} else if (!validateEmail(emailInput.value)) {
					showError(emailInput, 'กรุณาป้อนอีเมลที่ถูกต้อง (ความยาว 5-60 ตัวอักษร)');
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

			// ตรวจสอบรหัสผ่าน
			if (passwordInput) {
				if (passwordInput.value.trim() === '') {
					showError(passwordInput, 'กรุณากรอกรหัสผ่าน');
					isValid = false;
				} else if (!validatePassword(passwordInput.value)) {
					showError(passwordInput, 'รหัสผ่านต้องประกอบด้วยตัวอักษรอังกฤษหรือตัวเลข (ความยาว 8-20 ตัวอักษร)');
					isValid = false;
				}
			}

			// ตรวจสอบยืนยันรหัสผ่าน
			if (confirmPasswordInput) {
				if (confirmPasswordInput.value.trim() === '') {
					showError(confirmPasswordInput, 'กรุณายืนยันรหัสผ่าน');
					isValid = false;
				} else if (confirmPasswordInput.value !== passwordInput.value) {
					showError(confirmPasswordInput, 'รหัสผ่านไม่ตรงกัน');
					isValid = false;
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
	console.log("URL Parameters:", window.location.search);
	console.log("login parameter:", urlParams.get('login'));
	console.log("register parameter:", urlParams.get('register'));

	// แจ้งเตือนการลงทะเบียนสำเร็จ
	if (urlParams.get('register') === 'success') {
		Swal.fire({
			icon: 'success',
			title: 'สำเร็จ!',
			text: 'ลงทะเบียนเสร็จสมบูรณ์',
			showConfirmButton: false,
			allowOutsideClick: true,
			timer: 2000,
			timerProgressBar: true
		});
	}
});