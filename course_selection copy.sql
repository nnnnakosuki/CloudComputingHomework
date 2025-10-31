/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80026
 Source Host           : localhost:3306
 Source Schema         : course_selection

 Target Server Type    : MySQL
 Target Server Version : 80026
 File Encoding         : 65001

 Date: 30/10/2025 21:51:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for class_schedule
-- ----------------------------
DROP TABLE IF EXISTS `class_schedule`;
CREATE TABLE `class_schedule`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '排课ID',
  `course_schedule_id` bigint(0) NOT NULL COMMENT '开课ID',
  `week_day` tinyint(0) NOT NULL COMMENT '星期几：1-7',
  `start_time` time(0) NOT NULL COMMENT '开始时间',
  `end_time` time(0) NOT NULL COMMENT '结束时间',
  `classroom` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '教室',
  `weeks` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上课周次',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_class_schedule_course`(`course_schedule_id`) USING BTREE,
  INDEX `idx_class_schedule_time`(`week_day`, `start_time`) USING BTREE,
  CONSTRAINT `class_schedule_ibfk_1` FOREIGN KEY (`course_schedule_id`) REFERENCES `course_schedule` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '排课表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of class_schedule
-- ----------------------------
INSERT INTO `class_schedule` VALUES (1, 2, 1, '08:00:00', '09:40:00', 'A101', '1-16周11112321', '2025-10-25 15:45:26', '2025-10-25 16:38:27', 0);
INSERT INTO `class_schedule` VALUES (2, 1, 3, '08:00:00', '09:40:00', 'A101', '1-1623', '2025-10-25 15:45:26', '2025-10-25 17:13:38', 0);
INSERT INTO `class_schedule` VALUES (3, 2, 2, '10:00:00', '11:40:00', 'A102', '1-12', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `class_schedule` VALUES (4, 2, 4, '10:00:00', '11:40:00', 'A102', '1-12', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `class_schedule` VALUES (5, 3, 1, '14:00:00', '15:40:00', 'B201', '1-12', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `class_schedule` VALUES (6, 3, 3, '14:00:00', '15:40:00', 'B201', '1-12', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `class_schedule` VALUES (7, 4, 2, '16:00:00', '17:40:00', 'B202', '1-16', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `class_schedule` VALUES (8, 4, 4, '16:00:00', '17:40:00', 'B202', '1-16', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `class_schedule` VALUES (9, 7, 1, '08:00:00', '09:40:00', 'C301', '1-16', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `class_schedule` VALUES (10, 7, 3, '08:00:00', '09:40:00', 'C301', '1-16', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `class_schedule` VALUES (11, 8, 2, '10:00:00', '11:40:00', 'C302', '1-12', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `class_schedule` VALUES (12, 8, 4, '10:00:00', '11:40:00', 'C302', '1-12', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `class_schedule` VALUES (13, 13, 1, '16:36:16', '07:36:16', '234', '3242', '2025-10-25 16:36:28', '2025-10-25 16:36:28', 0);
INSERT INTO `class_schedule` VALUES (14, 1, 1, '15:38:27', '07:38:00', '324', '32423', '2025-10-25 16:38:37', '2025-10-25 16:38:37', 0);

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '课程ID',
  `course_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程编号',
  `course_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
  `credits` decimal(3, 1) NOT NULL COMMENT '学分',
  `course_type` tinyint(0) NOT NULL COMMENT '课程类型：1-必修，2-选修',
  `dept_id` bigint(0) NULL DEFAULT NULL COMMENT '开课院系ID',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '课程描述',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `course_code`(`course_code`) USING BTREE,
  INDEX `idx_course_code`(`course_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, 'CS101333432', '高等数学43', 4.5, 2, 2, '高等数学基础课程3', 1, 1, '2025-10-25 15:45:26', '2025-10-25 16:35:15');
INSERT INTO `course` VALUES (2, 'CS102', '线性代数', 3.0, 1, 1, '线性代数基础课程', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (3, 'CS103', 'C语言程序设计', 3.0, 1, 1, 'C语言编程基础', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (4, 'CS104', '数据结构', 4.0, 1, 1, '数据结构与算法', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (5, 'CS105', '操作系统', 3.0, 1, 1, '操作系统原理', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (6, 'CS106', '数据库原理', 3.0, 1, 1, '数据库系统原理', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (7, 'CS107', '计算机网络', 3.0, 1, 1, '计算机网络基础', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (8, 'CS108', '软件工程', 3.0, 1, 1, '软件工程概论', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (9, 'SE101', 'Java程序设计', 4.0, 1, 2, 'Java编程语言', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (10, 'SE102', 'Web开发技术', 3.0, 1, 2, 'Web前端开发技术', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (11, 'SE103', '软件测试', 2.0, 1, 2, '软件测试理论与方法', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (12, 'SE104', '移动应用开发', 3.0, 1, 2, '移动端应用开发', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (13, 'EL101', '人工智能导论', 2.0, 2, 1, '人工智能基础课程', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (14, 'EL102', '机器学习', 3.0, 2, 1, '机器学习算法与应用', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (15, 'EL103', '云计算技术', 2.0, 2, 2, '云计算平台与应用', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (16, 'EL104', '区块链技术', 2.0, 2, 2, '区块链原理与应用', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course` VALUES (17, '423', '423', 10.0, 2, 1, '23432', 1, 0, '2025-10-25 16:35:21', '2025-10-26 16:00:41');

-- ----------------------------
-- Table structure for course_grade
-- ----------------------------
DROP TABLE IF EXISTS `course_grade`;
CREATE TABLE `course_grade`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '成绩ID',
  `student_id` bigint(0) NOT NULL COMMENT '学生ID',
  `course_schedule_id` bigint(0) NOT NULL COMMENT '开课ID',
  `score` decimal(5, 2) NULL DEFAULT NULL COMMENT '成绩',
  `teacher_id` bigint(0) NOT NULL COMMENT '任课教师ID',
  `submit_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '提交时间',
  `audit_status` tinyint(0) NULL DEFAULT 1 COMMENT '审核状态：1-待审核，2-已通过，3-已驳回',
  `auditor_id` bigint(0) NULL DEFAULT NULL COMMENT '审核员ID',
  `audit_time` datetime(0) NULL DEFAULT NULL COMMENT '审核时间',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `query_start_time` datetime(0) NULL DEFAULT NULL COMMENT '查询开放开始时间',
  `query_end_time` datetime(0) NULL DEFAULT NULL COMMENT '查询开放结束时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `teacher_id`(`teacher_id`) USING BTREE,
  INDEX `auditor_id`(`auditor_id`) USING BTREE,
  INDEX `idx_grade_student`(`student_id`) USING BTREE,
  INDEX `idx_grade_course`(`course_schedule_id`) USING BTREE,
  CONSTRAINT `course_grade_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student_info` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `course_grade_ibfk_2` FOREIGN KEY (`course_schedule_id`) REFERENCES `course_schedule` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `course_grade_ibfk_3` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `course_grade_ibfk_4` FOREIGN KEY (`auditor_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '成绩表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_grade
-- ----------------------------
INSERT INTO `course_grade` VALUES (1, 1, 1, 85.50, 4, '2024-01-20 10:00:00', 2, NULL, NULL, 0, '2025-10-25 15:58:55', '2025-10-26 15:40:27', '2025-10-09 00:00:00', '2025-10-27 00:00:00');
INSERT INTO `course_grade` VALUES (2, 1, 2, 94.00, 4, '2025-10-26 15:17:12', 1, NULL, NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55', NULL, NULL);
INSERT INTO `course_grade` VALUES (3, 1, 3, 78.50, 4, '2024-01-20 10:10:00', 2, NULL, NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55', NULL, NULL);
INSERT INTO `course_grade` VALUES (4, 2, 1, 45.00, 4, '2025-10-26 15:59:38', 1, NULL, NULL, 0, '2025-10-25 15:58:55', '2025-10-26 15:40:27', '2025-10-09 00:00:00', '2025-10-27 00:00:00');
INSERT INTO `course_grade` VALUES (5, 2, 2, 92.00, 4, '2025-10-26 15:59:51', 1, NULL, NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55', NULL, NULL);
INSERT INTO `course_grade` VALUES (6, 2, 4, 88.00, 5, '2025-10-26 15:59:51', 2, NULL, NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55', NULL, NULL);
INSERT INTO `course_grade` VALUES (7, 3, 7, 95.00, 6, '2024-01-20 10:30:00', 2, NULL, NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55', NULL, NULL);
INSERT INTO `course_grade` VALUES (8, 3, 8, 87.50, 6, '2024-01-20 10:35:00', 2, NULL, NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55', NULL, NULL);
INSERT INTO `course_grade` VALUES (9, 4, 7, 91.00, 6, '2024-01-20 10:40:00', 2, NULL, NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55', NULL, NULL);
INSERT INTO `course_grade` VALUES (10, 4, 8, 89.50, 6, '2024-01-20 10:45:00', 2, NULL, NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55', NULL, NULL);
INSERT INTO `course_grade` VALUES (12, 2, 3, 95.00, 4, '2025-10-26 15:59:51', 1, NULL, NULL, 0, '2025-10-26 15:10:58', '2025-10-26 15:10:58', NULL, NULL);
INSERT INTO `course_grade` VALUES (13, 2, 5, 90.00, 5, '2025-10-26 15:59:51', 1, NULL, NULL, 0, '2025-10-26 15:10:58', '2025-10-26 15:10:58', NULL, NULL);
INSERT INTO `course_grade` VALUES (14, 2, 6, 85.00, 5, '2025-10-26 15:59:51', 1, NULL, NULL, 0, '2025-10-26 15:10:58', '2025-10-26 15:10:58', NULL, NULL);

-- ----------------------------
-- Table structure for course_schedule
-- ----------------------------
DROP TABLE IF EXISTS `course_schedule`;
CREATE TABLE `course_schedule`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '开课ID',
  `course_id` bigint(0) NOT NULL COMMENT '课程ID',
  `semester` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学期',
  `teacher_id` bigint(0) NOT NULL COMMENT '任课教师ID',
  `max_students` int(0) NULL DEFAULT 50 COMMENT '最大选课人数',
  `current_students` int(0) NULL DEFAULT 0 COMMENT '当前选课人数',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：1-开放选课，2-选课结束，0-取消开课',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_course_schedule_course`(`course_id`) USING BTREE,
  INDEX `idx_course_schedule_teacher`(`teacher_id`) USING BTREE,
  INDEX `idx_course_schedule_semester`(`semester`) USING BTREE,
  CONSTRAINT `course_schedule_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `course_schedule_ibfk_2` FOREIGN KEY (`teacher_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '开课计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_schedule
-- ----------------------------
INSERT INTO `course_schedule` VALUES (1, 4, '2023秋季', 4, 197, 2, 1, 0, '2025-10-25 15:45:26', '2025-10-25 16:57:11');
INSERT INTO `course_schedule` VALUES (2, 2, '2024春季', 4, 80, 2, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (3, 3, '2024春季', 4, 60, 1, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (4, 4, '2024春季', 5, 50, 1, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (5, 5, '2024春季', 5, 45, 0, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (6, 6, '2024春季', 5, 40, 0, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (7, 9, '2024春季', 6, 50, 2, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (8, 10, '2024春季', 6, 45, 2, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (9, 11, '2024春季', 6, 40, 1, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (10, 13, '2024春季', 4, 30, 1, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (11, 14, '2024春季', 5, 25, 1, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (12, 15, '2024春季', 6, 20, 0, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `course_schedule` VALUES (13, 17, '2024春季', 4, 200, 0, 2, 1, '2025-10-25 16:35:54', '2025-10-25 16:57:18');

-- ----------------------------
-- Table structure for course_selection
-- ----------------------------
DROP TABLE IF EXISTS `course_selection`;
CREATE TABLE `course_selection`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '选课ID',
  `student_id` bigint(0) NOT NULL COMMENT '学生ID',
  `course_schedule_id` bigint(0) NOT NULL COMMENT '开课ID',
  `selection_type` tinyint(0) NULL DEFAULT 1 COMMENT '选课类型：1-正常选课，2-补选',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：1-待确认，2-已确认，3-已退课',
  `select_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '选课时间',
  `confirm_time` datetime(0) NULL DEFAULT NULL COMMENT '确认时间',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_student_course`(`student_id`, `course_schedule_id`) USING BTREE,
  INDEX `idx_selection_student`(`student_id`) USING BTREE,
  INDEX `idx_selection_course`(`course_schedule_id`) USING BTREE,
  CONSTRAINT `course_selection_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student_info` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `course_selection_ibfk_2` FOREIGN KEY (`course_schedule_id`) REFERENCES `course_schedule` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '选课记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course_selection
-- ----------------------------
INSERT INTO `course_selection` VALUES (1, 1, 1, 1, 2, '2024-01-15 09:00:00', NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55');
INSERT INTO `course_selection` VALUES (2, 1, 2, 1, 2, '2024-01-15 09:05:00', NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55');
INSERT INTO `course_selection` VALUES (3, 1, 3, 1, 2, '2024-01-15 09:10:00', NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55');
INSERT INTO `course_selection` VALUES (4, 2, 1, 1, 2, '2024-01-15 09:15:00', NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55');
INSERT INTO `course_selection` VALUES (5, 2, 2, 1, 2, '2024-01-15 09:20:00', NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55');
INSERT INTO `course_selection` VALUES (6, 2, 4, 1, 2, '2024-01-15 09:25:00', NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55');
INSERT INTO `course_selection` VALUES (7, 3, 7, 1, 2, '2024-01-15 09:30:00', NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55');
INSERT INTO `course_selection` VALUES (8, 3, 8, 1, 2, '2024-01-15 09:35:00', NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55');
INSERT INTO `course_selection` VALUES (9, 4, 7, 1, 2, '2024-01-15 09:40:00', NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55');
INSERT INTO `course_selection` VALUES (10, 4, 8, 1, 2, '2024-01-15 09:45:00', NULL, 0, '2025-10-25 15:58:55', '2025-10-25 15:58:55');
INSERT INTO `course_selection` VALUES (11, 2, 5, 1, 3, '2025-10-25 17:04:53', NULL, 0, '2025-10-25 17:04:53', '2025-10-25 17:04:53');
INSERT INTO `course_selection` VALUES (12, 2, 12, 1, 3, '2025-10-25 17:05:27', NULL, 0, '2025-10-25 17:05:27', '2025-10-25 17:05:27');
INSERT INTO `course_selection` VALUES (13, 2, 11, 1, 2, '2025-10-25 17:11:22', '2025-10-25 17:13:31', 0, '2025-10-25 17:11:22', '2025-10-25 17:11:22');
INSERT INTO `course_selection` VALUES (14, 2, 9, 1, 1, '2025-10-25 17:14:04', NULL, 0, '2025-10-25 17:14:04', '2025-10-25 17:14:04');
INSERT INTO `course_selection` VALUES (15, 2, 10, 1, 1, '2025-10-26 15:57:52', NULL, 0, '2025-10-26 15:57:52', '2025-10-26 15:57:52');

-- ----------------------------
-- Table structure for schedule_adjustment
-- ----------------------------
DROP TABLE IF EXISTS `schedule_adjustment`;
CREATE TABLE `schedule_adjustment`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '调课ID',
  `class_schedule_id` bigint(0) NOT NULL COMMENT '原排课ID',
  `old_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '原排课信息',
  `new_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '新排课信息',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '调课原因',
  `operator_id` bigint(0) NOT NULL COMMENT '操作员ID',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：1-待审批，2-已通过，3-已驳回',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `class_schedule_id`(`class_schedule_id`) USING BTREE,
  INDEX `operator_id`(`operator_id`) USING BTREE,
  CONSTRAINT `schedule_adjustment_ibfk_1` FOREIGN KEY (`class_schedule_id`) REFERENCES `class_schedule` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `schedule_adjustment_ibfk_2` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '调课记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of schedule_adjustment
-- ----------------------------

-- ----------------------------
-- Table structure for student_info
-- ----------------------------
DROP TABLE IF EXISTS `student_info`;
CREATE TABLE `student_info`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '学生ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户ID',
  `student_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '学号',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '姓名',
  `gender` tinyint(0) NULL DEFAULT 1 COMMENT '性别：1-男，2-女',
  `dept_id` bigint(0) NULL DEFAULT NULL COMMENT '院系ID',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业',
  `class_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '班级',
  `enrollment_year` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '入学年份',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：1-在校，2-休学，3-退学，4-毕业',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `student_no`(`student_no`) USING BTREE,
  INDEX `idx_student_user`(`user_id`) USING BTREE,
  INDEX `idx_student_no`(`student_no`) USING BTREE,
  CONSTRAINT `student_info_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_info
-- ----------------------------
INSERT INTO `student_info` VALUES (1, 7, '2021001', '张三', 1, 1, '计算机科学与技术', '计科2101', '2026', 1, 1, '2025-10-25 15:45:26', '2025-10-25 16:15:59');
INSERT INTO `student_info` VALUES (2, 8, '2021002', '李四', 1, 2, '计算机科学与技术', '计科2101', '2021', 3, 0, '2025-10-25 15:45:26', '2025-10-25 16:32:19');
INSERT INTO `student_info` VALUES (3, 9, '2022001', '王五', 2, 2, '软件工程', '软工2201', '2022', 2, 1, '2025-10-25 15:45:26', '2025-10-25 17:12:55');
INSERT INTO `student_info` VALUES (4, 10, '2022002', '赵六', 1, 2, '软件工程', '软工2201', '2022', 1, 1, '2025-10-25 15:45:26', '2025-10-25 16:56:43');
INSERT INTO `student_info` VALUES (5, 12, '432', '423', 1, 1, '23423', '4', '2022', 1, 0, '2025-10-25 17:12:44', '2025-10-25 17:12:44');
INSERT INTO `student_info` VALUES (6, 13, '123', '123', 1, 1, '123', '123', '2022', 1, 0, '2025-10-26 16:00:28', '2025-10-26 16:00:28');

-- ----------------------------
-- Table structure for student_status
-- ----------------------------
DROP TABLE IF EXISTS `student_status`;
CREATE TABLE `student_status`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '变动ID',
  `student_id` bigint(0) NOT NULL COMMENT '学生ID',
  `status_type` tinyint(0) NOT NULL COMMENT '变动类型：1-注册，2-转学，3-毕业，4-休学，5-退学',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '变动原因',
  `operator_id` bigint(0) NOT NULL COMMENT '操作员ID',
  `operate_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '操作时间',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `student_id`(`student_id`) USING BTREE,
  INDEX `operator_id`(`operator_id`) USING BTREE,
  CONSTRAINT `student_status_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student_info` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `student_status_ibfk_2` FOREIGN KEY (`operator_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学籍变动表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_status
-- ----------------------------
INSERT INTO `student_status` VALUES (1, 2, 2, '1344234', 1, '2025-10-25 16:27:29', 0, '2025-10-25 16:27:29', '2025-10-25 16:27:29');
INSERT INTO `student_status` VALUES (2, 2, 3, '432r4234', 1, '2025-10-25 16:32:20', 0, '2025-10-25 16:32:20', '2025-10-25 16:32:20');
INSERT INTO `student_status` VALUES (3, 5, 1, '新生注册', 1, '2025-10-25 17:12:44', 0, '2025-10-25 17:12:44', '2025-10-25 17:12:44');
INSERT INTO `student_status` VALUES (4, 3, 2, '4234', 1, '2025-10-25 17:12:52', 0, '2025-10-25 17:12:52', '2025-10-25 17:12:52');
INSERT INTO `student_status` VALUES (5, 6, 1, '新生注册', 1, '2025-10-26 16:00:28', 0, '2025-10-26 16:00:28', '2025-10-26 16:00:28');

-- ----------------------------
-- Table structure for sys_dict
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict`;
CREATE TABLE `sys_dict`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '字典ID',
  `dict_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典类型',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典值',
  `sort_order` int(0) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_dict_type_value`(`dict_type`, `dict_value`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '数据字典表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict
-- ----------------------------

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限编码',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限URL',
  `menu_type` tinyint(0) NULL DEFAULT 1 COMMENT '菜单类型：1-菜单，2-按钮',
  `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父权限ID',
  `sort_order` int(0) NULL DEFAULT 0 COMMENT '排序',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `permission_code`(`permission_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, '用户管理', 'system:user', '/system/users', 1, 0, 1, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (2, '角色管理', 'system:role', '/system/roles', 1, 0, 2, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (3, '数据字典', 'system:dict', '/system/dict', 1, 0, 3, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (4, '学生信息管理', 'admin:student', '/admin/students', 1, 0, 4, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (5, '成绩审核', 'admin:grade', '/admin/grades', 1, 0, 5, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (6, '教学计划', 'admin:plan', '/admin/plans', 1, 0, 6, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (7, '开课计划', 'admin:schedule', '/admin/schedules', 1, 0, 7, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (8, '选课审核', 'admin:selection', '/admin/selections', 1, 0, 8, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (9, '排课管理', 'admin:arrangement', '/admin/arrangements', 1, 0, 9, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (10, '我的课程', 'teacher:course', '/teacher/courses', 1, 0, 10, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (11, '成绩录入', 'teacher:grade', '/teacher/grades', 1, 0, 11, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (12, '学生名单', 'teacher:student', '/teacher/students', 1, 0, 12, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (13, '个人信息', 'student:profile', '/student/profile', 1, 0, 13, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (14, '课程浏览', 'student:course', '/student/courses', 1, 0, 14, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (15, '我的选课', 'student:selection', '/student/selection', 1, 0, 15, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (16, '我的成绩', 'student:grade', '/student/grades', 1, 0, 16, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_permission` VALUES (17, '我的课表', 'student:schedule', '/student/schedule', 1, 0, 17, '2025-10-25 15:45:26', '2025-10-25 15:45:26');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色编码',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `role_code`(`role_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '管理员', 'admin', '系统管理员，拥有所有权限', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `sys_role` VALUES (2, '教务员', 'admin_edu', '教务管理员，负责学生管理、课程管理等', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `sys_role` VALUES (3, '教师', 'teacher', '任课教师，负责课程教学和成绩录入', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `sys_role` VALUES (4, '学生1', 'student', '在校学生，可以选课和查看成绩', '2025-10-25 15:45:26', '2025-10-25 15:45:26', 0);
INSERT INTO `sys_role` VALUES (5, '123', '123', '123', '2025-10-25 16:54:43', '2025-10-25 16:54:48', 1);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '真实姓名',
  `role_id` bigint(0) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(0) NULL DEFAULT NULL COMMENT '部门ID',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE,
  INDEX `idx_user_role`(`role_id`) USING BTREE,
  CONSTRAINT `sys_user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', '系统管理员11', 1, 1, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:47:52');
INSERT INTO `sys_user` VALUES (2, 'admin1', '$2a$10$7JB720yubVSOfvVWbKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjK', '张教务', 2, 1, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_user` VALUES (3, 'admin2', '$2a$10$7JB720yubVSOfvVWbKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjK', '李教务', 2, 2, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_user` VALUES (4, 'teacher1', '$2a$10$7JB720yubVSOfvVWbKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjK', '王老师', 3, 1, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_user` VALUES (5, 'teacher2', '$2a$10$7JB720yubVSOfvVWbKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjK', '刘老师', 3, 1, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_user` VALUES (6, 'teacher3', '$2a$10$7JB720yubVSOfvVWbKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjK', '陈老师', 3, 2, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_user` VALUES (7, 'student1', '$2a$10$7JB720yubVSOfvVWbKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjK', '张三', 4, 1, 1, 1, '2025-10-25 15:45:26', '2025-10-25 16:15:59');
INSERT INTO `sys_user` VALUES (8, 'student2', '$2a$10$7JB720yubVSOfvVWbKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjK', '李四', 4, 2, 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `sys_user` VALUES (9, 'student3', '$2a$10$7JB720yubVSOfvVWbKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjK', '王五', 4, 2, 1, 1, '2025-10-25 15:45:26', '2025-10-25 17:12:55');
INSERT INTO `sys_user` VALUES (10, 'student4', '$2a$10$7JB720yubVSOfvVWbKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjKjYzOjK', '赵六', 4, 2, 1, 1, '2025-10-25 15:45:26', '2025-10-25 16:48:07');
INSERT INTO `sys_user` VALUES (11, '312', '$2a$10$ZOtsH7a2yq8Rh9Tng17c/OP3R0T9YhroISd2Do3bOIdJlHD8E9aJ6', '3123', 1, NULL, 1, 0, '2025-10-25 16:48:12', '2025-10-25 16:48:12');
INSERT INTO `sys_user` VALUES (12, '423', '$2a$10$RKnAr4RGQXzV9D/idHvnzuwyFxwOkYnNc/QSaX9r4O4otz28a4BsW', '23', 4, 1, 1, 0, '2025-10-25 17:12:44', '2025-10-25 17:12:44');
INSERT INTO `sys_user` VALUES (13, '123', '$2a$10$NCuNoauVTsaEI7mffU4zRuwXVys78s6cYWxi1HC9pn9RbmND2PDfe', '123', 4, 1, 1, 0, '2025-10-26 16:00:28', '2025-10-26 16:00:28');

-- ----------------------------
-- Table structure for teaching_plan
-- ----------------------------
DROP TABLE IF EXISTS `teaching_plan`;
CREATE TABLE `teaching_plan`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '计划ID',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划名称',
  `dept_id` bigint(0) NULL DEFAULT NULL COMMENT '院系ID',
  `major` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '专业',
  `grade` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '年级',
  `total_credits` int(0) NULL DEFAULT 0 COMMENT '总学分',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '计划描述',
  `status` tinyint(0) NULL DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `deleted` tinyint(0) NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教学计划表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teaching_plan
-- ----------------------------
INSERT INTO `teaching_plan` VALUES (1, '计算机科学与技术2021级培养方案', 1, '计算机科学与技术', '2021', 160, '计算机科学与技术专业2021级本科培养方案', 1, 0, '2025-10-25 15:45:26', '2025-10-25 15:45:26');
INSERT INTO `teaching_plan` VALUES (2, '软件工程2022级培养方案', 2, '软件工程', '2022', 150, '软件工程专业2022级本科培养方案', 1, 1, '2025-10-25 15:45:26', '2025-10-25 16:33:25');
INSERT INTO `teaching_plan` VALUES (3, 'hiuhi342', 1, '432411', '4234234', 7, '4324', 1, 0, '2025-10-25 16:34:41', '2025-10-25 17:13:09');

SET FOREIGN_KEY_CHECKS = 1;
