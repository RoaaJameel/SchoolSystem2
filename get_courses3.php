<?php
header('Content-Type: application/json');

include "DBConnect.php";

$sql = "SELECT course_id, course_name FROM courses";
$result = mysqli_query($connect, $sql);

$courses = array();
while ($row = mysqli_fetch_assoc($result)) {
    $courses[] = $row;
}

echo json_encode($courses);
?>