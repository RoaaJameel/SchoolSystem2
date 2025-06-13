<?php
include 'DBConnect.php'; 

if (!isset($_GET['teacher_id'])) {
    echo json_encode(["error" => "Missing teacher_id"]);
    exit;
}

$teacher_id = intval($_GET['teacher_id']);

$sql = "SELECT assignment_id, title, class_id FROM assignments WHERE teacher_id = ?";
$stmt = $connect->prepare($sql);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$assignments = array();

while ($row = $result->fetch_assoc()) {
    $assignments[] = $row;
}

echo json_encode($assignments);

$stmt->close();
$connect->close();
?>
