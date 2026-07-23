-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 23-07-2026 a las 23:15:22
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `elsauce_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `horarios_prefijados`
--

CREATE TABLE `horarios_prefijados` (
  `id` smallint(5) UNSIGNED NOT NULL,
  `hora` time NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `horarios_prefijados`
--

INSERT INTO `horarios_prefijados` (`id`, `hora`, `created_at`, `updated_at`) VALUES
(1, '12:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(2, '13:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(3, '14:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(4, '15:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(5, '16:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(6, '17:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(7, '18:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(8, '19:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(9, '20:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(10, '21:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(11, '22:00:00', '2026-07-07 19:22:53', '2026-07-07 19:22:53');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mesas`
--

CREATE TABLE `mesas` (
  `id` int(10) UNSIGNED NOT NULL,
  `numero` varchar(255) DEFAULT NULL,
  `capacidad` int(11) DEFAULT NULL,
  `zona_id` int(10) UNSIGNED NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `mesas`
--

INSERT INTO `mesas` (`id`, `numero`, `capacidad`, `zona_id`, `created_at`, `updated_at`) VALUES
(1, 'M1', 2, 1, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(2, 'M2', 4, 1, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(3, 'M3', 6, 1, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(4, 'M4', 8, 1, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(5, 'M5', 2, 2, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(6, 'M6', 4, 2, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(7, 'M7', 6, 3, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(8, 'M8', 8, 3, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(9, 'M9', 2, 3, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(10, 'M10', 4, 3, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(11, 'M11', 6, 3, '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(12, 'M12', 8, 3, '2026-07-07 19:22:53', '2026-07-07 19:22:53');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pagos`
--

CREATE TABLE `pagos` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `reserva_id` bigint(20) UNSIGNED NOT NULL,
  `monto` decimal(8,2) NOT NULL CHECK (`monto` >= 0),
  `fecha_transaccion` datetime DEFAULT NULL,
  `metodo_pago` varchar(255) DEFAULT NULL,
  `estado_pago` enum('PAID','PENDING','FAILED','REFUNDED') DEFAULT 'PAID',
  `id_transaccion` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `pagos`
--

INSERT INTO `pagos` (`id`, `reserva_id`, `monto`, `fecha_transaccion`, `metodo_pago`, `estado_pago`, `id_transaccion`, `created_at`, `updated_at`) VALUES
(2, 3, 32.00, '2026-07-07 20:46:26', 'Tarjeta', 'PAID', 'API-1783475186906', '2026-07-07 20:46:26', '2026-07-07 20:46:26'),
(3, 4, 32.00, '2026-07-07 22:40:55', 'Tarjeta', 'PAID', 'API-1783482055579', '2026-07-07 22:40:55', '2026-07-07 22:40:55'),
(4, 5, 32.00, '2026-07-07 22:42:20', 'Tarjeta', 'PAID', 'API-1783482140817', '2026-07-07 22:42:20', '2026-07-07 22:42:20'),
(5, 6, 32.00, '2026-07-07 22:42:26', 'Tarjeta', 'PAID', 'API-1783482146724', '2026-07-07 22:42:26', '2026-07-07 22:42:26'),
(6, 7, 32.00, '2026-07-07 23:19:54', 'Efectivo', 'PENDING', 'API-CASH-1783484394044', '2026-07-07 23:19:54', '2026-07-07 23:19:54'),
(7, 8, 32.00, '2026-07-07 23:21:32', 'Efectivo', 'PENDING', 'API-CASH-1783484492067', '2026-07-07 23:21:32', '2026-07-07 23:21:32'),
(8, 9, 36.00, '2026-07-08 00:09:06', 'Tarjeta', 'PAID', 'PAY-793895', '2026-07-08 00:09:06', '2026-07-08 00:09:06'),
(9, 10, 20.00, '2026-07-08 12:24:31', 'Tarjeta', 'PAID', 'PAY-910543', '2026-07-08 12:24:31', '2026-07-08 12:24:31'),
(10, 11, 40.00, '2026-07-08 14:02:10', 'Tarjeta', 'PAID', 'PAY-717279', '2026-07-08 14:02:10', '2026-07-08 14:02:10'),
(11, 12, 16.00, '2026-07-08 15:38:48', 'Tarjeta', 'PAID', 'PAY-638093', '2026-07-08 15:38:48', '2026-07-08 15:38:48'),
(12, 13, 56.00, '2026-07-23 16:05:57', 'Tarjeta', 'PAID', 'PAY-166313', '2026-07-23 16:05:57', '2026-07-23 16:05:57');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `platos`
--

CREATE TABLE `platos` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `precio` decimal(8,2) NOT NULL CHECK (`precio` >= 0),
  `descripcion` varchar(255) DEFAULT NULL,
  `imagen_ruta` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `platos`
--

INSERT INTO `platos` (`id`, `nombre`, `precio`, `descripcion`, `imagen_ruta`, `created_at`, `updated_at`) VALUES
(2, 'Tacacho Especial', 45.00, 'Nueva descripción', '/imgs/platos/tacacho.jpg', '2026-07-07 19:22:53', '2026-07-07 19:54:35'),
(3, 'Juane', 25.00, 'Arroz sazonado con especias, pollo y huevo envuelto en hoja de bijao.', '/imgs/platos/juane.jpg', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(4, 'Inchicapi', 22.00, 'Sopa tradicional de maní, gallina, yuca y maíz.', '/imgs/platos/inchicapi.jpg', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(5, 'Patarashca', 35.00, 'Pescado de río marinado y cocinado dentro de hoja de bijao.', '/imgs/platos/patarashca.jpg', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(6, 'Cecina con Arroz Chaufa Amazónico', 32.00, 'Chaufa preparado con cecina, plátano frito y especias amazónicas.', '/imgs/platos/chaufa_cecina.jpg', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(7, 'Ensalada de Chonta', 18.00, 'Ensalada fresca de palmito amazónico con tomate, limón y culantro.', '/imgs/platos/chonta.jpg', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(8, 'Paiche a la Plancha', 48.00, 'Filete de paiche a la plancha con yuca y ensalada.', '/imgs/platos/paiche_plancha.jpg', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(9, 'Pescado Enrollado en Hoja', 37.00, 'Filete de pescado sazonado y cocinado en hoja de bijao.', '/imgs/platos/pescado_bijao.jpg', '2026-07-07 19:22:53', '2026-07-07 19:22:53');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservas`
--

CREATE TABLE `reservas` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `mesa_id` int(10) UNSIGNED DEFAULT NULL,
  `zona_id` int(10) UNSIGNED DEFAULT NULL,
  `cliente_nombre` varchar(255) DEFAULT NULL,
  `cliente_apellidos` varchar(255) DEFAULT NULL,
  `cliente_email` varchar(255) DEFAULT NULL,
  `cliente_telefono` varchar(255) DEFAULT NULL,
  `fecha_reserva` date NOT NULL,
  `hora_reserva` time NOT NULL,
  `personas` int(11) DEFAULT NULL,
  `tipo` enum('BOLETA') DEFAULT 'BOLETA',
  `numero_documento` varchar(50) DEFAULT NULL,
  `estado` enum('PENDIENTE','CANCELADA','ASISTIO') DEFAULT 'PENDIENTE',
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `reservas`
--

INSERT INTO `reservas` (`id`, `user_id`, `mesa_id`, `zona_id`, `cliente_nombre`, `cliente_apellidos`, `cliente_email`, `cliente_telefono`, `fecha_reserva`, `hora_reserva`, `personas`, `tipo`, `numero_documento`, `estado`, `created_at`, `updated_at`) VALUES
(3, NULL, 2, 1, 'Sebastian', 'Silva', 'sebas@gmail.com', '999999999', '2026-08-20', '19:00:00', 4, 'BOLETA', '12345678', 'PENDIENTE', '2026-07-07 20:46:26', NULL),
(4, NULL, 2, 1, 'Sebas', 'Silva', 'sebas@test.com', '999999999', '2026-07-20', '19:00:00', 4, 'BOLETA', '12345678', 'PENDIENTE', '2026-07-07 22:40:55', NULL),
(5, NULL, 3, 1, 'Sebas', 'Silva', 'sebas@test.com', '999999999', '2026-07-20', '19:00:00', 4, 'BOLETA', '12345678', 'PENDIENTE', '2026-07-07 22:42:20', NULL),
(6, NULL, 4, 1, 'Sebas', 'Silva', 'sebas@test.com', '999999999', '2026-07-20', '19:00:00', 4, 'BOLETA', '12345678', 'PENDIENTE', '2026-07-07 22:42:26', NULL),
(7, NULL, 2, 1, 'Sebastian', 'Silva', 'seb@test.com', '999999999', '2026-07-15', '18:00:00', 4, 'BOLETA', NULL, 'PENDIENTE', '2026-07-07 23:19:54', NULL),
(8, NULL, 3, 1, 'Sergio', 'Silva', 'seb@test.com', '999999999', '2026-07-15', '18:00:00', 4, 'BOLETA', NULL, 'PENDIENTE', '2026-07-07 23:21:32', NULL),
(9, 9, 6, 2, 'DANIELA', 'HERMOSA', 'aleinga9@gmail.com', '+51 956482862', '2026-07-18', '19:00:00', 4, 'BOLETA', '74214645', 'PENDIENTE', '2026-07-08 00:09:06', NULL),
(10, 9, 9, 3, 'DANIELA', 'HERMOSA', 'aleinga9@gmail.com', '+51 912412424', '2026-07-19', '21:00:00', 2, 'BOLETA', '74214655', 'PENDIENTE', '2026-07-08 12:24:31', NULL),
(11, 9, 3, 1, 'SEBAS', 'silva', 'sebasthiansilvalazo360@gmail.com', '+51 956482864', '2026-08-16', '17:00:00', 5, 'BOLETA', '82442124', 'PENDIENTE', '2026-07-08 14:02:10', NULL),
(12, 10, 1, 1, 'SEBAS', 'silva', 'sebasthiansilvalazo@gmail.com', '+51 943243242', '2026-08-27', '22:00:00', 2, 'BOLETA', '82442124', 'PENDIENTE', '2026-07-08 15:38:48', NULL),
(13, 11, 4, 1, 'Daniel', 'Inga Hermosa', 'DanielIngaHermosa@gmail.com', '+51 956424214', '2026-07-31', '22:00:00', 7, 'BOLETA', '61241241', 'PENDIENTE', '2026-07-23 16:05:57', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `roles`
--

CREATE TABLE `roles` (
  `id` int(10) UNSIGNED NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `roles`
--

INSERT INTO `roles` (`id`, `nombre`, `created_at`, `updated_at`) VALUES
(1, 'ADMIN', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(2, 'USER', '2026-07-07 19:22:53', '2026-07-07 19:22:53');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `apellido` varchar(255) DEFAULT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `tipo_documento` enum('DNI','RUC','BOLETA','FACTURA') DEFAULT 'DNI',
  `numero_documento` varchar(255) DEFAULT NULL,
  `razon_social` varchar(255) DEFAULT NULL,
  `direccion_fiscal` varchar(255) DEFAULT NULL,
  `email_facturacion` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `role_id` int(10) UNSIGNED NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `users`
--

INSERT INTO `users` (`id`, `nombre`, `apellido`, `email`, `password_hash`, `telefono`, `tipo_documento`, `numero_documento`, `razon_social`, `direccion_fiscal`, `email_facturacion`, `is_active`, `role_id`, `created_at`, `updated_at`) VALUES
(1, 'Fabian', 'Espinoza', 'fabian@example.com', '$2a$10$bowJZqvAOn7TdW/eD3kw0uRRnbfLHrtdxdiqa/HtHrmDNizKM.HXW', '900000001', 'DNI', NULL, NULL, NULL, NULL, 1, 1, '2026-07-07 19:22:53', '2026-07-23 15:54:59'),
(2, 'Milagros', 'Perez', 'milagros@example.com', '$2a$10$bowJZqvAOn7TdW/eD3kw0uRRnbfLHrtdxdiqa/HtHrmDNizKM.HXW', '900000002', 'DNI', NULL, NULL, NULL, NULL, 1, 1, '2026-07-07 19:22:53', '2026-07-23 15:54:59'),
(3, 'Alonso', 'Garcia', 'alonso@example.com', '$2a$10$bowJZqvAOn7TdW/eD3kw0uRRnbfLHrtdxdiqa/HtHrmDNizKM.HXW', '900000003', 'DNI', NULL, NULL, NULL, NULL, 1, 1, '2026-07-07 19:22:53', '2026-07-23 15:54:59'),
(4, 'Diego', 'Lopez', 'diego@example.com', '$2a$10$bowJZqvAOn7TdW/eD3kw0uRRnbfLHrtdxdiqa/HtHrmDNizKM.HXW', '900000004', 'DNI', NULL, NULL, NULL, NULL, 1, 1, '2026-07-07 19:22:53', '2026-07-23 15:54:59'),
(5, 'SEBASTHIAN ', 'Silva ', 'sebasthiansilvalazo360@gmail.com', '$2a$10$V0bgxFxc2l4W60KmmjPgBeePpcLcZb6ILzG04auUS7CAgV2B1WI2.', '+51 956482862', 'DNI', NULL, NULL, NULL, NULL, 1, 2, NULL, NULL),
(6, 'Sebastian', 'Silva', 'sebas@test.com', '$2a$10$F49dbvgk4RbWCxe37aEUwukiZjSbx86ovxzV8Jonwnf9B/nedx9P2', '987654321', 'DNI', '72123456', NULL, NULL, NULL, 1, 2, NULL, NULL),
(7, 'Carlos', 'Perez', 'carlos@test.com', '$2a$10$pQG2L3yMyKPEoj7HeetLLOj3wd773NXYVG6M0aBAh/tWsZa2OFRAC', '999999999', 'DNI', '12345678', NULL, NULL, NULL, 1, 2, NULL, NULL),
(8, 'Carlos', 'Perez', 'carlos@tessst.com', '$2a$10$.2rwVIox/SK5qdYYiN6/c.Gj5I5sJN0HhKQjgOIH/MqHJ/cpbi.hK', '999999999', 'DNI', '12345678', NULL, NULL, NULL, 1, 2, NULL, NULL),
(9, 'DANIELA', 'HERMOSA', 'aleinga9@gmail.com', '$2a$10$vTS6UXa.PfTt7XDl5Qd5q.//dZNMsKEWKMbAxqapZZtsiQuxjCs7m', '+51 954324324', 'DNI', NULL, NULL, NULL, NULL, 1, 2, NULL, NULL),
(10, 'Jair', 'Silva Sanchez', 'sebasthiansilvalazo@gmail.com', '$2a$10$zWRAL2jQoKVMcf8PJW1Yl.PNFLwIzNGh0vRo9shlJfeDM2e1u/j3y', '+51 932342432', 'DNI', NULL, NULL, NULL, NULL, 1, 2, NULL, NULL),
(11, 'Daniel', 'Inga HERMOSA ', 'Danielinga@gmail.com', '$2a$10$ohKzSvDkoq8GivnY8OZwjePvVtsN3QebtNjbCN30HWPgq30h/YKeu', '+51 937127312', 'DNI', NULL, NULL, NULL, NULL, 1, 2, NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `zonas`
--

CREATE TABLE `zonas` (
  `id` int(10) UNSIGNED NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `zonas`
--

INSERT INTO `zonas` (`id`, `nombre`, `descripcion`, `created_at`, `updated_at`) VALUES
(1, 'Muelle Panorámico', 'Brisa del lago y pasarela de madera.', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(2, 'Mirador Azul', 'Vista abierta a la Laguna Azul.', '2026-07-07 19:22:53', '2026-07-07 19:22:53'),
(3, 'Salón Bosque', 'Ambiente cálido y rodeado de plantas.', '2026-07-07 19:22:53', '2026-07-07 19:22:53');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `horarios_prefijados`
--
ALTER TABLE `horarios_prefijados`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `hora` (`hora`);

--
-- Indices de la tabla `mesas`
--
ALTER TABLE `mesas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `numero` (`numero`),
  ADD KEY `fk_mesas_zona` (`zona_id`);

--
-- Indices de la tabla `pagos`
--
ALTER TABLE `pagos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `reserva_id` (`reserva_id`);

--
-- Indices de la tabla `platos`
--
ALTER TABLE `platos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_platos_nombre` (`nombre`);

--
-- Indices de la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_reservas_user` (`user_id`),
  ADD KEY `fk_reservas_mesa` (`mesa_id`),
  ADD KEY `fk_reservas_zona` (`zona_id`),
  ADD KEY `idx_reservas_fecha` (`fecha_reserva`,`hora_reserva`);

--
-- Indices de la tabla `roles`
--
ALTER TABLE `roles`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `fk_users_roles` (`role_id`);

--
-- Indices de la tabla `zonas`
--
ALTER TABLE `zonas`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `horarios_prefijados`
--
ALTER TABLE `horarios_prefijados`
  MODIFY `id` smallint(5) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `mesas`
--
ALTER TABLE `mesas`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `pagos`
--
ALTER TABLE `pagos`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT de la tabla `platos`
--
ALTER TABLE `platos`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `reservas`
--
ALTER TABLE `reservas`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `zonas`
--
ALTER TABLE `zonas`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `mesas`
--
ALTER TABLE `mesas`
  ADD CONSTRAINT `fk_mesas_zona` FOREIGN KEY (`zona_id`) REFERENCES `zonas` (`id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `pagos`
--
ALTER TABLE `pagos`
  ADD CONSTRAINT `fk_pagos_reserva` FOREIGN KEY (`reserva_id`) REFERENCES `reservas` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD CONSTRAINT `fk_reservas_mesa` FOREIGN KEY (`mesa_id`) REFERENCES `mesas` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_reservas_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_reservas_zona` FOREIGN KEY (`zona_id`) REFERENCES `zonas` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Filtros para la tabla `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `fk_users_roles` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
