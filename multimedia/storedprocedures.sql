DELIMITER //

CREATE PROCEDURE InsertarUsuario(
    IN p_Nombre VARCHAR(50),
    IN p_Apellidos VARCHAR(50),
    IN p_Usuario VARCHAR(50),
    IN p_Correo VARCHAR(50),
    IN p_Pass VARCHAR(50),
    IN p_Foto BLOB,
    IN p_id_rol INT
)
BEGIN
    DECLARE v_FechaCreacion DATETIME;

    -- Obtenemos la fecha y hora actual
    SET v_FechaCreacion = NOW();

    -- Insertamos el nuevo usuario con el estado por defecto verdadero
    INSERT INTO Usuarios (Nombre, Apellidos, usuario, Correo, pass, Foto, Fecha_de_creacion, id_rol, Estado)
    VALUES (p_Nombre, p_Apellidos, p_Usuario, p_Correo, p_Pass, p_Foto, v_FechaCreacion, p_id_rol, true);
END //

DELIMITER ;


-- Definición del procedimiento almacenado
DELIMITER //

CREATE PROCEDURE InsertarServicio(
    IN p_nombre VARCHAR(50),
    IN p_descripcion VARCHAR(50),
    IN p_contacto VARCHAR(50),
    IN p_precio_hora VARCHAR(50),
    IN p_foto_base64 BLOB,
    IN p_nombre_usuario VARCHAR(50)
)
BEGIN
    DECLARE v_id_usuario INT;
    
    -- Obtener el ID del usuario a partir del nombre de usuario
    SELECT ID INTO v_id_usuario
    FROM Usuarios
    WHERE usuario = p_nombre_usuario;

    -- Decodificar la imagen de base64 a BLOB
   

    -- Insertar el servicio
    INSERT INTO servicio (id_usuario, Nombre, descripcion, contacto, precio_hora, Foto, Fecha_de_creacion, Estado)
    VALUES (v_id_usuario, p_nombre, p_descripcion, p_contacto, p_precio_hora, p_foto_base64, NOW(), true);

END //

DELIMITER ;

