<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';

// Obtener los datos del POST
$username = $_POST['username'];
$password = $_POST['password'];

// Consulta SQL para verificar el usuario y obtener sus datos
$sql = "SELECT ID, Nombre, Apellidos, usuario, Correo, Foto, id_rol, pass FROM Usuarios WHERE usuario = ? AND pass = ? and Estado = 1";
$stmt = $conn->prepare($sql);
$stmt->bind_param("ss", $username, $password);
$stmt->execute();
$result = $stmt->get_result();

$response = array();

if ($result->num_rows > 0) {
    // Usuario encontrado
    $row = $result->fetch_assoc();

    // Preparar la respuesta
    $response['success'] = true;
    $response['id'] = $row['ID'];
    $response['nombre'] = $row['Nombre'];
    $response['apellidos'] = $row['Apellidos'];
    $response['usuario'] = $row['usuario'];
    $response['correo'] = $row['Correo'];
    $response['rol'] = $row['id_rol'];
    $response['pass'] = $row['pass'];


    
    // Decodificar la imagen desde BLOB a base64
    $imageData = base64_encode($row['Foto']);
    $response['imagen'] = $imageData;

} else {
    // Usuario no encontrado
    $response['success'] = false;
}

// Cerrar declaración y conexión
$stmt->close();
$conn->close();

// Retornar respuesta en formato JSON
echo json_encode($response);
?>
