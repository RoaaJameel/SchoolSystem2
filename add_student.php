<?php
require_once 'DBConnect.php'; 

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'];
    $password = password_hash($_POST['password'], PASSWORD_BCRYPT);
    $full_name = $_POST['full_name'];
    $email = $_POST['email'];
    $phone = $_POST['phone'];
    $gender = $_POST['gender'];
    $dob = $_POST['dob'];
    $parent_contact = $_POST['parent_contact'];
    $class_id = $_POST['class_id'];
    $academic_year = $_POST['academic_year'];

    // تحقق من السعة أولاً
    $checkCapacityQuery = "
        SELECT 
            c.max_capacity, 
            COUNT(s.student_id) AS current_students
        FROM 
            classes c
        LEFT JOIN 
            students s ON c.class_id = s.class_id AND s.academic_year = ?
        WHERE 
            c.class_id = ?
        GROUP BY 
            c.max_capacity
    ";

    $stmt = $conn->prepare($checkCapacityQuery);
    $stmt->bind_param("si", $academic_year, $class_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        $max_capacity = (int)$row['max_capacity'];
        $current_students = (int)$row['current_students'];

        if ($current_students >= $max_capacity) {
            echo json_encode(["status" => "error", "message" => "The class has reached its maximum capacity."]);
            exit;
        }
    } else {
        echo json_encode(["status" => "error", "message" => "Class not found."]);
        exit;
    }

    // إذا كان هناك مساحة كافية، أضف الطالب
    $conn->begin_transaction();

    try {
        // إضافة المستخدم
        $stmt = $conn->prepare("INSERT INTO users (username, password, name, email, role, phone) VALUES (?, ?, ?, ?, 'student', ?)");
        $stmt->bind_param("sssss", $username, $password, $full_name, $email, $phone);
        $stmt->execute();
        $user_id = $stmt->insert_id;

        // إضافة الطالب
        $stmt = $conn->prepare("INSERT INTO students (user_id, parent_contact, date_of_birth, gender, class_id, academic_year) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("isssis", $user_id, $parent_contact, $dob, $gender, $class_id, $academic_year);
        $stmt->execute();

        $conn->commit();
        echo json_encode(["status" => "success"]);
    } catch (Exception $e) {
        $conn->rollback();
        echo json_encode(["status" => "error", "message" => $e->getMessage()]);
    }

    $conn->close();
}
?>
