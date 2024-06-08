<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';

// Verifica la conexión a la base de datos
if ($conn->connect_error) {
    echo json_encode(array("error" => "Conexión fallida: " . $conn->connect_error));
    exit();
}

// Consulta SQL para obtener usuarios
$sql = "SELECT ID, Nombre, Apellidos, usuario, Foto FROM Usuarios";

$resultado = $conn->query($sql);

if ($resultado === FALSE) {
    echo json_encode(array("error" => "Error en la consulta: " . $conn->error));
    exit();
}

if ($resultado->num_rows > 0) {
    $response = array();
    while($fila = $resultado->fetch_assoc()) {
        $usuario = array(
            "ID" => $fila['ID'],
            "Nombre" => $fila['Nombre'],
            "Apellidos" => $fila['Apellidos'],
            "usuario" => $fila['usuario'],
            "Foto" => base64_encode($fila['Foto'])  // Codificar la imagen a base64
        );
        $response[] = $usuario;
    }
    echo json_encode($response);
} else {
    echo json_encode(array("message" => "No se encontraron usuarios"));
}

$conn->close();
?>
