<?php
require_once 'DBConnect.php';

$sql = "SELECT class_id, class_name FROM classes";
$result = $conn->query($sql);

$response = array();

if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $response[] = array(
            "class_id" => $row["class_id"],
            "class_name" => $row["class_name"]
        );
    }
    echo json_encode($response);
} else {
    echo json_encode([]);
}

$conn->close();
?>
