document.addEventListener('DOMContentLoaded', function() {
	const hamburger = document.querySelector('.hamburger');
	const navMenu = document.querySelector('.nav-menu');

	hamburger.addEventListener('click', function() {
		hamburger.classList.toggle('active');
		navMenu.classList.toggle('active');
	});

	// Close mobile menu when clicking on a nav link
	document.querySelectorAll('.nav-link').forEach(link => {
		link.addEventListener('click', () => {
			hamburger.classList.remove('active');
			navMenu.classList.remove('active');
		});
	});
});

// Optional: Add this to handle active state for current page
const currentLocation = location.href;
const menuItems = document.querySelectorAll('.nav-link');
menuItems.forEach(link => {
	if (link.href === currentLocation) {
		link.classList.add('active');
	}
});

document.addEventListener('DOMContentLoaded', function() {
    const logoutButton = document.querySelector('.dropdown-item.logout');
    if (logoutButton) {
        logoutButton.addEventListener('click', function(event) {
            // Prevent the default action (following the link)
            event.preventDefault();

            // Store the href for later use
            const logoutUrl = this.getAttribute('href');
            Swal.fire({
                title: 'ออกจากระบบ?',
                text: 'คุณแน่ใจหรือไม่ว่าต้องการออกจากระบบ?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                confirmButtonText: 'ใช่, ออกจากระบบ!',
                cancelButtonText: 'ยกเลิก',
                customClass: {
                    title: 'sweet-alert-title',
                    content: 'sweet-alert-content'
                },
                animation: true
            }).then((result) => {
                if (result.isConfirmed) {
                    Swal.fire({
                        title: 'สำเร็จ!',
                        text: 'กำลังออกจากระบบ...',
                        icon: 'success',
                        timer: 2000,
                        showConfirmButton: false,
						timerProgressBar: true,
                        didClose: () => {
                            window.location.href = logoutUrl;
                        }
                    });
                }
            });
        });
    }
});