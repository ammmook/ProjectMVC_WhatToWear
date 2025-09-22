// DOM Elements
const imageInput = document.getElementById('clothing-image');
const uploadArea = document.getElementById('imageUploadArea');
const uploadPlaceholder = document.getElementById('uploadPlaceholder');
const uploadedImage = document.getElementById('uploadedImage');
const form = document.getElementById('clothingForm');
const submitBtn = document.getElementById('submitBtn');
const cancelBtn = document.getElementById('cancelBtn');
// Form fields
const subcategorySelect = document.getElementById('subcategory');
const patternGroup = document.getElementById('patternGroup');
const formalitytypeSelect = document.getElementById('formalitytype');
// Error elements
const imageError = document.getElementById('imageError');
const subcategoryError = document.getElementById('subcategoryError');
const patternError = document.getElementById('patternError');
const formalitytypeError = document.getElementById('formalitytypeError');

// --- ส่วนตรวจสอบเพศของผู้ใช้เมื่อเลือกหมวดหมู่ ---

function checkGenderAndSelection(event) {
	const selectedSubcategoryId = event.target.value;
	const selectedOptionText = event.target.options[event.target.selectedIndex].text;

	if (userGender === '1' && (selectedSubcategoryId === 'SC008' || selectedSubcategoryId === 'SC009')) {

		setTimeout(() => {
			Swal.fire({
				title: 'ยืนยันการเลือกเสื้อผ้า',
				text: `คุณเป็นผู้ชายและได้เลือก "${selectedOptionText}" คุณต้องการดำเนินการต่อใช่ไหม?`,
				icon: 'question',
				showCancelButton: true,
				confirmButtonText: 'ยืนยันการเลือก',
				cancelButtonText: 'ยกเลิกการเลือก',
				confirmButtonColor: '#3085d6',
				reverseButtons: true
			}).then((result) => {
				if (!result.isConfirmed) {
					event.target.value = "";
					console.log('User cancelled the selection.');
				}
			});
		}, 0);
	}
}

subcategorySelect.addEventListener('change', checkGenderAndSelection);

function createMappingFromDOM() {
	// สร้าง mapping จากข้อมูลที่มีใน DOM
	window.subcategoryMapping = {};
	window.formalityMapping = {};

	console.log('🗺️ Creating mapping from DOM data...');

	// SubCategory mapping
	const subcategoryOptions = Array.from(subcategorySelect.options);
	subcategoryOptions.forEach(option => {
		if (option.value.trim() !== '') {
			const dbValue = option.value.trim();
			const displayName = option.textContent.trim().toLowerCase();

			let apiValue = '';
			if (displayName.includes('เสื้อเชิ้ต') || displayName.includes('shirt')) apiValue = 'SC001';
			else if (displayName.includes('โปโล') || displayName.includes('polo')) apiValue = 'SC002';
			else if (displayName.includes('เสื้อยืด') || displayName.includes('t-shirt')) apiValue = 'SC003';
			else if (displayName.includes('แขนยาว') || displayName.includes('longsleeve')) apiValue = 'SC004';
			else if (displayName.includes('กางเกงขายาว') || displayName.includes('pants')) apiValue = 'SC005';
			else if (displayName.includes('ยีนส์') || displayName.includes('jeans')) apiValue = 'SC006';
			else if (displayName.includes('กางเกงขาสั้น') || displayName.includes('shorts')) apiValue = 'SC007';
			else if (displayName.includes('กระโปรง') || displayName.includes('skirt')) apiValue = 'SC008';
			else if (displayName.includes('เดรส') || displayName.includes('dress')) apiValue = 'SC009';
			else if (displayName.includes('เบลเซอร์') || displayName.includes('blazer')) apiValue = 'SC010';
			else if (displayName.includes('โค้ท') || displayName.includes('coat')) apiValue = 'SC011';
			else if (displayName.includes('แจ็คเก็ต') || displayName.includes('jacket')) apiValue = 'SC012';
			else if (displayName.includes('ฮู้ด') || displayName.includes('hoodie')) apiValue = 'SC013';
			else if (displayName.includes('สเวตเตอร์') || displayName.includes('sweater')) apiValue = 'SC014';

			if (apiValue) {
				window.subcategoryMapping[apiValue] = dbValue;
			} else {
				window.subcategoryMapping[dbValue] = dbValue;
			}
		}
	});

	// Formality mapping
	const formalityOptions = Array.from(formalitytypeSelect.options);
	formalityOptions.forEach(option => {
		if (option.value.trim() !== '') {
			const dbValue = option.value.trim();
			const displayName = option.textContent.trim().toLowerCase();

			let apiValue = '';
			if (displayName.includes('ทางการ') && !displayName.includes('กึ่ง')) apiValue = 'T01';
			else if (displayName.includes('กึ่งทางการ') || displayName.includes('semi')) apiValue = 'T02';
			else if (displayName.includes('สบาย') || displayName.includes('casual')) apiValue = 'T03';

			if (apiValue) {
				window.formalityMapping[apiValue] = dbValue;
			} else {
				window.formalityMapping[dbValue] = dbValue;
			}
		}
	});

	console.log('🗺️ Mapping created:', {
		subcategory: window.subcategoryMapping,
		formality: window.formalityMapping
	});
}

