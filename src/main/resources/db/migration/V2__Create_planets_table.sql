CREATE TABLE `trade_zones` (
  `id` tinyint unsigned NOT NULL AUTO_INCREMENT,
  `zone` VARCHAR(15),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT `trade_zones` (zone) VALUES ('Green'), ('Yellow'), ('Red');

CREATE TABLE `star_systems` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `coord_x` bigint NOT NULL,
  `coord_y` bigint NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  `sector` bigint unsigned,
  `subsector` bigint unsigned,
  `trade_zone_id` tinyint unsigned,
  `scout_base` BOOLEAN,
  `naval_base` BOOLEAN,
  PRIMARY KEY (`id`),
  KEY `coord_x` (`coord_x`,`coord_y`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `planets` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `star_system_id` int(11) unsigned NOT NULL,
  `name` varchar(128) NOT NULL DEFAULT '',
  `orbit` tinyint unsigned,
  `satellite_orbit` tinyint unsigned,
  `diameter` tinyint unsigned,
  `atmosphere` tinyint unsigned,
  `hydro` tinyint unsigned,
  `population` tinyint unsigned,
  `government` tinyint,
  `law_level` tinyint,
  `tech_level` tinyint,
  `star_port` char(1),
  `scout_base` BOOLEAN,
  `naval_base` BOOLEAN,
  `main` BOOLEAN,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `xboat_routes` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `start_system` int(11) unsigned NOT NULL,
  `end_system` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;