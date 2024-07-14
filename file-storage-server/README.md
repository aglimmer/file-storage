## spring data jpa使用方法
## 1.数据持库驱动、久化data-jpa依赖添加
```xml
 <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```
## 2.编写数据表实体模型
使用@Table注解标注数据表名，@Id标注主键字段，@GeneratedValue指定主键生成策略， @Column注解标注字段名称
```java
@Table(name = "file_object")
@Entity
@Getter
@NoArgsConstructor
public class FileObject {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "storage_mode")
    private StorageMode storageMode;

    @Column(name = "original_name")
    private String originalName;
    //....
}
```
## 3.数据操作接口继承
创建一个接口继承持久化接口 JpaRepository<T, ID>，其中T表示数据表模型，ID表示主键类型
以上面为例，数据表模型为FileObject，主键id为Long类型，创建一个接口FileObjectRepository继承接口 JpaRepository<FileObject, Long>
```java
public interface FileObjectRepository extends JpaRepository<FileObject, Long> {

}
```
## 4.使用持久化接口
在Service层，注入数据操作接口
```java
@AllArgsConstructor
@Service
public class LocalFileService {
    private final FileObjectRepository fileObjectRepository;
    // 调用fileObjectRepository的一些操作方法
}
```

## 文件记录表
```sql
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

```
