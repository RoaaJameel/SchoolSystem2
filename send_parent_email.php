<?php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $to = $_POST['email'];
    $message = $_POST['message'];
    $subject = "Message from School";

    $headers = "From: school@example.com";

    if (mail($to, $subject, $message, $headers)) {
        echo "success";
    } else {
        echo "fail";
    }
}