// Validation Functions
function validateImage() {
	const file = imageInput.files[0];
	if (!file) {
		showError(imageError, 'กรุณาเลือกรูปภาพเสื้อผ้า');
		uploadArea.classList.add('error');
		return false;
	}

	if (!['image/png', 'image/jpeg', 'image/jpg'].includes(file.type)) {
		showError(imageError, 'กรุณาเลือกไฟล์รูปภาพ PNG หรือ JPG เท่านั้น');
		uploadArea.classList.add('error');
		return false;
	}

	if (file.size > 5 * 1024 * 1024) { // 5MB
		showError(imageError, 'ขนาดไฟล์ต้องไม่เกิน 5MB');
		uploadArea.classList.add('error');
		return false;
	}

	hideError(imageError);
	uploadArea.classList.remove('error');
	return true;
}

function validateSubcategory() {
	if (!subcategorySelect.value) {
		showError(subcategoryError, 'กรุณาเลือกหมวดหมู่เสื้อผ้า');
		subcategorySelect.classList.add('error');
		return false;
	}
	hideError(subcategoryError);
	subcategorySelect.classList.remove('error');
	return true;
}

function validatePattern() {
	const patternInputs = document.querySelectorAll('input[name="pattern"]');
	const isChecked = Array.from(patternInputs).some(input => input.checked);

	if (!isChecked) {
		showError(patternError, 'กรุณาเลือกลวดลายของเสื้อผ้า');
		patternGroup.classList.add('error');
		return false;
	}
	hideError(patternError);
	patternGroup.classList.remove('error');
	return true;
}

function validateFormalityType() {
	if (!formalitytypeSelect.value) {
		showError(formalitytypeError, 'กรุณาเลือกความเป็นทางการของเสื้อผ้า');
		formalitytypeSelect.classList.add('error');
		return false;
	}
	hideError(formalitytypeError);
	formalitytypeSelect.classList.remove('error');
	return true;
}

function validateForm() {
	const isImageValid = validateImage();
	const isSubcategoryValid = validateSubcategory();
	const isPatternValid = validatePattern();
	const isFormalityTypeValid = validateFormalityType();

	return isImageValid && isSubcategoryValid && isPatternValid && isFormalityTypeValid;
}

// Error Display Functions
function showError(errorElement, message) {
	errorElement.textContent = message;
	errorElement.classList.add('show');
}

function hideError(errorElement) {
	errorElement.classList.remove('show');
}

// Image Upload Functions with AI Integration
function displayImage(file) {
	const reader = new FileReader();
	reader.onload = function(e) {
		uploadedImage.src = e.target.result;
		uploadedImage.classList.add('visible');
		uploadPlaceholder.classList.add('hidden');
		uploadArea.classList.add('has-image');
		uploadArea.classList.remove('error');
		hideError(imageError);

		// Call AI analysis after image is displayed
		analyzeClothingImage(file);
	};
	reader.readAsDataURL(file);
}

async function analyzeClothingImage(file) {
	try {
		console.log('🚀 Starting AI analysis for file:', file.name);
		showAnalysisLoading();
		const formData = new FormData();
		formData.append('imagefile', file);

		const response = await fetch('analyzeClothing', {
			method: 'POST',
			body: formData
		});

		if (!response.ok) {
			throw new Error(`Controller request failed with status: ${response.status}`);
		}

		const result = await response.json();

		if (result.success) {
			console.log('✅ Analysis successful, updating form...');
			updateFormWithPredictions(result);
		} else {
			throw new Error(result.error || 'Analysis failed');
		}

	} catch (error) {
		console.error('❌ AI Analysis Error:', error);
		showAnalysisError();
	} finally {
		hideAnalysisLoading();
	}
}

