<?php
// Incluir el archivo de conexión a la base de datos
include 'conexion.php';

// Verificar si se recibieron los datos del formulario
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    // Recoger los datos del formulario
    $nombre = $_POST['nombre'];
    $apellidos = $_POST['apellidos'];
    $usuario = $_POST['username'];
    $correo = $_POST['correo'];
    $pass = $_POST['password'];
    $foto = $_POST['foto'];
    $id_rol = $_POST['esPrestador'];

    if ($id_rol == 1){
        $id_rol = 2;
    }else{
        $id_rol = 1;
    }

    // Llamar al procedimiento almacenado InsertarUsuario
    $sql = "CALL InsertarUsuario('$nombre', '$apellidos', '$usuario', '$correo', '$pass', 'n', $id_rol)";
    
    if ($conn->query($sql) === TRUE) {
        echo "Nuevo usuario insertado correctamente.";
    } else {
        echo "Error al insertar usuario: " . $conn->error;
    }
}

// Cerrar la conexión a la base de datos
$conn->close();
?>
