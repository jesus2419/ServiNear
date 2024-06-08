<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';

// Obtener los datos enviados por POST
$idUsuario = $_POST['id_usuario'];
$nombre = $_POST['nombre'];
$descripcion = $_POST['descripcion'];
$informacion = $_POST['informacion'];
$precio = $_POST['precio'];
$fotoBase64 = $_POST['foto_base64'];
$fechaCreacion = $_POST['fecha_creacion'];
$estado = $_POST['estado'];

// Preparar la consulta SQL para insertar el servicio
$sql = "INSERT INTO servicio (id_usuario, Nombre, descripcion, contacto, precio_hora, Foto, Fecha_de_creacion, Estado)
        VALUES ('$idUsuario', '$nombre', '$descripcion', '$informacion', '$precio', '$fotoBase64', '$fechaCreacion', '$estado')";

if ($conn->query($sql) === TRUE) {
    echo "Servicio registrado correctamente";
} else {
    echo "Error al registrar el servicio: " . $conn->error;
}

$conn->close();
?>
