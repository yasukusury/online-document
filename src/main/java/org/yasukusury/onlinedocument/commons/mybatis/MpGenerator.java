package org.yasukusury.onlinedocument.commons.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

/**
 * @author 30254
 */
public class MpGenerator {

    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        mpg.setGlobalConfig(
            new GlobalConfig()
            .setOutputDir("E:\\WORK\\onlinedocument")
            .setFileOverride(true)
            .setActiveRecord(false)
            // XML 二级缓存
            .setEnableCache(false)
            // XML ResultMap
            .setBaseResultMap(true)
            // XML columList
            .setBaseColumnList(true)
            .setDateType(DateType.SQL_PACK)
            .setAuthor("yasukusury")
            // 自定义文件命名，注意 %s 会自动填充表实体属性！
            //gc.setMapperName("%sDao")
            //gc.setXmlName("%sDao")
            //gc.setServiceName("MP%sService")
            .setServiceImplName("%sService")
            //gc.setControllerName("%sAction")
        );

        // 数据源配置
        mpg.setDataSource(
            new DataSourceConfig()
            .setDbType(DbType.MYSQL)
            .setTypeConvert(new MySqlTypeConvert())
            .setDriverName("com.mysql.cj.jdbc.Driver")
            .setUsername("root")
            .setPassword("root")
            .setUrl("jdbc:mysql://localhost:3306/online_document?serverTimezone=UTC&useSSL=false&useUnicode=true&characterEncoding=utf8")
        );

        // 策略配置
        mpg.setStrategy(
            new StrategyConfig()
            // 全局大写命名 ORACLE 注意
            // .setCapitalMode(true)
            // 此处可以修改为您的表前缀
            .setTablePrefix("ysk")
            // 表名生成策略
            .setNaming(NamingStrategy.underline_to_camel)
            //.setDbColumnUnderline()
            // 需要生成的表
            //.setInclude()
            // 排除生成的表
            .setExclude("parent")
            // 自定义实体父类
            .setSuperEntityClass("org.yasukusury.onlinedocument.commons.base.BaseModel")
            // 自定义实体，公共字段
            .setSuperEntityColumns("id", "updateTime", "createTime", "deleteTime", "valid")
            // 自定义 mapper 父类
            // .setSuperMapperClass("com.baomidou.demo.TestMapper").
            // 自定义 service 父类
            // .setSuperServiceClass("com.baomidou.demo.TestService").
            // 自定义 service 实现类父类
            // .setSuperServiceImplClass().
            // 自定义 controller 父类
            // .setSuperControllerClass()
            // 【实体】是否生成字段常量（默认 false）
            // public static final String ID = "test_id"
            .setEntityColumnConstant(true)
            // 【实体】是否为lombok模型（默认 false）<a href="https://projectlombok.org/">document</a>
            .setEntityLombokModel(true)
            // 【实体】是否为构建者模型（默认 false）
            // public User setName(String name) {this.name = name; return this;}
            .setEntityBuilderModel(false)
        );

        // 包配置
        mpg.setPackageInfo(
                new PackageConfig()
                .setParent("org.yasukusury.onlinedocument")
                .setModuleName("biz")
        );
        // 关闭默认 xml 生成，调整生成 至 根目录
        mpg.setTemplate(new TemplateConfig()

        // 自定义模板配置，可以 copy 源码 mybatis-plus/src/main/resources/template 下面内容修改，
        // 放置自己项目的 src/main/resources/template 目录下, 默认名称一下可以不配置，也可以自定义模板名称
        // .setController("...")
        // .setEntity("...")
        // .setMapper("...")
        // .setXml("...")
        // .setService("...")
        // .setServiceImpl("...")
        // 如上任何一个模块如果设置 空 OR Null 将不生成该模块。
        );

        // 执行生成
        mpg.execute();
    }

}
