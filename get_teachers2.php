<?php
$conn = new mysqli("localhost", "root", "", "school_management");

$query = "SELECT teacher_id, u.name AS teacher_name FROM teachers t JOIN users u ON t.user_id = u.user_id";
$result = $conn->query($query);

$teachers = [];

while ($row = $result->fetch_assoc()) {
    $teachers[] = $row;
}

echo json_encode($teachers);
$conn->close();
?>
