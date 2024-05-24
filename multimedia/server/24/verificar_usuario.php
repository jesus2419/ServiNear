<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';

// Obtener el nombre de usuario enviado por POST
$username = $_POST['username'];

// Consulta SQL para verificar si el nombre de usuario existe
$sql = "SELECT ID FROM Usuarios WHERE usuario = '$username'";
$result = $conn->query($sql);

// Verificar si se encontraron resultados
if ($result->num_rows > 0) {
    // El nombre de usuario existe, obtener el ID
    $row = $result->fetch_assoc();
    $idUsuario = $row['ID'];

    // Devolver el ID de usuario como respuesta
    echo $idUsuario;
} else {
    // El nombre de usuario no existe
    echo "false";
}

$conn->close();
?>
