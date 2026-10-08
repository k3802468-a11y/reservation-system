const API_BASE = window.location.protocol === 'file:' ? 'http://localhost:8080/api' : '/api';

let token = localStorage.getItem('merchant_token') || null;
let currentFilter = 'ALL';
let allReservations = [];

// DOM Elements
const loginSection = document.getElementById('login-section');
const dashboardSection = document.getElementById('dashboard-section');
const navUser = document.getElementById('nav-user');
const loginForm = document.getElementById('login-form');
const loginError = document.getElementById('login-error');
const logoutBtn = document.getElementById('logout-btn');
const dateInput = document.getElementById('date-picker');
const searchInput = document.getElementById('search-input');
const reservationTableBody = document.getElementById('reservation-table-body');
const emptyMessage = document.getElementById('empty-message');

// Modal Elements
const addModal = document.getElementById('add-modal');
const openAddBtn = document.getElementById('open-add-btn');
const closeAddBtn = document.getElementById('close-add-btn');
const addForm = document.getElementById('add-form');

// Initialize Date to Today
if (dateInput) {
  dateInput.value = new Date().toLocaleDateString('en-CA');
}

// Request Helper
async function request(endpoint, options = {}) {
  options.headers = options.headers || {};
  options.headers['Content-Type'] = 'application/json';
  if (token) {
    options.headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE}${endpoint}`, options);
  const contentType = response.headers.get('content-type') || '';
  const data = contentType.includes('application/json') ? await response.json() : {};

  if (!response.ok) {
    if (response.status === 401) {
      logout();
      throw new Error('登入逾時，請重新登入。');
    }
    throw new Error(data.message || `請求失敗 (${response.status})`);
  }
  return data;
}

// Check Auth State
function checkAuth() {
  if (token) {
    loginSection.classList.add('hidden');
    dashboardSection.classList.remove('hidden');
    navUser.classList.remove('hidden');
    loadDashboardData();
  } else {
    loginSection.classList.remove('hidden');
    dashboardSection.classList.add('hidden');
    navUser.classList.add('hidden');
  }
}

// Login
loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  loginError.classList.add('hidden');
  const username = loginForm.username.value.trim();
  const password = loginForm.password.value;

  try {
    const data = await request('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });

    token = data.accessToken;
    localStorage.setItem('merchant_token', token);
    checkAuth();
  } catch (err) {
    loginError.textContent = err.message;
    loginError.classList.remove('hidden');
  }
});

// Logout
function logout() {
  token = null;
  localStorage.removeItem('merchant_token');
  checkAuth();
}

logoutBtn.addEventListener('click', logout);

// Load Dashboard Data
async function loadDashboardData() {
  const selectedDate = dateInput.value;
  try {
    // Load Reservations
    allReservations = await request(`/reservations?date=${selectedDate}`);
    renderReservations();

    // Load Stats
    try {
      const stats = await request('/statistics/today');
      document.getElementById('stat-total').textContent = stats.totalReservations ?? 0;
      document.getElementById('stat-completed').textContent = stats.completedCount ?? 0;
      document.getElementById('stat-noshow').textContent = stats.noShowCount ?? 0;
      document.getElementById('stat-returning').textContent = `${((stats.returningRate || 0) * 100).toFixed(0)}%`;
    } catch (_) {}
  } catch (err) {
    console.error('載入資料失敗:', err);
  }
}

// Filter Event Listeners
document.querySelectorAll('.filter-btn').forEach((btn) => {
  btn.addEventListener('click', () => {
    document.querySelectorAll('.filter-btn').forEach((b) => b.classList.remove('active'));
    btn.classList.add('active');
    currentFilter = btn.dataset.status;
    renderReservations();
  });
});

dateInput.addEventListener('change', loadDashboardData);
searchInput.addEventListener('input', renderReservations);

// Render Reservations Table
function renderReservations() {
  const query = searchInput.value.toLowerCase().trim();
  const filtered = allReservations.filter((item) => {
    const matchesFilter = currentFilter === 'ALL' || item.status === currentFilter;
    const matchesQuery = !query || 
      (item.customerName && item.customerName.toLowerCase().includes(query)) ||
      (item.customerPhone && item.customerPhone.includes(query));
    return matchesFilter && matchesQuery;
  });

  reservationTableBody.innerHTML = '';
  if (filtered.length === 0) {
    emptyMessage.classList.remove('hidden');
    return;
  }
  emptyMessage.classList.add('hidden');

  filtered.forEach((item) => {
    const tr = document.createElement('tr');
    const formattedTime = item.reservationTime ? item.reservationTime.substring(0, 5) : '';

    tr.innerHTML = `
      <td><strong>${formattedTime}</strong></td>
      <td><strong>${escapeHtml(item.customerName)}</strong></td>
      <td>${item.guestCount} 位</td>
      <td>${escapeHtml(item.customerPhone)}</td>
      <td>${escapeHtml(item.recommendedBy || '-')}</td>
      <td><span style="color: #cbd5e1; font-size: 0.90rem;">${escapeHtml(item.notes || '-')}</span></td>
      <td>
        <select class="status-select status-${item.status}" data-id="${item.id}">
          <option value="PENDING" ${item.status === 'PENDING' ? 'selected' : ''}>待確認</option>
          <option value="CONFIRMED" ${item.status === 'CONFIRMED' ? 'selected' : ''}>已確認</option>
          <option value="COMPLETED" ${item.status === 'COMPLETED' ? 'selected' : ''}>已完成</option>
          <option value="NO_SHOW" ${item.status === 'NO_SHOW' ? 'selected' : ''}>未到店</option>
          <option value="CANCELLED" ${item.status === 'CANCELLED' ? 'selected' : ''}>已取消</option>
        </select>
      </td>
    `;
    reservationTableBody.appendChild(tr);
  });

  // Attach Status Change Listener
  document.querySelectorAll('.status-select').forEach((select) => {
    select.addEventListener('change', async (e) => {
      const id = e.target.dataset.id;
      const newStatus = e.target.value;
      try {
        await request(`/reservations/${id}/status?status=${newStatus}`, { method: 'PATCH' });
        e.target.className = `status-select status-${newStatus}`;
        const targetItem = allReservations.find((r) => r.id == id);
        if (targetItem) targetItem.status = newStatus;
      } catch (err) {
        alert(`更新狀態失敗: ${err.message}`);
        loadDashboardData();
      }
    });
  });
}

function escapeHtml(str) {
  return String(str).replace(/[&<>"']/g, (m) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[m]));
}

// Modal Toggle
openAddBtn.addEventListener('click', () => addModal.classList.remove('hidden'));
closeAddBtn.addEventListener('click', () => addModal.classList.add('hidden'));

// Add Reservation
addForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const body = {
    name: addForm.name.value,
    phone: addForm.phone.value,
    reservationDate: dateInput.value,
    reservationTime: addForm.time.value,
    guestCount: Number(addForm.guestCount.value),
    recommendedBy: addForm.recommendedBy.value,
    notes: addForm.notes.value,
  };

  try {
    await request('/public/reservations', {
      method: 'POST',
      body: JSON.stringify(body),
    });
    addModal.classList.add('hidden');
    addForm.reset();
    loadDashboardData();
  } catch (err) {
    alert(`新增預約失敗: ${err.message}`);
  }
});

// Tab Elements
const tabBtns = document.querySelectorAll('.tab-btn');
const tabContents = document.querySelectorAll('.tab-content');

// Closure Elements
const weekdayBtns = document.querySelectorAll('.weekday-btn');
const saveRegularDaysBtn = document.getElementById('save-regular-days-btn');
const addClosureDateForm = document.getElementById('add-closure-date-form');
const closureDateInput = document.getElementById('closure-date-input');
const closureReasonInput = document.getElementById('closure-reason-input');
const closedDatesList = document.getElementById('closed-dates-list');
const closureEmptyMsg = document.getElementById('closure-empty-msg');

let currentRegularDays = [];
let currentClosedDates = [];

// Initialize Date Picker min for closure
if (closureDateInput) {
  closureDateInput.min = new Date().toLocaleDateString('en-CA');
  closureDateInput.value = new Date().toLocaleDateString('en-CA');
}

// Tab Switching
tabBtns.forEach((btn) => {
  btn.addEventListener('click', () => {
    tabBtns.forEach((b) => b.classList.remove('active'));
    tabContents.forEach((c) => c.classList.remove('active'));

    btn.classList.add('active');
    const targetTab = document.getElementById(btn.dataset.tab);
    if (targetTab) targetTab.classList.add('active');

    if (btn.dataset.tab === 'closure-tab') {
      loadClosureSettings();
    }
  });
});

// Weekday Toggle
weekdayBtns.forEach((btn) => {
  btn.addEventListener('click', () => {
    btn.classList.toggle('active');
  });
});

// Save Regular Closed Days
if (saveRegularDaysBtn) {
  saveRegularDaysBtn.addEventListener('click', async () => {
    const selectedDays = [];
    weekdayBtns.forEach((btn) => {
      if (btn.classList.contains('active')) {
        selectedDays.push(Number(btn.dataset.day));
      }
    });

    try {
      saveRegularDaysBtn.disabled = true;
      saveRegularDaysBtn.textContent = '儲存中...';
      await request('/store/closure/regular-days', {
        method: 'PUT',
        body: JSON.stringify({ days: selectedDays }),
      });
      alert('每週公休日設定已成功儲存！');
      loadClosureSettings();
    } catch (err) {
      alert(`儲存失敗: ${err.message}`);
    } finally {
      saveRegularDaysBtn.disabled = false;
      saveRegularDaysBtn.textContent = '儲存每週公休設定';
    }
  });
}

// Add Ad-hoc Closure Date Form Submit
if (addClosureDateForm) {
  addClosureDateForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const dateVal = closureDateInput.value;
    const reasonVal = closureReasonInput.value.trim();

    if (!dateVal) {
      alert('請選擇休假日期');
      return;
    }

    try {
      await request('/store/closure/dates', {
        method: 'POST',
        body: JSON.stringify({ date: dateVal, reason: reasonVal }),
      });
      closureReasonInput.value = '';
      loadClosureSettings();
      alert(`已成功設定 ${dateVal} 為臨時休假日！`);
    } catch (err) {
      alert(`新增休假失敗: ${err.message}`);
    }
  });
}

// Load Closure Settings
async function loadClosureSettings() {
  try {
    const data = await request('/store/closure/settings');
    currentRegularDays = data.regularClosedDays || [];
    currentClosedDates = data.closedDates || [];

    // Render regular weekday buttons
    weekdayBtns.forEach((btn) => {
      const day = Number(btn.dataset.day);
      if (currentRegularDays.includes(day)) {
        btn.classList.add('active');
      } else {
        btn.classList.remove('active');
      }
    });

    // Render ad-hoc closed dates list
    renderClosedDatesList();
  } catch (err) {
    console.error('載入公休設定失敗:', err);
  }
}

const WEEKDAY_NAMES = ['週日', '週一', '週二', '週三', '週四', '週五', '週六'];

function renderClosedDatesList() {
  if (!closedDatesList) return;
  closedDatesList.innerHTML = '';

  if (currentClosedDates.length === 0) {
    if (closureEmptyMsg) closureEmptyMsg.classList.remove('hidden');
    return;
  }
  if (closureEmptyMsg) closureEmptyMsg.classList.add('hidden');

  currentClosedDates.forEach((item) => {
    const dateObj = new Date(item.date + 'T00:00:00');
    const weekdayStr = WEEKDAY_NAMES[dateObj.getDay()] || '';

    const card = document.createElement('div');
    card.className = 'closed-date-card';
    card.innerHTML = `
      <div class="closed-date-info">
        <div class="closed-date-main">
          <span class="closed-date-val">${escapeHtml(item.date)}</span>
          <span class="closed-date-weekday">${weekdayStr}</span>
        </div>
        ${item.reason ? `<div class="closed-date-reason">${escapeHtml(item.reason)}</div>` : ''}
      </div>
      <button type="button" class="btn-delete-closure" data-id="${item.id}">刪除</button>
    `;
    closedDatesList.appendChild(card);
  });

  // Attach delete listeners
  document.querySelectorAll('.btn-delete-closure').forEach((btn) => {
    btn.addEventListener('click', async (e) => {
      const id = e.target.dataset.id;
      if (!confirm('確定要取消這天的休假設定嗎？')) return;

      try {
        await request(`/store/closure/dates/${id}`, { method: 'DELETE' });
        loadClosureSettings();
      } catch (err) {
        alert(`刪除失敗: ${err.message}`);
      }
    });
  });
}

// Check Auth State
function checkAuth() {
  if (token) {
    loginSection.classList.add('hidden');
    dashboardSection.classList.remove('hidden');
    navUser.classList.remove('hidden');
    loadDashboardData();
    loadClosureSettings();
  } else {
    loginSection.classList.remove('hidden');
    dashboardSection.classList.add('hidden');
    navUser.classList.add('hidden');
  }
}

// Initial Check
checkAuth();

