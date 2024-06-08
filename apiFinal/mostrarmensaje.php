<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';

// Obtener los parámetros del POST
$remitente = $_POST['remitente'];      // Aquí debería ser 'jesus6'
$idDestinatario = $_POST['idDestinatario'];  // Aquí debería ser 3

// Consulta SQL para obtener los mensajes de chat
$sql = "SELECT 
            c.ID AS ChatID,
            c.contenido,
            c.fecha_de_creacion,
            c.estado,
            u1.usuario AS Remitente,
            u2.usuario AS Destinatario
        FROM 
            chat c
        JOIN 
            Usuarios u1 ON c.id_remitente = u1.ID
        JOIN 
            Usuarios u2 ON c.id_destinatario = u2.ID
        WHERE 
            (u1.usuario = '$remitente' AND u2.ID = $idDestinatario) OR
            (u1.usuario = '$remitente' AND u2.usuario = '$remitente') OR
            (u1.ID = $idDestinatario AND u2.usuario = '$remitente') OR
            (u1.ID = $idDestinatario AND u2.ID = $idDestinatario)
        ORDER BY 
            c.fecha_de_creacion ASC";

// Ejecutar la consulta
$result = $conn->query($sql);

// Verificar si se obtuvieron resultados
if ($result->num_rows > 0) {
    $chatMessages = array();
    while ($row = $result->fetch_assoc()) {
        $message = array(
            "ChatID" => $row["ChatID"],
            "contenido" => $row["contenido"],
            "fecha_de_creacion" => $row["fecha_de_creacion"],
            "estado" => $row["estado"],
            "Remitente" => $row["Remitente"],
            "Destinatario" => $row["Destinatario"]
        );
        $chatMessages[] = $message;
    }
    // Devolver los mensajes como JSON
    echo json_encode($chatMessages);
} else {
    // No se encontraron mensajes
    echo json_encode(array()); // Devolver un array vacío en caso de no haber resultados
}

$conn->close();
?>
