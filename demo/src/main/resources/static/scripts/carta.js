document.addEventListener("DOMContentLoaded", () => {
    function initCarrusel() {
        const wrapper = document.querySelector('.cards-wrapper');
        // Usamos solo las tarjetas originales
        const cards = document.querySelectorAll('.cards-wrapper .card'); 
        const prevBtn = document.querySelector('.carousel-btn.prev');
        const nextBtn = document.querySelector('.carousel-btn.next');

        let index = 0; // El índice siempre empieza en cero

        function getVisibleCards() {
            const width = window.innerWidth;
            if (width <= 576) return 1;
            if (width <= 992) return 2;
            if (width <= 1200) return 3;
            return 4;
        }

        function updateTransform() {
            const visible = getVisibleCards();
            // Calcula el ancho de las tarjetas visibles y aplica la traslación
            wrapper.style.transform = `translateX(-${(100 / visible) * index}%)`;
        }

        // 💡 LÓGICA CORREGIDA PARA EL BOTÓN "SIGUIENTE" (NEXT)
        nextBtn.addEventListener('click', () => {
            const visible = getVisibleCards();
            
            // Si NO estamos en el último set de tarjetas, avanzamos
            if (index < cards.length - visible) {
                index++;
            } else {
                // Si estamos en el final, volvemos a la primera tarjeta
                index = 0;
            }
            updateTransform();
        });

        // LÓGICA PARA EL BOTÓN "ANTERIOR" (PREV)
        prevBtn.addEventListener('click', () => {
            if (index > 0) {
                index--;
                updateTransform();
            }
            // NOTA: Si quisieras que el botón PREV salte al final desde el inicio,
            // la lógica sería: else { index = cards.length - getVisibleCards(); }
        });

        window.addEventListener('resize', updateTransform);
        
        // Ejecutamos la transformación inicial
        updateTransform(); 

        // --- Animación scroll (IntersectionObserver) ---
        const observer = new IntersectionObserver(
            (entries) => {
                entries.forEach((entry) => {
                    // Usamos 'cards' (la lista de tarjetas originales) para encontrar el índice
                    const i = [...cards].indexOf(entry.target); 

                    if (entry.isIntersecting) {
                        // Aplica la animación con un pequeño retraso
                        setTimeout(() => entry.target.classList.add("visible"), i * 150);
                    } else {
                        entry.target.classList.remove("visible");
                    }
                });
            },
            { threshold: 0.2 }
        );

        // Observamos solo las tarjetas originales
        cards.forEach((card) => observer.observe(card));
    }

    initCarrusel();
});