<?php
require 'DBConnect.php';
file_put_contents("debug.txt", "PHP FILE HIT\n", FILE_APPEND);

header("Content-Type: application/json");

try {
    $sql = "
        SELECT 
            c.course_id,
            c.course_name,

            COALESCE(gl.name, '') AS grade_level,
            COALESCE(cls.class_name, '') AS class_name,
            COALESCE(u.name, '') AS teacher_name

        FROM courses c

        LEFT JOIN grade_courses gc ON c.course_id = gc.course_id
        LEFT JOIN grade_levels gl ON gc.grade_level_id = gl.grade_level_id

        LEFT JOIN class_course_teachers cct ON c.course_id = cct.course_id
        LEFT JOIN classes cls ON cct.class_id = cls.class_id
        LEFT JOIN teachers t ON cct.teacher_id = t.teacher_id
        LEFT JOIN users u ON t.user_id = u.user_id

        ORDER BY c.course_name ASC
    ";

    $result = $conn->query($sql);
    $coursesMap = [];

    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $course_id = $row["course_id"];

            if (!isset($coursesMap[$course_id])) {
                $coursesMap[$course_id] = [
                    "course_id" => $course_id,
                    "course_name" => $row["course_name"],
                    "grade_levels" => [],
                    "classes" => [],
                    "teachers" => []
                ];
            }

            // Avoid duplicates
            if ($row["grade_level"] && !in_array($row["grade_level"], $coursesMap[$course_id]["grade_levels"])) {
                $coursesMap[$course_id]["grade_levels"][] = $row["grade_level"];
            }

            if ($row["class_name"] && !in_array($row["class_name"], $coursesMap[$course_id]["classes"])) {
                $coursesMap[$course_id]["classes"][] = $row["class_name"];
            }

            if ($row["teacher_name"] && !in_array($row["teacher_name"], $coursesMap[$course_id]["teachers"])) {
                $coursesMap[$course_id]["teachers"][] = $row["teacher_name"];
            }
        }

        // Format response
        $courses = array_values(array_map(function ($course) {
            return [
                "course_id" => $course["course_id"],
                "course_name" => $course["course_name"],
                "grade_levels" => implode(", ", $course["grade_levels"]),
                "classes" => implode(", ", $course["classes"]),
                "teachers" => implode(", ", $course["teachers"]),
            ];
        }, $coursesMap));

        echo json_encode(["success" => true, "courses" => $courses], JSON_UNESCAPED_UNICODE);
    } else {
        echo json_encode(["success" => false, "message" => "No courses found."]);
    }

} catch (Exception $e) {
    echo json_encode(["success" => false, "message" => "Error: " . $e->getMessage()]);
}

$conn->close();
?>
