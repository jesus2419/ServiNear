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




DELIMITER //

CREATE PROCEDURE InsertChatMessage(
    IN p_id_destinatario INT,
    IN p_contenido VARCHAR(255),
    IN p_nombre_usuario_remitente VARCHAR(50)
)
BEGIN
    DECLARE v_id_remitente INT;

    -- Buscar el ID del remitente usando el nombre de usuario
    SELECT ID INTO v_id_remitente
    FROM Usuarios
    WHERE usuario = p_nombre_usuario_remitente;

    -- Verificar si se encontró el ID del remitente
    IF v_id_remitente IS NOT NULL THEN
        -- Insertar el nuevo mensaje en la tabla chat
        INSERT INTO chat (id_destinatario, id_remitente, contenido, fecha_de_creacion, estado)
        VALUES (p_id_destinatario, v_id_remitente, p_contenido, NOW(), 1);
    ELSE
        -- Manejo de error si el remitente no existe
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario remitente no existe';
    END IF;
END //

DELIMITER ;

