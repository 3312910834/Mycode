/*
 Navicat Premium Data Transfer

 Source Server         : linshiguang
 Source Server Type    : MySQL
 Source Server Version : 50724
 Source Host           : localhost:3306
 Source Schema         : campus_lost_found

 Target Server Type    : MySQL
 Target Server Version : 50724
 File Encoding         : 65001

 Date: 23/03/2026 22:46:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `admin_id` int(11) NOT NULL COMMENT '发布管理员',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '内容',
  `is_top` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态 1显示 0隐藏',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_announcement_user`(`admin_id`) USING BTREE,
  INDEX `idx_is_top`(`is_top`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  CONSTRAINT `fk_announcement_user` FOREIGN KEY (`admin_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (1, 1, '欢迎使用校园失物招领系统', '本系统旨在帮助同学们找回失物，请大家诚信使用。', 1, 1, '2026-01-05 14:27:01');
INSERT INTO `announcement` VALUES (2, 1, '物品发布规范', '发布物品时请提供详细信息和清晰图片。', 0, 1, '2026-01-05 14:27:01');
INSERT INTO `announcement` VALUES (3, 1, '联系我们', '如有问题请联系管理员：admin@campus.com', 0, 1, '2026-01-05 14:27:01');
INSERT INTO `announcement` VALUES (4, 1, '发布', '有问题联系管理员', 0, 1, '2026-01-08 15:54:18');
INSERT INTO `announcement` VALUES (5, 1, '失物功能', '有问题联系我', 0, 1, '2026-01-08 15:54:51');
INSERT INTO `announcement` VALUES (6, 1, '招领功能', '有问题联系我', 0, 1, '2026-01-08 15:55:08');
INSERT INTO `announcement` VALUES (7, 1, '个人信息部分', '有问题联系我', 0, 1, '2026-01-08 15:55:33');
INSERT INTO `announcement` VALUES (8, 1, '我的部分', '有问题联系我', 0, 1, '2026-01-08 15:55:47');
INSERT INTO `announcement` VALUES (9, 1, '建议部分', '有问题联系我管理员', 0, 1, '2026-01-08 15:56:11');
INSERT INTO `announcement` VALUES (10, 1, '搜索还有问题', '有问题联系管理员', 0, 1, '2026-01-08 15:56:41');
INSERT INTO `announcement` VALUES (11, 1, '有其他问题', '有问题联系管理员', 0, 1, '2026-01-08 15:57:00');

-- ----------------------------
-- Table structure for contact_message
-- ----------------------------
DROP TABLE IF EXISTS `contact_message`;
CREATE TABLE `contact_message`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `from_user_id` int(11) NOT NULL COMMENT '发送者',
  `to_user_id` int(11) NOT NULL COMMENT '接收者',
  `item_id` int(11) NOT NULL COMMENT '相关物品ID',
  `item_type` enum('lost','found') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '物品类型',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '消息内容',
  `status` enum('unread','read') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'unread' COMMENT '状态',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_from_user`(`from_user_id`) USING BTREE,
  INDEX `fk_to_user`(`to_user_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  CONSTRAINT `fk_from_user` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_to_user` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '联系消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of contact_message
-- ----------------------------
INSERT INTO `contact_message` VALUES (2, 15, 9, 42, 'lost', '您发布的失物招领中的物品可能是我丢失的', 'read', '2026-01-08 16:20:51');
INSERT INTO `contact_message` VALUES (3, 7, 9, 156, 'found', '请问这个物品的具体特征是什么？', 'read', '2026-01-08 16:20:51');
INSERT INTO `contact_message` VALUES (19, 1, 9, 101, 'lost', '您好，我看到您发布的丢失物品信息，想了解一下具体情况。', 'unread', '2024-01-15 09:30:00');
INSERT INTO `contact_message` VALUES (20, 2, 9, 102, 'lost', '我在食堂附近捡到一个类似您描述的物品，请和我联系确认。', 'unread', '2024-01-15 10:15:00');
INSERT INTO `contact_message` VALUES (21, 3, 9, 103, 'lost', '请问您的物品具体在什么时间丢失的？我可能有线索。', 'read', '2024-01-14 14:20:00');
INSERT INTO `contact_message` VALUES (22, 4, 9, 104, 'lost', '我在图书馆看到疑似您的物品，请尽快与我联系。', 'unread', '2024-01-15 11:45:00');
INSERT INTO `contact_message` VALUES (23, 5, 9, 105, 'lost', '您好，我找到了一个物品，和您描述的非常相似，请确认。', 'read', '2024-01-14 16:30:00');
INSERT INTO `contact_message` VALUES (24, 6, 9, 201, 'found', '请问这个物品是在哪里拾获的？我可能知道失主。', 'unread', '2024-01-15 13:10:00');
INSERT INTO `contact_message` VALUES (25, 7, 9, 202, 'found', '这个物品看起来很像我之前丢失的，可以详细描述一下吗？', 'unread', '2024-01-15 14:25:00');
INSERT INTO `contact_message` VALUES (26, 8, 9, 203, 'found', '您好，这个物品可能是我们实验室同学的，请保持联系。', 'read', '2024-01-14 18:40:00');
INSERT INTO `contact_message` VALUES (27, 10, 9, 204, 'found', '我朋友昨天丢失了类似的物品，能拍个照片确认一下吗？', 'unread', '2024-01-15 15:50:00');
INSERT INTO `contact_message` VALUES (28, 11, 9, 205, 'found', '非常感谢您拾获物品，请问什么时候方便认领？', 'read', '2024-01-14 20:15:00');
INSERT INTO `contact_message` VALUES (29, 12, 9, 106, 'lost', '我昨天在操场看到有人拿着类似的物品，需要调监控吗？', 'unread', '2024-01-15 16:30:00');
INSERT INTO `contact_message` VALUES (30, 13, 9, 206, 'found', '物品还在吗？我想现在过去认领。', 'read', '2024-01-14 21:45:00');
INSERT INTO `contact_message` VALUES (31, 14, 9, 107, 'lost', '请问物品有什么明显的特征吗？我可能有更多线索。', 'unread', '2024-01-15 17:20:00');
INSERT INTO `contact_message` VALUES (32, 15, 9, 207, 'found', '您好，我是失主的朋友，能先帮我预留一下吗？', 'read', '2024-01-15 18:10:00');
INSERT INTO `contact_message` VALUES (33, 16, 9, 108, 'lost', '我已经联系了保卫处，他们有相关监控记录，请保持手机畅通。', 'read', '2024-01-14 22:30:00');

-- ----------------------------
-- Table structure for found_item
-- ----------------------------
DROP TABLE IF EXISTS `found_item`;
CREATE TABLE `found_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '物品ID',
  `user_id` int(11) NOT NULL COMMENT '发布者ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '物品名称',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类',
  `found_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '拾获地点',
  `found_time` datetime NOT NULL COMMENT '拾获时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详细描述',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '图片地址',
  `storage_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '保管地点',
  `contact_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` enum('pending','published','claimed','closed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pending' COMMENT '状态',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_found_user`(`user_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  CONSTRAINT `fk_found_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '招领表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of found_item
-- ----------------------------
INSERT INTO `found_item` VALUES (1, 8, '拾获校园卡', '证件类', '教学楼B栋门口', '2026-01-05 20:49:16', '捡到一张校园卡，姓名：张三', NULL, '失物招领处', '电话：13888888888', 'published', 0, '2026-01-05 20:49:16', '2026-01-05 20:49:16');
INSERT INTO `found_item` VALUES (2, 3, '捡到钥匙串', '生活用品', '食堂二楼', '2026-01-04 20:49:16', '一串钥匙，有3把钥匙和一个U盘', NULL, '保卫处', '微信：zhangsan', 'published', 0, '2026-01-05 20:49:16', '2026-01-05 20:49:16');
INSERT INTO `found_item` VALUES (3, 4, '发现手表', '电子产品', '图书馆一楼', '2026-01-03 20:49:16', '黑色电子手表，卡西欧品牌', NULL, '图书馆前台', 'QQ：654321', 'claimed', 0, '2026-01-05 20:49:16', '2026-01-05 20:49:16');
INSERT INTO `found_item` VALUES (4, 5, '拾获课本', '学习用品', '体育馆', '2026-01-02 20:49:16', '高数课本，封面有姓名：王五', NULL, '教学楼办公室', '邮箱：wangwu@campus.com', 'published', 0, '2026-01-05 20:49:16', '2026-01-05 20:49:16');
INSERT INTO `found_item` VALUES (5, 1, '拾获学生校园卡一张', '证件类', '第一教学楼201教室', '2024-01-07 10:30:00', '蓝色卡套，内有学生证照片，姓名为张三，学号202301001', '/images/student_card1.jpg', '第一教学楼失物招领处', '电话：13800000001', 'published', 0, '2026-01-07 15:54:44', '2026-01-07 15:54:44');
INSERT INTO `found_item` VALUES (6, 2, '粉色保温杯招领', '生活用品', '图书馆三楼自习区', '2024-01-07 14:15:00', '粉色膳魔师保温杯，容量500ml，杯底有贴纸', '/images/cup1.jpg,/images/cup2.jpg', '图书馆前台', '电话：13800000002 或图书馆前台领取', 'published', 0, '2026-01-07 15:54:44', '2026-01-07 15:54:44');
INSERT INTO `found_item` VALUES (7, 1, '捡到一串钥匙', '个人物品', '篮球场观众席', '2024-01-06 18:45:00', '银色钥匙圈，共有5把钥匙（2把大门钥匙，3把小钥匙），带一个蓝色小挂件', '/images/keys1.jpg', '体育部办公室', '电话：13800000003', 'published', 0, '2026-01-07 15:54:44', '2026-01-07 15:54:44');
INSERT INTO `found_item` VALUES (8, 3, '黑色钱包招领', '钱包/卡包', '学校食堂二楼', '2024-01-07 12:00:00', '黑色皮质钱包，内有银行卡数张、身份证（李四）、少量现金', NULL, '保卫处', '请到学校保卫处认领，需验证身份', 'published', 0, '2026-01-07 15:54:44', '2026-01-07 15:54:44');
INSERT INTO `found_item` VALUES (9, 2, '捡到《Java编程思想》教材', '书籍类', '计算机学院机房', '2024-01-05 16:30:00', '第五版《Java编程思想》，书内有笔记和书签', '/images/book1.jpg', '计算机学院辅导员办公室', 'QQ：123456789', 'claimed', 0, '2026-01-07 15:54:44', '2026-01-07 15:54:44');
INSERT INTO `found_item` VALUES (10, 4, 'AirPods耳机招领', '电子产品', '校车站点', '2024-01-07 08:20:00', '白色AirPods Pro耳机，右侧耳机，带保护套', '/images/earphone1.jpg', '校车调度室', '电话：13800000004', 'pending', 0, '2026-01-07 15:54:44', '2026-01-07 15:54:44');
INSERT INTO `found_item` VALUES (11, 3, '长柄雨伞招领', '生活用品', '行政楼门口', '2024-01-06 17:30:00', '黑色长柄雨伞，有自动开关按钮，伞柄有磨损痕迹', NULL, '行政楼一楼大厅', '直接到行政楼一楼大厅领取', 'closed', 0, '2026-01-07 15:54:44', '2026-01-07 15:54:44');
INSERT INTO `found_item` VALUES (12, 1, '拾获U盘一个', '电子产品', '实验楼302实验室', '2024-01-04 09:15:00', '金色金属U盘，容量32GB，品牌SanDisk', '/images/usb1.jpg', '实验楼管理办公室', '电话：13800000005', 'published', 0, '2026-01-07 15:54:44', '2026-01-07 15:54:44');
INSERT INTO `found_item` VALUES (13, 4, '黑色眼镜框招领', '个人物品', '操场看台', '2024-01-07 15:45:00', '黑色全框眼镜，度数约300度，无镜片', '/images/glasses1.jpg', '体育器材室', '电话：13800000006', 'published', 0, '2026-01-07 15:54:44', '2026-01-07 15:54:44');
INSERT INTO `found_item` VALUES (14, 2, '运动手表招领', '电子产品', '游泳馆更衣室', '2024-01-06 20:00:00', '黑色小米运动手表，表带有使用痕迹', '/images/watch1.jpg', '游泳馆前台', '微信：wxid_123456', 'published', 0, '2026-01-07 15:54:44', '2026-01-07 15:54:44');
INSERT INTO `found_item` VALUES (16, 9, '拾获学生校园卡二张', '证件类', '第一教学楼201教室', '2026-01-07 10:30:00', '蓝色卡套，内有学生证照片，姓名为yehh，学号202301001', '/images/student_card1.jpg', '第一教学楼失物招领处', '电话：13800000001', 'published', 0, '2026-01-07 16:45:45', '2026-01-07 21:03:44');
INSERT INTO `found_item` VALUES (17, 9, '111112', 'study', '12', '2026-01-07 17:49:29', '11111111111', NULL, '11111', '13654940871', 'pending', 1, '2026-01-07 17:49:33', '2026-01-07 18:02:36');
INSERT INTO `found_item` VALUES (18, 9, '黑色手机', 'others', '图书馆2楼', '2026-01-10 18:01:31', '111111111111111111111111111', NULL, '宿舍楼202', '', 'pending', 0, '2026-01-10 18:01:47', '2026-01-10 18:01:47');
INSERT INTO `found_item` VALUES (19, 9, '白色钱包', 'others', '博学楼A105', '2026-01-06 06:05:45', '内含身份证，工商银行卡', NULL, 'C座', '123@qq.com', 'pending', 0, '2026-01-10 20:06:54', '2026-01-10 20:06:54');

-- ----------------------------
-- Table structure for lost_item
-- ----------------------------
DROP TABLE IF EXISTS `lost_item`;
CREATE TABLE `lost_item`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '物品ID',
  `user_id` int(11) NOT NULL COMMENT '发布者ID',
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '物品名称',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分类',
  `lost_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '丢失地点',
  `lost_time` datetime NOT NULL COMMENT '丢失时间',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '详细描述',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '图片地址，多个用逗号分隔',
  `contact_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系方式',
  `status` enum('pending','published','found','closed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pending' COMMENT '状态',
  `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_lost_user`(`user_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_create_time`(`create_time`) USING BTREE,
  CONSTRAINT `fk_lost_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '失物表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lost_item
-- ----------------------------
INSERT INTO `lost_item` VALUES (11, 8, '测试物品', NULL, '测试地点', '2024-01-01 10:00:00', NULL, NULL, NULL, 'pending', 0, '2026-01-08 17:29:23', '2026-01-08 17:29:23');
INSERT INTO `lost_item` VALUES (12, 8, '完整测试物品', 'others', '图书馆', '2024-01-01 10:00:00', '测试描述111111111111111', NULL, '13800000000', 'published', 0, '2026-01-08 17:31:16', '2026-01-10 20:57:26');
INSERT INTO `lost_item` VALUES (13, 8, '学生证', 'cards', '图书馆二楼自习区', '2024-01-15 14:30:00', '红色封面，内有个人信息和照片，学号：20231001', NULL, '13800138000', 'published', 0, '2026-01-08 17:35:14', '2026-01-10 19:59:20');
INSERT INTO `lost_item` VALUES (14, 8, '索尼蓝牙耳机', '电子产品', '操场跑道', '2024-01-13 18:45:00', '索尼WH-1000XM4黑色降噪耳机，左侧耳罩有轻微磨损', NULL, '13800138002', 'pending', 0, '2026-01-08 17:35:14', '2026-01-08 17:35:14');
INSERT INTO `lost_item` VALUES (15, 8, '星巴克保温杯', 'others', '体育馆休息室', '2024-01-10 19:10:00', '白色星巴克保温杯，容量500ml，杯底有划痕', NULL, '13800138005', 'published', 0, '2026-01-08 17:35:14', '2026-01-10 20:55:42');
INSERT INTO `lost_item` VALUES (16, 8, '佳明运动手表', '电子产品', '篮球场观众席', '2024-01-07 17:00:00', '佳明Forerunner 245，黑色表带，有心率监测功能', NULL, '13800138008', 'found', 0, '2026-01-08 17:35:14', '2026-01-08 17:35:14');
INSERT INTO `lost_item` VALUES (17, 9, '戴尔笔记本电脑', '电子产品', '教学楼301教室', '2024-01-14 10:15:00', '黑色戴尔Inspiron 15，贴有动漫贴纸，内有重要课程设计文件', NULL, '13800138001', 'published', 0, '2026-01-08 17:35:14', '2026-01-10 20:57:50');
INSERT INTO `lost_item` VALUES (18, 9, '高等数学教材', 'books', '自习室A区', '2024-01-11 16:20:00', '同济大学版高等数学第七版，书内有详细笔记', NULL, '13800138004', 'published', 0, '2026-01-08 17:35:14', '2026-01-10 20:56:10');
INSERT INTO `lost_item` VALUES (19, 9, '小米充电宝', '电子产品', '校门口咖啡店', '2024-01-08 15:30:00', '小米20000mAh充电宝，黑色，有轻微使用痕迹', NULL, '13800138007', 'closed', 1, '2026-01-08 17:35:14', '2026-01-08 17:48:18');
INSERT INTO `lost_item` VALUES (20, 9, '校园卡2', 'cards', '食堂二楼', '2024-01-05 12:00:00', '学生姓名：李四，学号：20231002，照片背景为红色', NULL, '13800138010', 'published', 0, '2026-01-08 17:35:14', '2026-01-10 20:56:59');
INSERT INTO `lost_item` VALUES (21, 8, '雨伞', '生活用品', '教学楼一楼', '2026-01-17 18:17:42', '无', NULL, NULL, 'pending', 0, '2026-01-08 18:17:58', '2026-01-08 18:17:58');
INSERT INTO `lost_item` VALUES (22, 9, '挂件1', '生活用品', '体育馆', '2026-01-21 18:18:51', '无11111111111111111111', NULL, '', 'pending', 0, '2026-01-08 18:19:00', '2026-01-10 18:04:40');
INSERT INTO `lost_item` VALUES (23, 9, '手机1', 'others', '图书馆1', '2026-01-10 19:29:59', '11111111111111111', NULL, '', 'found', 0, '2026-01-10 19:30:15', '2026-01-10 20:00:24');
INSERT INTO `lost_item` VALUES (24, 9, 'iphone19', 'others', '教室', '2026-01-10 19:30:28', '1111111111111111111', NULL, '', 'published', 0, '2026-01-10 19:30:50', '2026-01-10 20:58:23');
INSERT INTO `lost_item` VALUES (25, 9, '1111', 'others', '1111', '2026-01-10 19:31:10', '11111111111', NULL, '', 'pending', 1, '2026-01-10 19:31:19', '2026-01-10 19:31:27');
INSERT INTO `lost_item` VALUES (26, 9, '钱包1', 'others', '图书馆A202', '2026-01-10 20:00:42', '有现金500，还有钥匙串，U盘。', NULL, '123456@qq.com', 'closed', 0, '2026-01-10 20:02:06', '2026-01-19 13:50:05');
INSERT INTO `lost_item` VALUES (27, 9, '222222222222222', 'others', '11111111111111', '2026-01-11 09:06:24', '111111111111111111', NULL, '', 'pending', 1, '2026-01-11 09:06:30', '2026-01-11 09:06:44');

-- ----------------------------
-- Table structure for suggestion
-- ----------------------------
DROP TABLE IF EXISTS `suggestion`;
CREATE TABLE `suggestion`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '建议ID',
  `user_id` int(11) NOT NULL COMMENT '建议者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '建议内容',
  `status` enum('pending','read','processed') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'pending' COMMENT '状态',
  `reply` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '回复内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_suggestion_user`(`user_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  CONSTRAINT `fk_suggestion_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '建议反馈表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of suggestion
-- ----------------------------
INSERT INTO `suggestion` VALUES (1, 8, '希望增加手机号验证功能，提高安全性', 'processed', '感谢您的建议，我们将在下个版本中加入此功能', '2025-12-31 21:09:36');
INSERT INTO `suggestion` VALUES (2, 3, '搜索功能不够准确，希望能优化', 'read', NULL, '2026-01-02 21:09:36');
INSERT INTO `suggestion` VALUES (3, 4, '界面可以更美观一些', 'pending', NULL, '2026-01-04 21:09:36');
INSERT INTO `suggestion` VALUES (4, 5, '希望能增加消息推送功能', 'processed', '好建议，我们正在开发消息推送模块', '2026-01-05 21:09:36');
INSERT INTO `suggestion` VALUES (5, 8, '希望可以上传多张图片', 'pending', NULL, '2026-01-05 19:09:36');
INSERT INTO `suggestion` VALUES (6, 8, '建议类型：function\n联系方式：11111111111111111111\n建议内容：\n1111111111111111111111111111111111111', 'pending', NULL, '2026-01-08 15:33:30');
INSERT INTO `suggestion` VALUES (7, 8, '建议类型：function\n联系方式：111111111111111111111\n建议内容：\n11111111111111111111111111111111111111', 'pending', NULL, '2026-01-08 15:34:07');
INSERT INTO `suggestion` VALUES (8, 8, '建议类型：function\n联系方式：1\n建议内容：\n111111111111', 'pending', NULL, '2026-01-08 16:02:13');
INSERT INTO `suggestion` VALUES (9, 8, '建议类型：function\n联系方式：未提供\n建议内容：\n11111111111111111111111111111111111111111111111', 'pending', NULL, '2026-01-08 16:28:21');
INSERT INTO `suggestion` VALUES (10, 9, '希望平台能增加夜间模式，提升夜间使用体验', 'pending', NULL, '2026-01-08 16:32:15');
INSERT INTO `suggestion` VALUES (11, 8, '建议类型：function\n联系方式：未提供\n建议内容：\n1111111111111111111111111111', 'pending', NULL, '2026-01-08 16:48:33');
INSERT INTO `suggestion` VALUES (12, 8, '建议类型：function\n联系方式：未提供\n建议内容：\n11111111111111111111111111111111', 'pending', NULL, '2026-01-08 17:03:03');
INSERT INTO `suggestion` VALUES (13, 8, '建议类型：function\n联系方式：未提供\n建议内容：\n111111111111111', 'pending', NULL, '2026-01-08 17:07:04');
INSERT INTO `suggestion` VALUES (14, 8, '建议类型：function\n联系方式：未提供\n建议内容：\n可以提交。。。。。。。。。。。。', 'pending', NULL, '2026-01-08 17:22:04');
INSERT INTO `suggestion` VALUES (15, 8, '【提交人：111】\nwqertyui1234567890\n【联系方式：1@qq.com】', 'pending', NULL, '2026-01-08 19:44:44');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码',
  `real_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `student_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '学号',
  `phone` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `role` enum('admin','user') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'user' COMMENT '角色',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_student_id`(`student_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '123456', '系统管理员', NULL, '13654940871', 'admin@campus.com', NULL, 'admin', 1, '2026-01-05 14:27:01', '2026-03-22 12:00:41');
INSERT INTO `user` VALUES (2, 'zhangsan', 'user123', '张三', '20210001', '13800138001', 'zhangsan@campus.com', NULL, 'user', 1, '2026-01-05 14:27:01', '2026-01-05 14:27:01');
INSERT INTO `user` VALUES (3, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', '20210002', '13800138002', 'lisi@campus.com', NULL, 'user', 1, '2026-01-05 14:27:01', '2026-01-05 14:27:01');
INSERT INTO `user` VALUES (4, 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', '20210003', '13800138003', 'wangwu@campus.com', NULL, 'user', 1, '2026-01-05 14:27:01', '2026-01-05 14:27:01');
INSERT INTO `user` VALUES (5, 'testuser_996', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '13800000000', 'testuser_996@test.com', NULL, 'user', 1, '2026-01-05 19:20:06', '2026-01-05 19:20:06');
INSERT INTO `user` VALUES (6, 'testuser_330', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '13800000000', 'testuser_330@test.com', NULL, 'user', 1, '2026-01-05 19:20:26', '2026-01-05 19:20:26');
INSERT INTO `user` VALUES (7, 'user_test', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '13888888888', 'user_test@example.com', NULL, 'user', 1, '2026-01-05 19:36:11', '2026-01-05 19:36:11');
INSERT INTO `user` VALUES (8, 'testuser', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '13888888888', 'testuser@example.com', NULL, 'user', 1, '2026-01-05 19:58:09', '2026-01-05 19:58:09');
INSERT INTO `user` VALUES (9, 'yehh', 'e10adc3949ba59abbe56e057f20f883e', '余恩慧', NULL, NULL, '1351308650@qq.com', '/default-avatar.png', 'user', 1, '2026-01-06 10:33:01', '2026-01-08 12:57:55');
INSERT INTO `user` VALUES (10, 'gsyy', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '13243567587', 'gsyy@example.com', NULL, 'user', 1, '2026-01-08 20:19:33', '2026-01-08 20:19:33');
INSERT INTO `user` VALUES (12, 'admin1', 'e10adc3949ba59abbe56e057f20f883e', NULL, NULL, '13654940871', 'admin1@example.com', NULL, 'admin', 1, '2026-03-22 12:25:44', '2026-03-22 12:26:06');

SET FOREIGN_KEY_CHECKS = 1;
