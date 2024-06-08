<?php
$servername = "bd-servinear.mysql.database.azure.com";  // Cambia a la dirección de tu servidor si es diferente
$username = "servidor";   // Cambia al nombre de usuario de tu base de datos
$password = "Jesus.2023"; // Cambia a la contraseña de tu base de datos
$dbname = "servinear"; // Cambia al nombre de tu base de datos

//Crear conexion
$conn = new mysqli($servername, $username, $password, $dbname);

// Comprobar la conexion
if ($conn->connect_error) {
    die("La conexión a la base de datos ha fallado: " . $conn->connect_error);
}
//echo "Conexion exitosa a la base de datos";
?>
