<?php
$conn = new mysqli("localhost", "root", "", "school_management");

$class_id = $_POST['class_id'];
$course_id = $_POST['course_id'];
$teacher_id = $_POST['teacher_id'];
$day_of_week = $_POST['day_of_week'];
$start_time = $_POST['start_time'];
$end_time = $_POST['end_time'];

$stmt = $conn->prepare("INSERT INTO schedules (class_id, course_id, teacher_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)");
$stmt->bind_param("iiisss", $class_id, $course_id, $teacher_id, $day_of_week, $start_time, $end_time);
echo $stmt->execute() ? "success" : "error";
$conn->close();
?>
