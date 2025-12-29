/*
 扩展脚本 - 养猪信息管理系统
 
 新增功能：
 1. 角色权限管理
 2. 环境监测数据
 3. 操作日志
 4. 模型管理
 5. 数据上传记录
 
 使用方法：
 在已有 pigms 数据库基础上执行此脚本
 mysql -u root -p pigms < pigms_extension.sql
*/

USE pigms;

-- ----------------------------
-- Table structure for tb_role (角色表)
-- ----------------------------
DROP TABLE IF EXISTS `tb_role`;
CREATE TABLE `tb_role`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色编码(ADMIN/USER/RESEARCHER)',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_role
-- ----------------------------
INSERT INTO `tb_role` VALUES (1, '管理员', 'ADMIN', '系统管理员，拥有所有权限', '2024-01-01 00:00:00');
INSERT INTO `tb_role` VALUES (2, '普通用户', 'USER', '普通用户，可查看和管理数据', '2024-01-01 00:00:00');
INSERT INTO `tb_role` VALUES (3, '科研人员', 'RESEARCHER', '科研人员，可使用分析和预测功能', '2024-01-01 00:00:00');

-- ----------------------------
-- 修改 tb_login 表，添加角色字段
-- ----------------------------
ALTER TABLE `tb_login` ADD COLUMN `role_id` int NULL DEFAULT 1 COMMENT '角色ID' AFTER `state`;
UPDATE `tb_login` SET `role_id` = 1 WHERE `username` = 'admin';
UPDATE `tb_login` SET `role_id` = 1 WHERE `username` = 'root';

