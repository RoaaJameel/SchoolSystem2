<?php
require 'DBConnect.php';

header("Content-Type: application/json");

$sql = "SELECT grade_level_id, name FROM grade_levels";
$result = $conn->query($sql);

$gradeLevels = [];

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $gradeLevels[] = $row;
    }
    echo json_encode($gradeLevels);
} else {
    echo json_encode([]);
}

$conn->close();
?>
