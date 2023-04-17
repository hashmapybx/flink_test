package com.table;

import com.tool.Krb5Auth;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.catalog.hive.HiveCatalog;

/**
 * @ClassName FlinkConnectHive
 * @Description TODO
 * @Author oyc
 * @Date 2023/3/29 10:27
 * @Version
 */

public class FlinkToConnectHive {

    private static final String KAFKA_SQL = "CREATE TABLE `k_ods_patient_basic_info`(\n" +
            "  `sys_id` STRING,\n" +
            "  `kh` STRING,\n" +
            "  `klx` STRING, \n" +
            "  `yljgdm` STRING, \n" +
            "  `yljgmc` STRING, \n" +
            "  `zjhm` STRING, \n" +
            "  `xm` STRING \n" +
            ") WITH(\n" +
            " 'connector' = 'kafka',\n" +
            " 'topic' = 'ods_patient_basi_info_topic',\n" +
            " 'properties.bootstrap.servers' = '40.44.249.29:9092,40.44.249.30:9092,40.44.249.31:9092',\n" +
            " 'properties.group.id' = 'testGroup',\n" +
            " 'value.format' = 'json' ,\n" +
            " 'value.json.ignore-parse-errors' = 'true', \n" +
            " 'scan.startup.mode' = 'latest-offset'  \n" +
            ")";

    private static final String HIve_sql = "SELECT sys_id, kh, klx, yljgdm, yljgmc, " +
            "zjhm, xm FROM gz_qnz_gd_db.ods_gd_rmyy_patient_basic_info limit 5";

    public static void main(String[] args) throws Exception {
//        StreamExecutionEnvironment bsEnv = StreamExecutionEnvironment.getExecutionEnvironment();
//        bsEnv.setParallelism(1);
//        EnvironmentSettings bsSettings = EnvironmentSettings.newInstance()
//                .inBatchMode().build();
//        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(bsEnv, bsSettings);
//        Krb5Auth.getKerberosAuth();
//        String name = "myHive";
//        String defaultDatabase = "gz_qnz_gd_db";
//        String hiveConfDir = "/ietl/e3base560/hive/conf";
//// 创建一个 HiveCatalog，并在表环境中注册
//        HiveCatalog hive = new HiveCatalog(name, defaultDatabase, hiveConfDir);
//        StreamStatementSet statementSet = tableEnv.createStatementSet();
//        tableEnv.executeSql(KAFKA_SQL);
//        tableEnv.registerCatalog("myhive", hive);
//// 使用 HiveCatalog 作为当前会话的 catalog
//        tableEnv.useCatalog("myhive");
//
//        // sql查询hive里面的数据
//        Table sqlResult = tableEnv.sqlQuery(HIve_sql);
//
//        // 转换到FLink sql提供的默认的catalog
//        tableEnv.useCatalog("default_catalog");
//        //将执行hive里面的查询结果注册到内存中的一个临时视图
//        tableEnv.createTemporaryView("ods_gd_rmyy_patient_basic_info", sqlResult);
//
//        tableEnv.executeSql("insert into k_ods_patient_basic_info select * from ods_gd_rmyy_patient_basic_info");

//        sqlResult.execute().print();
//        bsEnv.execute("test");
    }

}
