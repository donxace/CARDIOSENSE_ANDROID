<?php
// Database connection settings
$servername = "localhost";
$username = "root";     // default XAMPP username
$password = "";         // default XAMPP password
$dbname = "arduino_db"; // Make sure this database exists

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get POST data from Arduino
$temperature = isset($_POST['temperature']) ? $_POST['temperature'] : 120;
$humidity = isset($_POST['humidity']) ? $_POST['humidity'] : 100;

// Insert data into database
$sql = "INSERT INTO sensor_data (temperature, humidity) VALUES ('$temperature', '$humidity')";

if ($conn->query($sql) === TRUE) {
    echo "Data inserted successfully";
} else {
    echo "Error: " . $conn->error;
}

$conn->close();
?>
