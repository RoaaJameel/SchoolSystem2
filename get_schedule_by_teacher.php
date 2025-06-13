

<?php $conn = new mysqli("localhost", "root", "", "school_management");

 $teacher_id = $_GET['teacher_id']; 


$stmt = $conn->prepare(" SELECT s.schedule_id, c.name AS class_name, co.name AS course_name, s.day_of_week, s.start_time, s.end_time FROM schedules s JOIN classes c ON s.class_id = c.class_id JOIN courses co ON s.course_id = co.course_id WHERE s.teacher_id = ? ORDER BY s.day_of_week, s.start_time "); 

$stmt->bind_param("i", $teacher_id); $stmt->execute();
 $result = $stmt->get_result();
 $schedules = []; while ($row = $result->fetch_assoc()) { $schedules[] = $row; }

 echo json_encode($schedules);

 $conn->close(); ?>
