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

$sql = "
SELECT 
    s.student_id, 
    u.name, 
    s.gender, 
    s.date_of_birth, 
    s.parent_contact 
FROM students s
JOIN users u ON s.user_id = u.user_id
";

$result = $conn->query($sql);
$students = [];

if ($result && $result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $students[] = $row;
    }
}

echo json_encode($students);

$conn->close();
?>
