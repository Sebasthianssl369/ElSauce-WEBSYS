// Botón scroll-up
document.addEventListener('DOMContentLoaded', function() {
    const scrollUp = document.querySelector(".scroll-up");

    function alternarBotonScrollUp(pixeles) {
        let ticking = false;
        
        function updateScrollUp() {
            const scroll = window.pageYOffset || document.documentElement.scrollTop;
            
            if (scroll > pixeles) {
                scrollUp.classList.add("visible");
            } else {
                scrollUp.classList.remove("visible");
            }
            
            ticking = false;
        }

        window.addEventListener("scroll", () => {
            if (!ticking) {
                requestAnimationFrame(updateScrollUp);
                ticking = true;
            }
        });
    }

    // Función para hacer scroll hacia arriba
    scrollUp.addEventListener('click', function(e) {
        e.preventDefault();
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });

    // Inicializar con 350px de scroll
    alternarBotonScrollUp(350);





});



// main.js (corregido)
// Envuelve TODO en DOMContentLoaded para asegurar que los elementos existen
document.addEventListener('DOMContentLoaded', function () {

  // =========================
  // MENÚ HAMBURGUESA MÓVIL
  // =========================
  const btnHambur = document.querySelector('.menu-hambur');
  const menu = document.querySelector('.menu');
  const overlay = document.querySelector('.menu-overlay');
  const menuLinks = document.querySelectorAll('.menu ul li a');

  // Solo intentar obtener el <i> si btnHambur existe
  let iconHambur = null;
  if (btnHambur) {
    iconHambur = btnHambur.querySelector('i') || null;
  }

  function abrirMenu() {
    if (menu) menu.classList.add('menu-activo');
    if (overlay) overlay.classList.add('activo');
    document.body.style.overflow = 'hidden';
    if (iconHambur) {
      iconHambur.classList.remove('fa-bars');
      iconHambur.classList.add('fa-times');
    }
  }

  function cerrarMenu() {
    if (menu) menu.classList.remove('menu-activo');
    if (overlay) overlay.classList.remove('activo');
    document.body.style.overflow = '';
    if (iconHambur) {
      iconHambur.classList.remove('fa-times');
      iconHambur.classList.add('fa-bars');
    }
  }

  // Solo añade listeners si los elementos existen
  if (btnHambur && menu && overlay) {
    btnHambur.addEventListener('click', function (e) {
      e.stopPropagation();
      if (menu.classList.contains('menu-activo')) {
        cerrarMenu();
      } else {
        abrirMenu();
      }
    });

    overlay.addEventListener('click', cerrarMenu);
  }

  if (menuLinks && menuLinks.length) {
    menuLinks.forEach(link => {
      link.addEventListener('click', cerrarMenu);
    });
  }

  window.addEventListener('resize', function () {
    if (window.innerWidth > 900) {
      cerrarMenu();
    }
  });


  // =========================
  // CAROUSEL DE CARDS
  // =========================
  const wrapper = document.querySelector('.cards-wrapper');
  const cardsNodeList = document.querySelectorAll('.cards-wrapper .card');
  const prevBtn = document.querySelector('.carousel-btn.prev');
  const nextBtn = document.querySelector('.carousel-btn.next');

  // Convertir NodeList a Array para indexOf y otras operaciones
  const cards = Array.from(cardsNodeList || []);
  let index = 0;

  function getVisibleCards() {
    const width = window.innerWidth;
    if (width <= 576) return 1;
    if (width <= 992) return 2;
    if (width <= 1200) return 3;
    return 4;
  }

  function updateTransform() {
    if (!wrapper) return;
    const visible = getVisibleCards();
    // Asegurarse de que index no se salga del rango
    if (index < 0) index = 0;
    if (index > Math.max(0, cards.length - visible)) index = Math.max(0, cards.length - visible);
    wrapper.style.transform = `translateX(-${(100 / visible) * index}%)`;
  }

  if (nextBtn) {
    nextBtn.addEventListener('click', () => {
      const visible = getVisibleCards();
      if (index < cards.length - visible) {
        index++;
        updateTransform();
      }
    });
  }

  if (prevBtn) {
    prevBtn.addEventListener('click', () => {
      if (index > 0) {
        index--;
        updateTransform();
      }
    });
  }

  window.addEventListener('resize', updateTransform);

  // Llamar a updateTransform inicialmente para asegurar posición correcta
  updateTransform();


  // =========================
  // ANIMACIÓN AL HACER SCROLL (IntersectionObserver)
  // =========================
  if (cards.length) {
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          // Calcula el índice real de la card dentro del array
          const cardIndex = cards.indexOf(entry.target);

          if (entry.isIntersecting) {
            // Añadimos clase visible con pequeño delay (si cardIndex >= 0)
            setTimeout(() => {
              entry.target.classList.add("visible");
            }, (cardIndex >= 0 ? cardIndex : 0) * 150);
          } else {
            entry.target.classList.remove("visible");
          }
        });
      },
      { threshold: 0.2 }
    );

    cards.forEach((card) => observer.observe(card));
  }

}); // end DOMContentLoaded

