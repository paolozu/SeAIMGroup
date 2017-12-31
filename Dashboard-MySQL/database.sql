DROP DATABASE IF EXISTS `ir_viewer`;
CREATE DATABASE `ir_viewer`;

USE `ir_viewer`;


/* CLUSTER */

CREATE TABLE `cluster`(
  `cluster_id` INT PRIMARY KEY,
  `area_id` INT NOT NULL,
  `cluster_ir` DOUBLE(5,2) NOT NULL DEFAULT '0'
);

/* ROBOT */

CREATE TABLE `robot`(
  `robot_id` INT PRIMARY KEY,
  `cluster_id` INT NOT NULL,
  `robot_ir` DOUBLE(5,2) NOT NULL DEFAULT '0',
  FOREIGN KEY (cluster_id) REFERENCES cluster(`cluster_id`) ON DELETE CASCADE
);
