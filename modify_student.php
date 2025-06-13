<?php
require 'DBConnect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $student_id = intval($_POST['student_id']);

    $stmt = $conn->prepare("SELECT user_id, class_id FROM students WHERE student_id = ?");
    $stmt->bind_param("i", $student_id);
    $stmt->execute();
    $stmt->bind_result($user_id, $current_class_id);
    if (!$stmt->fetch()) {
        echo json_encode(["success" => false, "message" => "Student not found."]);
        exit();
    }
    $stmt->close();

    // المدخلات القادمة من التطبيق
    $name           = $_POST['name'] ?? null;
    $gender         = $_POST['gender'] ?? null;
    $dob            = $_POST['date_of_birth'] ?? null;
    $parent_contact = $_POST['parent_contact'] ?? null;
    $class_id       = isset($_POST['class_id']) ? intval($_POST['class_id']) : null;
    $academic_year  = $_POST['academic_year'] ?? null;

    $conn->begin_transaction();
    $somethingChanged = false;

    try {
        // ✅ تعديل الاسم في العمود الصحيح
        if ($name) {
            $stmt = $conn->prepare("UPDATE users SET name = ? WHERE user_id = ?");
            $stmt->bind_param("si", $name, $user_id);
            $stmt->execute();
            $stmt->close();
            $somethingChanged = true;
        }

        // ✅ تحديث جدول الطلاب
        $updates = [];
        $params = [];
        $types = "";

        if ($gender) {
            $updates[] = "gender = ?";
            $params[] = $gender;
            $types .= "s";
        }
        if ($dob) {
            $updates[] = "date_of_birth = ?";
            $params[] = $dob;
            $types .= "s";
        }
        if ($parent_contact) {
            $updates[] = "parent_contact = ?";
            $params[] = $parent_contact;
            $types .= "s";
        }
        if ($class_id) {
            $updates[] = "class_id = ?";
            $params[] = $class_id;
            $types .= "i";
        }
        if ($academic_year) {
            $updates[] = "academic_year = ?";
            $params[] = $academic_year;
            $types .= "s";
        }

        if (!empty($updates)) {
            $sql = "UPDATE students SET " . implode(", ", $updates) . " WHERE student_id = ?";
            $params[] = $student_id;
            $types .= "i";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param($types, ...$params);
            $stmt->execute();
            $stmt->close();
            $somethingChanged = true;
        }

        // ✅ تعديل الجداول المرتبطة
        if ($class_id) {
            $stmt = $conn->prepare("UPDATE marks SET class_id = ? WHERE student_id = ?");
            $stmt->bind_param("ii", $class_id, $student_id);
            $stmt->execute();
            $stmt->close();
        }

        if ($somethingChanged) {
            $conn->commit();
            echo json_encode(["success" => true, "message" => "Student and related data updated successfully."]);
        } else {
            $conn->rollback();
            echo json_encode(["success" => false, "message" => "No changes were made."]);
        }

    } catch (Exception $e) {
        $conn->rollback();
        echo json_encode(["success" => false, "message" => "Update failed: " . $e->getMessage()]);
    }

} else {
    echo json_encode(["success" => false, "message" => "Invalid request method."]);
}
?>
