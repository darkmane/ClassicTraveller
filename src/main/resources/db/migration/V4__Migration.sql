ALTER TABLE `regions`
ADD COLUMN `horizontal_size` int,
ADD COLUMN `vertical_size` int;

ALTER TABLE `orbits`
MODIFY COLUMN `planetary_orbit` bigint NULL,
MODIFY COLUMN `id` bigint;

GRANT ALL ON traveller.* TO 'traveller'@'%';
