package com.maywide.dbt.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.maywide.dbt.config.datasource.dynamic.DynamicDataSource;
import com.maywide.dbt.core.execute.DataTransport;
import com.maywide.dbt.core.execute.TableTransport;
import com.maywide.dbt.core.pojo.oracle.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

@RestController
@RequestMapping("/transport")
public class DataBaseController {

    @Autowired
    private TableTransport tableTransport;

    @Autowired
    private DataTransport dataTransport;

    private static Logger log = LoggerFactory.getLogger(DataBaseController.class);

    /**
     * @Description: 从Oracle中复制表到mysql中
     * @Param: table 表名
     * @Param: schema 表空间  (auth 用户表空间AUTH)
     * @Param: oridb 数据库连接信息
     * @return: String
     * @Author: sunyulong
     * @Date: 2019/11/22
     */
    @RequestMapping("/table")
    public String tableTransport(@RequestParam(defaultValue = "*") String table, @RequestParam(defaultValue = "UAPDB") String schema, @RequestParam(defaultValue = "dataSource") String oridb) {
        log.info("table = " + table);
        if (tableTransport.execute(schema, oridb, table)) {
            return "异步执行中，请稍后...";
        } else {
            return "oracle 转mysql 表结果出错";
        }
    }

    /**
     * @Description: 查询oracle中用户的表空间
     * @Param:
     * @return:
     * @Author: sunyulong
     * @Date: 2019/11/22
     */
    @RequestMapping("/schemas")
    public String schemasTransport(@RequestParam(defaultValue = "*") String table, @RequestParam(defaultValue = "UAPDB") String schema, @RequestParam(defaultValue = "dataSource") String oridb) {
        log.info("table = " + table);
        try {
            tableTransport.getSchemasInfo(oridb);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @Description: 从oracle数据库中复制数据到mysql
     * @Param: table 表名
     * @Param: schema 表空间  (auth 用户表空间AUTH)
     * @Param: oridb 数据库连接信息
     * @return: String
     * @Author: sunyulong
     * @Date: 2019/11/22
     */
    @RequestMapping("/data")
    public String dataTransport(@RequestParam(defaultValue = "*") String table, @RequestParam(defaultValue = "UAPDB") String schema,
                                @RequestParam(defaultValue = "dataSource") String oridb, @RequestParam(defaultValue = "true") boolean isDelete) {
        List<TableInfo> list = tableTransport.getTablesList(oridb, schema, table);
        if (null != list && list.size() > 0) {
            for (TableInfo tableInfo : list) {
                dataTransport.startCopyData(oridb, schema, DynamicDataSource.otherDataSource, tableInfo.getTABLE_NAME(), isDelete);
            }
            return "异步执行中，请稍后检查日志和数据库,可以调用 /transport/getLastDataResult 接口查看耗时信息";
        } else {
            return "根据参数未能获取到指定的表信息，请检查";
        }
    }

    /**
     * @Description: 从oracle数据库中复制数据到mysql
     * @Param: table 表名
     * @Param: schema 表空间  (auth 用户表空间AUTH)
     * @Param: oridb 数据库连接信息
     * @return: String
     * @Author: sunyulong
     * @Date: 2019/11/22
     */
    @RequestMapping("/selectedData")
    public String selectedDataTransport(String user, @RequestParam(defaultValue = "UAPDB") String schema,
                                        @RequestParam(defaultValue = "dataSource") String oridb, @RequestParam(defaultValue = "true") boolean isDelete) {

        String tables = ResourceBundle.getBundle(user).getString("tables");
        List<TableInfo> list = tableTransport.getTablesList(oridb, schema, tables.split(","));
        if (null != list && list.size() > 0) {
            for (TableInfo tableInfo : list) {
                dataTransport.startCopyData(oridb, schema, DynamicDataSource.otherDataSource, tableInfo.getTABLE_NAME(), isDelete);
            }
            return "异步执行中，请稍后检查日志和数据库,可以调用 /transport/getLastDataResult 接口查看耗时信息";
        } else {
            return "根据参数未能获取到指定的表信息，请检查";
        }
    }

    @RequestMapping("/seq")
    public String seqTransport(@RequestParam(defaultValue = "UAPDB") String schema, @RequestParam(defaultValue = "dataSource") String oridb) {
        //序列处理
        if (tableTransport.transprotOracleSeqToMysqlTable(oridb, schema)) {
            return "oracle序列转换成功，请查询mysql 数据库";
        } else {
            return "oracle序列转换失败，请检查日志";
        }
    }

    @RequestMapping("/getLastDataResult")
    public String getLastDataResult() {
        Map<String, JSONObject> map = DataTransport.successMap;
        // 总耗时
        //
        long allTime = 0;
        Set<String> set = map.keySet();
        for (String key : set) {
            JSONObject one = map.get(key);
            allTime += one.getLong("spend_time");
        }
        JSONObject result = new JSONObject();
        result.put("allTime", allTime);
        result.put("detail", map);
        return result.toJSONString();
    }

}
