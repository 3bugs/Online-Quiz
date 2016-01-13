<?php

error_reporting(E_ERROR | E_PARSE);
header('Content-type=application/json; charset=utf-8');

$response = array();

require_once 'db_config.php';
$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);

if (mysqli_connect_errno()) {
    $response["success"] = 0;
    $response["message"] = "Database connection failed: " . mysqli_connect_error();
	echo json_encode($response);
    exit();
}

$charset = "SET character_set_results=utf8";
$db->query($charset);

$sql = "SELECT quizzes.*, COUNT(questions.question_id) AS number_of_questions FROM quizzes, questions WHERE quizzes.quiz_id = questions.quiz_id GROUP BY quizzes.title ORDER BY quizzes.quiz_id";

if ($result = $db->query($sql)) {
    $response["success"] = 1;
    $response["quiz_data"] = array();

    $rowCount = $result->num_rows;

    if ($rowCount > 0) {
        while ($row = $result->fetch_assoc()) {
            $quiz = array();
            $quiz["quiz_id"] = (int) $row["quiz_id"];
            $quiz["title"] = $row["title"];
            $quiz["detail"] = $row["detail"];
			$quiz["number_of_questions"] = (int) $row["number_of_questions"];

            array_push($response["quiz_data"], $quiz);
        }
    }
    $result->close();
} else {
    $response["success"] = 0;
    $response["message"] = "An error occurred while retrieving data.";
}

$db->close();
echo json_encode($response);

?>