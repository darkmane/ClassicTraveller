CREATE TABLE `orbits` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `star_system_id` int NOT NULL,
  `stellar_orbit` int NOT NULL,
  `planetary_orbit` int NOT NULL,
  `type_id` int NOT NULL,
  `body_id` bigint NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `orbits` ADD UNIQUE `unique_orbits` (`star_system_id`, `stellar_orbit`, `planetary_orbit`);

CREATE TABLE `stars` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `star_system_id` VARCHAR(15),
  `class` CHAR(1) NOT NULL,
  `size` VARCHAR(3),
  `orbit_id` bigint,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `planets`
ADD COLUMN `orbit_id` bigint,
DROP COLUMN `orbit`,
DROP COLUMN `satellite_orbit`;

