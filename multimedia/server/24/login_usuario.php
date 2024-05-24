<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';
// Obtener los datos del POST
$username = $_POST['username'];
$password = $_POST['password'];

// Consulta SQL para verificar el usuario y obtener su ID
$sql = "SELECT ID FROM Usuarios WHERE usuario = '$username' AND pass = '$password'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // Usuario encontrado, retornar ID
    $row = $result->fetch_assoc();
    $response['success'] = true;
    $response['id'] = $row['ID'];
} else {
    // Usuario no encontrado
    $response['success'] = false;
}

// Cerrar conexión
$conn->close();

// Retornar respuesta en formato JSON
header('Content-Type: application/json');
echo json_encode($response);
?>
