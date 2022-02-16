package com.zrys;

import com.zrys.pojo.Order;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.util.Arrays;

import static org.apache.flink.table.api.Expressions.$;

/**
 * flink的sqlTable api 01
 *  将DataStream注册为Table和View并进行SQL统计
 * @author rocky
 * @create 2021 - 09 - 02 8:34
 */
public class TableSql_01 {
    public static void main(String[] args) throws Exception {
        //TODO 1、环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env);
        //TODO 2、Source
        DataStream<Order> orderA = env.fromCollection(Arrays.asList(
                new Order(1L, "beer", 3),
                new Order(1L, "diaper", 4),
                new Order(3L, "rubber", 2)));

        DataStream<Order> orderB = env.fromCollection(Arrays.asList(
                new Order(2L, "pen", 3),
                new Order(2L, "rubber", 3),
                new Order(4L, "beer", 1)));
        //TODO 3、注册表 转为table
        Table tableA = tableEnvironment.fromDataStream(orderA, $("user"), $("product"), $("amount"));
        //convert stream to view
        tableEnvironment.createTemporaryView("OrderB", orderB, $("user"), $("product"), $("amount"));

        //TODO 4、执行查询
        System.out.println(tableA);
        Table resultTable = tableEnvironment.sqlQuery(
                "SELECT * FROM " + tableA + " WHERE amount > 2 " +
                        "UNION ALL " +
                        "SELECT * FROM OrderB WHERE amount < 2"
        );

        //TODO 5、输出结果
        DataStream<Order> resultDS =tableEnvironment.toAppendStream(resultTable, Order.class);
        resultDS.print();

        env.execute();



    }
}