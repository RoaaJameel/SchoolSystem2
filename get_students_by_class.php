<?php
header("Content-Type: application/json");
require_once 'DBConnect.php';

if (!isset($_GET['class_id'])) {
    echo json_encode(["status" => "error", "message" => "Missing class_id"]);
    exit;
}

$class_id = intval($_GET['class_id']);

$sql = "
    SELECT s.student_id, u.name AS student_name
    FROM students s
    JOIN users u ON s.user_id = u.user_id
    WHERE s.class_id = ?
";

$stmt = $connect->prepare($sql);

if (!$stmt) {
    echo json_encode(["status" => "error", "message" => "SQL error: " . $connect->error]);
    exit;
}

$stmt->bind_param("i", $class_id);
$stmt->execute();
$result = $stmt->get_result();

$students = [];

while ($row = $result->fetch_assoc()) {
    $students[] = [
        "student_id" => $row["student_id"],
        "student_name" => $row["student_name"]
    ];
}

echo json_encode($students);

$stmt->close();
$connect->close();
?>


