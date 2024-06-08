<?php
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';


// Consulta SQL para obtener los servicios
$sql = "SELECT 
c.ID AS ChatID,
c.contenido,
c.fecha_de_creacion,
c.estado,
u1.usuario AS Remitente,
u2.usuario AS Destinatario,
u2.ID AS id_destino,
u2.Foto AS foto
FROM 
chat c
JOIN 
Usuarios u1 ON c.id_remitente = u1.ID
JOIN 
Usuarios u2 ON c.id_destinatario = u2.ID
JOIN 
(
    SELECT 
        GREATEST(id_remitente, id_destinatario) AS usuario, 
        MAX(fecha_de_creacion) AS max_fecha
    FROM 
        chat
    GROUP BY 
        GREATEST(id_remitente, id_destinatario)
) subq ON GREATEST(c.id_remitente, c.id_destinatario) = subq.usuario AND c.fecha_de_creacion = subq.max_fecha
WHERE 
u1.usuario = 'jesus6' OR u2.usuario = 'jesus6'
ORDER BY 
c.fecha_de_creacion ASC";  // Suponemos que Estado 1 significa activo
$result = $conn->query($sql);

$servicios = array();

// Verificar si se encontraron resultados
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $servicio = array(
            "ChatID" => $row["ChatID"],
            "contenido" => $row["contenido"],
            "fecha_de_creacion" => $row["fecha_de_creacion"],
            "estado" => $row["estado"],
            "Remitente" => $row["Remitente"],
            "Destinatario" => $row["Destinatario"],
            "id_destino" => $row["id_destino"],
            "foto" => $row["foto"]
        );
        $servicios[] = $servicio;
    }
}

// Devolver los servicios como JSON
echo json_encode($servicios);

$conn->close();
?>


<?php
/*
header("Content-Type: application/json; charset=UTF-8");

// Incluir el archivo de conexión
include 'conexion.php';

// Función para escribir en el archivo de depuración

// Verificar si el nombre de usuario se ha enviado mediante POST
$usuario = $_POST['usuario'];

// Registrar el parámetro recibido para depuración
error_log("Usuario recibido: " . $usuario);

// Consulta SQL para obtener los chats más recientes del usuario especificado
$sql = "SELECT 
            c.ID AS ChatID,
            c.contenido,
            c.fecha_de_creacion,
            c.estado,
            u1.usuario AS Remitente,
            u2.usuario AS Destinatario,
            u2.ID AS id_destino,
            u2.Foto AS foto
        FROM 
            chat c
        JOIN 
            Usuarios u1 ON c.id_remitente = u1.ID
        JOIN 
            Usuarios u2 ON c.id_destinatario = u2.ID
        JOIN 
            (
                SELECT 
                    GREATEST(id_remitente, id_destinatario) AS usuario, 
                    MAX(fecha_de_creacion) AS max_fecha
                FROM 
                    chat
                GROUP BY 
                    GREATEST(id_remitente, id_destinatario)
            ) subq ON GREATEST(c.id_remitente, c.id_destinatario) = subq.usuario AND c.fecha_de_creacion = subq.max_fecha
        WHERE 
            u1.usuario = 'jesus6' OR u2.usuario = 'jesus6'
        ORDER BY 
            c.fecha_de_creacion ASC";

// Preparar la consulta
$stmt = $conn->prepare($sql);
if ($stmt === false) {
    die(json_encode(array("error" => "Error en la preparación de la consulta: " . $conn->error)));
}

// Vincular parámetros
//$stmt->bind_param("ss", $usuario, $usuario);

// Ejecutar la consulta
$stmt->execute();

// Obtener el resultado
$result = $stmt->get_result();

$chats = array();

// Verificar si se encontraron resultados
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $chat = array(
            "ChatID" => $row["ChatID"],
            "contenido" => $row["contenido"],
            "fecha_de_creacion" => $row["fecha_de_creacion"],
            "estado" => $row["estado"],
            "Remitente" => $row["Remitente"],
            "Destinatario" => $row["Destinatario"],
            "id_destino" => $row["id_destino"],
            "foto" => $row["foto"]
        );
        $chats[] = $chat;
    }
} else {
    error_log("No se encontraron chats para el usuario: " . $usuario);
}

// Devolver los chats como JSON
echo json_encode($chats);

// Cerrar la declaración
$stmt->close();
 /*else {
    error_log("Parámetro 'usuario' no recibido o método no es POST");
    escribirEnArchivo("Parámetro 'usuario' no recibido o método no es POST");
    echo json_encode(array("error" => "Se requiere el nombre de usuario"));
}

// Cerrar la conexión
$conn->close();
*/
?>