# Usage
* 1.添加`spring-boot-config-parent`依赖管理到 `pom.xml` 文件
```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.tomoncle</groupId>
            <artifactId>spring-boot-config-parent</artifactId>
            <version>${spring.boot.config.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

* 2.引用依赖时，自动使用`spring-boot-config-parent`对应的版本
```
<dependencies>
    <!--...省略-->

    <!--spring-boot-config-->
    <dependency>
        <groupId>com.tomoncle</groupId>
        <artifactId>spring-boot-config-constants</artifactId>
    </dependency>
    <dependency>
        <groupId>com.tomoncle</groupId>
        <artifactId>spring-boot-config-errors</artifactId>
    </dependency>
</dependencies>
```

