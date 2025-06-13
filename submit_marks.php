<?php
header("Content-Type: application/json");
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

include "DBConnect.php";

// Get the POSTed JSON body
$data = json_decode(file_get_contents("php://input"), true);

// Validate input
if (
    !isset($data['assignment_id']) ||
    !isset($data['exam_type']) ||
    !isset($data['marks']) ||
    !is_array($data['marks'])
) {
    echo json_encode(["status" => "error", "message" => "Invalid input"]);
    exit;
}

$assignment_id = $data['assignment_id'];
$exam_type = $data['exam_type'];
$marks = $data['marks'];
$date_recorded = date('Y-m-d');

$success = true;
$error_message = "";


foreach ($marks as $entry) {
    $student_id = $entry['student_id'];
    $mark = $entry['mark'];

    $sql = "INSERT INTO marks (student_id, assignment_id, mark, exam_type, date_recorded)
            VALUES (?, ?, ?, ?, ?)";
    $stmt = $connect->prepare($sql);

    if (!$stmt) {
        echo json_encode(["status" => "error", "message" => "Prepare failed: " . $connect->error]);
        exit;
    }

    $stmt->bind_param("iidss", $student_id, $assignment_id, $mark, $exam_type, $date_recorded);

    if (!$stmt->execute()) {
        $success = false;
        $error_message = $stmt->error;
        break;
    }
        $stmt->close();
}

if ($success) {
    echo json_encode(["status" => "success"]);
} else {
    echo json_encode(["status" => "error", "message" => $error_message]);
}
?>