// แก้ไขฟังก์ชันนี้เท่านั้น
function updateFormWithPredictions(predictions) {
	if (!window.subcategoryMapping || !window.formalityMapping) {
		createMappingFromDOM();
	}

	const formalityId = predictions.formality_id || predictions.status_id;
	let updatedFields = [];
	let failedFields = [];

	// 1. Update subcategory
	if (predictions.subject_id) {
		const dbSubcategoryValue = window.subcategoryMapping[predictions.subject_id] || predictions.subject_id;
		let subcategoryFound = false;
		for (let option of subcategorySelect.options) {
			if (option.value.trim() === dbSubcategoryValue.trim()) {
				option.selected = true;
				subcategoryFound = true;
				updatedFields.push('subcategory');
				subcategorySelect.classList.add('ai-updated');
				setTimeout(() => subcategorySelect.classList.remove('ai-updated'), 2000);
				validateSubcategory();

				// START: เพิ่มโค้ดตรวจสอบเพศหลังจาก AI เลือกให้
				const predictedSubcategoryId = predictions.subject_id;

				if (userGender === '1' && (predictedSubcategoryId === 'SC008' || predictedSubcategoryId === 'SC009')) {
					// ใช้ Timeout เล็กน้อยเพื่อให้ UI อัปเดตก่อนแสดง Alert
					setTimeout(() => {
						const selectedOptionText = subcategorySelect.options[subcategorySelect.selectedIndex].text;
						Swal.fire({
							title: 'ยืนยันการเลือกเสื้อผ้า',
							text: `AI ได้เลือก "${selectedOptionText}" แต่คุณเป็นผู้ชาย คุณต้องการดำเนินการต่อใช่ไหม?`,
							icon: 'question',
							showCancelButton: true,
							confirmButtonText: 'ยืนยันการเลือก',
							cancelButtonText: 'ยกเลิกการเลือก',
							confirmButtonColor: '#3085d6',
							reverseButtons: true
						}).then((result) => {
							if (!result.isConfirmed) {
								subcategorySelect.selectedIndex = 0; // รีเซ็ตกลับไปที่ "-- เลือกหมวดหมู่ --"
								console.log('User cancelled the AI-selected gender-specific item.');
								validateSubcategory(); // ตรวจสอบความถูกต้องอีกครั้งหลังรีเซ็ต
							}
						});
					}, 100); // หน่วงเวลา 0.1 วินาที
				}
				// END: สิ้นสุดโค้ดตรวจสอบเพศ

				break;
			}
		}
		if (!subcategoryFound) failedFields.push(`subcategory (API: ${predictions.subject_id})`);
	}

	// 2. Update pattern
	if (predictions.pattern !== undefined) {
		const patternValue = predictions.pattern.toString();
		const radio = document.querySelector(`input[name="pattern"][value="${patternValue}"]`);
		if (radio) {
			radio.checked = true;
			updatedFields.push('pattern');
			patternGroup.classList.add('ai-updated');
			setTimeout(() => patternGroup.classList.remove('ai-updated'), 2000);
			validatePattern();
		} else {
			failedFields.push(`pattern (${patternValue})`);
		}
	}

	// 3. Update formality type
	if (formalityId) {
		const dbFormalityValue = window.formalityMapping[formalityId] || formalityId;
		let formalityFound = false;
		for (let option of formalitytypeSelect.options) {
			if (option.value.trim() === dbFormalityValue.trim()) {
				option.selected = true;
				formalityFound = true;
				updatedFields.push('formality');
				formalitytypeSelect.classList.add('ai-updated');
				setTimeout(() => formalitytypeSelect.classList.remove('ai-updated'), 2000);
				validateFormalityType();
				break;
			}
		}
		if (!formalityFound) failedFields.push(`formality (API: ${formalityId})`);
	}

	// Show result notification
	if (updatedFields.length > 0 && failedFields.length === 0) {
		showSuccessNotification();
	} else if (updatedFields.length > 0 && failedFields.length > 0) {
		showPartialSuccessNotification(updatedFields, failedFields);
	} else {
		showAnalysisError();
	}
}

function showPartialSuccessNotification(updated, failed) {
	Swal.fire({
		icon: 'warning',
		title: 'วิเคราะห์สำเร็จบางส่วน',
		html: `<p><strong>อัปเดตสำเร็จ:</strong> ${updated.join(', ')}</p><p><strong>ไม่พบข้อมูล:</strong> ${failed.join(', ')}</p><p><small>กรุณาตรวจสอบและเลือกข้อมูลที่เหลือด้วยตนเอง</small></p>`,
		confirmButtonText: 'ตกลง',
	});
}

