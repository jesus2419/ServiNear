<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';

// Obtener el ID del usuario desde la solicitud POST
$idUsuario = $_POST['usuario'];

// Consulta SQL para obtener los datos del usuario
$sql = "SELECT Nombre, Apellidos, usuario, Correo, Foto, pass FROM Usuarios WHERE usuario = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $idUsuario);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    // Usuario encontrado, retornar datos
    $row = $result->fetch_assoc();
    $response['success'] = true;
    $response['nombre'] = $row['Nombre'];
    $response['apellidos'] = $row['Apellidos'];
    $response['usuario'] = $row['usuario'];
    $response['pass'] = $row['pass'];
    $response['correo'] = $row['Correo'];
    $response['imagen'] = base64_encode($row['Foto']); // Convertir la imagen a base64
} else {
    // Usuario no encontrado
    $response['success'] = false;
}

// Cerrar la conexión
$stmt->close();
$conn->close();

// Retornar respuesta en formato JSON
header('Content-Type: application/json');
echo json_encode($response);
?>
