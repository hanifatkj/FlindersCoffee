'use strict';

/**
 * FLINDERS COFFEE - SISTEM KASIR (POS)
 * Didesain khusus untuk kasir laptop: cepat, simpel, bebas hambatan.
 * 6 Halaman lengkap: POS, Tagihan Meja, Booking Masuk, Riwayat Pesanan, Pengeluaran, Tutup Kas.
 * Kotak shift / profil di sidebar telah dihapus sesuai permintaan.
 */

const KEY = 'flinders-pos-clean-v3';
const HOLD_MINUTES = 120; // 2 jam batas hold reservasi
const OPEN_CASH = 500000; // Modal awal laci Rp 500.000
const ROOMS = ['A', 'B', 'C'];
const TABLES = [1, 2, 3, 4, 5, 6];

// Katalog Menu Asli Flinders Coffee (Tercetak Fisik / Default Dashboard Display)
// Sesuai daftar menu cetak fisik meja Flinders Coffee.
// Catatan: Menu ini belum dimasukkan ke database MySQL. Jika database nanti diisi oleh Owner, data database akan meng-overwrite tampilan ini.
const PRINTED_MENU = [
  // 1. COFFEE (ICED 🔵 / HOT 🔴)
  { n: 'Americano', p: 18000, hpp: 6500, c: 'Coffee', ic: '☕', d: 'Espresso klasik dengan air mineral segar (Dingin 🔵 / Panas 🔴)' },
  { n: 'Kopi Susu', p: 18000, hpp: 6500, c: 'Coffee', ic: '☕', d: 'Espresso double shot dengan creamy milk & aren murni (Dingin 🔵 / Panas 🔴)' },
  { n: 'Flat White', p: 20000, hpp: 7500, c: 'Coffee', ic: '☕', d: 'Espresso velvety dengan microfoam steamed milk (Dingin 🔵 / Panas 🔴)' },
  { n: 'Cafe Latte', p: 20000, hpp: 7500, c: 'Coffee', ic: '☕', d: 'Espresso lembut berpadu steamed milk halus berbusa (Dingin 🔵 / Panas 🔴)' },
  { n: 'Cappucino', p: 20000, hpp: 7500, c: 'Coffee', ic: '☕', d: 'Espresso kaya rasa dengan foam tebal gurih berpadu (Dingin 🔵 / Panas 🔴)' },

  // 2. FLAVOURED COFFEE (ICED 🔵 / HOT 🔴)
  { n: 'Butterscotch Latte', p: 22000, hpp: 8000, c: 'Flavoured Coffee', ic: '☕', d: 'Espresso berpadu sirup butterscotch gurih karamel (Dingin 🔵 / Panas 🔴)' },
  { n: 'Vanilla Latte', p: 22000, hpp: 8000, c: 'Flavoured Coffee', ic: '☕', d: 'Espresso harum ekstrak vanila manis lembut (Dingin 🔵 / Panas 🔴)' },
  { n: 'Hazelnut Latte', p: 22000, hpp: 8000, c: 'Flavoured Coffee', ic: '☕', d: 'Espresso beraroma hazelnut panggang gurih (Dingin 🔵 / Panas 🔴)' },
  { n: 'Pandan Latte', p: 22000, hpp: 8000, c: 'Flavoured Coffee', ic: '☕', d: 'Espresso dengan aroma wangi pandan alami (Dingin 🔵 / Panas 🔴)' },
  { n: 'Chococino', p: 22000, hpp: 8500, c: 'Flavoured Coffee', ic: '☕', d: 'Paduan kopi moka espresso dan cokelat nikmat (Dingin 🔵 / Panas 🔴)' },
  { n: 'Caramel Macchiato', p: 22000, hpp: 8500, c: 'Flavoured Coffee', ic: '☕', d: 'Espresso, susu, sirup vanilla & saus karamel lezat (Dingin 🔵 / Panas 🔴)' },

  // 3. SIGNATURE
  { n: 'Kopi Nera (Gula Aren)', p: 20000, hpp: 7500, c: 'Signature', ic: '✨', d: '❤️❤️ Signature kopi aren khas Flinders racikan spesial (Dingin 🔵 / Panas 🔴)' },
  { n: 'Creamy Butterscotch', p: 23000, hpp: 9000, c: 'Signature', ic: '✨', d: 'Minuman khas butterscotch creamy kaya tekstur dan manis pas' },
  { n: 'Butterscotch Seasalt', p: 23000, hpp: 9000, c: 'Signature', ic: '✨', d: 'Perpaduan manis gurih butterscotch dengan sentuhan seasalt seimbang' },
  { n: 'Mont Blanc', p: 23000, hpp: 9000, c: 'Signature', ic: '✨', d: 'Minuman dingin signature lembut dengan rasa dessert pegunungan salju' },
  { n: 'Choco Hazelnut', p: 22000, hpp: 8500, c: 'Signature', ic: '✨', d: 'Cokelat pekat berpadu kacang hazelnut gurih nikmat' },
  { n: 'Choco Berry', p: 22000, hpp: 8500, c: 'Signature', ic: '✨', d: 'Kombinasi kaya rasa cokelat lumer dan kesegaran rasa berry' },
  { n: 'Creamy Lotus', p: 22000, hpp: 8500, c: 'Signature', ic: '✨', d: 'Minuman creamy biskuit Lotus Biscoff legit renyah' },
  { n: 'Crunchy Caramel', p: 23000, hpp: 9000, c: 'Signature', ic: '✨', d: '❤️ Karamel manis legit dengan sensasi renyah kriuk istimewa' },

  // 4. AMERICANO SERIES
  { n: 'Blueberry Americano', p: 20000, hpp: 7000, c: 'Americano Series', ic: '🫐', d: '❤️ Sensasi Americano segar berpadu buah blueberry manis-asam' },
  { n: 'Peach Americano', p: 20000, hpp: 7000, c: 'Americano Series', ic: '🍑', d: 'Americano dingin menyegarkan dengan aroma dan rasa buah peach' },
  { n: 'Passion Fruit Americano', p: 20000, hpp: 7000, c: 'Americano Series', ic: '🍹', d: 'Americano tropis dengan kesegaran markisa alami' },

  // 5. NON COFFEE
  { n: 'Matcha Latte', p: 22000, hpp: 8500, c: 'Non Coffee', ic: '🍵', d: 'Matcha murni Uji Jepang dengan susu segar lembut (Dingin 🔵 / Panas 🔴)' },
  { n: 'Chocolate', p: 20000, hpp: 7500, c: 'Non Coffee', ic: '🍫', d: '❤️❤️ Cokelat Belgia pekat berpadu fresh milk (Dingin 🔵 / Panas 🔴)' },
  { n: 'Cookies & Cream', p: 20000, hpp: 7500, c: 'Non Coffee', ic: '🍪', d: 'Minuman susu manis lembut dengan remahan biskuit cokelat gurih' },
  { n: 'Choco Nutella', p: 20000, hpp: 8000, c: 'Non Coffee', ic: '🍫', d: 'Cokelat susu dengan selai hazelnut cokelat Nutella asli' },
  { n: 'Red Velvet', p: 20000, hpp: 7500, c: 'Non Coffee', ic: '🥛', d: 'Minuman red velvet lembut beraroma kue manis legit (Dingin 🔵 / Panas 🔴)' },
  { n: 'Vanilla', p: 20000, hpp: 7000, c: 'Non Coffee', ic: '🥛', d: 'Minuman susu vanila klasik lembut yang menenangkan (Dingin 🔵 / Panas 🔴)' },
  { n: 'Fruity Milkshake', p: 20000, hpp: 7500, c: 'Non Coffee', ic: '🥤', d: 'Milkshake buah segar lembut dan creamy berbusa halus' },
  { n: 'Fruity Yogurt', p: 18000, hpp: 6500, c: 'Non Coffee', ic: '🍧', d: '❤️ Yogurt segar pilihan: Blueberry, Strawberry, Lychee, Mango, Passion Fruit' },

  // 6. FRAPPE
  { n: 'Chocolate Frappe', p: 23000, hpp: 9000, c: 'Frappe', ic: '🥤', d: 'Blended es cokelat kental dan creamy bertabur garnish' },
  { n: 'Red Velvet Frappe', p: 23000, hpp: 9000, c: 'Frappe', ic: '🥤', d: 'Frappe red velvet dingin pekat bertabur topping lembut' },
  { n: 'Matcha Frappe', p: 23000, hpp: 9000, c: 'Frappe', ic: '🥤', d: 'Blended matcha Jepang dingin kaya rasa susu segar' },
  { n: 'Vanilla Frappe', p: 23000, hpp: 9000, c: 'Frappe', ic: '🥤', d: 'Frappe vanila manis dingin segar dengan tekstur halus' },
  { n: 'Flinderscino', p: 23000, hpp: 9000, c: 'Frappe', ic: '🥤', d: 'Signature ice-blended khas Flinders Coffee kaya cita rasa' },

  // 7. TEA (ICED 🔵 / HOT 🔴)
  { n: 'Original Tea', p: 15000, hpp: 4500, c: 'Tea', ic: '🫖', d: 'Seduhan teh murni berkualitas harum dan menyegarkan (Dingin 🔵 / Panas 🔴)' },
  { n: 'Milk Tea', p: 18000, hpp: 6000, c: 'Tea', ic: '🫖', d: 'Teh aromatik dipadukan dengan susu kental gurih (Dingin 🔵 / Panas 🔴)' },
  { n: 'Thai Tea', p: 18000, hpp: 6000, c: 'Tea', ic: '🫖', d: 'Teh Thailand otentik dengan aroma rempah manis creamy (Dingin 🔵 / Panas 🔴)' },
  { n: 'Green Tea', p: 18000, hpp: 6000, c: 'Tea', ic: '🫖', d: 'Teh hijau segar aromatik kaya antioksidan (Dingin 🔵 / Panas 🔴)' },
  { n: 'Lemon Tea', p: 18000, hpp: 6000, c: 'Tea', ic: '🍋', d: 'Teh segar berpadu sari jeruk lemon asli penghilang dahaga (Dingin 🔵 / Panas 🔴)' },
  { n: 'Lychee Tea', p: 18000, hpp: 6500, c: 'Tea', ic: '🍹', d: 'Teh melati harum disajikan dengan buah leci segar (Dingin 🔵 / Panas 🔴)' },

  // 8. MEAL & SNACK
  { n: 'Nasi Ayam', p: 25000, hpp: 12000, c: 'Meal & Snack', ic: '🍗', d: 'Nasi putih hangat dengan olahan ayam bumbu rempah gurih lezat' },
  { n: 'Beef Burger Ori', p: 25000, hpp: 12000, c: 'Meal & Snack', ic: '🍔', d: 'Burger roti panggang empuk isi patty daging sapi gurih & sayuran segar' },
  { n: 'Mix Platter', p: 20000, hpp: 9500, c: 'Meal & Snack', ic: '🍟', d: 'Paket camilan lengkap aneka gorengan gurih renyah favorit kumpul' },
  { n: 'Mie Goreng / Rebus', p: 15000, hpp: 6000, c: 'Meal & Snack', ic: '🍜', d: 'Mie lezat pilihan (Goreng / Rebus) disajikan hangat menggoda' },
  { n: 'Kentang Goreng', p: 15000, hpp: 5500, c: 'Meal & Snack', ic: '🍟', d: 'Kentang goreng renyah bumbu gurih lezat renyah di luar lembut di dalam' },
  { n: 'Pisang Goreng Ori', p: 15000, hpp: 5500, c: 'Meal & Snack', ic: '🍌', d: 'Pisang goreng renyah manis alami cocok dinikmati bersama kopi' },
  { n: 'Bakpao Mini', p: 10000, hpp: 3500, c: 'Meal & Snack', ic: '🥟', d: 'Kukusan bakpao mini empuk lembut beraneka isian manis lezat' },
  { n: 'Roti Bakar', p: 10000, hpp: 3500, c: 'Meal & Snack', ic: '🍞', d: 'Roti bakar mentega pilihan: Coklat, Keju, Blueberry, Strawberry' }
];

