<?php
header('Content-Type: application/json');

// Incluir el archivo de conexión a la base de datos
include_once 'conexion.php';

// Archivo de log para depuración
$logFile = 'registro_log.txt';

// Array para almacenar la respuesta
$response = array('success' => false, 'message' => 'Unknown error');

// Verificar que la solicitud sea POST
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Obtener los datos del POST
    $nombre = isset($_POST['nombre']) ? $_POST['nombre'] : '';
    $apellidos = isset($_POST['apellidos']) ? $_POST['apellidos'] : '';
    $correo = isset($_POST['correo']) ? $_POST['correo'] : '';
    $username = isset($_POST['username']) ? $_POST['username'] : '';
    $password = isset($_POST['password']) ? $_POST['password'] : '';
    $esPrestador = isset($_POST['esPrestador']) ? $_POST['esPrestador'] : false;
    $imagenBase64 = isset($_POST['imagenBase64']) ? $_POST['imagenBase64'] : '';
    $id_rol = 1;

    // Log de los datos recibidos
    file_put_contents($logFile, "Datos recibidos: " . print_r($_POST, true) . "\n", FILE_APPEND);

    // Validar los datos recibidos
    if (empty($nombre) || empty($apellidos) || empty($correo) || empty($username) || empty($password)) {
        $response['message'] = 'Todos los campos son obligatorios.';
    } else {
        // Decodificar la imagen Base64
        $imageData = base64_decode($imagenBase64);
        
        if ($imageData === false) {
            $response['message'] = 'Error al decodificar la imagen Base64.';
        } else {
            if ($esPrestador == true){
                $id_rol = 2;
            }else{
                $id_rol = 1;
            }
            // Preparar la llamada al procedimiento almacenado
            $stmt = $conn->prepare("CALL InsertarUsuario(?, ?, ?, ?, ?, ?, ?)");
            if ($stmt === false) {
                // Si la preparación de la declaración falla, registra el error
                $response['message'] = 'Error en la preparación de la llamada: ' . $conn->error;
            } else {
                // Bind de los parámetros y ejecución de la consulta
                $stmt->bind_param("ssssssi", $nombre, $apellidos, $username, $correo, $password, $imageData, $id_rol);
                
                if ($stmt->execute()) {
                    $response['success'] = true;
                    $response['message'] = 'Registro exitoso';
                } else {
                    $response['message'] = 'Error al registrar: ' . $stmt->error;
                }

                $stmt->close();
            }
        }
    }
} else {
    $response['message'] = 'Método no permitido';
}

// Log de la respuesta
file_put_contents($logFile, "Respuesta: " . json_encode($response) . "\n", FILE_APPEND);

// Devolver respuesta en formato JSON
echo json_encode($response);
?>
