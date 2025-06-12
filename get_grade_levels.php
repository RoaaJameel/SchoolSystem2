<?php
require_once 'DBConnect.php';

$sql = "SELECT grade_level_id, name FROM grade_levels";
$result = $conn->query($sql);

$response = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $response[] = array(
            "grade_level_id" => $row["grade_level_id"],
            "name" => $row["name"]
        );
    }
    echo json_encode($response);
} else {
    echo json_encode([]);
}

$conn->close();
?>