let MENU = [...PRINTED_MENU];

const EXPENSE_CATS = ['Kebersihan & perlengkapan', 'Transport', 'Listrik & air', 'Perawatan alat', 'Lainnya'];

// Navigasi Utama (6 Halaman Lengkap)
const NAV = [
  ['pos', 'Kasir (POS)', 'M4 8h12v6a4 4 0 01-4 4H8a4 4 0 01-4-4zM16 9h2a2 2 0 010 5h-2'],
  ['pembayaran', 'Tagihan Meja', 'M3 7h16a2 2 0 012 2v9a2 2 0 01-2 2H5a2 2 0 01-2-2zM3 7l2-3h12M16 13h2'],
  ['booking', 'Booking Masuk', 'M6 8a6 6 0 0112 0c0 7 3 8 3 8H3s3-1 3-8M10 20a2 2 0 004 0'],
  ['riwayat', 'Riwayat Pesanan', 'M4 12a8 8 0 108-8 8 8 0 00-6 3M4 4v4h4M12 8v4l3 2'],
  ['pengeluaran', 'Pengeluaran', 'M12 3v18M16 7h-6a3 3 0 000 6h4a3 3 0 010 6H8'],
  ['tutup', 'Tutup Kas', 'M5 4h14v16H5zM9 9l6 6M15 9l-6 6']
];

