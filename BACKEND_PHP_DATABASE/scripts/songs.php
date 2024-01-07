<?php
class Product{
 
    // database connection and table name
    private $conn;
    private $table_name = "Songs";
 
    // object properties
    public $id;
    public $name;
    public $image;
    public $song_link;
    public $singer_id;

    
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

function songNames(){
    
    $query = "SELECT Name FROM " . $this->table_name . " WHERE 1 ;";
    $stmt = $this->conn->prepare($query);
    $stmt->execute();
    return $stmt;
}

function searchSongName($song_name_search){
    
    $query = "SELECT * FROM " . $this->table_name . " WHERE Name LIKE " . $song_name_search . " ; " ;
    $stmt = $this->conn->prepare($query);
    $stmt->execute();
    return $stmt;
}

function singerSongs($singer_id,$page_id){
    
    $no_pages = $this->singerSongsPages($singer_id);
    $number = (ceil($no_pages) - $page_id) * 10;
    //SELECT * FROM `Songs` WHERE Singer_id = 1 ORDER BY Id LIMIT 20,10 pageid = 3

    
    $query = "SELECT * FROM " . $this->table_name . " WHERE Singer_id = " . $singer_id . " ORDER BY Id LIMIT ". $number . " , 10 ; " ;
    $stmt = $this->conn->prepare($query);
    $stmt->execute();
    return $stmt;
}
function singerSongsPages($singer_id){
    $query = "SELECT * FROM " . $this->table_name . " WHERE Singer_id = " . $singer_id . " ; " ;
    $stmt = $this->conn->prepare($query);
    // execute query
    $stmt->execute();
    $row = $stmt->rowCount();
    return ($row / 10);
}
}
?>