-- ----------------------------
-- Table structure for tb_environment_data (环境监测数据表)
-- ----------------------------
DROP TABLE IF EXISTS `tb_environment_data`;
CREATE TABLE `tb_environment_data`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `monitor_point` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '监测点名称',
  `monitor_location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '监测点位置',
  `latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '纬度',
  `longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '经度',
  `temperature` decimal(5, 2) NULL DEFAULT NULL COMMENT '温度(℃)',
  `humidity` decimal(5, 2) NULL DEFAULT NULL COMMENT '湿度(%)',
  `co2_concentration` decimal(8, 2) NULL DEFAULT NULL COMMENT 'CO₂浓度(ppm)',
  `nh3_concentration` decimal(8, 2) NULL DEFAULT NULL COMMENT '氨气浓度(ppm)',
  `light_intensity` decimal(8, 2) NULL DEFAULT NULL COMMENT '光照强度(lux)',
  `collect_time` datetime NULL DEFAULT NULL COMMENT '采集时间',
  `is_abnormal` tinyint NULL DEFAULT 0 COMMENT '是否异常(0正常 1异常)',
  `abnormal_reason` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '异常原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_monitor_point`(`monitor_point`) USING BTREE,
  INDEX `idx_collect_time`(`collect_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '环境监测数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_environment_data (测试数据)
-- ----------------------------
INSERT INTO `tb_environment_data` VALUES (1, '1号猪舍', '江西省南昌市南昌县', 28.682892, 115.857537, 25.5, 65.0, 450.0, 15.0, 300.0, '2024-10-26 08:00:00', 0, NULL, '2024-10-26 08:00:00');
INSERT INTO `tb_environment_data` VALUES (2, '2号猪舍', '江西省南昌市南昌县', 28.683892, 115.858537, 26.0, 68.0, 480.0, 18.0, 280.0, '2024-10-26 08:00:00', 0, NULL, '2024-10-26 08:00:00');
INSERT INTO `tb_environment_data` VALUES (3, '3号猪舍', '江西省九江市武宁县', 29.256371, 115.098982, 24.8, 70.0, 500.0, 20.0, 320.0, '2024-10-26 08:00:00', 0, NULL, '2024-10-26 08:00:00');
INSERT INTO `tb_environment_data` VALUES (4, '1号猪舍', '江西省南昌市南昌县', 28.682892, 115.857537, 32.5, 75.0, 800.0, 35.0, 250.0, '2024-10-26 09:00:00', 1, '温度过高，氨气浓度超标', '2024-10-26 09:00:00');
INSERT INTO `tb_environment_data` VALUES (5, '4号猪舍', '江西省上饶市万年县', 28.694622, 117.067344, 25.0, 66.0, 460.0, 16.0, 310.0, '2024-10-26 08:00:00', 0, NULL, '2024-10-26 08:00:00');

-- ----------------------------
-- Table structure for tb_environment_standard (环境标准表)
-- ----------------------------
DROP TABLE IF EXISTS `tb_environment_standard`;
CREATE TABLE `tb_environment_standard`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `parameter_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数名称',
  `min_value` decimal(10, 2) NULL DEFAULT NULL COMMENT '最小值',
  `max_value` decimal(10, 2) NULL DEFAULT NULL COMMENT '最大值',
  `unit` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单位',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '说明',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '环境标准表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_environment_standard
-- ----------------------------
INSERT INTO `tb_environment_standard` VALUES (1, '温度', 18.0, 28.0, '℃', '适宜温度范围');
INSERT INTO `tb_environment_standard` VALUES (2, '湿度', 50.0, 70.0, '%', '适宜湿度范围');
INSERT INTO `tb_environment_standard` VALUES (3, 'CO₂浓度', 0.0, 600.0, 'ppm', '二氧化碳浓度标准');
INSERT INTO `tb_environment_standard` VALUES (4, '氨气浓度', 0.0, 25.0, 'ppm', '氨气浓度标准');
INSERT INTO `tb_environment_standard` VALUES (5, '光照强度', 200.0, 500.0, 'lux', '光照强度标准');

-- ----------------------------
-- Table structure for tb_operation_log (操作日志表)
-- ----------------------------
DROP TABLE IF EXISTS `tb_operation_log`;
CREATE TABLE `tb_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `operation` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作内容',
  `method` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '请求方法',
  `params` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '请求参数',
  `ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `execute_time` bigint NULL DEFAULT NULL COMMENT '执行时长(ms)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_model (预测模型表)
-- ----------------------------
DROP TABLE IF EXISTS `tb_model`;
CREATE TABLE `tb_model`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `model_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型名称',
  `model_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型类型(GROWTH/ENVIRONMENT/DISEASE)',
  `model_path` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型文件路径',
  `accuracy` decimal(5, 4) NULL DEFAULT NULL COMMENT '模型精度',
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '模型描述',
  `input_features` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '输入特征',
  `output_features` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '输出特征',
  `creator_id` int NULL DEFAULT NULL COMMENT '创建者ID',
  `is_public` tinyint NULL DEFAULT 0 COMMENT '是否公开(0私有 1公开)',
  `status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'ACTIVE' COMMENT '状态(ACTIVE/INACTIVE)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_model_type`(`model_type`) USING BTREE,
  INDEX `idx_creator_id`(`creator_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '预测模型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tb_model (预置模型)
-- ----------------------------
INSERT INTO `tb_model` VALUES (1, '生猪体重预测模型', 'GROWTH', '/models/pig_weight_prediction.pkl', 0.9250, '基于BP神经网络的生猪体重预测模型', '日龄,饲料摄入量,品种,性别', '预测体重', 1, 1, 'ACTIVE', '2024-01-01 00:00:00', '2024-01-01 00:00:00');
INSERT INTO `tb_model` VALUES (2, '环境质量评价模型', 'ENVIRONMENT', '/models/environment_evaluation.pkl', 0.8800, '基于国标GB/T 19295-2020的环境质量评价模型', '温度,湿度,CO2浓度,氨气浓度,光照强度', '环境等级(I-IV)', 1, 1, 'ACTIVE', '2024-01-01 00:00:00', '2024-01-01 00:00:00');
INSERT INTO `tb_model` VALUES (3, '疾病风险预测模型', 'DISEASE', '/models/disease_prediction.pkl', 0.8650, '根据环境和养殖数据预测疾病风险', '温度,湿度,密度,日龄,疫苗接种情况', '疾病风险等级', 1, 1, 'ACTIVE', '2024-01-01 00:00:00', '2024-01-01 00:00:00');

-- ----------------------------
-- Table structure for tb_upload_record (数据上传记录表)
-- ----------------------------
DROP TABLE IF EXISTS `tb_upload_record`;
CREATE TABLE `tb_upload_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件名',
  `original_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原始文件名',
  `file_path` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件路径',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小(bytes)',
  `file_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `data_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据类型(PIG/ENVIRONMENT/USER)',
  `upload_user_id` int NULL DEFAULT NULL COMMENT '上传用户ID',
  `upload_username` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上传用户名',
  `status` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'PROCESSING' COMMENT '处理状态(PROCESSING/SUCCESS/FAILED)',
  `success_count` int NULL DEFAULT 0 COMMENT '成功导入数量',
  `failed_count` int NULL DEFAULT 0 COMMENT '失败数量',
  `error_message` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '错误信息',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_upload_user`(`upload_user_id`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数据上传记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_prediction_record (预测记录表)
-- ----------------------------
DROP TABLE IF EXISTS `tb_prediction_record`;
CREATE TABLE `tb_prediction_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model_id` int NULL DEFAULT NULL COMMENT '模型ID',
  `model_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模型名称',
  `input_data` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '输入数据(JSON)',
  `output_data` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '输出结果(JSON)',
  `accuracy` decimal(5, 4) NULL DEFAULT NULL COMMENT '预测精度',
  `execute_time` bigint NULL DEFAULT NULL COMMENT '执行时长(ms)',
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_model_id`(`model_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '预测记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- 完成提示
-- ----------------------------
SELECT '数据库扩展完成！新增了以下表：' AS message;
SELECT 'tb_role - 角色表' AS tables UNION ALL
SELECT 'tb_environment_data - 环境监测数据表' UNION ALL
SELECT 'tb_environment_standard - 环境标准表' UNION ALL
SELECT 'tb_operation_log - 操作日志表' UNION ALL
SELECT 'tb_model - 预测模型表' UNION ALL
SELECT 'tb_upload_record - 数据上传记录表' UNION ALL
SELECT 'tb_prediction_record - 预测记录表';