// Utilitas
const $ = s => document.querySelector(s);
const esc = s => String(s || '').replace(/[&<>"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
const rp = n => (n < 0 ? '− ' : '') + 'Rp ' + Math.abs(n || 0).toLocaleString('id-ID');
const num = s => +String(s || '').replace(/\D/g, '') || 0;
const wt = (t, o) => new Intl.DateTimeFormat('id-ID', { timeZone: 'Asia/Makassar', hourCycle: 'h23', ...o }).format(t);
const hm = t => wt(t, { hour: '2-digit', minute: '2-digit' });
const dl = t => wt(t, { day: '2-digit', month: 'short', year: 'numeric' });
const today = () => new Intl.DateTimeFormat('en-CA', { timeZone: 'Asia/Makassar' }).format(Date.now());
const tot = items => (items || []).reduce((a, i) => a + (i.p * i.q), 0);
const rem = b => Math.ceil(((b.created || Date.now()) + HOLD_MINUTES * 6e4 - Date.now()) / 6e4);
const ord = id => S.orders.find(o => o.id === id);
const ico = p => `<svg class="i" viewBox="0 0 24 24"><path d="${p}"/></svg>`;

// Bersih tanpa dummy data: Semua meja kosong, tanpa booking dummy, tanpa order dummy, tanpa pengeluaran dummy
function seed() {
  return {
    day: today(),
    next: 1,
    bookings: [],
    members: {
      'FLN-482910': { name: 'Bima Bagaskara', stamps: 0, phone: '081234567801' },
      'FLN-482911': { name: 'Aris Munandar', stamps: 0, phone: '081234567802' },
      'FLN-482912': { name: 'Dina Salsabila', stamps: 0, phone: '081234567803' },
      'FLN-482913': { name: 'Rina Puspita', stamps: 0, phone: '081234567804' }
    },
    orders: [],
    expenses: [],
    closing: null,
    closings: [],
    openings: [],
    shiftStart: 0,
    openCash: 0,
    opened: false,
    lastCounted: null
  };
}

let S = JSON.parse(localStorage.getItem(KEY) || 'null');
if (!S || S.day !== today()) {
  const old = S;
  S = seed();
  if (old && old.closing) S.lastCounted = old.closing.counted;
  localStorage.setItem(KEY, JSON.stringify(S));
}

// Lengkapi data lama di localStorage
if (S.closings === undefined) S.closings = [];
if (S.shiftStart === undefined) S.shiftStart = 0;
if (S.openCash === undefined) S.openCash = OPEN_CASH;
if (S.opened === undefined) S.opened = true;
if (!S.openings) S.openings = [];
if (S.lastCounted === undefined) S.lastCounted = null;
const kasMsg = () => S.closing ? 'Kas sudah ditutup. Buka shift baru di menu Tutup Kas.' : (!S.opened ? 'Kas belum dibuka. Isi modal kas awal di menu Tutup Kas.' : '');

const save = () => localStorage.setItem(KEY, JSON.stringify(S));

// Status Meja (Jika tidak ada order aktif / booking, meja 100% Kosong / Tersedia)
function getTableStatus(room, tableNum) {
  const activeOrder = S.orders.find(o => o.type === 'dine' && o.room === room && o.table === tableNum && ['Baru', 'Diterima', 'Disajikan'].includes(o.status));
  if (activeOrder) return { status: 'Terisi', label: 'Terisi', cls: 'busy', free: false, order: activeOrder };

  const booking = S.bookings.find(b => b.room === room && b.tables.includes(tableNum) && ['Menunggu', 'Hadir'].includes(b.status));
  if (booking) {
    if (booking.status === 'Hadir') return { status: 'Terisi', label: 'Tamu Hadir', cls: 'busy', free: false, booking };
    return { status: 'Dibooking', label: 'Dibooking', cls: 'booked', free: false, booking };
  }
  return { status: 'Tersedia', label: 'Kosong', cls: 'free', free: true };
}

// Rekap Keuangan Kasir
const paidT = o => o.paidAt || o.time || 0;
const cashIn = () => S.orders.filter(o => o.status === 'Dibayar' && o.method === 'Tunai' && paidT(o) >= S.shiftStart).reduce((a, o) => a + o.total, 0);
const qrisIn = () => S.orders.filter(o => o.status === 'Dibayar' && o.method === 'QRIS' && paidT(o) >= S.shiftStart).reduce((a, o) => a + o.total, 0);
const spent = () => S.expenses.filter(e => e.t >= S.shiftStart).reduce((a, e) => a + e.nom, 0);
const expectedCash = () => S.openCash + cashIn() - spent();

// Toast
function toast(msg) {
  const el = $('#toast');
  if (!el) return;
  el.textContent = msg;
  el.className = 'on';
  clearTimeout(toast.t);
  toast.t = setTimeout(() => el.className = '', 2600);
}

// UI State
const newCart = () => ({ type: 'dine', room: 'A', table: null, items: [], member: null, fln: '' });
let ui = {
  cat: 'Semua',
  q: '',
  cart: newCart(),
  payId: null,
  payMethod: 'Tunai',
  payRecv: 0,
  roomFilter: 'Semua',
  mapRoom: 'A',
  historyFilter: 'Semua',
  historyQuery: '',
  exp: { cat: EXPENSE_CATS[0], nom: '', desc: '' },
  closeCount: '',
  closeNote: '',
  openCash: '',
  openNote: ''
};

// Modal Helper
function showModal(html) {
  const dlg = $('#dlg');
  dlg.innerHTML = html;
  if (!dlg.open) dlg.showModal();
}

function closeModal() {
  const dlg = $('#dlg');
  if (dlg && dlg.open) dlg.close();
}

// ==========================================
// TAMPILAN HALAMAN (VIEWS)
// ==========================================
const V = {};

// 1. POS (KASIR / BUAT PESANAN)
V.pos = () => {
  const c = ui.cart;
  const m = c.member ? S.members[c.member] : null;
  const filteredMenu = MENU.filter(it => {
    const matchCat = ui.cat === 'Semua' || it.c === ui.cat ||
      (ui.cat === 'Coffee' && (it.c === 'Coffee' || it.c === 'Kopi')) ||
      (ui.cat === 'Non Coffee' && (it.c === 'Non Coffee' || it.c === 'Nonkopi')) ||
      (ui.cat === 'Meal & Snack' && (it.c === 'Meal & Snack' || it.c === 'Makanan'));
    const matchQ = it.n.toLowerCase().includes(ui.q.toLowerCase()) ||
                   (it.d && it.d.toLowerCase().includes(ui.q.toLowerCase()));
    return matchCat && matchQ;
  });
  const totalItemCount = c.items.reduce((a, i) => a + i.q, 0);
  const totalPrice = tot(c.items);
  const orderTag = S.next < 10 ? `0${S.next}` : `${S.next}`;

  return `
    <h1>Kasir & Pemesanan Menu</h1>
    <p class="sub"></p>

    <div class="g2">
      <!-- SISI KIRI: KATALOG MENU -->
      <div>
        <div style="display:flex;gap:10px;align-items:center;margin-bottom:14px;">
          <input type="text" data-i="q" placeholder="🔍 Cari menu kopi, makanan, minuman..." value="${esc(ui.q)}" style="flex:1;">
          <button class="btn sm out" data-a="openCustomItem" style="white-space:nowrap;">+ Item Tambahan</button>
        </div>

        <!-- TAB KATEGORI LENGKAP FLINDERS COFFEE -->
        <div class="tabs" style="margin-bottom:12px;">
          ${[
            ['Semua', 'Semua Menu'],
            ['Coffee', '☕ Coffee'],
            ['Flavoured Coffee', '☕ Flavoured'],
            ['Signature', '✨ Signature'],
            ['Americano Series', '🫐 Americano Series'],
            ['Non Coffee', '🍵 Non Coffee'],
            ['Frappe', '🥤 Frappe'],
            ['Tea', '🫖 Tea'],
            ['Meal & Snack', '🥐 Meal & Snack']
          ].map(([cat, label]) => `
            <button class="tab ${ui.cat === cat ? 'on' : ''}" data-a="setCat" data-v="${cat}">
              ${label}
            </button>
          `).join('')}
        </div>

        <!-- INFO BANNER CATATAN MENU MEJA FISIK -->
        <div style="background:#f5efe2;padding:8px 14px;border-radius:var(--radius-sm);margin-bottom:14px;font-size:12px;color:var(--primary-dark);display:flex;flex-wrap:wrap;gap:12px;align-items:center;border:1px solid #e2d4bd;">
          <span>🔵 <b>ICED</b> &nbsp; 🔴 <b>HOT</b></span>
          <span>·</span>
          <span>➕ <b>Extra Shot</b>: +Rp 3.000</span>
          <span>·</span>
          <span>☕ <b>Full Arabica (Single Origin)</b>: +Rp 2.000</span>
        </div>

        <div class="menu-grid">
          ${filteredMenu.length ? filteredMenu.map(it => `
            <div class="menu-item" data-a="addItem" data-v="${it.n}">
              <div class="menu-item-icon">${it.ic}</div>
              <div class="menu-item-name">${it.n}</div>
              <div class="menu-item-desc">${it.d}</div>
              <div class="menu-item-footer">
                <span class="menu-item-price">${rp(it.p)}</span>
                <button class="btn-add-item" title="Tambah ke keranjang">+</button>
              </div>
            </div>
          `).join('') : `
            <div class="card" style="grid-column:1/-1;text-align:center;padding:48px 16px;color:var(--text-muted);">
              <div style="font-size:36px;margin-bottom:8px;">📋</div>
              <b style="font-size:15px;color:var(--text-title);display:block;margin-bottom:4px;">${MENU.length === 0 ? 'Belum Ada Menu di Database' : 'Menu Tidak Ditemukan'}</b>
              <span>${MENU.length === 0 ? 'Katalog menu dikelola dan diimpor oleh Owner melalui aplikasi.' : 'Silakan ganti kata kunci pencarian.'}</span>
            </div>
          `}
        </div>
      </div>

      <!-- SISI KANAN: PANEL KERANJANG -->
      <div>
        <div class="cart-panel">
          <div class="card-header">
            <div>
              <span class="card-title">Keranjang Pesanan</span>
              <span class="chip gold" style="margin-left:6px;">#FL-${orderTag}</span>
            </div>
            ${c.items.length ? `<button class="btn sm out danger" data-a="clearCart">Kosongkan</button>` : ''}
          </div>

          <!-- Tipe: Makan di Tempat / Bawa Pulang -->
          <div class="tabs" style="margin-bottom:14px;">
            <button class="tab ${c.type === 'dine' ? 'on' : ''}" data-a="setCartType" data-v="dine">🍽️ Makan di Tempat</button>
            <button class="tab ${c.type === 'takeaway' ? 'on' : ''}" data-a="setCartType" data-v="takeaway">🥡 Bawa Pulang</button>
          </div>

          <!-- Pilih Meja jika Dine In -->
          ${c.type === 'dine' ? `
            <div style="background:var(--bg);padding:10px 12px;border-radius:var(--radius-md);margin-bottom:14px;border:1px solid var(--border-light);">
              <div class="rw" style="margin-bottom:6px;">
                <b style="font-size:12.5px;">Pilih Meja Tamu:</b>
                <div class="tabs">
                  ${ROOMS.map(r => `
                    <button class="tab ${c.room === r ? 'on' : ''}" style="padding:2px 10px;font-size:11.5px;" data-a="setCartRoom" data-v="${r}">Ruang ${r}</button>
                  `).join('')}
                </div>
              </div>
              <div class="tbl-picker-grid">
                ${TABLES.map(n => {
                  const t = getTableStatus(c.room, n);
                  const isSel = c.table === n;
                  return `
                    <button class="tb-btn ${t.cls} ${isSel ? 'sel' : ''}" 
                            data-a="${t.free ? 'pickTable' : ''}" 
                            data-v="${n}" 
                            ${!t.free ? 'disabled' : ''}>
                      <b>Meja 0${n}</b>
                      <small>${isSel ? '✓ Dipilih' : t.label}</small>
                    </button>
                  `;
                }).join('')}
              </div>
              ${!c.table ? `<p style="font-size:11.5px;color:var(--danger);margin-top:4px;">* Pilih salah satu meja kosong untuk pesanan ini.</p>` : ''}
            </div>
          ` : ''}

          <!-- Member Flinders -->
          <div style="margin-bottom:12px;">
            <div style="display:flex;gap:8px;">
              <input type="text" data-i="fln" placeholder="Nomor Member (cth: FLN-482910)" value="${esc(c.fln)}" style="font-size:12.5px;padding:8px 12px;">
              <button class="btn sm out" data-a="findMember" style="flex:none;">Cari</button>
            </div>
            ${m ? `
              <div style="background:var(--primary-light);border:1px solid #c7e1cf;border-radius:var(--radius-sm);padding:8px 12px;margin-top:6px;display:flex;justify-content:space-between;align-items:center;">
                <div>
                  <b style="color:var(--primary);">${esc(m.name)}</b>
                  <div class="small mut">${c.member}</div>
                </div>
                <span class="chip gold" style="font-size:12px;">⭐ ${m.stamps}/10 Stamp</span>
              </div>
            ` : ''}
          </div>

          <!-- Daftar Item Keranjang -->
          <div class="cart-list">
            ${c.items.length ? c.items.map((it, idx) => `
              <div class="cart-row">
                <div class="cart-row-info">
                  <div class="cart-row-title">${esc(it.n)}</div>
                  <div class="cart-row-price">${rp(it.p)} × ${it.q}</div>
                </div>
                <div class="cart-qty">
                  <button data-a="cartDec" data-v="${idx}">−</button>
                  <span>${it.q}</span>
                  <button data-a="cartInc" data-v="${idx}">+</button>
                </div>
              </div>
            `).join('') : `
              <div style="text-align:center;padding:26px 10px;color:var(--text-muted);">
                <div style="font-size:26px;margin-bottom:6px;">🛒</div>
                Keranjang masih kosong.<br><small>Pilih menu di sebelah kiri untuk menambah pesanan.</small>
              </div>
            `}
          </div>

          <!-- Ringkasan Total & Aksi -->
          <div class="cart-total-box">
            <div class="rw">
              <span class="mut">Total Item</span>
              <b>${totalItemCount} item</b>
            </div>
            <div class="rw" style="margin-top:6px;">
              <b style="font-size:16px;">Total Tagihan</b>
              <span class="cart-total-val">${rp(totalPrice)}</span>
            </div>
          </div>

          <div style="display:flex;flex-direction:column;gap:8px;margin-top:14px;">
            <button class="btn" data-a="quickPay" ${!c.items.length || (c.type === 'dine' && !c.table) ? 'disabled' : ''}>
              💳 Bayar Sekarang (${rp(totalPrice)})
            </button>
            ${c.type === 'dine' ? `
              <button class="btn out" data-a="saveOpenBill" ${!c.items.length || !c.table ? 'disabled' : ''}>
                📋 Simpan ke Meja (Bayar Nanti)
              </button>
            ` : ''}
          </div>
        </div>
      </div>
    </div>
  `;
};

// 2. TAGIHAN MEJA (OPEN BILLS / PEMBAYARAN)
V.pembayaran = () => {
  const openOrders = S.orders.filter(o => ['Baru', 'Diterima', 'Disajikan'].includes(o.status));

  return `
    <h1>Tagihan Belum Selesai (Open Bills)</h1>
    <p class="sub"></p>

    ${openOrders.length ? `
      <div class="grid2">
        ${openOrders.map(o => {
          const m = o.member ? S.members[o.member] : null;
          return `
            <div class="card">
              <div class="card-header">
                <div>
                  <b style="font-size:16px;">#${o.id}</b>
                  <span class="chip gold" style="margin-left:6px;">${o.type === 'dine' ? `Ruang ${o.room} · Meja 0${o.table}` : 'Bawa Pulang'}</span>
                </div>
                <span class="chip ok">${o.status}</span>
              </div>
              <div style="margin:10px 0;">
                <div class="rw small mut">
                  <span>Waktu Pesan:</span>
                  <span>${hm(o.time)} WITA</span>
                </div>
                ${m ? `<div class="rw small mut"><span>Member:</span><b>${esc(m.name)}</b></div>` : ''}
              </div>
              <hr>
              <div style="margin:10px 0;">
                ${o.items.map(it => `
                  <div class="rw" style="font-size:13px;">
                    <span>${it.q}x ${esc(it.n)}</span>
                    <span>${rp(it.p * it.q)}</span>
                  </div>
                `).join('')}
              </div>
              <hr>
              <div class="rw" style="margin-bottom:14px;">
                <b>Total Tagihan</b>
                <b style="font-size:18px;color:var(--primary);">${rp(o.total)}</b>
              </div>
              <button class="btn" data-a="openBillPay" data-v="${o.id}">
                💳 Proses Pembayaran (${rp(o.total)})
              </button>
            </div>
          `;
        }).join('')}
      </div>
    ` : `
      <div class="card" style="text-align:center;padding:50px 20px;">
        <div style="font-size:36px;margin-bottom:10px;">✨</div>
        <span class="card-title" style="display:block;margin-bottom:6px;">Semua Tagihan Sudah Lunas</span>
        <p class="mut">Tidak ada pesanan aktif yang menunggu pembayaran saat ini.</p>
        <a href="#pos" class="btn sm" style="margin-top:16px;display:inline-block;width:auto;">Ke Menu Kasir (POS)</a>
      </div>
    `}
  `;
};

// 3. BOOKING MASUK
V.booking = () => {
  const pending = S.bookings.filter(b => b.status === 'Menunggu' && (ui.roomFilter === 'Semua' || b.room === ui.roomFilter));

  return `
    <div class="rw" style="margin-bottom:16px;">
      <div>
        <h1>Daftar Booking Masuk</h1>
        <p class="sub" style="margin-bottom:0;"></p>
      </div>
      <div class="tabs">
        ${['Semua', ...ROOMS].map(r => `
          <button class="tab ${ui.roomFilter === r ? 'on' : ''}" data-a="setRoomFilter" data-v="${r}">
            ${r === 'Semua' ? 'Semua Ruang' : 'Ruang ' + r}
          </button>
        `).join('')}
      </div>
    </div>

    <div class="g2">
      <!-- DAFTAR BOOKING -->
      <div style="display:flex;flex-direction:column;gap:12px;">
        ${pending.length ? pending.map(b => {
          const mLeft = rem(b);
          return `
            <div class="card" style="${mLeft <= 20 ? 'border-left:4px solid var(--gold);' : ''}">
              <div class="card-header">
                <div>
                  <b style="font-size:16px;">${esc(b.name)}</b>
                  <span class="chip gold" style="margin-left:8px;">${b.id}</span>
                </div>
                <span class="chip ${mLeft <= 20 ? 'red' : 'ok'}">⏱️ Sisa ${mLeft} menit</span>
              </div>
              <div class="rw" style="margin:10px 0;">
                <div>
                  <b>Ruang ${b.room} · Meja ${b.tables.map(t => '0' + t).join(' & ')}</b>
                  <div class="small mut">${b.pax} Orang · Jam: ${b.s} - ${b.e} WITA</div>
                </div>
                <div class="small mut">${b.fln}</div>
              </div>
              <div style="display:flex;gap:10px;margin-top:14px;">
                <button class="btn gold" style="flex:1;" data-a="bookingCheckin" data-v="${b.id}">
                  ✓ Tamu Hadir & Buka Pesanan
                </button>
                <button class="btn out danger sm" data-a="bookingCancel" data-v="${b.id}">
                  Batalkan
                </button>
              </div>
            </div>
          `;
        }).join('') : `
          <div class="card" style="text-align:center;padding:40px 20px;color:var(--text-muted);">
            Tidak ada reservasi menunggu saat ini.
          </div>
        `}
      </div>

      <!-- DENAH RUANG VISUAL -->
      <div>
        <div class="card">
          <div class="card-header">
            <span class="card-title">Peta Ruang & Meja</span>
            <div class="tabs">
              ${ROOMS.map(r => `
                <button class="tab ${ui.mapRoom === r ? 'on' : ''}" style="padding:2px 10px;font-size:11.5px;" data-a="setMapRoom" data-v="${r}">${r}</button>
              `).join('')}
            </div>
          </div>
          <div class="tbl-picker-grid">
            ${TABLES.map(n => {
              const t = getTableStatus(ui.mapRoom, n);
              return `
                <div class="tb-btn ${t.cls}">
                  <b>Meja 0${n}</b>
                  <small>${t.label}</small>
                </div>
              `;
            }).join('')}
          </div>
          <div style="display:flex;gap:12px;justify-content:center;margin-top:14px;font-size:11.5px;color:var(--text-muted);">
            <span>🟢 Kosong</span>
            <span>⚪ Terisi</span>
            <span>🟡 Reservasi</span>
          </div>
        </div>
      </div>
    </div>
  `;
};

// 4. RIWAYAT HARI INI
V.riwayat = () => {
  // Tampilan hanya untuk shift berjalan; setelah tutup kas daftar dikosongkan (data tetap tersimpan)
  const shiftOrders = S.closing ? [] : S.orders.filter(o =>
    ['Baru', 'Diterima', 'Disajikan'].includes(o.status) || Math.max(o.time || 0, o.paidAt || 0) >= S.shiftStart
  );
  const filtered = shiftOrders.filter(o => {
    const matchFilter = ui.historyFilter === 'Semua' || 
      (ui.historyFilter === 'Aktif' && ['Baru', 'Diterima', 'Disajikan'].includes(o.status)) ||
      o.status === ui.historyFilter;
    const matchQ = o.id.toLowerCase().includes(ui.historyQuery.toLowerCase()) ||
      (o.room && (o.room + o.table).toLowerCase().includes(ui.historyQuery.toLowerCase()));
    return matchFilter && matchQ;
  }).sort((a, b) => b.time - a.time);

  return `
    <h1>Riwayat Transaksi Hari Ini</h1>
    <p class="sub">${dl(Date.now())}</p>

    <div class="card">
      <div class="rw" style="margin-bottom:14px;flex-wrap:wrap;">
        <input type="text" data-i="hq" placeholder="🔍 Cari nomor pesanan (#FL-0xx) atau meja..." value="${esc(ui.historyQuery)}" style="max-width:320px;">
        <div class="tabs">
          ${['Semua', 'Aktif', 'Dibayar', 'Dibatalkan'].map(f => `
            <button class="tab ${ui.historyFilter === f ? 'on' : ''}" data-a="setHistoryFilter" data-v="${f}">${f}</button>
          `).join('')}
        </div>
      </div>

      <div style="overflow-x:auto;">
        <table>
          <thead>
            <tr>
              <th>ID PESANAN</th>
              <th>WAKTU</th>
              <th>LOKASI</th>
              <th>ITEM PESANAN</th>
              <th>TOTAL</th>
              <th>METODE</th>
              <th>STATUS</th>
              <th>AKSI</th>
            </tr>
          </thead>
          <tbody>
            ${filtered.length ? filtered.map(o => `
              <tr class="cl">
                <td><b>#${o.id}</b></td>
                <td>${hm(o.time)} WITA</td>
                <td>${o.type === 'dine' ? `Ruang ${o.room} · Meja 0${o.table}` : 'Bawa Pulang'}</td>
                <td style="font-size:12.5px;">${o.items.map(i => `${i.q}x ${i.n}`).join(', ')}</td>
                <td><b>${rp(o.total)}</b></td>
                <td>${o.method || '—'}</td>
                <td>
                  <span class="chip ${o.status === 'Dibayar' ? 'ok' : o.status === 'Dibatalkan' ? 'red' : 'gold'}">
                    ${o.status === 'Dibayar' ? '✓ ' : ''}${o.status}
                  </span>
                </td>
                <td>
                  <button class="btn sm out" data-a="viewReceipt" data-v="${o.id}">Detail / Struk</button>
                </td>
              </tr>
            `).join('') : `
              <tr>
                <td colspan="8" style="text-align:center;padding:24px;color:var(--text-muted);">${S.closing ? 'Kas sudah ditutup. Riwayat shift sebelumnya disembunyikan dari tampilan (data tetap tersimpan).' : 'Belum ada transaksi yang dilakukan pada shift ini.'}</td>
              </tr>
            `}
          </tbody>
        </table>
      </div>
    </div>
  `;
};

// 5. PENGELUARAN OPERASIONAL
V.pengeluaran = () => {
  // Hanya shift berjalan; setelah tutup kas daftar dikosongkan dari tampilan (data tetap tersimpan)
  const list = S.closing ? [] : S.expenses.filter(e => e.t >= S.shiftStart).reverse();
  const totalExpenses = S.closing ? 0 : spent();

  return `
    <h1>Pengeluaran Operasional</h1>
    <p class="sub"></p>

    <div class="g2">
      <!-- FORM INPUT -->
      <div class="card">
        <span class="card-title">Catat Pengeluaran Baru</span>
        <div style="margin-top:14px;">
          <label>Kategori Biaya</label>
          <select data-i="ecat">
            ${EXPENSE_CATS.map(c => `<option value="${c}" ${ui.exp.cat === c ? 'selected' : ''}>${c}</option>`).join('')}
          </select>

          <label>Nominal Pengeluaran (Rp) *</label>
          <input type="text" data-i="enom" inputmode="numeric" placeholder="Contoh: 50.000" value="${esc(ui.exp.nom)}">

          <label>Keperluan / Keterangan *</label>
          <input type="text" data-i="edesc" placeholder="Contoh: Es batu tambahan / tisu toilet" value="${esc(ui.exp.desc)}">

          <button class="btn" style="margin-top:16px;" data-a="submitExpense">
            Simpan Pengeluaran
          </button>
        </div>
      </div>

      <!-- DAFTAR PENGELUARAN HARI INI -->
      <div class="card">
        <div class="card-header">
          <span class="card-title">Daftar Pengeluaran Hari Ini</span>
          <span class="chip red">Total: ${rp(totalExpenses)}</span>
        </div>
        <div style="display:flex;flex-direction:column;gap:10px;margin-top:10px;">
          ${list.length ? list.map(e => `
            <div style="border:1px solid var(--border-light);border-radius:var(--radius-md);padding:12px;">
              <div class="rw">
                <b>${esc(e.desc)}</b>
                <b style="color:var(--danger);">${rp(e.nom)}</b>
              </div>
              <div class="rw small mut" style="margin-top:4px;">
                <span>${esc(e.cat)} · Oleh: ${e.by}</span>
                <span>${hm(e.t)} WITA</span>
              </div>
            </div>
          `).join('') : `<div style="text-align:center;padding:24px;color:var(--text-muted);">${S.closing ? 'Kas sudah ditutup. Pengeluaran shift sebelumnya disembunyikan dari tampilan (data tetap tersimpan).' : 'Belum ada pengeluaran dicatat pada shift ini.'}</div>`}
        </div>
      </div>
    </div>
  `;
};

// 6. TUTUP KAS
function syncOpen() {
  const amt = num(ui.openCash), last = S.lastCounted;
  const diff = last != null && amt && amt !== last;
  const row = $('#openNoteRow'), btn = $('#openShiftBtn');
  if (row) row.style.display = diff ? '' : 'none';
  if (btn) btn.disabled = !(amt && (!diff || ui.openNote.trim()));
}
function openForm() {
  const last = S.lastCounted, amt = num(ui.openCash);
  const diff = last != null && amt && amt !== last;
  return `
    <h1>Buka Kas</h1>
    <p class="sub"></p>
    <div class="card" style="max-width:520px;">
      <span class="card-title">Modal Kas Awal</span>
      ${last != null ? `<div class="small mut" style="margin:8px 0;">Kas akhir shift sebelumnya: <b>${rp(last)}</b></div>` : ''}
      <label>Modal kas awal di laci *</label>
      <input type="text" data-i="openCash" inputmode="numeric" placeholder="Masukkan modal awal (Rp)" value="${esc(ui.openCash)}">
      <div id="openNoteRow" style="${diff ? '' : 'display:none;'}">
        <label>Keterangan jika berbeda dari kas akhir sebelumnya *</label>
        <input type="text" data-i="openNote" placeholder="Cth: ditambah modal dari owner" value="${esc(ui.openNote)}">
      </div>
      <button id="openShiftBtn" class="btn" style="margin-top:16px;" data-a="openShift" ${amt && (!diff || ui.openNote.trim()) ? '' : 'disabled'}>Buka Kas &amp; Mulai Shift</button>
    </div>`;
}
V.tutup = () => {
  if (!S.opened && !S.closing) return openForm();
  const expCash = expectedCash();
  const c = S.closing;
  const counted = num(ui.closeCount);
  const diff = counted ? counted - expCash : 0;

  return `
    <h1>Tutup Kas</h1>
    <p class="sub"></p>

    <div class="g2">
      <!-- RINGKASAN SISTEM -->
      <div class="card">
        <span class="card-title">Rekapitulasi Saldo Laci Kasir</span>
        <div style="margin:16px 0;">
          <div class="rw">
            <span class="mut">Modal Kas Awal</span>
            <b>${rp(S.openCash)}</b>
          </div>
          <div class="rw" style="margin-top:8px;">
            <span class="mut">Total Penjualan Tunai Hari Ini</span>
            <b style="color:var(--success);">+ ${rp(cashIn())}</b>
          </div>
          <div class="rw" style="margin-top:8px;">
            <span class="mut">Pengeluaran dari Laci</span>
            <b style="color:var(--danger);">- ${rp(spent())}</b>
          </div>
          <hr>
          <div class="rw">
            <b style="font-size:16px;">Uang Tunai Seharusnya di Laci</b>
            <b style="font-size:22px;color:var(--primary);">${rp(expCash)}</b>
          </div>
          <div class="small mut" style="margin-top:6px;">
            * Pembayaran QRIS (${rp(qrisIn())}) langsung masuk rekening dan tidak masuk ke laci kasir fisik.
          </div>
        </div>
      </div>

      <!-- INPUT HITUNG FISIK -->
      <div class="card">
        <span class="card-title">Hitung Uang Fisik Kasir</span>
        ${c ? `
          <div class="change-box" style="margin-top:16px;">
            <div class="small mut">Kasir Ditutup Pukul ${hm(c.t)} WITA</div>
            <div class="change-val">${rp(c.counted)}</div>
            <div class="small mut">Oleh ${c.by} · Selisih: ${rp(c.diff)}</div>
            ${c.note ? `<div style="margin-top:6px;font-size:12px;"><b>Catatan:</b> ${esc(c.note)}</div>` : ''}
          </div>
          <button class="btn gold" style="margin-top:14px;" data-a="openNewShift">Buka Shift Baru</button>
          <div class="small mut" style="margin-top:8px;">Riwayat tutup kas tetap tersimpan. Modal kas awal shift baru diisi saat membuka shift.</div>
        ` : `
          <div style="margin-top:14px;">
            <label>Jumlah Uang Fisik Terhitung di Laci *</label>
            <input type="text" data-i="closeCount" inputmode="numeric" placeholder="Masukkan total uang di laci (Rp)" value="${esc(ui.closeCount)}">

            <div id="closeDiffBox" class="change-box ${counted && diff !== 0 ? 'short' : ''}" style="${counted ? '' : 'display:none;'}">
              <div class="small mut">Hasil Pencocokan Kas:</div>
              <div class="change-val" style="font-size:22px;">
                ${diff === 0 ? '✓ Sesuai (Kas Pas)' : (diff < 0 ? `Kurang ${rp(Math.abs(diff))}` : `Lebih ${rp(diff)}`)}
              </div>
            </div>

            <div id="closeNoteRow" style="${counted && diff !== 0 ? '' : 'display:none;'}">
              <label>Keterangan Selisih *</label>
              <input type="text" data-i="closeNote" placeholder="Jelaskan alasan selisih kas..." value="${esc(ui.closeNote)}">
            </div>

            <button id="closeCashBtn" class="btn" style="margin-top:16px;" data-a="submitCloseCash" ${!counted ? 'disabled' : ''}>
              Konfirmasi & Selesaikan Tutup Kas
            </button>
          </div>
        `}
      </div>
    </div>
  `;
};

// ==========================================
// MODAL PEMBAYARAN & STRUK
// ==========================================
function openPaymentModal(order) {
  ui.payId = order.id;
  ui.payMethod = 'Tunai';
  ui.payRecv = order.total;
  renderPaymentModal(order);
}

function renderPaymentModal(order) {
  const quickNominals = [order.total, 50000, 100000, 150000, 200000].filter((v, i, a) => v >= order.total && a.indexOf(v) === i);

  const html = `
    <div class="dlg-content">
      <div class="dlg-header">
        <div>
          <b class="dlg-title">Pembayaran Pesanan #${order.id}</b>
          <div class="small mut">${order.type === 'dine' ? `Ruang ${order.room} · Meja 0${order.table}` : 'Bawa Pulang'}</div>
        </div>
        <button class="dlg-close" onclick="closeModal()">✕</button>
      </div>

      <div class="rw" style="font-size:16px;margin-bottom:12px;">
        <span>Total Tagihan:</span>
        <b style="font-size:22px;color:var(--primary);">${rp(order.total)}</b>
      </div>

      <div class="tabs" style="margin-bottom:14px;">
        <button class="tab ${ui.payMethod === 'Tunai' ? 'on' : ''}" data-a="setPayMethod" data-v="Tunai">💵 Uang Tunai (Cash)</button>
        <button class="tab ${ui.payMethod === 'QRIS' ? 'on' : ''}" data-a="setPayMethod" data-v="QRIS">📱 QRIS</button>
      </div>

      ${ui.payMethod === 'Tunai' ? `
        <div>
          <label>Pilihan Cepat Nominal Uang:</label>
          <div class="quick-cash-grid">
            ${quickNominals.map(n => `
              <button class="btn-quick-cash" data-a="setQuickCash" data-v="${n}">
                ${n === order.total ? 'Uang Pas' : rp(n)}
              </button>
            `).join('')}
          </div>

          <label>Uang Tunai Diterima:</label>
          <input type="text" id="payRecvInput" data-i="payRecv" inputmode="numeric" value="${rp(ui.payRecv)}" style="font-size:16px;font-weight:700;">

          <div id="payChangeBox" class="change-box ${ui.payRecv < order.total ? 'short' : ''}">
            <div class="small mut">Kembalian Pelanggan:</div>
            <div class="change-val">${rp(Math.max(0, ui.payRecv - order.total))}</div>
            ${ui.payRecv < order.total ? `<small style="color:var(--danger)">Uang masih kurang ${rp(order.total - ui.payRecv)}</small>` : ''}
          </div>

          <button id="payFinishBtn" class="btn" data-a="finishPayment" ${ui.payRecv < order.total ? 'disabled' : ''}>
            Selesaikan Transaksi Tunai
          </button>
        </div>
      ` : `
        <div style="text-align:center;padding:12px 0;">
          <div style="background:#fff;border:2px solid var(--primary);border-radius:var(--radius-md);display:inline-block;padding:16px;margin:10px 0;">
            <img src="logo.png" style="width:60px;height:60px;border-radius:50%;margin-bottom:8px;">
            <div style="font-weight:700;font-size:16px;">FLINDERS COFFEE</div>
            <div class="small mut">NMID: ID10293847561</div>
            <div style="font-size:38px;margin:8px 0;">🏁</div>
            <div style="font-weight:700;font-size:18px;color:var(--primary);">${rp(order.total)}</div>
          </div>
          <p class="small mut" style="margin-bottom:14px;">Pastikan notifikasi berhasil di aplikasi kasir atau bukti di ponsel tamu sudah benar.</p>
          <button class="btn" data-a="finishPayment">
            Konfirmasi Pembayaran QRIS Sukses
          </button>
        </div>
      `}
    </div>
  `;
  showModal(html);
}

function updatePayChange() {
  const o = ord(ui.payId);
  if (!o) return;
  const box = $('#payChangeBox');
  const btn = $('#payFinishBtn');
  const diff = ui.payRecv - o.total;

  if (box) {
    box.className = 'change-box ' + (diff < 0 ? 'short' : '');
    box.innerHTML = `
      <div class="small mut">Kembalian Pelanggan:</div>
      <div class="change-val">${rp(Math.max(0, diff))}</div>
      ${diff < 0 ? `<small style="color:var(--danger)">Uang masih kurang ${rp(Math.abs(diff))}</small>` : ''}
    `;
  }
  if (btn) {
    btn.disabled = ui.payRecv < o.total;
  }
}

function openReceiptModal(order) {
  const m = order.member ? S.members[order.member] : null;
  const html = `
    <div class="dlg-content">
      <div class="dlg-header">
        <b class="dlg-title">Bukti Transaksi</b>
        <button class="dlg-close" onclick="closeModal()">✕</button>
      </div>

      <div class="receipt-card">
        <div class="receipt-header">
          <h3>FLINDERS COFFEE</h3>
          <div>Jl. Flinders No. 8, Makassar</div>
          <div class="small">${dl(order.time)} · ${hm(order.time)} WITA</div>
          <div class="small">Nota: #${order.id} | Kasir: Rani Putri</div>
          <div class="small">${order.type === 'dine' ? `Makan di Tempat (Ruang ${order.room}-0${order.table})` : 'Bawa Pulang'}</div>
        </div>
        <hr style="border-style:dashed;">
        <div>
          ${order.items.map(it => `
            <div class="rw" style="font-size:12.5px;">
              <span>${it.n} x${it.q}</span>
              <span>${rp(it.p * it.q)}</span>
            </div>
          `).join('')}
        </div>
        <hr style="border-style:dashed;">
        <div class="rw">
          <b>TOTAL</b>
          <b>${rp(order.total)}</b>
        </div>
        <div class="rw small">
          <span>Metode Bayar</span>
          <span>${order.method || 'Tunai'}</span>
        </div>
        ${order.paid && order.paid.recv ? `
          <div class="rw small">
            <span>Uang Diterima</span>
            <span>${rp(order.paid.recv)}</span>
          </div>
          <div class="rw small">
            <span>Kembalian</span>
            <span>${rp(order.paid.recv - order.total)}</span>
          </div>
        ` : ''}
        ${m ? `
          <hr style="border-style:dashed;">
          <div style="text-align:center;" class="small">
            Member: <b>${esc(m.name)}</b> (${m.stamps}/10 Stamp)
          </div>
        ` : ''}
        <div style="text-align:center;margin-top:14px;" class="small mut">
          Terima kasih atas kunjungan Anda!<br>Follow Instagram @flinderscoffee
        </div>
      </div>

      <div style="display:flex;gap:10px;margin-top:14px;">
        <button class="btn out" onclick="window.print()" style="flex:1;">🖨️ Cetak Struk</button>
        <button class="btn" onclick="closeModal()" style="flex:1;">Tutup</button>
      </div>
    </div>
  `;
  showModal(html);
}

// ==========================================
// ACTION HANDLERS
// ==========================================
const A = {
  setCat(v) { ui.cat = v; R(); },
  setCartType(v) { ui.cart.type = v; R(); },
  setCartRoom(v) { ui.cart.room = v; ui.cart.table = null; R(); },
  pickTable(v) { ui.cart.table = +v; R(); },
  addItem(name) {
    const item = MENU.find(m => m.n === name);
    if (!item) return;
    const existing = ui.cart.items.find(i => i.n === name);
    if (existing) existing.q++;
    else ui.cart.items.push({ n: item.n, p: item.p, hpp: item.hpp || Math.round(item.p * 0.4), q: 1 });
    R();
  },
  cartInc(idx) { ui.cart.items[idx].q++; R(); },
  cartDec(idx) {
    if (--ui.cart.items[idx].q <= 0) ui.cart.items.splice(idx, 1);
    R();
  },
  clearCart() {
    ui.cart.items = [];
    R();
  },
  findMember() {
    const code = ui.cart.fln.trim().toUpperCase();
    if (S.members[code]) {
      ui.cart.member = code;
      toast(`Member terhubung: ${S.members[code].name}`);
      R();
    } else {
      toast('Kode member tidak ditemukan. Periksa format (cth: FLN-482910)');
    }
  },
  openCustomItem() {
    const name = prompt('Nama item tambahan / kustom:');
    if (!name) return;
    const priceStr = prompt('Harga (Rp):', '10000');
    const price = num(priceStr);
    if (!price) return toast('Harga tidak valid.');
    ui.cart.items.push({ n: name, p: price, hpp: Math.round(price * 0.4), q: 1 });
    toast(`Item ${name} ditambahkan.`);
    R();
  },
  quickPay() {
    const c = ui.cart;
    if (!c.items.length) return toast('Pilih minimal satu menu.');
    if (c.type === 'dine' && !c.table) return toast('Pilih nomor meja terlebih dahulu.');

    const orderNum = S.next++;
    const orderTag = orderNum < 10 ? `0${orderNum}` : `${orderNum}`;
    const newOrder = {
      id: 'FL-' + orderTag,
      time: Date.now(),
      room: c.room,
      table: c.type === 'dine' ? c.table : null,
      type: c.type,
      items: [...c.items],
      total: tot(c.items),
      member: c.member,
      status: 'Baru',
      method: null,
      log: [{ t: Date.now(), x: 'Pesanan dibuat di kasir' }]
    };
    S.orders.push(newOrder);
    save();
    openPaymentModal(newOrder);
  },
  saveOpenBill() {
    const c = ui.cart;
    if (!c.items.length) return toast('Pilih minimal satu menu.');
    if (!c.table) return toast('Pilih nomor meja terlebih dahulu.');

    const orderNum = S.next++;
    const orderTag = orderNum < 10 ? `0${orderNum}` : `${orderNum}`;
    const newOrder = {
      id: 'FL-' + orderTag,
      time: Date.now(),
      room: c.room,
      table: c.table,
      type: 'dine',
      items: [...c.items],
      total: tot(c.items),
      member: c.member,
      status: 'Baru',
      method: null,
      log: [{ t: Date.now(), x: 'Pesanan disimpan untuk meja' }]
    };
    S.orders.push(newOrder);
    ui.cart = newCart();
    save();
    toast(`Pesanan Meja ${newOrder.room}-0${newOrder.table} tersimpan!`);
    R();
  },
  openBillPay(orderId) {
    const o = ord(orderId);
    if (o) openPaymentModal(o);
  },
  setPayMethod(v) {
    ui.payMethod = v;
    const o = ord(ui.payId);
    if (o) renderPaymentModal(o);
  },
  setQuickCash(v) {
    ui.payRecv = +v;
    const inp = $('#payRecvInput');
    if (inp) inp.value = rp(ui.payRecv);
    updatePayChange();
  },
  finishPayment() {
    const o = ord(ui.payId);
    if (!o) return;
    if (kasMsg()) return toast(kasMsg());
    o.status = 'Dibayar';
    o.paidAt = Date.now();
    o.method = ui.payMethod;
    o.paid = { recv: ui.payMethod === 'Tunai' ? ui.payRecv : o.total };

    // Update member stamp
    if (o.member && S.members[o.member]) {
      const m = S.members[o.member];
      if (m.stamps < 10 && m.last !== today()) {
        m.stamps++;
        m.last = today();
      }
    }

    save();
    ui.cart = newCart();
    closeModal();
    toast(`Pembayaran pesanan #${o.id} berhasil!`);
    openReceiptModal(o);

    // Kirim ke MySQL XAMPP jika service aktif (non-blocking)
    try {
      const totalHpp = o.items.reduce((acc, it) => acc + ((it.hpp || Math.round(it.p * 0.4)) * it.q), 0);
      fetch('http://localhost/flinders-api/api.php?action=create_order', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          id: o.id,
          customer: o.member && S.members[o.member] ? S.members[o.member].name : 'Pelanggan Umum',
          order_type: o.type,
          table_location: o.table ? `Ruang ${o.room}-0${o.table}` : 'Bawa Pulang',
          items: o.items,
          total: o.total,
          totalHpp: totalHpp,
          paymentMethod: o.method,
          cashReceived: o.paid.recv,
          cashier: 'Rani Putri'
        })
      }).catch(() => {});
    } catch (e) {}

    R();
  },
  viewReceipt(orderId) {
    const o = ord(orderId);
    if (o) openReceiptModal(o);
  },
  setRoomFilter(v) { ui.roomFilter = v; R(); },
  setMapRoom(v) { ui.mapRoom = v; R(); },
  bookingCheckin(id) {
    const b = S.bookings.find(x => x.id === id);
    if (!b) return;
    b.status = 'Hadir';
    save();
    toast(`Tamu ${b.name} hadir! Membuka kasir...`);
    ui.cart.type = 'dine';
    ui.cart.room = b.room;
    ui.cart.table = b.tables[0];
    ui.cart.fln = b.fln;
    if (S.members[b.fln]) ui.cart.member = b.fln;
    location.hash = '#pos';
  },
  bookingCancel(id) {
    const reason = prompt('Alasan pembatalan reservasi:', 'Tamu tidak hadir setelah waktu tunggu');
    if (!reason) return;
    const b = S.bookings.find(x => x.id === id);
    if (b) {
      b.status = 'Dibatalkan';
      b.cancelReason = reason;
      save();
      toast('Reservasi dibatalkan.');
      R();
    }
  },
  setHistoryFilter(v) { ui.historyFilter = v; R(); },
  submitExpense() {
    const nom = num(ui.exp.nom);
    const desc = ui.exp.desc.trim();
    if (kasMsg()) return toast(kasMsg());
    if (!nom || !desc) return toast('Nominal dan keperluan pengeluaran wajib diisi.');

    S.expenses.push({
      id: Date.now(),
      t: Date.now(),
      cat: ui.exp.cat,
      nom,
      desc,
      by: 'Rani Putri'
    });
    save();
    ui.exp.nom = '';
    ui.exp.desc = '';
    toast('Pengeluaran berhasil dicatat!');

    // Kirim ke MySQL XAMPP jika service aktif (non-blocking)
    try {
      fetch('http://localhost/flinders-api/api.php?action=add_expense', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ cat: ui.exp.cat, nom, desc, by: 'Rani Putri' })
      }).catch(() => {});
    } catch (e) {}

    R();
  },
  openNewShift() {
    const c = S.closing;
    if (!c) return;
    S.closings.push(c);
    S.lastCounted = c.counted;
    S.closing = null;
    S.opened = false;
    ui.closeCount = '';
    ui.closeNote = '';
    ui.openCash = c.counted ? c.counted.toLocaleString('id-ID') : '';
    ui.openNote = '';
    save();
    R();
  },
  openShift() {
    const amt = num(ui.openCash), last = S.lastCounted;
    if (!amt) return toast('Isi modal kas awal.');
    if (last != null && amt !== last && !ui.openNote.trim()) return toast('Modal berbeda dari kas akhir sebelumnya. Isi keterangan.');
    S.openings.push({ t: Date.now(), amount: amt, prev: last, note: ui.openNote.trim(), by: 'Rani Putri' });
    S.openCash = amt;
    S.shiftStart = Date.now();
    S.opened = true;
    ui.openCash = '';
    ui.openNote = '';
    save();
    toast('Kas dibuka. Kasir siap dipakai.');
    R();
  },
  submitCloseCash() {
    const counted = num(ui.closeCount);
    const expCash = expectedCash();
    const diff = counted - expCash;

    if (diff !== 0 && !ui.closeNote.trim()) {
      return toast('Harap isi keterangan alasan selisih kas.');
    }

    S.closing = {
      t: Date.now(),
      counted,
      expected: expCash,
      diff,
      note: ui.closeNote,
      by: 'Rani Putri'
    };
    save();
    toast('Tutup kas berhasil disimpan!');
    R();
  }
};

