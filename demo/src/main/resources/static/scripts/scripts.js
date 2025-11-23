window.addEventListener("load", () => {
    const platos = window.platosData || [];

    console.log("PLATOS DATA =", platos);

    const wrapper = document.querySelector("#cardsWrapper");
    if (!wrapper || platos.length === 0) return;

    // Crear cards dinámicas con estructura correcta
    platos.forEach(plato => {
        const card = document.createElement("div");
        card.classList.add("card"); // Usa tu clase del CSS

        card.innerHTML = `
            <div class="image-wrapper">
                <img src="${plato.imagenRuta}" alt="${plato.nombre}">
            </div>
            <div class="card-body">
                <h3 class="card-title">${plato.nombre}</h3>
                <p class="card-text">${plato.descripcion}</p>
            </div>
        `;

        wrapper.appendChild(card);
    });

    // Carousel
    let currentIndex = 0;
    const cardWidth = wrapper.querySelector(".card").offsetWidth + 20; // margen incluido
    const visibleCards = 3;

    const btnPrev = document.querySelector(".carousel-btn.prev");
    const btnNext = document.querySelector(".carousel-btn.next");

    function updateCarousel() {
        wrapper.style.transform = `translateX(-${currentIndex * cardWidth}px)`;
    }

    btnNext.addEventListener("click", () => {
        if (currentIndex < platos.length - visibleCards) {
            currentIndex++;
            updateCarousel();
        }
    });

    btnPrev.addEventListener("click", () => {
        if (currentIndex > 0) {
            currentIndex--;
            updateCarousel();
        }
    });
});
