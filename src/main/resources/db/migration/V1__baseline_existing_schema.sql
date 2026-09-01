CREATE TABLE `alignments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `census_id` VARCHAR(255) DEFAULT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `allies` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `census_id` VARCHAR(255) DEFAULT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `artifacts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `census_id` VARCHAR(255) DEFAULT NULL,
  `discord_emoji_id` VARCHAR(255) DEFAULT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `genders` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `census_id` VARCHAR(255) DEFAULT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `guild_alignments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `census_id` VARCHAR(255) DEFAULT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `movement_modes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `census_id` VARCHAR(255) DEFAULT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `personalities` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `census_id` VARCHAR(255) DEFAULT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `power_types` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `census_id` VARCHAR(255) DEFAULT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;

CREATE TABLE `leagues` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `average_combat_rating` DOUBLE NOT NULL,
  `average_pvp_combat_rating` DOUBLE NOT NULL,
  `average_skill_points` DOUBLE NOT NULL,
  `census_id` VARCHAR(255) DEFAULT NULL,
  `member_count` INT NOT NULL,
  `name` VARCHAR(255) DEFAULT NULL,
  `world_id` VARCHAR(255) DEFAULT NULL,
  `alignment_id` BIGINT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_leagues_alignment` (`alignment_id`),
  CONSTRAINT `fk_leagues_alignment` FOREIGN KEY (`alignment_id`) REFERENCES `guild_alignments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_uca1400_ai_ci;
