<?php
/**
 * FLINDERS COFFEE - CORE BACKEND REST API (XAMPP MYSQL)
 * Version: 2.0 Final / Production-Ready
 * Endpoints melayani:
 * - Web POS Kasir Laptop (flinders-pos & simple-pos)
 * - Aplikasi Mobile Android Owner & Member (com.flinders.coffee)
 */

header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
header('Content-Type: application/json; charset=UTF-8');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// -------------------------------------------------------------
// KONEKSI DATABASE MYSQL XAMPP
// -------------------------------------------------------------
$host = 'localhost';
$user = 'root';
$pass = '';
$dbname = 'flinders_db';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $user, $pass, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
    ]);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Gagal koneksi ke MySQL XAMPP: ' . $e->getMessage() . '. Pastikan MySQL di XAMPP Control Panel aktif.'
    ]);
    exit();
}

// -------------------------------------------------------------
// HELPER FUNCTIONS
// -------------------------------------------------------------
function getJsonInput() {
    $raw = file_get_contents('php://input');
    $decoded = json_decode($raw, true);
    return is_array($decoded) ? $decoded : $_POST;
}

function sendResponse($status, $dataOrMessage, $extra = []) {
    $response = ['status' => $status];
    if (is_string($dataOrMessage)) {
        $response['message'] = $dataOrMessage;
    } elseif (is_array($dataOrMessage)) {
        $response['data'] = $dataOrMessage;
    }
    foreach ($extra as $k => $v) {
        $response[$k] = $v;
    }
    echo json_encode($response, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
    exit();
}

$action = $_GET['action'] ?? ($input['action'] ?? '');
$input = getJsonInput();

// -------------------------------------------------------------
// ROUTER AKSI API
// -------------------------------------------------------------
switch ($action) {

    // =========================================================
    // 1. AUTENTIKASI (USERS)
    // =========================================================
    case 'login':
        $username = trim($input['username'] ?? '');
        $password = trim($input['password'] ?? '');

        if (!$username || !$password) {
            sendResponse('error', 'Username dan password wajib diisi');
        }

        $stmt = $pdo->prepare("SELECT * FROM users WHERE (username = ? OR email = ?) LIMIT 1");
        $stmt->execute([$username, $username]);
        $u = $stmt->fetch();

        if ($u && ($u['password'] === $password || password_verify($password, $u['password']))) {
            unset($u['password']);
            sendResponse('success', $u, ['message' => 'Login berhasil']);
        } else {
            sendResponse('error', 'Username/Email atau password salah');
        }
        break;

    case 'register':
        $username = trim($input['username'] ?? '');
        $email = trim($input['email'] ?? null);
        $password = trim($input['password'] ?? '');
        $name = trim($input['full_name'] ?? $username);
        $role = in_array($input['role'] ?? '', ['owner', 'kasir', 'barista', 'member']) ? $input['role'] : 'member';
        $phone = trim($input['phone'] ?? null);

        if (!$username || !$password) {
            sendResponse('error', 'Username dan password wajib diisi');
        }

        try {
            $stmt = $pdo->prepare("INSERT INTO users (username, email, password, full_name, role, phone) VALUES (?, ?, ?, ?, ?, ?)");
            $stmt->execute([$username, $email, $password, $name, $role, $phone]);
            sendResponse('success', 'Pendaftaran akun pengguna berhasil', ['id' => $pdo->lastInsertId()]);
        } catch (PDOException $e) {
            sendResponse('error', 'Username atau email sudah digunakan: ' . $e->getMessage());
        }
        break;

    // =========================================================
    // 2. KATALOG MENU & HPP (MENUS)
    // =========================================================
    case 'get_menus':
        $onlyActive = isset($_GET['active']) && $_GET['active'] === '1';
        $category = $_GET['category'] ?? '';

        $sql = "SELECT * FROM menus WHERE 1=1";
        $params = [];

        if ($onlyActive) {
            $sql .= " AND is_active = 1";
        }
        if ($category && $category !== 'Semua') {
            $sql .= " AND category = ?";
            $params[] = $category;
        }
        $sql .= " ORDER BY category ASC, name ASC";

        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        $menus = $stmt->fetchAll();
        sendResponse('success', $menus, ['count' => count($menus)]);
        break;

    case 'get_menu':
        $id = $_GET['id'] ?? '';
        $stmt = $pdo->prepare("SELECT * FROM menus WHERE id = ?");
        $stmt->execute([$id]);
        $menu = $stmt->fetch();
        if ($menu) {
            sendResponse('success', $menu);
        } else {
            sendResponse('error', 'Menu tidak ditemukan');
        }
        break;

    case 'save_menu':
        $id = trim($input['id'] ?? '');
        if (!$id) {
            $id = 'MN-' . strtoupper(substr(uniqid(), -5));
        }
        $name = trim($input['name'] ?? '');
        $cat = trim($input['category'] ?? 'Coffee');
        $price = floatval($input['price'] ?? 0);
        $hpp = floatval($input['hpp'] ?? 0);
        $isActive = isset($input['isActive']) ? ($input['isActive'] ? 1 : 0) : 1;
        $desc = trim($input['description'] ?? '');
        $eff = $input['effectiveDate'] ?? date('d M Y');
        $img = $input['image_url'] ?? null;

        if (empty($name) || $price <= 0) {
            sendResponse('error', 'Nama menu dan harga jual wajib diisi (> 0)');
        }

        $stmt = $pdo->prepare("
            INSERT INTO menus (id, name, category, price, hpp, is_active, description, image_url, effective_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                name = VALUES(name),
                category = VALUES(category),
                price = VALUES(price),
                hpp = VALUES(hpp),
                is_active = VALUES(is_active),
                description = VALUES(description),
                image_url = VALUES(image_url),
                effective_date = VALUES(effective_date)
        ");
        $stmt->execute([$id, $name, $cat, $price, $hpp, $isActive, $desc, $img, $eff]);
        sendResponse('success', 'Menu berhasil disimpan ke database MySQL', ['id' => $id]);
        break;

    case 'toggle_menu':
        $id = $input['id'] ?? ($_GET['id'] ?? '');
        if ($id) {
            $stmt = $pdo->prepare("UPDATE menus SET is_active = IF(is_active = 1, 0, 1) WHERE id = ?");
            $stmt->execute([$id]);

            $check = $pdo->prepare("SELECT is_active FROM menus WHERE id = ?");
            $check->execute([$id]);
            $newStatus = $check->fetchColumn();

            sendResponse('success', 'Status menu diperbarui', ['id' => $id, 'is_active' => (int)$newStatus]);
        } else {
            sendResponse('error', 'ID menu tidak valid');
        }
        break;

    case 'delete_menu':
        $id = $input['id'] ?? ($_GET['id'] ?? '');
        if ($id) {
            $stmt = $pdo->prepare("DELETE FROM menus WHERE id = ?");
            $stmt->execute([$id]);
            sendResponse('success', 'Menu berhasil dihapus dari database');
        } else {
            sendResponse('error', 'ID menu tidak valid');
        }
        break;

    case 'import_menus':
        $items = $input['items'] ?? [];
        if (!is_array($items) || empty($items)) {
            sendResponse('error', 'Format data impor spreadsheet tidak valid atau kosong');
        }
        $count = 0;
        foreach ($items as $it) {
            $name = trim($it['name'] ?? '');
            if (!$name) continue;
            $id = !empty($it['id']) ? $it['id'] : ('MN-' . strtoupper(substr(uniqid(), -5)));
            $cat = !empty($it['category']) ? $it['category'] : 'Coffee';
            $price = floatval($it['price'] ?? 0);
            $hpp = floatval($it['hpp'] ?? 0);
            $isActive = isset($it['isActive']) ? ($it['isActive'] ? 1 : 0) : 1;
            $desc = $it['description'] ?? '';
            $eff = $it['effectiveDate'] ?? date('d M Y');

            $stmt = $pdo->prepare("
                INSERT INTO menus (id, name, category, price, hpp, is_active, description, effective_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                ON DUPLICATE KEY UPDATE
                    category = VALUES(category),
                    price = VALUES(price),
                    hpp = VALUES(hpp),
                    is_active = VALUES(is_active),
                    description = VALUES(description),
                    effective_date = VALUES(effective_date)
            ");
            $stmt->execute([$id, $name, $cat, $price, $hpp, $isActive, $desc, $eff]);
            $count++;
        }
        sendResponse('success', "$count menu berhasil diimpor & disinkronkan ke database MySQL");
        break;

    // =========================================================
    // 3. DENAH & STATUS MEJA (TABLES)
    // =========================================================
    case 'get_tables':
        $room = $_GET['room'] ?? '';
        $sql = "SELECT * FROM tables";
        $params = [];
        if ($room && $room !== 'Semua') {
            $sql .= " WHERE room_code = ?";
            $params[] = $room;
        }
        $sql .= " ORDER BY room_code ASC, table_number ASC";
        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        $tables = $stmt->fetchAll();
        sendResponse('success', $tables, ['count' => count($tables)]);
        break;

    case 'update_table':
        $id = $input['id'] ?? ($_GET['id'] ?? '');
        $status = strtoupper($input['status'] ?? 'AVAILABLE');
        $orderId = $input['order_id'] ?? null;
        $customer = $input['customer'] ?? null;

        if ($id && in_array($status, ['AVAILABLE', 'OCCUPIED', 'BOOKED'])) {
            $stmt = $pdo->prepare("UPDATE tables SET status = ?, current_order_id = ?, customer_name = ? WHERE id = ?");
            $stmt->execute([$status, $orderId, $customer, $id]);
            sendResponse('success', "Status meja $id berhasil diupdate ke $status");
        } else {
            sendResponse('error', 'Parameter ID atau status meja tidak valid');
        }
        break;

    case 'reset_tables':
        $pdo->query("UPDATE tables SET status = 'AVAILABLE', current_order_id = NULL, customer_name = NULL");
        sendResponse('success', 'Semua meja berhasil direset menjadi KOSONG / AVAILABLE');
        break;

    // =========================================================
    // 4. TRANSAKSI PENJUALAN KASIR (ORDERS)
    // =========================================================
    case 'create_order':
        $id = $input['id'] ?? ('FL-' . date('YmdHis'));
        $customer = $input['customer'] ?? ($input['customer_name'] ?? 'Pelanggan Umum');
        $type = $input['order_type'] ?? 'dine';
        $room = $input['room'] ?? ($input['room_code'] ?? null);
        $tableNum = isset($input['table']) ? intval($input['table']) : (isset($input['table_number']) ? intval($input['table_number']) : null);
        $tableLoc = $input['table_location'] ?? ($tableNum ? "Ruang $room · Meja 0$tableNum" : null);

        $items = $input['items'] ?? [];
        $itemsJson = is_string($items) ? $items : json_encode($items, JSON_UNESCAPED_UNICODE);

        $subtotal = floatval($input['subtotal'] ?? 0);
        $discount = floatval($input['discount'] ?? 0);
        $total = floatval($input['total'] ?? ($input['total_amount'] ?? 0));
        $totalHpp = floatval($input['totalHpp'] ?? ($input['total_hpp'] ?? ($total * 0.38)));
        $method = $input['paymentMethod'] ?? ($input['payment_method'] ?? 'Tunai');
        $received = floatval($input['cashReceived'] ?? ($input['cash_received'] ?? $total));
        $change = floatval($input['change'] ?? ($input['cash_change'] ?? max(0, $received - $total)));
        $cashier = $input['cashier'] ?? ($input['cashier_name'] ?? 'Rani Putri');
        $memberCode = $input['memberCode'] ?? ($input['member_code'] ?? null);

        $stmt = $pdo->prepare("
            INSERT INTO orders (id, customer_name, order_type, room_code, table_number, table_location, items_json, subtotal, discount, total_amount, total_hpp, payment_method, cash_received, cash_change, payment_status, cashier_name, member_code)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Dibayar', ?, ?)
        ");
        $stmt->execute([$id, $customer, $type, $room, $tableNum, $tableLoc, $itemsJson, $subtotal, $discount, $total, $totalHpp, $method, $received, $change, $cashier, $memberCode]);

        // Jika Dine In dengan Meja -> Otomatis Set Meja Terisi (OCCUPIED)
        if ($type === 'dine' && $room && $tableNum) {
            $tableId = $room . str_pad($tableNum, 2, '0', STR_PAD_LEFT);
            $stmtTbl = $pdo->prepare("UPDATE tables SET status = 'OCCUPIED', current_order_id = ?, customer_name = ? WHERE id = ?");
            $stmtTbl->execute([$id, $customer, $tableId]);
        }

        // Jika ada Member Code -> Tambah Kunjungan & Stamp
        if ($memberCode) {
            $itemCount = is_array($items) ? array_sum(array_column($items, 'q')) : 1;
            $stampsToAdd = max(1, $itemCount);
            $stmtMem = $pdo->prepare("
                UPDATE members 
                SET stamps_count = stamps_count + ?, total_visits = total_visits + 1, last_stamp_date = CURRENT_DATE 
                WHERE member_code = ?
            ");
            $stmtMem->execute([$stampsToAdd, $memberCode]);
        }

        sendResponse('success', 'Transaksi kasir berhasil disimpan ke database MySQL', ['id' => $id]);
        break;

    case 'get_orders':
        $limit = isset($_GET['limit']) ? intval($_GET['limit']) : 50;
        $status = $_GET['status'] ?? '';
        $date = $_GET['date'] ?? '';

        $sql = "SELECT * FROM orders WHERE 1=1";
        $params = [];

        if ($status && $status !== 'Semua') {
            $sql .= " AND payment_status = ?";
            $params[] = $status;
        }
        if ($date) {
            $sql .= " AND DATE(created_at) = ?";
            $params[] = $date;
        }
        $sql .= " ORDER BY created_at DESC LIMIT $limit";

        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        $orders = $stmt->fetchAll();

        foreach ($orders as &$o) {
            $o['items'] = json_decode($o['items_json'], true) ?: [];
            unset($o['items_json']);
        }

        sendResponse('success', $orders, ['count' => count($orders)]);
        break;

    case 'get_order':
        $id = $_GET['id'] ?? '';
        $stmt = $pdo->prepare("SELECT * FROM orders WHERE id = ?");
        $stmt->execute([$id]);
        $order = $stmt->fetch();
        if ($order) {
            $order['items'] = json_decode($order['items_json'], true) ?: [];
            unset($order['items_json']);
            sendResponse('success', $order);
        } else {
            sendResponse('error', 'Pesanan tidak ditemukan');
        }
        break;

    case 'update_order_status':
        $id = $input['id'] ?? ($_GET['id'] ?? '');
        $status = $input['status'] ?? '';
        if ($id && $status) {
            $stmt = $pdo->prepare("UPDATE orders SET payment_status = ? WHERE id = ?");
            $stmt->execute([$status, $id]);

            // Jika status selesai / batal, bersihkan meja terkait
            if (in_array($status, ['Dibayar', 'Batal', 'Selesai'])) {
                $pdo->prepare("UPDATE tables SET status = 'AVAILABLE', current_order_id = NULL, customer_name = NULL WHERE current_order_id = ?")->execute([$id]);
            }

            sendResponse('success', "Status pesanan $id diupdate menjadi $status");
        } else {
            sendResponse('error', 'ID pesanan atau status tidak valid');
        }
        break;

    // =========================================================
    // 5. PENGELUARAN OPERASIONAL (EXPENSES)
    // =========================================================
    case 'get_expenses':
        $limit = isset($_GET['limit']) ? intval($_GET['limit']) : 50;
        $month = $_GET['month'] ?? '';
        $sql = "SELECT * FROM expenses WHERE 1=1";
        $params = [];

        if ($month) {
            $sql .= " AND DATE_FORMAT(expense_date, '%Y-%m') = ?";
            $params[] = $month;
        }
        $sql .= " ORDER BY created_at DESC LIMIT $limit";

        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        $expenses = $stmt->fetchAll();
        sendResponse('success', $expenses, ['count' => count($expenses)]);
        break;

    case 'add_expense':
        $cat = trim($input['category'] ?? ($input['cat'] ?? 'Lainnya'));
        $nom = floatval($input['amount'] ?? ($input['nom'] ?? 0));
        $desc = trim($input['description'] ?? ($input['desc'] ?? ''));
        $by = trim($input['recorded_by'] ?? ($input['by'] ?? 'Rani Putri'));
        $date = $input['expense_date'] ?? ($input['date'] ?? date('Y-m-d'));

        if ($nom <= 0 || empty($desc)) {
            sendResponse('error', 'Nominal pengeluaran (> 0) dan keterangan wajib diisi');
        }

        $stmt = $pdo->prepare("INSERT INTO expenses (category, amount, description, recorded_by, expense_date) VALUES (?, ?, ?, ?, ?)");
        $stmt->execute([$cat, $nom, $desc, $by, $date]);

        sendResponse('success', 'Pengeluaran berhasil dicatat ke database MySQL', ['id' => $pdo->lastInsertId()]);
        break;

    case 'delete_expense':
        $id = intval($input['id'] ?? ($_GET['id'] ?? 0));
        if ($id > 0) {
            $stmt = $pdo->prepare("DELETE FROM expenses WHERE id = ?");
            $stmt->execute([$id]);
            sendResponse('success', 'Pengeluaran berhasil dihapus');
        } else {
            sendResponse('error', 'ID pengeluaran tidak valid');
        }
        break;

    // =========================================================
    // 6. BOOKING & RESERVASI MEJA (BOOKINGS)
    // =========================================================
    case 'get_bookings':
        $status = $_GET['status'] ?? '';
        $date = $_GET['date'] ?? '';

        $sql = "SELECT * FROM bookings WHERE 1=1";
        $params = [];

        if ($status && $status !== 'Semua') {
            $sql .= " AND status = ?";
            $params[] = $status;
        }
        if ($date) {
            $sql .= " AND booking_date = ?";
            $params[] = $date;
        }
        $sql .= " ORDER BY booking_date ASC, booking_time ASC";

        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        $bookings = $stmt->fetchAll();
        sendResponse('success', $bookings, ['count' => count($bookings)]);
        break;

    case 'create_booking':
        $id = $input['id'] ?? ('BK-' . date('YmdHis'));
        $name = trim($input['customer_name'] ?? '');
        $phone = trim($input['customer_phone'] ?? '');
        $bDate = $input['booking_date'] ?? date('Y-m-d');
        $bTime = $input['booking_time'] ?? date('H:i:s');
        $duration = intval($input['duration_hours'] ?? 2);
        $guests = intval($input['guest_count'] ?? 2);
        $room = $input['room_code'] ?? 'A';
        $tables = $input['table_numbers'] ?? '01';
        $dp = floatval($input['dp_amount'] ?? 0);
        $notes = $input['notes'] ?? '';

        if (!$name || !$phone) {
            sendResponse('error', 'Nama pemesan dan no WhatsApp/HP wajib diisi');
        }

        $stmt = $pdo->prepare("
            INSERT INTO bookings (id, customer_name, customer_phone, booking_date, booking_time, duration_hours, guest_count, room_code, table_numbers, dp_amount, status, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Menunggu', ?)
        ");
        $stmt->execute([$id, $name, $phone, $bDate, $bTime, $duration, $guests, $room, $tables, $dp, $notes]);

        sendResponse('success', 'Reservasi meja berhasil disimpan ke database', ['id' => $id]);
        break;

    case 'update_booking_status':
        $id = $input['id'] ?? ($_GET['id'] ?? '');
        $status = $input['status'] ?? 'Menunggu';

        if ($id && in_array($status, ['Menunggu', 'Hadir', 'Selesai', 'Batal'])) {
            $stmt = $pdo->prepare("UPDATE bookings SET status = ? WHERE id = ?");
            $stmt->execute([$status, $id]);
            sendResponse('success', "Status reservasi $id diupdate menjadi $status");
        } else {
            sendResponse('error', 'ID atau status booking tidak valid');
        }
        break;

    // =========================================================
    // 7. MEMBER LOYALTY & STAMP (MEMBERS)
    // =========================================================
    case 'get_members':
        $search = trim($_GET['search'] ?? '');
        $sql = "SELECT * FROM members";
        $params = [];
        if ($search) {
            $sql .= " WHERE member_code LIKE ? OR name LIKE ? OR phone LIKE ?";
            $params = ["%$search%", "%$search%", "%$search%"];
        }
        $sql .= " ORDER BY stamps_count DESC, name ASC";
        $stmt = $pdo->prepare($sql);
        $stmt->execute($params);
        $members = $stmt->fetchAll();
        sendResponse('success', $members, ['count' => count($members)]);
        break;

    case 'get_member':
        $code = $_GET['code'] ?? '';
        $stmt = $pdo->prepare("SELECT * FROM members WHERE member_code = ? LIMIT 1");
        $stmt->execute([$code]);
        $member = $stmt->fetch();
        if ($member) {
            sendResponse('success', $member);
        } else {
            sendResponse('error', 'Member tidak ditemukan');
        }
        break;

    case 'add_stamp':
        $code = $input['member_code'] ?? ($_GET['code'] ?? '');
        $count = intval($input['count'] ?? 1);
        if ($code && $count > 0) {
            $stmt = $pdo->prepare("UPDATE members SET stamps_count = stamps_count + ?, total_visits = total_visits + 1, last_stamp_date = CURRENT_DATE WHERE member_code = ?");
            $stmt->execute([$count, $code]);

            $check = $pdo->prepare("SELECT stamps_count FROM members WHERE member_code = ?");
            $check->execute([$code]);
            $currentStamps = $check->fetchColumn();

            sendResponse('success', "Berhasil menambahkan $count stamp loyalty", ['stamps_count' => (int)$currentStamps]);
        } else {
            sendResponse('error', 'Kode member atau jumlah stamp tidak valid');
        }
        break;

    case 'redeem_stamp':
        $code = $input['member_code'] ?? ($_GET['code'] ?? '');
        $count = intval($input['count'] ?? 10);
        if ($code && $count > 0) {
            $check = $pdo->prepare("SELECT stamps_count FROM members WHERE member_code = ?");
            $check->execute([$code]);
            $currentStamps = (int)$check->fetchColumn();

            if ($currentStamps < $count) {
                sendResponse('error', "Stamp tidak cukup ($currentStamps stamp tersisa, butuh $count)");
            }

            $stmt = $pdo->prepare("UPDATE members SET stamps_count = stamps_count - ? WHERE member_code = ?");
            $stmt->execute([$count, $code]);

            sendResponse('success', "Hadiah voucher berhasil ditukar ($count stamp digunakan)", ['remaining_stamps' => $currentStamps - $count]);
        } else {
            sendResponse('error', 'Kode member atau jumlah stamp tidak valid');
        }
        break;

    case 'register_member':
        $code = trim($input['member_code'] ?? ('FLN-' . rand(100000, 999999)));
        $name = trim($input['name'] ?? '');
        $phone = trim($input['phone'] ?? '');
        $email = trim($input['email'] ?? '');

        if (!$name) {
            sendResponse('error', 'Nama member wajib diisi');
        }

        $stmt = $pdo->prepare("INSERT INTO members (member_code, name, phone, email, stamps_count, total_visits) VALUES (?, ?, ?, ?, 0, 1)");
        $stmt->execute([$code, $name, $phone, $email]);

        sendResponse('success', 'Pendaftaran member loyalty berhasil', ['member_code' => $code]);
        break;

    // =========================================================
    // 8. RINGKASAN FINANSIAL & OWNER DASHBOARD (SUMMARY)
    // =========================================================
    case 'get_summary':
        $period = $_GET['period'] ?? ''; // Format YYYY-MM
        $today = $_GET['today'] ?? '';   // Format YYYY-MM-DD

        $ordersSql = "SELECT COALESCE(SUM(total_amount), 0) AS total_sales, COALESCE(SUM(total_hpp), 0) AS total_hpp, COUNT(*) AS total_orders FROM orders WHERE payment_status = 'Dibayar'";
        $expensesSql = "SELECT COALESCE(SUM(amount), 0) AS total_expenses FROM expenses WHERE 1=1";

        $paramsOrders = [];
        $paramsExpenses = [];

        if ($period) {
            $ordersSql .= " AND DATE_FORMAT(created_at, '%Y-%m') = ?";
            $expensesSql .= " AND DATE_FORMAT(expense_date, '%Y-%m') = ?";
            $paramsOrders[] = $period;
            $paramsExpenses[] = $period;
        } elseif ($today) {
            $ordersSql .= " AND DATE(created_at) = ?";
            $expensesSql .= " AND expense_date = ?";
            $paramsOrders[] = $today;
            $paramsExpenses[] = $today;
        }

        $stmtOrders = $pdo->prepare($ordersSql);
        $stmtOrders->execute($paramsOrders);
        $salesData = $stmtOrders->fetch();

        $stmtExpenses = $pdo->prepare($expensesSql);
        $stmtExpenses->execute($paramsExpenses);
        $expenseData = $stmtExpenses->fetch();

        $totalSales = floatval($salesData['total_sales']);
        $totalHpp = floatval($salesData['total_hpp']);
        $totalExpenses = floatval($expenseData['total_expenses']);
        $grossProfit = $totalSales - $totalHpp;
        $netProfit = $grossProfit - $totalExpenses;
        $totalOrders = intval($salesData['total_orders']);

        // Data status meja
        $tblStmt = $pdo->query("SELECT status, COUNT(*) as count FROM tables GROUP BY status");
        $tableCounts = ['AVAILABLE' => 0, 'OCCUPIED' => 0, 'BOOKED' => 0];
        while ($row = $tblStmt->fetch()) {
            $tableCounts[$row['status']] = intval($row['count']);
        }

        sendResponse('success', [
            'total_sales' => $totalSales,
            'total_hpp' => $totalHpp,
            'gross_profit' => $grossProfit,
            'total_expenses' => $totalExpenses,
            'net_profit' => $netProfit,
            'total_orders' => $totalOrders,
            'table_status' => $tableCounts
        ]);
        break;

    // =========================================================
    // 9. REKAP SHIFT & TUTUP KAS (SHIFT_CLOSINGS)
    // =========================================================
    case 'save_closing':
        $shiftDate = $input['shift_date'] ?? date('Y-m-d');
        $openedAt = $input['opened_at'] ?? date('Y-m-d 08:00:00');
        $closedAt = $input['closed_at'] ?? date('Y-m-d H:i:s');
        $cashier = $input['cashier_name'] ?? 'Rani Putri';
        $openCash = floatval($input['opening_cash'] ?? 500000);
        $cashSales = floatval($input['total_cash_sales'] ?? 0);
        $qrisSales = floatval($input['total_qris_sales'] ?? 0);
        $exp = floatval($input['total_expenses'] ?? 0);
        $expected = floatval($input['expected_cash'] ?? ($openCash + $cashSales - $exp));
        $counted = floatval($input['counted_cash'] ?? $expected);
        $diff = floatval($input['cash_difference'] ?? ($counted - $expected));
        $notes = $input['notes'] ?? '';

        $stmt = $pdo->prepare("
            INSERT INTO shift_closings (shift_date, opened_at, closed_at, cashier_name, opening_cash, total_cash_sales, total_qris_sales, total_expenses, expected_cash, counted_cash, cash_difference, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ");
        $stmt->execute([$shiftDate, $openedAt, $closedAt, $cashier, $openCash, $cashSales, $qrisSales, $exp, $expected, $counted, $diff, $notes]);

        sendResponse('success', 'Data Tutup Kas shift berhasil disimpan ke database', ['id' => $pdo->lastInsertId()]);
        break;

    case 'get_closings':
        $limit = isset($_GET['limit']) ? intval($_GET['limit']) : 30;
        $stmt = $pdo->query("SELECT * FROM shift_closings ORDER BY closed_at DESC LIMIT $limit");
        sendResponse('success', $stmt->fetchAll());
        break;

    // Default info
    default:
        sendResponse('online', [
            'service' => 'Flinders Coffee Core REST API',
            'version' => '2.0 Final',
            'database' => 'MySQL XAMPP (flinders_db)',
            'time_makassar' => date('Y-m-d H:i:s'),
            'available_modules' => [
                'users' => ['login', 'register'],
                'menus' => ['get_menus', 'get_menu', 'save_menu', 'toggle_menu', 'delete_menu', 'import_menus'],
                'tables' => ['get_tables', 'update_table', 'reset_tables'],
                'orders' => ['create_order', 'get_orders', 'get_order', 'update_order_status'],
                'expenses' => ['get_expenses', 'add_expense', 'delete_expense'],
                'bookings' => ['get_bookings', 'create_booking', 'update_booking_status'],
                'members' => ['get_members', 'get_member', 'add_stamp', 'redeem_stamp', 'register_member'],
                'summary' => ['get_summary'],
                'shift' => ['save_closing', 'get_closings']
            ]
        ]);
        break;
}
