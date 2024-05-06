CREATE TABLE Rol (
    Id INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50),
    Estado BOOLEAN
);

-- Insertar rol de usuario
INSERT INTO Rol (Nombre, Estado) VALUES ('Usuario', true);

-- Insertar rol de vendedor
INSERT INTO Rol (Nombre, Estado) VALUES ('Vendedor', true);


select * from rol;


-- Crear la tabla Usuarios
CREATE TABLE Usuarios (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50),
    Apellidos VARCHAR(50),
    usuario VARCHAR(50),
    Correo VARCHAR(50),
    pass VARCHAR(50),
    Foto BLOB,
    Fecha_de_creacion DATETIME,
    id_rol INT,
    Estado BOOLEAN,
    FOREIGN KEY (id_rol) REFERENCES Rol(Id) -- Agregar la clave externa que referencia a la tabla Rol
);
