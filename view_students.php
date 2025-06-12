<?php
header('Content-Type: application/json');
require_once("DBConnect.php"); // هذا الملف يجب أن يحتوي على اتصال باستخدام mysqli

$gradeLevel = $_GET['grade_level'] ?? '';
$classId = $_GET['class_id'] ?? '';
$academicYear = $_GET['academic_year'] ?? '';

$sql = "
    SELECT 
        s.student_id,
        u.name AS full_name,
        s.gender,
        s.date_of_birth,
        s.parent_contact,
        gl.name AS grade_level,
        c.class_name
    FROM students s
    JOIN users u ON s.user_id = u.user_id
    JOIN classes c ON s.class_id = c.class_id
    JOIN grade_levels gl ON c.grade_level = gl.grade_level_id
    WHERE 1=1
";

$params = [];
$types = "";

if (!empty($gradeLevel)) {
    $sql .= " AND gl.grade_level_id = ?";
    $params[] = $gradeLevel;
    $types .= "s";
}
if (!empty($classId)) {
    $sql .= " AND c.class_id = ?";
    $params[] = $classId;
    $types .= "s";
}
if (!empty($academicYear)) {
    $sql .= " AND s.academic_year = ?";
    $params[] = $academicYear;
    $types .= "s";
}

$stmt = $conn->prepare($sql);

if (!empty($params)) {
    $stmt->bind_param($types, ...$params);
}

$stmt->execute();
$result = $stmt->get_result();

$students = [];
while ($row = $result->fetch_assoc()) {
    $students[] = $row;
}

if (empty($students)) {
    echo json_encode([
        "success" => false,
        "message" => "No students found"
    ]);
} else {
    echo json_encode([
        "success" => true,
        "students" => $students
    ]);
}
?>
