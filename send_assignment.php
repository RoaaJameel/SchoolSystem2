<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

include "DBConnect.php";

$title = $_POST['title'] ?? '';
$description = $_POST['description']?? '';
$due_date = $_POST['due_date']?? '';
$course_id = $_POST['course_id']?? '';
$teacher_id = $_POST['teacher_id']?? '';
$class_id = $_POST['class_id'] ?? '';


if (!$title || !$description || !$due_date || !$course_id || !$teacher_id || !$class_id) {
    echo "Missing required fields: \n";
    echo "title: $title\n";
    echo "description: $description\n";
    echo "due_date: $due_date\n";
    echo "course_id: $course_id\n";
    echo "teacher_id: $teacher_id\n";
    echo "class_id: $class_id\n";
    exit;
}

$stmt = $connect->prepare("INSERT INTO assignments (course_id, title, description, due_date, teacher_id, class_id) VALUES (?, ?, ?, ?, ?, ?)");
$stmt->bind_param("isssii", $course_id, $title, $description, $due_date, $teacher_id, $class_id);

if ($stmt->execute()) {
    echo "success";
} else {
    echo "error: " . $stmt->error;
}

?>

