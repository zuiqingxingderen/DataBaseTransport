package com.maywide.dbt;

import com.alibaba.fastjson.JSON;
import com.maywide.dbt.config.datasource.dynamic.DynamicDataSource;
import com.maywide.dbt.core.execute.DataTransport;
import com.maywide.dbt.core.execute.TableTransport;
import com.maywide.dbt.core.pojo.oracle.TableInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationTests {


    @Autowired
    private TableTransport tableTransport;
    @Autowired
    private DataTransport dataTransport;


    /**
     * @Description: copyDataToMysql
     * @Param: []
     * @return: void
     * @Author: sunyulong
     * @Date: 2019/11/23
     */
    @Test
    public void copyDataToMysql() {
        String oridb = "dataSource";
        String schema = "AUTH";
        String table = "BMS_USER";
        boolean isDelete = true;
        try {
            List<TableInfo> list = tableTransport.getTablesList(oridb, schema, table);
            if (null != list && list.size() > 0) {
                for (TableInfo tableInfo : list) {
                    dataTransport.startCopyData(oridb, schema, DynamicDataSource.otherDataSource, tableInfo.getTABLE_NAME(), isDelete);
                }
                System.err.println("异步执行中，请稍后检查日志和数据库,可以调用 /transport/getLastDataResult 接口查看耗时信息");
            } else {
                System.err.println("根据参数未能获取到指定的表信息，请检查");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 测试线程是否可以走debug
     * @Param:
     * @return:
     * @Author: sunyulong
     * @Date: 2019/11/23
     */
    @Test
    public void testThreadDebug() {

        class TestThread implements Runnable {
            @Override
            public void run() {
                System.err.println("1111111");
            }
        }
        new Thread(new TestThread()).start();
    }


    /**
     * @Description: 获取properties的值
     * @Param:
     * @return:
     * @Author: sunyulong
     * @Date: 2019/11/23
     */
    @Test
    public void test1() {
        ResourceBundle bundl = ResourceBundle.getBundle("auth");//读取配置文件中的配置信息
        String tables = bundl.getString("tables");
        for (String table : tables.split(",")) {
            System.err.println(table);
        }
    }


}
