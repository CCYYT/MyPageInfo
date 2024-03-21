/*
 Navicat Premium Data Transfer

 Source Server         : Test
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3306
 Source Schema         : test_2

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 12/03/2024 15:40:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for device_data
-- ----------------------------
DROP TABLE IF EXISTS `device_data`;
CREATE TABLE `device_data`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `data` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `create_time` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of device_data
-- ----------------------------
INSERT INTO `device_data` VALUES (1, 'test', '2024-02-04 21:23:38');
INSERT INTO `device_data` VALUES (2, 'test2', '2024-02-13 21:24:13');
INSERT INTO `device_data` VALUES (3, 'test3', '2024-02-04 21:24:17');
INSERT INTO `device_data` VALUES (4, 'test4', '2024-02-04 21:24:19');
INSERT INTO `device_data` VALUES (6, 'test4', '2024-02-04 21:24:21');
INSERT INTO `device_data` VALUES (7, '123', '2021-10-03 08:00:00');
INSERT INTO `device_data` VALUES (8, '123', '2021-10-03 08:00:00');
INSERT INTO `device_data` VALUES (9, 'test9', '2024-02-23 16:22:19');
INSERT INTO `device_data` VALUES (10, 'test9', '2024-03-09 15:41:52');

SET FOREIGN_KEY_CHECKS = 1;
