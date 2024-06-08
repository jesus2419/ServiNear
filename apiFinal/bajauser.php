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
   
    $username = isset($_POST['username']) ? $_POST['username'] : '';
    
    

    // Log de los datos recibidos
    
            
            // Preparar la llamada al procedimiento almacenado
            $stmt = $conn->prepare("UPDATE usuarios
            SET Estado = 0
            WHERE usuario = ?");
            if ($stmt === false) {
                // Si la preparación de la declaración falla, registra el error
                $response['message'] = 'Error en la preparación de la llamada: ' . $conn->error;
            } else {
                // Bind de los parámetros y ejecución de la consulta
                $stmt->bind_param("s", $username);
                
                if ($stmt->execute()) {
                    $response['success'] = true;
                    $response['message'] = 'Registro exitoso';
                } else {
                    $response['message'] = 'Error al registrar: ' . $stmt->error;
                }

                $stmt->close();
            }
        
    
} else {
    $response['message'] = 'Método no permitido';
}

// Log de la respuesta
file_put_contents($logFile, "Respuesta: " . json_encode($response) . "\n", FILE_APPEND);

// Devolver respuesta en formato JSON
echo json_encode($response);
?>
