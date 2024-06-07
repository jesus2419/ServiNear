SELECT Nombre, descripcion, contacto,precio_hora, Foto FROM servicio WHERE Estado = 1;


SELECT ID, id_usuario, Nombre, descripcion, contacto, precio_hora, Foto, Fecha_de_creacion, Estado FROM servicio WHERE Estado = 1 ;

select * from usuarios;
select * from chat;


UPDATE usuarios
SET Nombre  = ?, Apellidos  = ?, Correo  = ?, pass  = ?, Foto  = ?
WHERE usuario = ?;

UPDATE usuarios
SET Estado = 1
WHERE ID = 7;

UPDATE usuarios
SET id_rol = 2
WHERE ID = 11;

UPDATE usuarios
SET usuario = 'edgarin'
WHERE ID = 13;

select * from servicio;
-- ID, id_usuario, Nombre, descripcion, contacto, precio_hora, Foto, Fecha_de_creacion, Estado

UPDATE servicio
SET Nombre  = ?, descripcion  = ?, contacto  = ?, precio_hora  = ?, Foto  = ?
WHERE ID = ?;

UPDATE servicio
SET Foto  = ''
WHERE ID = 4;

delete from servicio where ID = 2


SELECT 
    servicio.ID,
    servicio.id_usuario,
    servicio.Nombre AS NombreServicio,
    servicio.descripcion,
    servicio.contacto,
    servicio.precio_hora,
    servicio.Foto AS FotoServicio,
    servicio.Fecha_de_creacion AS FechaDeCreacionServicio,
    servicio.Estado AS EstadoServicio,
    Usuarios.usuario AS NombreUsuario
FROM 
    servicio
JOIN 
    Usuarios ON servicio.id_usuario = Usuarios.ID;


SELECT 
                servicio.ID,
                servicio.id_usuario,
                servicio.Nombre AS NombreServicio,
                servicio.descripcion,
                servicio.contacto,
                servicio.precio_hora,
                servicio.Foto AS FotoServicio,
                servicio.Fecha_de_creacion AS FechaDeCreacionServicio,
                servicio.Estado AS EstadoServicio,
                Usuarios.usuario AS NombreUsuario
            FROM 
                servicio
            JOIN 
                Usuarios ON servicio.id_usuario = Usuarios.ID
            WHERE 
                Usuarios.usuario = 'jesus6';
                
                
                
                
SELECT 
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
    Usuarios u2 ON c.id_destinatario = u2.ID where c.id_destinatario = 3 or c.id_remitente = 3 or  c.id_destinatario = 7 or c.id_remitente = 7 ;
    select * from chat;



SELECT 
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
    c.id_destinatario IN (3, 7) OR c.id_remitente IN (3, 7)
ORDER BY 
    c.fecha_de_creacion ASC;


SELECT 
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
    (u1.usuario = 'jesus2' AND u2.ID = 3) OR
    (u1.usuario = 'jesus2' AND u2.usuario = 'jesus2') OR
    (u1.ID = 3 AND u2.usuario = 'jesus2') OR
    (u1.ID = 3 AND u2.ID = 3)
ORDER BY 
    c.fecha_de_creacion ASC;

