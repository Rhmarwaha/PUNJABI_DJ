<?php
class Singer{
 
    // database connection and table name
    private $conn;
    private $table_name = "Singers";
 
    // object properties
    public $id;
    public $name;
    public $image;

    // constructor with $db as database connection
    public function __construct($db){
        $this->conn = $db;
    }
// read images
function read($page_id){
    $no_pages = $this->pages();
    $number = ($no_pages + 1 - $page_id) * 10;
    
    // select all query
	$query = "SELECT * FROM " . $this->table_name . " WHERE id IN ( " . $number . "," . ($number - 1) . "," . ($number - 2) . "," . ($number - 3) . "," . ($number - 4) . "," . ($number - 5) . "," . ($number - 6) . "," . ($number - 7) . "," . ($number - 8) . "," . ($number - 9) ." ) ORDER BY Id DESC";
    // prepare query statement
    $stmt = $this->conn->prepare($query);
 
    // execute query
    $stmt->execute();
    return $stmt;
}

function pages(){
    
    $query = "SELECT * FROM " . $this->table_name . " ; " ;
    $stmt = $this->conn->prepare($query);
    // execute query
    $stmt->execute();
    $row = $stmt->rowCount();
    return ($row / 10);
    
}

}
?>