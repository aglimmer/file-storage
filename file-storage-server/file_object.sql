-- 文件描述信息记录表
DROP TABLE IF EXISTS `file_object`;
CREATE TABLE `file_object`  (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建日期时间',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新日期时间',
    `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称，有后缀',
    `original_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '原始名称，有后缀部分',
    `storage_mode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '路径本地或阿里云：LOCAL,ALIYUN',
    `size` bigint NULL DEFAULT NULL COMMENT '大小',
    `suffix` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '后缀',
    `url` varchar(128) comment '请求路径',
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 88 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;
