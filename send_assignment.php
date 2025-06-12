<?php
header("Content-Type: application/json; charset=UTF-8");

$host = "localhost";
$db = "school_management";
$user = "root";
$pass = "";

$conn = new mysqli($host, $user, $pass, $db);

if ($conn->connect_error) {
    die(json_encode(["error" => "Connection failed: " . $conn->connect_error]));
}

$title = $conn->real_escape_string($_POST['title']);
$description = $conn->real_escape_string($_POST['description']);
$due_date = $conn->real_escape_string($_POST['due_date']);
$course_id = $conn->real_escape_string($_POST['course_id']);
$teacher_id = $conn->real_escape_string($_POST['teacher_id']);
$class_id = $conn->real_escape_string($_POST['class_id']);


$sql = "INSERT INTO assignments (course_id, title, description, due_date, teacher_id, class_id) 
        VALUES ('$course_id', '$title', '$description', '$due_date', '$teacher_id', '$class_id')";

if ($conn->query($sql) === TRUE) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["error" => $conn->error]);
}

$conn->close();
?>
