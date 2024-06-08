<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';

// Obtener los datos enviados por POST
$idservicio = $_POST['id_servicio'];
$nombre = $_POST['nombre'];
$descripcion = $_POST['descripcion'];
$informacion = $_POST['informacion'];
$precio = $_POST['precio'];
$fotoBase64 = $_POST['foto_base64'];

// Decodificar la imagen Base64 a un blob
$fotoBlob = base64_decode($fotoBase64);

// Preparar la consulta usando consultas preparadas
$sql = "UPDATE servicio
        SET Nombre = ?, descripcion = ?, contacto = ?, precio_hora = ?, Foto = ?
        WHERE ID = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("sssssi", $nombre, $descripcion, $informacion, $precio, $fotoBase64, $idservicio);

// Ejecutar la consulta
if ($stmt->execute()) {
    echo "Servicio actualizado correctamente";
} else {
    echo "Error al actualizar el servicio: " . $conn->error;
}

$stmt->close();
$conn->close();
?>
