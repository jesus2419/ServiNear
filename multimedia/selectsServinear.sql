SELECT Nombre, descripcion, contacto,precio_hora, Foto FROM servicio WHERE Estado = 1;


SELECT ID, id_usuario, Nombre, descripcion, contacto, precio_hora, Foto, Fecha_de_creacion, Estado FROM servicio WHERE Estado = 1 ;

select * from usuarios;

UPDATE usuarios
SET Nombre  = ?, Apellidos  = ?, Correo  = ?, pass  = ?, Foto  = ?
WHERE usuario = ?;

UPDATE usuarios
SET Estado = 1
WHERE ID = 7;


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
                Usuarios.usuario = 'jesus6'