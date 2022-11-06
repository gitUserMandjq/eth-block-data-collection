/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : localhost:3306
 Source Schema         : ethereum_ens

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 06/11/2022 11:34:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for eth_blocks
-- ----------------------------
DROP TABLE IF EXISTS `eth_blocks`;
CREATE TABLE `eth_blocks`  (
  `id` bigint(20) UNSIGNED NOT NULL,
  `block_number` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '区块高度',
  `block_hash` char(66) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '区块HASH',
  `parent_hash` char(66) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '父块HASH',
  `miner` varchar(42) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '挖矿账号',
  `difficulty` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '区块难度',
  `total_difficulty` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '总体挖矿难度',
  `block_size` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '交易容量',
  `nonce` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '区块随机数',
  `extra_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '挖矿额外信息',
  `gas_limit` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '区块GAS限制',
  `gas_used` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '区块已消耗GAS',
  `timestamp` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '区块产出时间',
  `txn_count` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '交易容量',
  `base_fee_per_gas` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '基础GAS费（销毁ETH）',
  `burnt_fee` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '销毁ETH',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '系统时间',
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '系统时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `block_number`(`block_number`) USING BTREE,
  INDEX `timestamp`(`timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '区块基本信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_blocks_uncles
-- ----------------------------
DROP TABLE IF EXISTS `eth_blocks_uncles`;
CREATE TABLE `eth_blocks_uncles`  (
  `uncle_hash` char(66) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '叔伯区块HASH',
  `block_number` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '区块高度',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '系统时间',
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '系统时间',
  PRIMARY KEY (`uncle_hash`) USING BTREE,
  INDEX `block_number`(`block_number`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '叔伯区块' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_ens_info
-- ----------------------------
DROP TABLE IF EXISTS `eth_ens_info`;
CREATE TABLE `eth_ens_info`  (
  `token_id` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键，nft编号',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `image` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片',
  `background_image` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '背景图片',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '在ens上查看的路径',
  `character_set` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '文字类型',
  `constract_address` char(42) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '合约地址',
  `domain` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '域名',
  `owner` char(42) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '目前所有人地址',
  `last_txn_hash` char(66) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '最近交易hash',
  `last_txn_time` datetime(0) NULL DEFAULT NULL COMMENT '最近交易时间',
  `last_txn_fee` bigint(20) NULL DEFAULT NULL COMMENT '最近交易金额',
  `normailized` tinyint(4) NULL DEFAULT NULL COMMENT '是否规范（0否1是）',
  `expiration_date` datetime(0) NULL DEFAULT NULL COMMENT '过期时间',
  `length` int(11) NULL DEFAULT NULL COMMENT '字符长度',
  `letters_only` tinyint(4) NULL DEFAULT NULL COMMENT '只有文字（0否1是）',
  `has_numbers` tinyint(4) NULL DEFAULT NULL COMMENT '包含数字（0否1是）',
  `has_unicode` tinyint(4) NULL DEFAULT NULL COMMENT '包含unicode（0否1是）',
  `has_emoji` tinyint(4) NULL DEFAULT NULL COMMENT '包含表情（0否1是）',
  `has_invisibles` tinyint(4) NULL DEFAULT NULL COMMENT '包含隐藏字符（0否1是）',
  `registration_date` datetime(0) NULL DEFAULT NULL COMMENT '注册时间',
  `meta` json NULL COMMENT 'meta信息',
  `open_sea_price` bigint(20) NULL DEFAULT NULL COMMENT '价格',
  `open_sea_price_updated_time` datetime(0) NULL DEFAULT NULL COMMENT '价格最近更新时间',
  `open_sea_price_token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '使用的代币，ETH之类的',
  `open_sea_auction` tinyint(4) NULL DEFAULT NULL COMMENT '是否拍卖',
  `open_sea_auction_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '拍卖类型',
  `open_sea_listing_date` datetime(0) NULL DEFAULT NULL COMMENT '上市时间',
  PRIMARY KEY (`token_id`) USING BTREE,
  INDEX `constractAddress`(`constract_address`) USING BTREE,
  INDEX `domain`(`domain`, `expiration_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_events
-- ----------------------------
DROP TABLE IF EXISTS `eth_events`;
CREATE TABLE `eth_events`  (
  `block_number` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '区块高度',
  `log_index` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '事件区块编号',
  `address` char(42) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'token地址',
  `txn_hash` char(66) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '交易hash',
  `timestamp` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '交易时间',
  `removed` tinyint(4) NOT NULL DEFAULT 0,
  `method_id` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '事件签名',
  `topic` char(66) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '事件主题',
  `topics` json NULL COMMENT '输入信息',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `method` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '解码事件名称',
  `data` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '输入Data',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '系统时间',
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '系统时间',
  PRIMARY KEY (`block_number`, `log_index`) USING BTREE,
  INDEX `topic`(`topic`) USING BTREE,
  INDEX `block_number`(`block_number`) USING BTREE,
  INDEX `timestamp`(`timestamp`) USING BTREE,
  INDEX `address`(`address`, `timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '交易事件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_events_ens
-- ----------------------------
DROP TABLE IF EXISTS `eth_events_ens`;
CREATE TABLE `eth_events_ens`  (
  `block_number` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '区块高度',
  `log_index` int(10) UNSIGNED NOT NULL DEFAULT 0 COMMENT '事件区块编号',
  `address` char(42) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'token地址',
  `txn_hash` char(66) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '交易hash',
  `timestamp` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '交易时间',
  `removed` tinyint(4) NOT NULL DEFAULT 0,
  `method_id` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '事件签名',
  `topic` char(66) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '事件主题',
  `topics` json NULL COMMENT '输入信息',
  `type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `method` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '解码事件名称',
  `data` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '输入Data',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '系统时间',
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '系统时间',
  PRIMARY KEY (`block_number`, `log_index`) USING BTREE,
  INDEX `topic`(`topic`) USING BTREE,
  INDEX `block_number`(`block_number`) USING BTREE,
  INDEX `timestamp`(`timestamp`) USING BTREE,
  INDEX `address`(`address`, `timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '交易事件' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_txns
-- ----------------------------
DROP TABLE IF EXISTS `eth_txns`;
CREATE TABLE `eth_txns`  (
  `txn_hash` char(66) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `block_number` bigint(20) UNSIGNED NOT NULL DEFAULT 0,
  `txn_index` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `from_address` char(42) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `to_address` char(42) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `eth_value` bigint(20) NOT NULL DEFAULT 0,
  `gas_used` bigint(20) NOT NULL DEFAULT 0,
  `gas_price` bigint(20) NOT NULL DEFAULT 0,
  `gas_fee` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `status` tinyint(4) NOT NULL DEFAULT -1,
  `nonce` bigint(20) NOT NULL DEFAULT 0,
  `timestamp` bigint(20) UNSIGNED NOT NULL DEFAULT 0,
  `contract_address` varchar(42) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `cumulative_gas_used` bigint(20) NOT NULL DEFAULT 0,
  `effective_gas_price` bigint(20) NOT NULL DEFAULT 0,
  `input` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `max_fee_per_gas` bigint(20) NOT NULL DEFAULT 0,
  `max_priority_fee_per_gas` bigint(20) NOT NULL DEFAULT 0,
  `is_error` tinyint(4) NOT NULL DEFAULT 0,
  `err_msg` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `method_id` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `type` tinyint(4) NOT NULL DEFAULT 0,
  `logs_num` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`txn_hash`) USING BTREE,
  INDEX `timestamp`(`timestamp`) USING BTREE,
  INDEX `to_address`(`to_address`, `timestamp`) USING BTREE,
  INDEX `from_address`(`from_address`, `timestamp`) USING BTREE,
  INDEX `block_number`(`block_number`) USING BTREE,
  INDEX `contract_address`(`contract_address`, `timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '基本交易数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for eth_txns_ens
-- ----------------------------
DROP TABLE IF EXISTS `eth_txns_ens`;
CREATE TABLE `eth_txns_ens`  (
  `txn_hash` char(66) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `block_number` bigint(20) UNSIGNED NOT NULL DEFAULT 0,
  `txn_index` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `from_address` char(42) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `to_address` char(42) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `eth_value` bigint(20) NOT NULL DEFAULT 0,
  `gas_used` bigint(20) NOT NULL DEFAULT 0,
  `gas_price` bigint(20) NOT NULL DEFAULT 0,
  `gas_fee` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `status` tinyint(4) NOT NULL DEFAULT -1,
  `nonce` bigint(20) NOT NULL DEFAULT 0,
  `timestamp` bigint(20) UNSIGNED NOT NULL DEFAULT 0,
  `contract_address` varchar(42) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `cumulative_gas_used` bigint(20) NOT NULL DEFAULT 0,
  `effective_gas_price` bigint(20) NOT NULL DEFAULT 0,
  `input` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `max_fee_per_gas` bigint(20) NOT NULL DEFAULT 0,
  `max_priority_fee_per_gas` bigint(20) NOT NULL DEFAULT 0,
  `is_error` tinyint(4) NOT NULL DEFAULT 0,
  `err_msg` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `method_id` char(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '',
  `type` tinyint(4) NOT NULL DEFAULT 0,
  `logs_num` int(10) UNSIGNED NOT NULL DEFAULT 0,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`txn_hash`) USING BTREE,
  INDEX `timestamp`(`timestamp`) USING BTREE,
  INDEX `to_address`(`to_address`, `timestamp`) USING BTREE,
  INDEX `from_address`(`from_address`, `timestamp`) USING BTREE,
  INDEX `block_number`(`block_number`) USING BTREE,
  INDEX `contract_address`(`contract_address`, `timestamp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '基本交易数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `user_address` char(42) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户钱包地址',
  `user_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名称',
  `user_nick_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `last_login_time` datetime(0) NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `last_update_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '最后更新时间',
  `enabled` tinyint(4) NULL DEFAULT NULL COMMENT '是否可用（0不可用1可用）',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `ind_user_address`(`user_address`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
