// dashboard-reservas.js (versión corregida y robusta)

function showSection(section) {
  document.querySelectorAll('.section').forEach(sec => sec.classList.remove('active'));
  document.getElementById(`section-${section}`).classList.add('active');
}

// DOM
const reserveList   = document.getElementById('reserveList');
const filterDate    = document.getElementById('filterDate');
const filterPeople  = document.getElementById('filterPeople');
const filterName    = document.getElementById('filterName');
const applyFilters  = document.getElementById('applyFilters');
const clearFilters  = document.getElementById('clearFilters');


// Agregar reserva
if (reserveForm) {
  reserveForm.addEventListener('submit', e => {
    e.preventDefault();


    const name   = document.getElementById('resName').value.trim();
    const date   = document.getElementById('resDate').value;
    const time   = document.getElementById('resTime').value;
    const people = Number(document.getElementById('resPeople').value);
    const mesa= Number(document.getElementById('resMesa').value);
    const zona  = document.getElementById('resZona').value.trim();

    if (!name || !date || !time || !people) return;

    const newRes = {
      id: Date.now() + Math.floor(Math.random() * 10000),
      name,
      date,
      time,
      people,
      mesa:mesa,
      zone: zona
    };

    reservas.push(newRes);
    localStorage.setItem('reservas', JSON.stringify(reservas));
    renderReservas(reservas);
    reserveForm.reset();
    alert('Reserva guardada ✅');
  });
}

// Aplicar filtros
if (applyFilters) {
  applyFilters.addEventListener('click', () => {
    const fDateVal = filterDate.value;
    const fPeopleVal = (filterPeople.value || '').toString().trim();
    const fPeople = fPeopleVal === '' ? null : Number(fPeopleVal);
    const fName = (filterName.value || '').trim().toLowerCase();

    const filtradas = reservas.filter(r => {
      const matchDate = fDateVal ? r.date === fDateVal : true;
      const matchPeople = (fPeople === null) ? true : (Number(r.people) === fPeople);
      const matchName = fName ? r.name.toLowerCase().includes(fName) : true;
      return matchDate && matchPeople && matchName;
    });

    renderReservas(filtradas);
  });
}

// Limpiar filtros
if (clearFilters) {
  clearFilters.addEventListener('click', () => {
    filterDate.value = '';
    filterPeople.value = '';
    filterName.value = '';
    renderReservas(reservas);
  });
}

// Renderizado (recibe un arreglo de reservas a mostrar)
function renderReservas(lista) {
  if (!reserveList) return;
  reserveList.innerHTML = '';

  lista.forEach((res, index) => {
    const tr = document.createElement('tr');
    tr.dataset.id = res.id || index+1;

    tr.innerHTML = `
      <td>${res.id || index + 1}</td> 
      <td>${escapeHtml(res.name)}</td>
      <td>${escapeHtml(res.date)}</td>
      <td>${escapeHtml(res.time)}</td>
      <td>${escapeHtml(String(res.people))}</td>
      <td>${escapeHtml(String(res.mesa))}</td>
            <td>${escapeHtml(res.zone)}</td> 
      <td>
        <button class="delete-btn">Eliminar</button>
      </td>
    `;

    // ... (el resto de la función se mantiene)
    // ...
  });
}

// pequeña utilidad para evitar inyección de HTML en los campos
function escapeHtml(text) {
  return String(text)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    
    .replace(/'/g, '&#039;');
}


  
