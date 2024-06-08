<?php
// Incluir el archivo de conexión a la base de datos
include 'conexion.php';

// Inicializar el array de respuesta
$response = array();

// Verificar si se recibieron los datos del formulario
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Recoger los datos del formulario
    $idDestinatario = $_POST['idDestinatario'];
    $contenido = $_POST['contenido'];
    $remitente = $_POST['remitente'];

    // Llamar al procedimiento almacenado InsertarUsuario
    $sql = "CALL InsertChatMessage($idDestinatario, '$contenido', '$remitente')";

    if ($conn->query($sql) === TRUE) {
        $response['success'] = true;
        $response['message'] = "Mensaje enviado correctamente.";
    } else {
        $response['success'] = false;
        $response['message'] = "Error al enviar mensaje: " . $conn->error;
    }
} else {
    $response['success'] = false;
    $response['message'] = "Método no permitido.";
}

// Cerrar la conexión a la base de datos
$conn->close();

// Enviar la respuesta en formato JSON
header('Content-Type: application/json');
echo json_encode($response);
?>
