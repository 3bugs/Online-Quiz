<?php

error_reporting(E_ERROR | E_PARSE);
header('Content-type=application/json; charset=utf-8');

$response = array();

if (!isset($_GET['quiz_id'])) {
    $response["success"] = 0;
    $response["message"] = "Required GET parameter is missing.";
	echo json_encode($response);
	exit();
}

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

$quizId = $_GET['quiz_id'];

$sql = "SELECT * FROM questions, choices WHERE quiz_id = $quizId AND questions.question_id = choices.question_id ORDER BY questions.question_id, choice_id";
//$sql = "SELECT * FROM questions WHERE quiz_id = $quizId ORDER BY question_id";

if ($result = $db->query($sql)) {
    $response["success"] = 1;
    $response["quiz_id"] = (int) $quizId;
    $response["question_data"] = array();

    $rowCount = $result->num_rows;

	$lastQuestionId = 0;
	
    if ($rowCount > 0) {
        while ($row = $result->fetch_assoc()) {
			
            if ($lastQuestionId != (int) $row["question_id"]) {
				$lastQuestionId = (int) $row["question_id"];
			
                if (isset($question)) {
                    array_push($response["question_data"], $question);	
                }
            
				$question = array();
				$question["question_id"] = (int) $row["question_id"];
				$question["title"] = $row["title"];
				$question["detail"] = $row["detail"];
				$question["picture"] = $row["picture"];
				
				$question["choice_data"] = array();
			}

            $choice = array();
            $choice["choice_id"] = (int) $row["choice_id"];
            $choice["text"] = $row["text"];
            $choice["is_answer"] = (boolean) $row["is_answer"];

            array_push($question["choice_data"], $choice);
        }
        array_push($response["question_data"], $question);	
    }
    $result->close();
} else {
    $response["success"] = 0;
    $response["message"] = "An error occurred while retrieving data.";
}

$db->close();
echo json_encode($response);

?>