// ==========================================
// INPUT DISPATCHER
// ==========================================
const I = {
  openCash(v, el) { const n = num(v); ui.openCash = n ? n.toLocaleString('id-ID') : ''; el.value = ui.openCash; syncOpen(); },
  openNote(v) { ui.openNote = v; syncOpen(); },
  q(v) { ui.q = v; R(true); },
  fln(v) { ui.cart.fln = v; },
  hq(v) { ui.historyQuery = v; R(true); },
  ecat(v) { ui.exp.cat = v; },
  enom(v, el) {
    const n = num(v);
    ui.exp.nom = n ? n.toLocaleString('id-ID') : '';
    el.value = ui.exp.nom;
  },
  edesc(v) { ui.exp.desc = v; },
  closeCount(v, el) {
    const n = num(v);
    ui.closeCount = n ? n.toLocaleString('id-ID') : '';
    el.value = ui.closeCount;
    const diffBox = $('#closeDiffBox');
    const noteRow = $('#closeNoteRow');
    const btn = $('#closeCashBtn');
    const expCash = expectedCash();
    const diff = n - expCash;

    if (diffBox) {
      if (n > 0) {
        diffBox.style.display = 'block';
        diffBox.className = 'change-box ' + (diff !== 0 ? 'short' : '');
        diffBox.innerHTML = `
          <div class="small mut">Hasil Pencocokan Kas:</div>
          <div class="change-val" style="font-size:22px;">
            ${diff === 0 ? '✓ Sesuai (Kas Pas)' : (diff < 0 ? `Kurang ${rp(Math.abs(diff))}` : `Lebih ${rp(diff)}`)}
          </div>
        `;
      } else {
        diffBox.style.display = 'none';
      }
    }
    if (noteRow) {
      noteRow.style.display = (n > 0 && diff !== 0) ? 'block' : 'none';
    }
    if (btn) {
      btn.disabled = !(n > 0 && (diff === 0 || ui.closeNote.trim().length > 0));
    }
  },
  closeNote(v) {
    ui.closeNote = v;
    const counted = num(ui.closeCount);
    const expCash = expectedCash();
    const diff = counted - expCash;
    const btn = $('#closeCashBtn');
    if (btn) {
      btn.disabled = !(counted > 0 && (diff === 0 || v.trim().length > 0));
    }
  },
  payRecv(v) {
    ui.payRecv = num(v);
    updatePayChange();
  }
};

