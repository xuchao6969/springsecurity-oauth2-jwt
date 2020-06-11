/*
 Navicat MySQL Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50726
 Source Host           : localhost:3306
 Source Schema         : jwt_demo

 Target Server Type    : MySQL
 Target Server Version : 50726
 File Encoding         : 65001

 Date: 11/06/2020 15:56:57
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `username` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码',
  `user_permission` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户权限',
  `user_role` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户角色',
  `token` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'token',
  `refresh_token` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '刷新token',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of t_user
-- ----------------------------
INSERT INTO `t_user` VALUES ('aa', 'admin', '123', '增删改查', 'sys_admin', '', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiU09KX0RFTU8iXSwidXNlcl9uYW1lIjoiYWRtaW4iLCJzY29wZSI6WyJhbGwiXSwiYXRpIjoiZjM1ZThmMTYtYmM0NS00ZjZiLThhYzktZTg0NjYzMmQ3NGExIiwiZXhwIjoxNTkxODIzMjU5LCJhdXRob3JpdGllcyI6WyJzeXNfYWRtaW4iXSwianRpIjoiZDE0YjU3MzktZjc2My00YzFjLWExZDItNmQ3ZDMyMTYxYjdjIiwidXNlcmluZm8iOiJ7XCJhdXRoT3JpdHlcIjpbe1wiYXV0aG9yaXR5XCI6XCJzeXNfYWRtaW5cIn1dLFwidXNlcklkXCI6XCIwMDFcIixcInVzZXJuYW1lXCI6XCJhZG1pblwifSIsImNsaWVudF9pZCI6IlNPSl9ERU1PIn0.RGw-o94SwMRe7Qh3R0PDp3cTKkPxA1N1UU_S5aWY8BU');
INSERT INTO `t_user` VALUES ('bb', 'xc', '123', '增查', 'admin', '', 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiU09KX0RFTU8iXSwidXNlcl9uYW1lIjoieGMiLCJzY29wZSI6WyJhbGwiXSwiZXhwIjoxNTkxODYwMzkyLCJhdXRob3JpdGllcyI6WyJhZG1pbiJdLCJqdGkiOiI0NjBjMDgzNi01YjMwLTRhOWQtYTllZS00MWEwNmQxYWU4YjMiLCJ1c2VyaW5mbyI6IntcImF1dGhPcml0eVwiOlt7XCJhdXRob3JpdHlcIjpcImFkbWluXCJ9XSxcInVzZXJJZFwiOlwiMDAxXCIsXCJ1c2VybmFtZVwiOlwieGNcIn0iLCJjbGllbnRfaWQiOiJTT0pfREVNTyJ9.J5ypTNE0R9Sqj56ePr8ElAhLqgO03bWnoNompWuqxbU');

SET FOREIGN_KEY_CHECKS = 1;
