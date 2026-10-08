const form = document.querySelector('#booking-form');
const message = document.querySelector('#message');
const dateInput = document.querySelector('#date');
const submit = document.querySelector('#submit');
const cancel = document.querySelector('#cancel');
const businessDaysText = document.querySelector('#business-days-text');
const dateClosedHint = document.querySelector('#date-closed-hint');
let token = window.location.hash.substring(1);

let regularClosedDays = [];
let closedDates = []; // List of objects: { date, reason }

dateInput.min = new Date().toLocaleDateString('en-CA');

const WEEKDAY_NAMES = ['週日', '週一', '週二', '週三', '週四', '週五', '週六'];

const API_BASE = window.location.protocol === 'file:' ? 'http://localhost:8080' : '';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, options);
  const contentType = response.headers.get('content-type') || '';
  const body = contentType.includes('application/json') ? await response.json() : {};
  if (!response.ok) throw new Error(body.message || '系統暫時無法處理您的請求，請稍後再試。');
  return body;
}

function showMessage(text, type) {
  message.className = `message ${type}`;
  message.textContent = text;
}

// Load Store Closure Settings
async function loadClosureSettings() {
  try {
    const data = await request('/api/public/closure/settings');
    regularClosedDays = data.regularClosedDays || [];
    closedDates = data.closedDates || [];

    // Update Hero Business Days Banner
    if (businessDaysText) {
      if (regularClosedDays.length === 0) {
        businessDaysText.textContent = '每日營業';
      } else if (regularClosedDays.length === 1 && regularClosedDays.includes(0)) {
        businessDaysText.textContent = '週一至週六營業（週日公休）';
      } else {
        const closedDayNames = regularClosedDays.map((d) => WEEKDAY_NAMES[d]).join('、');
        businessDaysText.textContent = `每週 ${closedDayNames} 公休`;
      }
    }
  } catch (err) {
    console.error('無法取得公休設定:', err);
  }
}

// Validate Date Closure
function checkDateClosure(dateStr) {
  if (!dateStr) {
    hideDateWarning();
    return { isClosed: false };
  }

  const dateObj = new Date(dateStr + 'T00:00:00');
  const dayOfWeek = dateObj.getDay(); // 0=Sun, 1=Mon, ..., 6=Sat

  // Check regular weekly days off
  if (regularClosedDays.includes(dayOfWeek)) {
    return {
      isClosed: true,
      reason: `每週 ${WEEKDAY_NAMES[dayOfWeek]} 為固定公休日，暫不開放訂位`,
    };
  }

  // Check ad-hoc closed dates
  const adHoc = closedDates.find((item) => item.date === dateStr);
  if (adHoc) {
    const reasonText = adHoc.reason ? `（原因：${adHoc.reason}）` : '';
    return {
      isClosed: true,
      reason: `店家當日公休／休假${reasonText}，暫不開放訂位`,
    };
  }

  return { isClosed: false };
}

function showDateWarning(reason) {
  if (dateClosedHint) {
    dateClosedHint.textContent = `⚠️ ${reason}`;
    dateClosedHint.hidden = false;
  }
  showMessage(reason, 'error');
}

function hideDateWarning() {
  if (dateClosedHint) {
    dateClosedHint.hidden = true;
    dateClosedHint.textContent = '';
  }
  if (message.classList.contains('error')) {
    message.textContent = '';
    message.className = 'message';
  }
}

dateInput.addEventListener('change', () => {
  const closureInfo = checkDateClosure(dateInput.value);
  if (closureInfo.isClosed) {
    showDateWarning(closureInfo.reason);
  } else {
    hideDateWarning();
  }
});

function fillReservation(reservation) {
  const values = {
    name: reservation.customerName,
    phone: reservation.customerPhone,
    reservationDate: reservation.reservationDate,
    reservationTime: reservation.reservationTime,
    guestCount: reservation.guestCount,
    recommendedBy: reservation.recommendedBy,
    notes: reservation.notes,
  };
  Object.entries(values).forEach(([key, value]) => {
    const field = form.elements.namedItem(key);
    if (field && value != null) field.value = value;
  });
}

async function loadReservation() {
  if (!token) return;
  try {
    const reservation = await request(`/api/public/reservations/${encodeURIComponent(token)}`);
    fillReservation(reservation);
    submit.innerHTML = '儲存修改 <span>→</span>';
    cancel.hidden = false;
    showMessage(`正在管理預約 #${reservation.id}，可直接修改資料後儲存。`, 'success');
  } catch (error) {
    showMessage(error.message, 'error');
  }
}

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  const data = Object.fromEntries(new FormData(form));
  data.guestCount = Number(data.guestCount);

  // Check closure validation before submit
  const closureInfo = checkDateClosure(data.reservationDate);
  if (closureInfo.isClosed) {
    showDateWarning(closureInfo.reason);
    dateInput.focus();
    return;
  }

  const isNewReservation = !token;
  submit.disabled = true;
  try {
    const path = token ? `/api/public/reservations/${encodeURIComponent(token)}` : '/api/public/reservations';
    const reservation = await request(path, {
      method: token ? 'PATCH' : 'POST',
      headers: {'Content-Type': 'application/json'},
      body: JSON.stringify(data),
    });
    token = reservation.publicToken;
    window.location.hash = token;
    submit.innerHTML = '儲存修改 <span>→</span>';
    cancel.hidden = false;
    showMessage(`預約已${isNewReservation ? '建立' : '儲存'}，請保留此頁連結以管理預約。`, 'success');
  } catch (error) {
    showMessage(error.message, 'error');
  } finally {
    submit.disabled = false;
  }
});

cancel.addEventListener('click', async () => {
  if (!window.confirm('確定要取消這筆預約嗎？')) return;
  try {
    await request(`/api/public/reservations/${encodeURIComponent(token)}/cancel`, {method: 'POST'});
    form.querySelectorAll('input, textarea, button').forEach((element) => { element.disabled = true; });
    showMessage('您的預約已取消。', 'success');
  } catch (error) {
    showMessage(error.message, 'error');
  }
});

// Initialize
async function init() {
  await loadClosureSettings();
  await loadReservation();
}

init();
