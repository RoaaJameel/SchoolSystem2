<?php
require 'DBConnect.php'; // Make sure this file exists and includes the database connection

if (isset($_GET['student_id'])) {
    $student_id = intval($_GET['student_id']);

    // Step 1: Retrieve the user_id associated with the student
    $stmt = $conn->prepare("SELECT user_id FROM students WHERE student_id = ?");
    $stmt->bind_param("i", $student_id);
    $stmt->execute();
    $stmt->bind_result($user_id);
    if (!$stmt->fetch()) {
        die("Student not found.");
    }
    $stmt->close();

    // Begin the deletion process using safe transactions
    $conn->begin_transaction();

    try {
        // Delete attendance records
        $stmt = $conn->prepare("DELETE FROM attendance WHERE student_id = ?");
        $stmt->bind_param("i", $student_id);
        $stmt->execute();

        // Delete marks
        $stmt = $conn->prepare("DELETE FROM marks WHERE student_id = ?");
        $stmt->bind_param("i", $student_id);
        $stmt->execute();

        // Delete student from students table
        $stmt = $conn->prepare("DELETE FROM students WHERE student_id = ?");
        $stmt->bind_param("i", $student_id);
        $stmt->execute();

        // Delete account from users table
        $stmt = $conn->prepare("DELETE FROM users WHERE user_id = ?");
        $stmt->bind_param("i", $user_id);
        $stmt->execute();

        $conn->commit();
        echo "Student deleted successfully.";
    } catch (Exception $e) {
        $conn->rollback();
        echo "An error occurred during deletion: " . $e->getMessage();
    }
} else {
    echo "Student ID is not specified.";
}
?>