// Global Events
document.addEventListener('click', e => {
  const el = e.target.closest('[data-a]');
  if (el && A[el.dataset.a]) A[el.dataset.a](el.dataset.v, el);
});

document.addEventListener('input', e => {
  const f = e.target.dataset.i;
  if (f && I[f]) I[f](e.target.value, e.target);
});

document.addEventListener('focusout', e => {
  if (e.target.dataset.i === 'payRecv') {
    e.target.value = rp(ui.payRecv);
  }
});

// Render Function
function R(keepFocus) {
  const page = (location.hash.slice(1) || 'pos');
  const curNav = NAV.find(n => n[0] === page) || NAV[0];

  // 1. Render Sidebar (Tanpa kotak profil / shift di bawah)
  $('#side').innerHTML = `
    <div class="brand">
      <img src="logo.png" alt="Logo Flinders" class="brand-img">
      <div class="brand-info">
        <span class="brand-name">Flinders</span>
        <span class="brand-sub">Coffee</span>
      </div>
    </div>
    <div class="nav-links">
      ${NAV.map(n => {
        const isCur = n[0] === curNav[0];
        let badge = '';
        if (n[0] === 'booking') {
          const waitCount = S.bookings.filter(b => b.status === 'Menunggu').length;
          if (waitCount > 0) badge = `<span class="nav-badge">${waitCount}</span>`;
        } else if (n[0] === 'pembayaran') {
          const openCount = S.orders.filter(o => ['Baru', 'Diterima', 'Disajikan'].includes(o.status)).length;
          if (openCount > 0) badge = `<span class="nav-badge">${openCount}</span>`;
        }
        return `
          <a href="#${n[0]}" class="${isCur ? 'on' : ''}">
            ${ico(n[2])}
            <span>${n[1]}</span>
            ${badge}
          </a>
        `;
      }).join('')}
    </div>
  `;

  // 2. Render Header
  $('#top').innerHTML = `
    <div class="top-title">
      <span>Operasional Kasir</span>
      <span>/</span>
      <b>${curNav[1]}</b>
    </div>
    <div class="top-meta">
      <span class="chip ok"><span class="dot"></span> Online</span>
      <span id="clk" class="small mut">${dl(Date.now())} · ${hm(Date.now())} WITA</span>
    </div>
  `;

  // 3. Render Page Content
  const a = document.activeElement;
  const activeInput = keepFocus && a && a.dataset.i;
  const cursor = activeInput && a.selectionStart;

  const banner = (kasMsg() && curNav[0] !== 'tutup') ? `<div class="card" style="background:var(--gold-light);border-color:#edd49c;display:flex;justify-content:space-between;align-items:center;gap:12px;"><span>${kasMsg()}</span><a class="btn gold sm" href="#tutup">Ke Tutup Kas</a></div>` : '';
  $('#view').innerHTML = banner + (V[curNav[0]] ? V[curNav[0]]() : V.pos());

  if (activeInput) {
    const el = document.querySelector(`[data-i="${activeInput}"]`);
    if (el) {
      el.focus();
      el.setSelectionRange(cursor, cursor);
    }
  }
}

