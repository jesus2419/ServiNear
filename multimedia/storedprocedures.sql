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
