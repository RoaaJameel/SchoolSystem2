<?php
require 'DBConnect.php';

header("Content-Type: application/json");

$sql = "SELECT teacher_id, u.name AS teacher_name FROM teachers t JOIN users u ON t.user_id = u.user_id";
$result = $conn->query($sql);

$teachers = [];

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $teachers[] = $row;
    }
    echo json_encode($teachers);
} else {
    echo json_encode([]);
}

$conn->close();
?>
