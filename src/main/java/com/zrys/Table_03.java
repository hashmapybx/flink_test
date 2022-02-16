package com.zrys;

import com.zrys.pojo.WordCount;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

/**
 * 使用sql实现Table api操作
 *
 * @author rocky
 * @create 2021 - 09 - 02 8:57
 */
public class Table_03 {
    public static void main(String[] args) throws Exception {
        //TODO 代码实现sql
        //TODO 1、环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env);

        //todo 2.Source
        DataStream<WordCount> input = env.fromElements(
                new WordCount("Hello", 1),
                new WordCount("World", 2),
                new WordCount("Hello", 4)
        );

        //todo 3.注册表 Table
        Table table = tableEnvironment.fromDataStream(input, $("word"), $("frequency"));
        table.printSchema();
        //todo 4.执行查询计划
        Table resultTable = table
                .groupBy($("word"))
                .select($("word"), $("frequency").sum().as("frequency"))
                .filter($("frequency").isEqual(2));

        //todo 5.输出查询结果
        DataStream<Tuple2<Boolean, WordCount>> tuple2DataStream =
                tableEnvironment.toRetractStream(resultTable, WordCount.class);
//                tableEnvironment.toAppendStream(resultTable,WordCount.class);
        tuple2DataStream.print();
        env.execute();


    }
}