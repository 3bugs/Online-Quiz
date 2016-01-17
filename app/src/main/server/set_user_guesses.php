<?php
 
error_reporting(E_ERROR | E_PARSE);
header('Content-type=application/json; charset=utf-8');

$response = array();

if (!isset($_POST['user_id']) || !isset($_POST['quiz_id']) || !isset($_POST['question_id']) || !isset($_POST['choice_id'])) {
    $response["success"] = 0;
    $response["message"] = "Required POST parameter is missing.";
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

$db->query("SET character_set_results=utf8");
$db->query('SET character set utf8'); // กำหนดเพื่อให้ข้อมูลที่เขียนลงฐานข้อมูลเป็นภาษาไทย

/****************************
 *** Insert multiple rows ***
 ****************************
INSERT INTO tbl_name
    (a,b,c)
VALUES
    (1,2,3),
    (4,5,6),
    (7,8,9);
****************************/

$userId = $_POST['user_id'];
$quizId = $_POST['quiz_id'];

$sql = "INSERT INTO user_guesses(user_id, quiz_id, question_id, choice_id) VALUES ";

for ($i = 0; $i < count($_POST['question_id']); $i++) {
    $questionId = $_POST['question_id'][$i];
    $choiceId = $_POST['choice_id'][$i];
    
    $sql .= "($userId, $quizId, $questionId, $choiceId),";    
}
$sql = substr($sql, 0, strlen($sql) - 1); // remove last comma

if ($result = $db->query($sql)) {
    $response["success"] = 1;
    $response["message"] = "Data added successfully.";
} else {
    $response["success"] = 0;
    $response["message"] = "An error occurred while inserting data: $sql";
}

$db->close();

echo json_encode($response);

?>