<?php
session_start();

// ==============================================
// 1. تنظیمات ورود پنل ادمین
// ==============================================
$ADMIN_USER = "admin";
$ADMIN_PASS = "admin123"; // رمز عبور ادمین را در اینجا تغییر دهید

// ==============================================
// 2. تنظیمات دیتابیس (مشخصات دایرکت ادمین خود را وارد کنید)
// ==============================================
$db_host = "localhost";
$db_name = "YOUR_DB_NAME";
$db_user = "YOUR_DB_USER";
$db_pass = "YOUR_DB_PASSWORD";

try {
    $pdo = new PDO("mysql:host=$db_host;dbname=$db_name;charset=utf8mb4", $db_user, $db_pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("خطا در اتصال به دیتابیس. لطفا اطلاعات دیتابیس را در فایل admin.php تنظیم کنید.");
}

// ----------------------------------------------
// هندل کردن خروج
// ----------------------------------------------
if (isset($_GET['logout'])) {
    session_destroy();
    header("Location: admin.php");
    exit;
}

// ----------------------------------------------
// هندل کردن ورود
// ----------------------------------------------
$error = "";
if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['login'])) {
    if ($_POST['username'] === $ADMIN_USER && $_POST['password'] === $ADMIN_PASS) {
        $_SESSION['is_admin'] = true;
        header("Location: admin.php");
        exit;
    } else {
        $error = "نام کاربری یا رمز عبور اشتباه است!";
    }
}

