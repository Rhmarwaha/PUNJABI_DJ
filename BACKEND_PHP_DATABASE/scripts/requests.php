<?php
class Requests{
 
    // database connection and table name
    private $conn;
    private $table_name = "Requests";
 
    // object properties
    public $id;
    public $userName;
    public $songName;
    public $solved;

    // constructor with $db as database connection
    public function __construct($db){
        $this->conn = $db;
    }
// read images
function storeRequest($name, $request_song){
    
    //INSERT INTO `Requests` (`Id`, `User_Name`, `Song_Name`, `Solved`) VALUES (NULL, 'qw', 'qwweer', '0');
    
    // select all query
	$query = "INSERT INTO " . $this->table_name . " (`Id`, `User_Name`, `Song_Name`, `Solved`) VALUES (NULL, " . $name . " , " . $request_song . " , '0' ) ; "; 
    
    // prepare query statement
    $stmt = $this->conn->prepare($query);
    
    // execute query
    $stmt->execute();
    return $stmt;
}

}
?>