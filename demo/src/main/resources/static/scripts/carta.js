document.addEventListener("DOMContentLoaded", () => {

    // ... (createCardsFromData function remains the same) ...
    function createCardsFromData(platos) {
        const cardsWrapper = document.querySelector('.cards-wrapper');
        if (!cardsWrapper) return;
        cardsWrapper.innerHTML = ''; 
        platos.forEach(plato => {
            const cardHtml = `
                <div class="card">
                    <div class="image-wrapper">
                        <img src="${plato.imagenRuta}" alt="${plato.nombre}">
                    </div>
                    <div class="card-body">
                        <h5 class="card-title">${plato.nombre}</h5>
                        <p class="card-text">${plato.descripcion}</p>
                        <p class="card-price">Precio: S/${plato.precio}</p> 
                    </div>
                </div>
            `;
            cardsWrapper.insertAdjacentHTML('beforeend', cardHtml);
        });
    }
    
    // ... (Initial data loading and createCardsFromData call) ...
    if (window.platosData && window.platosData.length > 0) {
        createCardsFromData(window.platosData); 
    } else {
        console.warn("No se encontraron datos de platos para cargar el carrusel.");
    }

    function initCarrusel() {
        const wrapper = document.querySelector('.cards-wrapper');
        const cards = document.querySelectorAll('.cards-wrapper .card'); 
        
        if (cards.length === 0) {
            return;
        }

        const prevBtn = document.querySelector('.carousel-btn.prev');
        const nextBtn = document.querySelector('.carousel-btn.next');

        let index = 0;

        function getVisibleCards() {
            const width = window.innerWidth;
            if (width <= 576) return 1;
            if (width <= 992) return 2;
            if (width <= 1200) return 3;
            return 4;
        }

        function getMaxIndex() {
             const visible = getVisibleCards();
             return Math.max(0, cards.length - visible);
        }

        function updateButtonVisibility() {
            const maxIndex = getMaxIndex(); 
            
            if (index <= 0) {
                prevBtn.disabled = true;
                prevBtn.style.opacity = '0.5';
                prevBtn.style.cursor = 'default';
            } else {
                prevBtn.disabled = false;
                prevBtn.style.opacity = '1';
                prevBtn.style.cursor = 'pointer';
            }

            if (index >= maxIndex) { 
                nextBtn.disabled = true;
                nextBtn.style.opacity = '0.5';
                nextBtn.style.cursor = 'default';
            } else {
                nextBtn.disabled = false;
                nextBtn.style.opacity = '1';
                nextBtn.style.cursor = 'pointer';
            }
        }

        // 💡 FUNCIÓN MODIFICADA: Calcula el desplazamiento por píxeles
        function updateTransform() {
            const visible = getVisibleCards();
            
            // Si hay tarjetas, usa el ancho de la primera tarjeta como referencia.
            // Esto incluye el margin, si lo tiene.
            let cardWidth = 0;
            if (cards.length > 0) {
                // Obtenemos el ancho real de la tarjeta, incluyendo márgenes.
                const firstCard = cards[0];
                const cardStyle = getComputedStyle(firstCard);
                const marginLeft = parseFloat(cardStyle.marginLeft);
                const marginRight = parseFloat(cardStyle.marginRight);
                
                cardWidth = firstCard.offsetWidth + marginLeft + marginRight;
            } else {
                // Fallback si no hay tarjetas, aunque initCarrusel ya debería haber salido.
                wrapper.style.transform = `translateX(0)`;
                updateButtonVisibility();
                return;
            }

            // Calcula el desplazamiento total en píxeles.
            // Multiplicamos por el 'index' y el número de tarjetas a la vista.
            // Para asegurar que el desplazamiento sea preciso y muestre las tarjetas completas.
            const offsetPx = index * cardWidth; 

            wrapper.style.transform = `translateX(-${offsetPx}px)`;
            updateButtonVisibility(); 
        }

        // ... (Event Listeners remain the same, calling updateTransform) ...
        nextBtn.addEventListener('click', () => {
            const maxIndex = getMaxIndex();
            if (index < maxIndex) {
                index++;
                updateTransform();
            }
        });

        prevBtn.addEventListener('click', () => {
            if (index > 0) {
                index--;
                updateTransform();
            }
        });

        window.addEventListener('resize', () => {
             // Al redimensionar, también ajustamos el índice si fuera necesario para no salirse de límites
             const maxIndex = getMaxIndex();
             if (index > maxIndex) {
                 index = maxIndex;
             }
             updateTransform();
        });


        updateTransform(); 
        updateButtonVisibility(); 

        // ... (IntersectionObserver remains the same) ...
        const observer = new IntersectionObserver(
            (entries) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        entry.target.classList.add("visible");
                    } else {
                        entry.target.classList.remove("visible");
                    }
                });
            },
            { threshold: 0.2 }
        );

        cards.forEach((card) => observer.observe(card));
    }

    initCarrusel();
});