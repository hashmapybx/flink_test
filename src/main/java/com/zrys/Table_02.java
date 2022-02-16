package com.zrys;

import com.zrys.pojo.WordCount;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

/**
 * 使用Table api对单词进行统计
 *  需求： 使用SQL和Table两种方式对DataStream中的单词进行统计
 * @author rocky
 * @create 2021 - 09 - 02 8:50
 */
public class Table_02 {
    public static void main(String[] args) throws Exception {
        //TODO 代码实现sql
        //TODO 1、环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env);

        //2.Source
        DataStream<WordCount> input = env.fromElements(
                new WordCount("Hello", 1),
                new WordCount("World", 1),
                new WordCount("Hello", 4)
        );

        //TODO 3、注册表
        tableEnvironment.createTemporaryView("wordCount", input, $("word"), $("frequency"));

        //TODO 4.执行查询
        Table resultTable = tableEnvironment
                .sqlQuery("SELECT word, SUM(frequency) as frequency FROM wordCount GROUP BY word having SUM(frequency) > 3");

        //TODO 5.执行查询
        DataStream<Tuple2<Boolean, WordCount>> resultDS = tableEnvironment
                .toRetractStream(resultTable, WordCount.class);

        resultDS.print();
        env.execute();

    }
}