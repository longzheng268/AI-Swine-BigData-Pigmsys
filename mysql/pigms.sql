/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80024
 Source Host           : localhost:3306
 Source Schema         : pigms

 Target Server Type    : MySQL
 Target Server Version : 80024
 File Encoding         : 65001

 Date: 23/05/2022 22:34:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_login
-- ----------------------------
DROP TABLE IF EXISTS `tb_login`;
CREATE TABLE `tb_login`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `userpassword` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户密码',
  `usermobile` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `state` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '账号状态',
  `more` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '其他信息',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_login
-- ----------------------------
INSERT INTO `tb_login` VALUES (1, 'admin', '123456789', '13612344563', '启用', '这是管理员账号');
INSERT INTO `tb_login` VALUES (2, 'root', '987654321', '13788889999', '启用', '管理员2');

-- ----------------------------
-- Table structure for tb_pig
-- ----------------------------
DROP TABLE IF EXISTS `tb_pig`;
CREATE TABLE `tb_pig`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `pigISBN` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '猪的编号',
  `pigName` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '猪的名称',
  `pigAge` int NULL DEFAULT NULL COMMENT '年龄',
  `pinSex` int NULL DEFAULT NULL COMMENT '性别',
  `manufactureDate` datetime NULL DEFAULT NULL COMMENT '出生日期',
  `pigAddress` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '生产地',
  `pigImage` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图片链接',
  `pigType` int NULL DEFAULT NULL COMMENT '猪的种类',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_pig
