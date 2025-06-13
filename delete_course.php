<?php
require_once 'DBConnect.php';
header("Content-Type: application/json");

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (!isset($_POST['course_id'])) {
        echo json_encode(["success" => false, "message" => "Missing course_id."]);
        exit;
    }

    $course_id = intval($_POST['course_id']);
    $conn->begin_transaction();

    try {
        $stmt = $conn->prepare("DELETE FROM marks WHERE assignment_id IN (
            SELECT assignment_id FROM assignments WHERE course_id = ?)");
        $stmt->bind_param("i", $course_id);
        $stmt->execute();
        $stmt->close();

        $stmt = $conn->prepare("DELETE FROM assignments WHERE course_id = ?");
        $stmt->bind_param("i", $course_id);
        $stmt->execute();
        $stmt->close();

        $tables = ["schedules", "class_course_teachers", "grade_courses"];
        foreach ($tables as $table) {
            $stmt = $conn->prepare("DELETE FROM $table WHERE course_id = ?");
            $stmt->bind_param("i", $course_id);
            $stmt->execute();
            $stmt->close();
        }

        $stmt = $conn->prepare("DELETE FROM courses WHERE course_id = ?");
        $stmt->bind_param("i", $course_id);
        if ($stmt->execute()) {
            $conn->commit();
            echo json_encode(["success" => true, "message" => "Course deleted successfully."]);
        } else {
            throw new Exception("Failed to delete course: " . $stmt->error);
        }
        $stmt->close();

    } catch (Exception $e) {
        $conn->rollback();
        echo json_encode(["success" => false, "message" => $e->getMessage()]);
    }

} else {
    echo json_encode(["success" => false, "message" => "Invalid request method."]);
}

$conn->close();
?>
