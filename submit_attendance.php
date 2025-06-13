<?php
header("Content-Type: application/json");
include "DBConnect.php";

$data = json_decode(file_get_contents("php://input"), true);

$class_id = $data["class_id"];
$date = $data["date"];
$attendance = $data["attendance"]; // array of { student_id, status }

$success = true;

foreach ($attendance as $entry) {
    $student_id = $entry["student_id"];
    $status = $entry["status"];

    $sql = "INSERT INTO attendance (student_id, class_id, date, status) VALUES (?, ?, ?, ?)";
    $stmt = $connect->prepare($sql);
    $stmt->bind_param("iiss", $student_id, $class_id, $date, $status);

    if (!$stmt->execute()) {
        $success = false;
        break;
    }
}

if ($success) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error", "message" => $stmt->error]);
}
?>
