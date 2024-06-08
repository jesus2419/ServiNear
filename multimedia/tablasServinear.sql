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

ALTER TABLE Usuarios
ADD CONSTRAINT unique_usuario UNIQUE (usuario);


SELECT Nombre, descripcion, Foto FROM servicio WHERE Estado = 1;

CREATE TABLE servicio (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    Nombre VARCHAR(50),
    descripcion VARCHAR(50),
    contacto VARCHAR(50),
    precio_hora VARCHAR(50),
    Foto BLOB,
    Fecha_de_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    Estado BOOLEAN DEFAULT true,
    
    FOREIGN KEY (id_usuario) REFERENCES Usuarios(ID) -- Agregar la clave externa que referencia a la tabla Rol
);

ALTER TABLE servicio MODIFY descripcion VARCHAR(255);

CREATE TABLE `chat` (
  `ID` int NOT NULL AUTO_INCREMENT,
  `id_destinatario` int DEFAULT NULL,
  `id_remitente` int DEFAULT NULL,
  `contenido` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_de_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `estado` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `id_destinatario` (`id_destinatario`),
  KEY `id_remitente` (`id_remitente`),
  CONSTRAINT `chat_ibfk_1` FOREIGN KEY (`id_destinatario`) REFERENCES `usuarios` (`ID`),
  CONSTRAINT `chat_ibfk_2` FOREIGN KEY (`id_remitente`) REFERENCES `usuarios` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci