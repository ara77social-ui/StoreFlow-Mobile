<?php
// تنظیمات هدر برای پذیرش درخواست‌های JSON از اپلیکیشن اندروید
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST, GET, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type");

if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit;
}

// ==============================================
// 1. تنظیمات دیتابیس (مشخصات دایرکت ادمین خود را وارد کنید)
// ==============================================
$db_host = "localhost"; // معمولا روی هاست‌ها localhost است
$db_name = "YOUR_DB_NAME"; // نام دیتابیس
$db_user = "YOUR_DB_USER"; // یوزر دیتابیس
$db_pass = "YOUR_DB_PASSWORD"; // رمز عبور دیتابیس

// ==============================================
// 2. تنظیمات SMTP ایمیل (مشخصات ایمیل دایرکت ادمین)
// ==============================================
// برای ارسال ایمیل با SMTP نیازمند PHPMailer هستید.
// ابتدا فایل‌های PHPMailer را دانلود و در پوشه PHPMailer آپلود کنید.
// اگر از تابع پیش‌فرض mail() استفاده می‌کنید می‌توانید این بخش را نادیده بگیرید (اما SMTP امن‌تر است).
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

// در صورتی که PHPMailer را نصب کرده‌اید، کدهای زیر را از کامنت در بیاورید:
/*
require 'PHPMailer/src/Exception.php';
require 'PHPMailer/src/PHPMailer.php';
require 'PHPMailer/src/SMTP.php';
*/

// ==============================================
// اتصال به دیتابیس با امنیت بالا (PDO برای جلوگیری از SQL Injection)
// ==============================================
try {
    $pdo = new PDO("mysql:host=$db_host;dbname=$db_name;charset=utf8mb4", $db_user, $db_pass);
    // نمایش ارورها
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    echo json_encode(["success" => false, "message" => "خطا در اتصال به دیتابیس"]);
    exit;
}

// گرفتن دیتای ارسالی از اندروید (JSON)
$inputJSON = file_get_contents('php://input');
$input = json_decode($inputJSON, TRUE);

// بررسی نوع اکشن (مثلا signup, login, has_password)
$action = isset($_GET['action']) ? $_GET['action'] : '';

// ----------------------------------------------
// اکشن 1: بررسی وجود ایمیل در دیتابیس
// ----------------------------------------------
if ($action === 'has_password') {
    $email = $input['p_email'] ?? '';
    
    $stmt = $pdo->prepare("SELECT id FROM users WHERE email = :email");
    $stmt->execute(['email' => $email]);
    
    if ($stmt->fetch()) {
        echo "true";
    } else {
        echo "false";
    }
    exit;
}

// ----------------------------------------------
// اکشن 2: ثبت نام کاربر جدید (Signup)
// ----------------------------------------------
if ($action === 'signup') {
    $email = $input['p_email'] ?? '';
    $password = $input['p_password'] ?? '';
    $username = $input['username'] ?? explode('@', $email)[0]; // استفاده از بخش اول ایمیل در صورت نبود یوزرنیم

    if (empty($email) || empty($password)) {
        echo json_encode(["success" => false, "message" => "ایمیل و رمز عبور الزامی است"]);
        exit;
    }

    // بررسی تکراری نبودن ایمیل
    $stmt = $pdo->prepare("SELECT id FROM users WHERE email = :email");
    $stmt->execute(['email' => $email]);
    if ($stmt->fetch()) {
        echo json_encode(["success" => false, "message" => "این ایمیل قبلا ثبت شده است"]);
        exit;
    }

    // هش کردن رمز عبور با Bcrypt (بهترین روش امنیتی در PHP)
    $hashed_password = password_hash($password, PASSWORD_DEFAULT);

    try {
        $stmt = $pdo->prepare("INSERT INTO users (email, username, password) VALUES (:email, :username, :password)");
        $stmt->execute([
            'email' => $email,
            'username' => $username,
            'password' => $hashed_password
        ]);
        
        // ارسال ایمیل خوش‌آمدگویی پس از ثبت نام موفق
        sendWelcomeEmail($email, $username);

        echo "true"; // اندروید انتظار کلمه true را دارد
    } catch (Exception $e) {
        echo "false";
    }
    exit;
}

// ----------------------------------------------
// اکشن 3: لاگین کاربر (Login)
// ----------------------------------------------
if ($action === 'login') {
    $email = $input['p_email'] ?? '';
    $password = $input['p_password'] ?? '';

    $stmt = $pdo->prepare("SELECT password FROM users WHERE email = :email");
    $stmt->execute(['email' => $email]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    // بررسی اینکه آیا کاربر وجود دارد و پسورد با هش دیتابیس مطابقت دارد
    if ($user && password_verify($password, $user['password'])) {
        echo "true"; // موفقیت
    } else {
        echo "false"; // شکست
    }
    exit;
}

// ----------------------------------------------
// تابع ارسال ایمیل تاییدیه / خوش‌آمدگویی
// ----------------------------------------------
function sendWelcomeEmail($to_email, $username) {
    // روش 1: با استفاده از تابع پیش‌فرض mail() در PHP
    $subject = "به StoreFlow خوش آمدید!";
    $message = "سلام $username,\n\nثبت نام شما با موفقیت انجام شد. به StoreFlow خوش آمدید!\n\nبا احترام,\nتیم پشتیبانی";
    $headers = "From: noreply@yourdomain.com" . "\r\n" .
               "Reply-To: support@yourdomain.com" . "\r\n" .
               "X-Mailer: PHP/" . phpversion();
               
    @mail($to_email, $subject, $message, $headers);

    // روش 2: با استفاده از PHPMailer برای اتصال به SMTP دایرکت ادمین (پيشنهادی)
    /*
    $mail = new PHPMailer(true);
    try {
        // تنظیمات سرور
        $mail->isSMTP();
        $mail->Host       = 'mail.yourdomain.com'; // آدرس سرور SMTP دایرکت ادمین شما
        $mail->SMTPAuth   = true;
        $mail->Username   = 'noreply@yourdomain.com'; // ایمیل شما
        $mail->Password   = 'YOUR_EMAIL_PASSWORD'; // رمز عبور ایمیل
        $mail->SMTPSecure = PHPMailer::ENCRYPTION_SMTPS; // یا ENCRYPTION_STARTTLS
        $mail->Port       = 465; // پورت معمولا 465 برای SSL یا 587 برای TLS

        // گیرنده و فرستنده
        $mail->setFrom('noreply@yourdomain.com', 'StoreFlow');
        $mail->addAddress($to_email, $username);

        // محتوا
        $mail->isHTML(true);
        $mail->CharSet = 'UTF-8';
        $mail->Subject = 'تاییدیه ثبت نام در StoreFlow';
        $mail->Body    = "<b>سلام $username عزیز</b><br><br>ثبت نام شما با موفقیت انجام شد. به اپلیکیشن ما خوش آمدید!";
        $mail->AltBody = "سلام $username\n\nثبت نام شما با موفقیت انجام شد.";

        $mail->send();
    } catch (Exception $e) {
        // لاگ خطا در صورت عدم ارسال
        error_log("خطا در ارسال ایمیل: {$mail->ErrorInfo}");
    }
    */
}
?>
