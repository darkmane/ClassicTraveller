CREATE TABLE `regions` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `coord_x` int NOT NULL,
  `coord_y` int NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `coord_x` (`coord_x`,`coord_y`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;