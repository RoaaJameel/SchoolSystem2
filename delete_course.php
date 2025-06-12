<?php
require_once 'DBConnect.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $course_id = intval($_POST['course_id']);

    // حذف السجلات المرتبطة بالكورس
    $conn->begin_transaction();

    try {
        // حذف من الجداول المرتبطة أولاً حسب العلاقات
        $conn->prepare("DELETE FROM marks WHERE assignment_id IN (
            SELECT assignment_id FROM assignments WHERE course_id = ?
        )")->execute([$course_id]);

        $conn->prepare("DELETE FROM assignments WHERE course_id = ?")->execute([$course_id]);
        $conn->prepare("DELETE FROM schedules WHERE course_id = ?")->execute([$course_id]);
        $conn->prepare("DELETE FROM class_course_teachers WHERE course_id = ?")->execute([$course_id]);
        $conn->prepare("DELETE FROM grade_courses WHERE course_id = ?")->execute([$course_id]);

        // أخيرًا حذف الكورس نفسه
        $stmt = $conn->prepare("DELETE FROM courses WHERE course_id = ?");
        $stmt->bind_param("i", $course_id);

        if ($stmt->execute()) {
            $conn->commit();
            echo "Course deleted successfully.";
        } else {
            throw new Exception("Failed to delete course: " . $stmt->error);
        }

        $stmt->close();
    } catch (Exception $e) {
        $conn->rollback();
        echo "Error: " . $e->getMessage();
    }
}
$conn->close();
?>
