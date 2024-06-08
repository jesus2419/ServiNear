<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';


// Consulta SQL para obtener los servicios
$sql = "SELECT id_usuario, Nombre, descripcion, contacto,precio_hora, Foto FROM servicio WHERE Estado = 1";  // Suponemos que Estado 1 significa activo
$result = $conn->query($sql);

$servicios = array();

// Verificar si se encontraron resultados
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $servicio = array(
            "id_usuario" => $row["id_usuario"],
            "Nombre" => $row["Nombre"],
            "descripcion" => $row["descripcion"],
            "contacto" => $row["contacto"],
            "precio_hora" => $row["precio_hora"],
            "Foto" => $row["Foto"]
        );
        $servicios[] = $servicio;
    }
}

// Devolver los servicios como JSON
echo json_encode($servicios);

$conn->close();
?>
