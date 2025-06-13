<?php
include "db_connection.php";

$sql = "SELECT class_id, class_name FROM classes";
$result = mysqli_query($connect, $sql);

$classes = array();
while ($row = mysqli_fetch_assoc($result)) {
    $classes[] = $row;
}

echo json_encode($classes);
?>
