<?php
$conn = new mysqli("localhost", "root", "", "school_management");

$result = $conn->query("SELECT class_id AS id, class_name as name FROM classes");
$classes = [];

while ($row = $result->fetch_assoc()) {
    $classes[] = $row;
}

echo json_encode($classes);
$conn->close();
?>