// Background Clock Update
function tick() {
  const c = $('#clk');
  if (c) c.textContent = `${dl(Date.now())} · ${hm(Date.now())} WITA`;
}

// Sinkronisasi Menu Dinamis dari Database MySQL XAMPP
// Jika database memiliki data (diinput/diimpor oleh Owner), data database akan meng-overwrite katalog tampilan ini.
async function syncMenusFromDatabase() {
  try {
    const res = await fetch('http://localhost/flinders-api/api.php?action=get_menus&active=1');
    const json = await res.json();
    if (json.status === 'success' && Array.isArray(json.data) && json.data.length > 0) {
      MENU = json.data.map(m => {
        let ic = '☕';
        const c = (m.category || '').toLowerCase();
        if (c.includes('non') || c === 'nonkopi') ic = '🍵';
        else if (c.includes('tea') || c.includes('teh')) ic = '🫖';
        else if (c.includes('frappe')) ic = '🥤';
        else if (c.includes('signature')) ic = '✨';
        else if (c.includes('americano')) ic = '🫐';
        else if (c.includes('snack') || c.includes('meal') || c.includes('makanan') || c.includes('pastry')) ic = '🥐';
        return {
          n: m.name,
          p: parseFloat(m.price),
          hpp: parseFloat(m.hpp),
          c: m.category,
          ic: ic,
          d: m.description || ''
        };
      });
      R();
      console.log('Katalog menu berhasil dimuat dari database MySQL XAMPP (' + MENU.length + ' menu, menimpa menu default)');
    } else {
      console.log('Database menu kosong; menggunakan katalog fisik Flinders Coffee bawaan (' + MENU.length + ' menu)');
    }
  } catch (e) {
    console.log('Menggunakan katalog lokal bawaan (offline mode / API unreachable)');
  }
}

window.addEventListener('hashchange', () => R());
R();
syncMenusFromDatabase();
setInterval(tick, 30000);
