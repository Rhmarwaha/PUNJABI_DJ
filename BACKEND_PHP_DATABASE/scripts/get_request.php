<?php
// required headers
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
 

// database connection will be here

// include database and object files
include_once 'database.php';
include_once 'requests.php';
 
// instantiate database and product object
$database = new Database();
$db = $database->getConnection();
 
// initialize object
$request = new Requests($db);
 
if ($_GET) {
    $name = $_GET['name'];
    $songName = $_GET['request'];
} 

// read products will be here
// query products

$stmt = $request->storeRequest($name, $songName);
$num = $stmt->rowCount();


// check if more than 0 record found
if($num == 1){
    
    $output = array("response_code" => "200","message" => "ok");
    // set response code - 200 OK
    http_response_code(200);
    // show products data in json format
    echo json_encode($output);
}
else{
 
    // set response code - 404 Not found
    http_response_code(404);
 
    // tell the user no products found
    echo json_encode(
        array("response_code" => "404","message" => "not found")
    );
}
?>
