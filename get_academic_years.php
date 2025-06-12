<?php
require_once 'DBConnect.php';

$sql = "SELECT DISTINCT academic_year FROM students WHERE academic_year IS NOT NULL";
$result = $conn->query($sql);

$response = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $response[] = ["year" => $row["academic_year"]];
    }
    echo json_encode($response);
} else {
    echo json_encode([]);
}

$conn->close();
?>