// ----------------------------------------------
// عملیات ادمین (آپدیت اشتراک و حذف کاربر)
// ----------------------------------------------
if (isset($_SESSION['is_admin']) && $_SESSION['is_admin'] === true) {
    if ($_SERVER['REQUEST_METHOD'] == 'POST') {
        
        // تغییر وضعیت اشتراک
        if (isset($_POST['update_subscription'])) {
            $user_id = $_POST['user_id'];
            $status = $_POST['subscription_status'];
            $start = !empty($_POST['subscription_start']) ? $_POST['subscription_start'] : null;
            $end = !empty($_POST['subscription_end']) ? $_POST['subscription_end'] : null;
            
            $stmt = $pdo->prepare("UPDATE users SET subscription_status=?, subscription_start=?, subscription_end=? WHERE id=?");
            $stmt->execute([$status, $start, $end, $user_id]);
            $success = "وضعیت اشتراک با موفقیت بروزرسانی شد.";
        }
        
        // حذف کاربر
        if (isset($_POST['delete_user'])) {
            $user_id = $_POST['user_id'];
            $stmt = $pdo->prepare("DELETE FROM users WHERE id=?");
            $stmt->execute([$user_id]);
            $success = "کاربر با موفقیت حذف شد.";
        }
    }
    
    // دریافت لیست تمامی کاربران
    $stmt = $pdo->query("SELECT * FROM users ORDER BY id DESC");
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
}
?>
<!DOCTYPE html>
<html lang="fa" dir="rtl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>پنل مدیریت StoreFlow</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.rtl.min.css" rel="stylesheet">
    <style>
        body { background-color: #f8f9fa; font-family: Tahoma, Arial, sans-serif; }
        .login-container { max-width: 400px; margin: 100px auto; }
        .card { box-shadow: 0 4px 8px rgba(0,0,0,0.1); border-radius: 10px; }
        .badge-active { background-color: #198754; }
        .badge-expired { background-color: #dc3545; }
        .badge-none { background-color: #6c757d; }
    </style>
</head>
<body>

<?php if (!isset($_SESSION['is_admin']) || $_SESSION['is_admin'] !== true): ?>
    <!-- فرم لاگین -->
    <div class="container login-container">
        <div class="card p-4">
            <h3 class="text-center mb-4">ورود به پنل مدیریت</h3>
            <?php if ($error): ?>
                <div class="alert alert-danger"><?= $error ?></div>
            <?php endif; ?>
            <form method="POST">
                <div class="mb-3">
                    <label>نام کاربری</label>
                    <input type="text" name="username" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label>رمز عبور</label>
                    <input type="password" name="password" class="form-control" required>
                </div>
                <button type="submit" name="login" class="btn btn-primary w-100">ورود</button>
            </form>
        </div>
    </div>
<?php else: ?>
    <!-- داشبورد ادمین -->
    <nav class="navbar navbar-dark bg-dark mb-4">
        <div class="container">
            <a class="navbar-brand" href="#">پنل مدیریت StoreFlow</a>
            <a href="?logout=1" class="btn btn-danger btn-sm">خروج</a>
        </div>
    </nav>

    <div class="container">
        <?php if (isset($success)): ?>
            <div class="alert alert-success"><?= $success ?></div>
        <?php endif; ?>

        <div class="card p-4">
            <h5 class="mb-4">لیست کاربران و اشتراک‌ها</h5>
            <div class="table-responsive">
                <table class="table table-bordered table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>شناسه</th>
                            <th>ایمیل / یوزرنیم</th>
                            <th>تاریخ ثبت نام</th>
                            <th>وضعیت اشتراک</th>
                            <th>عملیات و مدیریت</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($users as $user): ?>
                            <tr>
                                <td><?= $user['id'] ?></td>
                                <td>
                                    <strong><?= htmlspecialchars($user['email']) ?></strong><br>
                                    <small class="text-muted"><?= htmlspecialchars($user['username']) ?></small>
                                </td>
                                <td dir="ltr" class="text-end"><?= date('Y-m-d H:i', strtotime($user['created_at'])) ?></td>
                                <td>
                                    <?php 
                                        if($user['subscription_status'] == 'active') echo '<span class="badge badge-active">فعال</span>';
                                        elseif($user['subscription_status'] == 'expired') echo '<span class="badge badge-expired">منقضی شده</span>';
                                        else echo '<span class="badge badge-none">بدون اشتراک</span>';
                                        
                                        if($user['subscription_end']) {
                                            echo "<br><small>تا: {$user['subscription_end']}</small>";
                                        }
                                    ?>
                                </td>
                                <td>
                                    <!-- دکمه باز کردن مودال ویرایش -->
                                    <button class="btn btn-sm btn-primary" data-bs-toggle="modal" data-bs-target="#editModal<?= $user['id'] ?>">ویرایش اشتراک</button>
                                    
                                    <!-- دکمه حذف (با تاییدیه) -->
                                    <form method="POST" class="d-inline" onsubmit="return confirm('آیا از حذف این کاربر مطمئن هستید؟ این عملیات غیرقابل بازگشت است.');">
                                        <input type="hidden" name="user_id" value="<?= $user['id'] ?>">
                                        <button type="submit" name="delete_user" class="btn btn-sm btn-danger">حذف</button>
                                    </form>

                                    <!-- مودال ویرایش اشتراک -->
                                    <div class="modal fade" id="editModal<?= $user['id'] ?>" tabindex="-1">
                                        <div class="modal-dialog">
                                            <div class="modal-content">
                                                <div class="modal-header">
                                                    <h5 class="modal-title">ویرایش اشتراک: <?= htmlspecialchars($user['email']) ?></h5>
                                                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                </div>
                                                <form method="POST">
                                                    <div class="modal-body">
                                                        <input type="hidden" name="user_id" value="<?= $user['id'] ?>">
                                                        
                                                        <div class="mb-3">
                                                            <label>وضعیت اشتراک</label>
                                                            <select name="subscription_status" class="form-select">
                                                                <option value="none" <?= $user['subscription_status'] == 'none' ? 'selected' : '' ?>>بدون اشتراک</option>
                                                                <option value="active" <?= $user['subscription_status'] == 'active' ? 'selected' : '' ?>>فعال (ویژه)</option>
                                                                <option value="expired" <?= $user['subscription_status'] == 'expired' ? 'selected' : '' ?>>منقضی شده</option>
                                                            </select>
                                                        </div>
                                                        
                                                        <div class="mb-3">
                                                            <label>تاریخ شروع اشتراک</label>
                                                            <input type="date" name="subscription_start" class="form-control" value="<?= $user['subscription_start'] ?>">
                                                        </div>
                                                        
                                                        <div class="mb-3">
                                                            <label>تاریخ پایان اشتراک</label>
                                                            <input type="date" name="subscription_end" class="form-control" value="<?= $user['subscription_end'] ?>">
                                                        </div>
                                                    </div>
                                                    <div class="modal-footer">
                                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">انصراف</button>
                                                        <button type="submit" name="update_subscription" class="btn btn-success">ذخیره تغییرات</button>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                    <!-- پایان مودال -->
                                </td>
                            </tr>
                        <?php endforeach; ?>
                        <?php if(empty($users)): ?>
                            <tr><td colspan="5" class="text-center">هیچ کاربری یافت نشد.</td></tr>
                        <?php endif; ?>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<?php endif; ?>
</body>
</html>
