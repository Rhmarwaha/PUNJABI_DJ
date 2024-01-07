<?php
// required headers
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");
 

// database connection will be here

// include database and object files
include_once 'database.php';
include_once 'singers.php';
 
// instantiate database and product object
$database = new Database();
$db = $database->getConnection();
 
// initialize object
$singer = new Singer($db);
 
if ($_GET) {
    $page_id = $_GET['page_id'];

} 

// read products will be here
// query products
$no_pages = $singer->pages();
$stmt = $singer->read($page_id);
$num = $stmt->rowCount();

// check if more than 0 record found
if($num>0){
    
    // products array
    $products_arr=array();
    $products_arr["records"]=array();
 
    // retrieve our table contents
    // fetch() is faster than fetchAll()
    // http://stackoverflow.com/questions/2770630/pdofetchall-vs-pdofetch-in-a-loop
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)){
        // extract row
        // this will make $row['name'] to
        // just $name only
        extract($row);
 
        $product_item=array(
            "id" => $Id,
            "name" => $Name,
            "image" => $Image
        );
 
        array_push($products_arr["records"], $product_item);
    }
    
    $output = array("no_pages" => ceil($no_pages),"data" => $products_arr);
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
        array("message" => "No products found.")
    );
}
?>
