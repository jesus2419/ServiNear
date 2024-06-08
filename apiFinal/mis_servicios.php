<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';

// Verificar si el nombre de usuario se ha enviado mediante POST
if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST['usuario'])) {
    $usuario = $_POST['usuario'];

    // Consulta SQL para obtener los servicios del usuario especificado
    $sql = "SELECT 
                servicio.ID,
                servicio.id_usuario,
                servicio.Nombre AS NombreServicio,
                servicio.descripcion,
                servicio.contacto,
                servicio.precio_hora,
                servicio.Foto AS FotoServicio,
                servicio.Fecha_de_creacion AS FechaDeCreacionServicio,
                servicio.Estado AS EstadoServicio,
                Usuarios.usuario AS NombreUsuario
            FROM 
                servicio
            JOIN 
                Usuarios ON servicio.id_usuario = Usuarios.ID
            WHERE 
                Usuarios.usuario = ?";

    // Preparar la consulta
    $stmt = $conn->prepare($sql);
    if ($stmt === false) {
        die(json_encode(array("error" => "Error en la preparación de la consulta: " . $conn->error)));
    }

    // Vincular parámetros
    $stmt->bind_param("s", $usuario);

    // Ejecutar la consulta
    $stmt->execute();

    // Obtener el resultado
    $result = $stmt->get_result();

    $servicios = array();

    // Verificar si se encontraron resultados
    if ($result->num_rows > 0) {
        while ($row = $result->fetch_assoc()) {
            $servicio = array(
                "ID" => $row["ID"],
                "id_usuario" => $row["id_usuario"],
                "NombreServicio" => $row["NombreServicio"],
                "descripcion" => $row["descripcion"],
                "contacto" => $row["contacto"],
                "precio_hora" => $row["precio_hora"],
                "FotoServicio" => $row["FotoServicio"],
                "FechaDeCreacionServicio" => $row["FechaDeCreacionServicio"],
                "EstadoServicio" => $row["EstadoServicio"],
                "NombreUsuario" => $row["NombreUsuario"]
            );
            $servicios[] = $servicio;
        }
    }

    // Devolver los servicios como JSON
    echo json_encode($servicios);

    // Cerrar la declaración
    $stmt->close();
} else {
    echo json_encode(array("error" => "Se requiere el nombre de usuario"));
}

// Cerrar la conexión
$conn->close();
?>
