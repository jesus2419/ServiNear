<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';

// Obtener los datos enviados por POST
$idservicio = $_POST['id_servicio'];




// Preparar la consulta usando consultas preparadas
$sql = "DELETE from servicio where ID = ?";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $idservicio);

// Ejecutar la consulta
if ($stmt->execute()) {
    echo "Servicio actualizado correctamente";
} else {
    echo "Error al actualizar el servicio: " . $conn->error;
}

$stmt->close();
$conn->close();
?>
