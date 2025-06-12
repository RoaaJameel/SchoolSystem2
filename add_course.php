<?php
require_once 'DBConnect.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $course_name = trim($_POST['course_name']);
    $grade_levels = $_POST['grade_levels'] ?? []; 
    $class_teacher_links = $_POST['class_teacher_links'] ?? [];

    if (empty($course_name)) {
        echo "Course name is required.";
        exit;
    }

    $conn->begin_transaction();

    try {
        $stmt = $conn->prepare("INSERT INTO courses (course_name) VALUES (?)");
        $stmt->bind_param("s", $course_name);
        $stmt->execute();
        $course_id = $conn->insert_id;
        $stmt->close();

        if (!empty($grade_levels)) {
            $stmt = $conn->prepare("INSERT INTO grade_courses (grade_level_id, course_id) VALUES (?, ?)");
            foreach ($grade_levels as $grade_level_id) {
                $stmt->bind_param("ii", $grade_level_id, $course_id);
                $stmt->execute();
            }
            $stmt->close();
        }

        if (!empty($class_teacher_links)) {
            $stmt = $conn->prepare("INSERT INTO class_course_teachers (class_id, course_id, teacher_id) VALUES (?, ?, ?)");
            foreach ($class_teacher_links as $link) {
                $class_id = $link['class_id'];
                $teacher_id = $link['teacher_id'];
                $stmt->bind_param("iii", $class_id, $course_id, $teacher_id);
                $stmt->execute();
            }
            $stmt->close();
        }

        $conn->commit();
        echo "Course and relations added successfully.";
    } catch (Exception $e) {
        $conn->rollback();
        echo "Error: " . $e->getMessage();
    }
}
$conn->close();
?>