function showAnalysisLoading() {
	const loadingOverlay = document.createElement('div');
	loadingOverlay.id = 'aiLoadingOverlay';
	loadingOverlay.className = 'ai-loading-overlay';
	loadingOverlay.innerHTML = `<div class="loading-content"><div class="spinner"></div><p>กำลังวิเคราะห์รูปภาพด้วย AI...</p></div>`;
	const formSection = document.querySelector('.form-section');
	formSection.style.position = 'relative';
	formSection.appendChild(loadingOverlay);
	document.querySelectorAll('.form-section .form-select, .form-section input[type="radio"]').forEach(el => el.disabled = true);
}

function hideAnalysisLoading() {
	const loadingOverlay = document.getElementById('aiLoadingOverlay');
	if (loadingOverlay) loadingOverlay.remove();
	document.querySelectorAll('.form-section .form-select, .form-section input[type="radio"]').forEach(el => el.disabled = false);
}

function showSuccessNotification() {
	Swal.fire({
		icon: 'success',
		title: 'วิเคราะห์สำเร็จ!',
		text: 'ระบบได้เติมข้อมูลให้แล้ว คุณสามารถแก้ไขได้หากต้องการ',
		toast: true,
		position: 'top-end',
		showConfirmButton: false,
		timer: 2500,
		timerProgressBar: true
	});
}

function showAnalysisError() {
	Swal.fire({
		icon: 'warning',
		title: 'ไม่สามารถวิเคราะห์รูปภาพได้',
		text: 'กรุณาเลือกข้อมูลด้วยตนเอง หรือลองอัปโหลดรูปภาพใหม่',
		toast: true,
		position: 'top-end',
		showConfirmButton: false,
		timer: 3000
	});
}

function resetImageUpload() {
	imageInput.value = '';
	uploadedImage.src = '';
	uploadedImage.classList.remove('visible');
	uploadPlaceholder.classList.remove('hidden');
	uploadArea.classList.remove('has-image', 'error');
	hideError(imageError);
}

// Event Listeners
imageInput.addEventListener('change', function(e) {
	const file = e.target.files[0];
	if (file) {
		if (validateImage()) {
			displayImage(file);
		} else {
			imageInput.value = '';
		}
	}
});

// Real-time validation
subcategorySelect.addEventListener('change', validateSubcategory);
formalitytypeSelect.addEventListener('change', validateFormalityType);
document.querySelectorAll('input[name="pattern"]').forEach(input => {
	input.addEventListener('change', validatePattern);
});

// Drag and Drop
['dragover', 'dragleave', 'drop'].forEach(eventName => {
	uploadArea.addEventListener(eventName, e => {
		e.preventDefault();
		e.stopPropagation();
	});
});
uploadArea.addEventListener('dragover', () => uploadArea.classList.add('dragover'));
uploadArea.addEventListener('dragleave', () => uploadArea.classList.remove('dragover'));
uploadArea.addEventListener('drop', e => {
	uploadArea.classList.remove('dragover');
	const files = e.dataTransfer.files;
	if (files.length > 0) {
		imageInput.files = files;
		const event = new Event('change');
		imageInput.dispatchEvent(event);
	}
});

// Form Submission
form.addEventListener('submit', function(e) {
	e.preventDefault();
	if (!validateForm()) {
		Swal.fire({
			icon: 'error',
			title: 'ข้อมูลไม่ครบถ้วน!',
			text: 'กรุณากรอกข้อมูลให้ครบถ้วนและถูกต้อง',
			confirmButtonText: 'ตกลง'
		});
		return;
	}
	submitBtn.disabled = true;
	submitBtn.textContent = 'กำลังบันทึก...';
	form.submit();
});

// Cancel button
cancelBtn.addEventListener('click', function() {
	const hasData = uploadedImage.classList.contains('visible') ||
		subcategorySelect.value ||
		formalitytypeSelect.value ||
		document.querySelector('input[name="pattern"]:checked');

	if (hasData) {
		Swal.fire({
			title: 'ยืนยันการยกเลิก',
			text: 'คุณต้องการยกเลิกและล้างข้อมูลทั้งหมดหรือไม่?',
			icon: 'question',
			showCancelButton: true,
			confirmButtonText: 'ยืนยัน',
			cancelButtonText: 'ไม่',
			confirmButtonColor: '#dc3545'
		}).then((result) => {
			if (result.isConfirmed) {
				resetForm();
			}
		});
	} else {
		resetForm();
	}
});

function resetForm() {
	form.reset();
	resetImageUpload();
	document.querySelectorAll('.error-message.show').forEach(e => hideError(e));
	document.querySelectorAll('.error').forEach(e => e.classList.remove('error'));
	submitBtn.disabled = false;
	submitBtn.textContent = 'บันทึก';
}

// Initialize mapping when page loads
document.addEventListener('DOMContentLoaded', createMappingFromDOM);