-- ----------------------------
INSERT INTO `tb_pig` VALUES (1, '000xxx123', '0号猪', 4, 1, '2022-06-01 00:00:00', '黑龙江省哈尔滨市', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F8435e5dde71190ef76c6c9a4cd498a16fdfaaf51ca34&refer=http%3A%2F%2Fbkimg.cdn.bcebos.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655175723&t=713a13979a53119f70fd54314a7009ee', 1);
INSERT INTO `tb_pig` VALUES (2, '1000xxx123', '1号猪', 3, 0, '2022-05-13 16:09:33', '江西省景德镇市乐平市', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F8435e5dde71190ef76c6c9a4cd498a16fdfaaf51ca34&refer=http%3A%2F%2Fbkimg.cdn.bcebos.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655175723&t=713a13979a53119f70fd54314a7009ee', 1);
INSERT INTO `tb_pig` VALUES (3, '2000xxx123', '2号猪', 2, 1, '2022-05-12 16:09:33', '江西省南昌市南昌县', 'https://img0.baidu.com/it/u=2621247161,371898787&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=349', 2);
INSERT INTO `tb_pig` VALUES (4, '3000xxx123', '3号猪', 3, 1, '2022-05-11 16:09:33', '江西省九江市武宁县', 'https://img2.baidu.com/it/u=1512281726,144753391&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=294', 3);
INSERT INTO `tb_pig` VALUES (5, '4000xxx123', '4号猪', 4, 0, '2022-05-10 16:09:33', '江西省上饶市万年县', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F8435e5dde71190ef76c6c9a4cd498a16fdfaaf51ca34&refer=http%3A%2F%2Fbkimg.cdn.bcebos.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655175723&t=713a13979a53119f70fd54314a7009ee', 1);
INSERT INTO `tb_pig` VALUES (6, '5000xxx123', '5号猪', 5, 1, '2022-05-15 16:09:33', '江西省上饶市婺源县', 'https://img0.baidu.com/it/u=2621247161,371898787&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=349', 2);
INSERT INTO `tb_pig` VALUES (7, '6000xxx123', '6号猪', 6, 0, '2022-05-19 16:09:33', '江西省吉安市吉水县', 'https://img2.baidu.com/it/u=1512281726,144753391&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=294', 3);
INSERT INTO `tb_pig` VALUES (8, '7000xxx123', '7号猪', 7, 1, '2022-05-18 16:09:33', '江西省宜昌市丰城县', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F8435e5dde71190ef76c6c9a4cd498a16fdfaaf51ca34&refer=http%3A%2F%2Fbkimg.cdn.bcebos.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655175723&t=713a13979a53119f70fd54314a7009ee', 1);
INSERT INTO `tb_pig` VALUES (9, '8000xxx123', '8号猪', 7, 1, '2022-05-18 16:09:33', '湖南省长沙市望城', 'https://img0.baidu.com/it/u=2621247161,371898787&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=349', 2);
INSERT INTO `tb_pig` VALUES (10, '9000xxx123', '9号猪', 9, 0, '2022-05-18 16:09:33', '湖南省湘潭市湘潭', 'https://img2.baidu.com/it/u=1512281726,144753391&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=294', 3);
INSERT INTO `tb_pig` VALUES (11, '1100xxx123', '10号猪', 10, 1, '2022-05-18 16:09:33', '湖南省长沙市浏阳市', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F8435e5dde71190ef76c6c9a4cd498a16fdfaaf51ca34&refer=http%3A%2F%2Fbkimg.cdn.bcebos.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655175723&t=713a13979a53119f70fd54314a7009ee', 1);
INSERT INTO `tb_pig` VALUES (12, '1200xxx123', '11号猪', 5, 0, '2022-05-18 16:09:33', '湖南省岳阳市岳阳县', 'https://img0.baidu.com/it/u=2621247161,371898787&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=349', 2);
INSERT INTO `tb_pig` VALUES (14, '1300xxx321', '12号猪', 11, 1, '2022-06-02 00:00:00', '北京市朝阳区', 'https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fbkimg.cdn.bcebos.com%2Fpic%2F8435e5dde71190ef76c6c9a4cd498a16fdfaaf51ca34&refer=http%3A%2F%2Fbkimg.cdn.bcebos.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1655175723&t=713a13979a53119f70fd54314a7009ee', 1);

-- ----------------------------
-- Table structure for tb_pigtype
-- ----------------------------
DROP TABLE IF EXISTS `tb_pigtype`;
CREATE TABLE `tb_pigtype`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `pig_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '猪的种类',
  `pig_type_sum` int NULL DEFAULT NULL COMMENT '猪的种类的数量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_pigtype
-- ----------------------------
INSERT INTO `tb_pigtype` VALUES (1, '白猪', 500);
INSERT INTO `tb_pigtype` VALUES (2, '黑猪', 600);
INSERT INTO `tb_pigtype` VALUES (3, '花猪', 300);

-- ----------------------------
-- Table structure for tb_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_user`;
CREATE TABLE `tb_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `userISBN` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户信息号',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户姓名',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `sex` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '性别',
  `birthday` datetime NULL DEFAULT NULL COMMENT '出生日期',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '家庭住址',
  `phone` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '电话号码',
  `userRole` int NULL DEFAULT NULL COMMENT '角色编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_user
-- ----------------------------
INSERT INTO `tb_user` VALUES (1, '112243', '李太白', 45, '男', '2022-05-20 00:00:00', '斗魂大陆神魂村一号', '17899996666', 2);
INSERT INTO `tb_user` VALUES (2, '112234', '杜甫', 30, '男', '2022-06-14 16:51:01', '江西省南昌市南昌县', '13688889999', 2);
INSERT INTO `tb_user` VALUES (3, '112231', '杜牧', 78, '男', '2022-05-20 00:00:00', '北京市朝阳区', '13566668888', 1);
INSERT INTO `tb_user` VALUES (4, '112235', 'JACK', 31, '男', '2022-06-15 16:51:01', '江西省南昌市南昌县', '13688889999', 1);
INSERT INTO `tb_user` VALUES (5, '112236', 'TOM', 20, '女', '2022-06-16 16:51:01', '江西省南昌市南昌县', '13688889999', 1);
INSERT INTO `tb_user` VALUES (6, '112237', '天天', 32, '女', '2022-06-18 16:51:01', '江西省南昌市南昌县', '13688889999', 1);
INSERT INTO `tb_user` VALUES (7, '112238', '纪检', 13, '女', '2022-06-19 16:51:01', '江西省南昌市南昌县', '13688889999', 1);
INSERT INTO `tb_user` VALUES (8, '112239', '南北', 33, '男', '2022-06-24 16:51:01', '江西省南昌市南昌县', '13688889999', 1);
INSERT INTO `tb_user` VALUES (9, '112240', '葡萄', 43, '女', '2022-06-14 16:51:01', '江西省南昌市南昌县', '13688889999', 1);
INSERT INTO `tb_user` VALUES (10, '112241', '码农', 53, '女', '2022-06-14 16:51:01', '江西省南昌市南昌县', '13688889999', 1);
INSERT INTO `tb_user` VALUES (11, '112242', '光辉', 63, '男', '2022-06-24 16:51:01', '江西省南昌市南昌县', '13688889999', 2);
INSERT INTO `tb_user` VALUES (12, '112243', '大师兄', 30, '男', '2022-06-10 16:51:01', '江西省南昌市南昌县', '13688889999', 2);
INSERT INTO `tb_user` VALUES (14, '000000', '黄金甲', 89, '男', '2022-05-09 00:00:00', '江西省上饶市', '13133336666', 1);

SET FOREIGN_KEY_CHECKS = 1;
