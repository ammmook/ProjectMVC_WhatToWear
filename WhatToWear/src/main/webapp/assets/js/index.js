document.addEventListener('DOMContentLoaded', function() {
    // Smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');
            const targetElement = document.querySelector(targetId);
            
            if (targetElement) {
                window.scrollTo({
                    top: targetElement.offsetTop - 70, // Offset for fixed header
                    behavior: 'smooth'
                });
            }
        });
    });
    
    // Animate elements when they enter the viewport
    const animateOnScroll = () => {
        const elements = document.querySelectorAll('.feature-card, .style-card, .tech-feature');
        
        elements.forEach(element => {
            const elementTop = element.getBoundingClientRect().top;
            const elementVisible = 150;
            
            if (elementTop < window.innerHeight - elementVisible) {
                element.classList.add('visible');
            }
        });
    };
    
    // Add visible class for CSS animations
    const animatedElements = document.querySelectorAll('.style-card, .feature-card, .tech-feature');
    animatedElements.forEach(element => {
        element.classList.add('fade-in-element');
    });
    
    // Initial check for elements in viewport
    animateOnScroll();
    
    // Add scroll event listener for animation
    window.addEventListener('scroll', animateOnScroll);
    
    // Add the active class to current nav link
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.nav-link');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPath || (currentPath === '/' && href === '/index.html')) {
            link.classList.add('active');
        }
    });
});