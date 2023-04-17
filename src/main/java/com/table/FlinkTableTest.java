package com.table;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

/**
 * @ClassName FlinkTableTest
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/7 22:50
 * @Version
 */
public class FlinkTableTest {
    public static void main(String[] args) {
        // FLink table 直接读取kafka
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        EnvironmentSettings bsSettings = EnvironmentSettings.newInstance()
                .inStreamingMode().build();
        // table env
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, bsSettings);
        String ddl = "CREATE TABLE tgeneral_order (\n" +
                "  `order_id` BIGINT,\n" +
                "  `treat_shop_id` BIGINT,\n" +
                "  `customer_id` STRING,\n" +
                "  `status` INT,\n" +
                "  `is_tax_inclusive` INT,\n" +
                "  `charge` DOUBLE,\n" +
                "  `createTime` TIMESTAMP(3) METADATA FROM 'timestamp',\n" +
                "  `acceptTime` TIMESTAMP(3) METADATA FROM 'timestamp',\n" +
                "  `association_contract` STRING,\n" +
                "  `scene` STRING,\n" +
                "  `is_delete` STRING,\n" +
                "  `field1` STRING,\n" +
                "  `partner_id` STRING,\n" +
                "  `channel_source` STRING\n" +
                ") WITH (\n" +
                " 'connector' = 'kafka',\n" +
                " 'topic' = 'tgeneral_order',\n" +
                " 'properties.bootstrap.servers' = 'qn-flink01:9092,qn-flink02:9092,qn-flink03:9092',\n" +
                " 'properties.group.id' = 'testGroup',\n" +
                " 'value.format' = 'json' ,\n" +
                " 'value.json.ignore-parse-errors' = 'true', \n" +
                " 'scan.startup.mode' = 'earliest-offset'  \n" +
                ")";


        tableEnv.executeSql(ddl);
        TableResult tableResult = tableEnv.executeSql("select order_id,treat_shop_id from tgeneral_order order by createTime desc limit 3");

        tableResult.print();


    }
}