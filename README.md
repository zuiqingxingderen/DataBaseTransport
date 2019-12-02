# DataBaseTransport
数据迁移;Oracle迁移至MySQL（MySql也可互迁）;支持一对多数据库,表结构,表数据独立迁移；去IOE;  
纯JAVA,直接运行；基于SpringBoot,SringMvc,Spring-Jdbc  
多数据源，数据库实时动态切换

# 环境
JDK >= 1.8  
Maven  
oracle jar包问题,maven 可能加载不下oracle的jar包，需要的需要自行添加

# 开始运行
运行Application方法  
调用DataBaseController的接口方法即可
# 20191122fork此项目  改造如下
1.项目中的pom文件jar包有冲突，springboot 版本太低 升级到2.14；
2.DataTransport 这个文件中的线程池 ThreadPoolExecutor 原来引用的是tomcat 的并发包，
3.自己换成java.util.concurrent.ThreadPoolExecutor；
4.添加logback.xml文件；
5.修改application-oldauth.properties 中数据库配置的文件；
6.添加必要方法的注释  ：
   复制表结构：
      路径：http://127.0.0.1:9999/transport/table?schema=AUTH&table=BMS_GROUP
   传输表数据：
      路径： http://127.0.0.1:9999/transport/data?schema=AUTH&table=BMS_USER&isDelete=false
      指定表进行数据迁移
      http://127.0.0.1:9999/transport/selectedData?schema=AUTH&table=BMS_USER&user=auth
