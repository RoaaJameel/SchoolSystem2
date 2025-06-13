<?php
$conn = new mysqli("localhost", "root", "", "school_management");

$query = "SELECT course_id AS id, course_name AS name FROM courses";
$result = $conn->query($query);

$courses = [];

while ($row = $result->fetch_assoc()) {
    $courses[] = $row;
}

echo json_encode($courses);
$conn->close();
?>
