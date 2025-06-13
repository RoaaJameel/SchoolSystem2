<?php
header('Content-Type: application/json');
include 'DbConnect.php';

$result = $connect->query("SELECT student_id, name, parent_contact FROM students");

$students = [];
while ($row = $result->fetch_assoc()) {
    $students[] = $row;
}

echo json_encode($students);
