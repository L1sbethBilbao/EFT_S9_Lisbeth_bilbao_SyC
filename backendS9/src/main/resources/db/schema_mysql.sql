-- =============================================================================
-- Esquema de referencia · Unidos por Los Animales (backend Spring Boot + JPA)
-- Motor: MySQL 8.x · Charset UTF-8
--
-- NOTA: En desarrollo, las tablas suelen crearse/actualizarse solas con Hibernate
--       (spring.jpa.hibernate.ddl-auto=update en application.properties).
--       Este script sirve para documentar el modelo, crear la BD en otro entorno
--       o cumplir entregables que pidan SQL explícito.
--
-- Usuario/contraseña de aplicación (ejemplo): app / app — debe coincidir con
-- spring.datasource.* en application.properties
-- =============================================================================

CREATE DATABASE IF NOT EXISTS unidos_animales
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE unidos_animales;

-- ---------------------------------------------------------------------------
-- Tablas base (sin FK externas al resto del dominio clínico/adopción)
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS duenos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre_completo VARCHAR(160) NOT NULL,
  email VARCHAR(160) NULL,
  telefono VARCHAR(40) NULL,
  direccion VARCHAR(255) NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS veterinarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(160) NOT NULL,
  especialidad VARCHAR(120) NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS animales (
  id BIGINT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(120) NOT NULL,
  especie VARCHAR(120) NULL,
  raza VARCHAR(120) NULL,
  edad INT NOT NULL,
  ubicacion VARCHAR(160) NULL,
  genero VARCHAR(24) NULL,
  estado_adopcion VARCHAR(32) NULL,
  foto_url VARCHAR(512) NULL,
  dueno_id BIGINT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_animales_dueno FOREIGN KEY (dueno_id) REFERENCES duenos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(120) NOT NULL,
  rol VARCHAR(32) NULL,
  veterinario_id BIGINT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_usuarios_username (username),
  CONSTRAINT fk_usuarios_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Adopciones: solicitudes y mensajes del hilo con coordinación
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS solicitudes_adopcion (
  id BIGINT NOT NULL AUTO_INCREMENT,
  codigo_seguimiento VARCHAR(40) NOT NULL,
  animal_id BIGINT NOT NULL,
  nombre_completo VARCHAR(200) NOT NULL,
  email VARCHAR(255) NOT NULL,
  telefono VARCHAR(40) NULL,
  direccion VARCHAR(300) NULL,
  ciudad VARCHAR(120) NULL,
  tipo_vivienda VARCHAR(24) NOT NULL,
  personas_hogar INT NOT NULL,
  tiene_ninos TINYINT(1) NULL,
  tiene_otras_mascotas TINYINT(1) NULL,
  experiencia_mascotas VARCHAR(4000) NULL,
  motivacion_adopcion VARCHAR(4000) NOT NULL,
  estado VARCHAR(24) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_solicitudes_codigo (codigo_seguimiento),
  CONSTRAINT fk_solicitudes_animal FOREIGN KEY (animal_id) REFERENCES animales (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS mensajes_solicitud_adopcion (
  id BIGINT NOT NULL AUTO_INCREMENT,
  solicitud_id BIGINT NOT NULL,
  rol_autor VARCHAR(24) NOT NULL,
  cuerpo VARCHAR(4000) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_mensajes_solicitud FOREIGN KEY (solicitud_id) REFERENCES solicitudes_adopcion (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- Clínica: citas → registro médico → factura y líneas
-- ---------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS citas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  animal_id BIGINT NOT NULL,
  veterinario_id BIGINT NOT NULL,
  fecha_hora DATETIME(6) NOT NULL,
  motivo VARCHAR(500) NULL,
  estado VARCHAR(24) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_citas_animal FOREIGN KEY (animal_id) REFERENCES animales (id),
  CONSTRAINT fk_citas_veterinario FOREIGN KEY (veterinario_id) REFERENCES veterinarios (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS registros_medicos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cita_id BIGINT NOT NULL,
  fecha_atencion DATETIME(6) NOT NULL,
  diagnostico VARCHAR(2000) NULL,
  tratamiento VARCHAR(2000) NULL,
  medicamentos VARCHAR(2000) NULL,
  notas VARCHAR(4000) NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_registros_cita (cita_id),
  CONSTRAINT fk_registros_cita FOREIGN KEY (cita_id) REFERENCES citas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS facturas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  registro_medico_id BIGINT NOT NULL,
  numero_factura VARCHAR(40) NOT NULL,
  fecha_emision DATE NOT NULL,
  total DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_facturas_registro (registro_medico_id),
  UNIQUE KEY uk_facturas_numero (numero_factura),
  CONSTRAINT fk_facturas_registro FOREIGN KEY (registro_medico_id) REFERENCES registros_medicos (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS lineas_factura (
  id BIGINT NOT NULL AUTO_INCREMENT,
  factura_id BIGINT NOT NULL,
  tipo VARCHAR(24) NOT NULL,
  descripcion VARCHAR(500) NOT NULL,
  monto DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_lineas_factura FOREIGN KEY (factura_id) REFERENCES facturas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
