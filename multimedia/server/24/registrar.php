<?php
header('Content-Type: application/json');

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

    // Log de los datos recibidos
    file_put_contents($logFile, "Datos recibidos: " . print_r($_POST, true) . "\n", FILE_APPEND);

    // Validar los datos recibidos
    if (empty($nombre) || empty($apellidos) || empty($correo) || empty($username) || empty($password)) {
        $response['message'] = 'Todos los campos son obligatorios.';
    } else {
        // Aquí puedes incluir tu lógica para insertar los datos en la base de datos
        // Ejemplo de conexión y consulta a la base de datos
        $servername = "localhost";
        $dbusername = "root";
        $dbpassword = "";
        $dbname = "servinear";

        // Crear conexión
        $conn = new mysqli($servername, $dbusername, $dbpassword, $dbname);

        // Verificar la conexión
        if ($conn->connect_error) {
            $response['message'] = "Error de conexión: " . $conn->connect_error;
        } else {
            $stmt = $conn->prepare("INSERT INTO usuarios (nombre, apellidos, correo, username, password, es_prestador) VALUES (?, ?, ?, ?, ?, ?)");
            $stmt->bind_param("sssssi", $nombre, $apellidos, $correo, $username, $password, $esPrestador);

            if ($stmt->execute()) {
                $response['success'] = true;
                $response['message'] = 'Registro exitoso';
            } else {
                $response['message'] = 'Error al registrar: ' . $stmt->error;
            }

            $stmt->close();
            $conn->close();
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
