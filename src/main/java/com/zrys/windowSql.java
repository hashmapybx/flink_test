package com.zrys;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.apache.flink.table.api.Expressions.$;

/**
 * 使用窗口函数来操作sql
 * 案例需求：使用Flink SQL来统计5秒内 每个用户的 订单总数、订单的最大金额、订单的最小金额
 *  也就是每隔5秒统计最近5秒的每个用户的订单总数、订单的最大金额、订单的最小金额
 *  上面的需求使用流处理的Window的基于时间的滚动窗口就可以搞定!
 * @author rocky
 * @create 2021 - 09 - 02 9:45
 */
public class windowSql {
    public static void main(String[] args) throws Exception {
        //TODO 1、环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env);

        //TODO 2、source
        DataStreamSource<Orders> source = env.addSource(new RichSourceFunction<Orders>() {
            private Boolean isRunning = true;

            @Override
            public void run(SourceContext<Orders> ctx) throws InterruptedException {
                Random random = new Random();
                while (isRunning) {
                    Orders order = new Orders(UUID.randomUUID().toString(), random.nextInt(3), random.nextInt(101), System.currentTimeMillis());
                    TimeUnit.SECONDS.sleep(1);
                    ctx.collect(order);

                }
            }

            @Override
            public void cancel() {
                isRunning = true;
            }
        });


        //TODO 3、transformation
        SingleOutputStreamOperator<Orders> watermarkDS = source.assignTimestampsAndWatermarks(
                WatermarkStrategy.<Orders>forBoundedOutOfOrderness(Duration.ofSeconds(2))
                        .withTimestampAssigner((event, timestamp) -> event.getCreateTime())
        );

        //TODO 4.注册表
        tableEnvironment.createTemporaryView("t_order", watermarkDS,
                $("orderId"), $("userId"), $("money"), $("createTime").rowtime()
                );

        //TODO 5.执行sql
        String sql = "select " +
                "userId," +
                "count(*) as totalCount," +
                "max(money) as maxMoney," +
                "min(money) as minMoney " +
                "from t_order " +
                "group by userId," +
                    "tumble(createTime, interval '5' second)";

        Table tableResult = tableEnvironment.sqlQuery(sql);

        //TODO 6.Sink
        //将SQL的执行结果转换成DataStream再打印出来
        //toAppendStream → 将计算后的数据append到结果DataStream中去
        //toRetractStream  → 将计算后的新的数据在DataStream原数据的基础上更新true或是删除false

        DataStream<Tuple2<Boolean, Row>> stream = tableEnvironment.toRetractStream(tableResult, Row.class);
        stream.print();


        env.execute();


    }


//    toAppendStream → 将计算后的数据append到结果DataStream中去

//    toRetractStream  → 将计算后的新的数据在DataStream原数据的基础上更新true或是删除false

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Orders {
        private String orderId;
        private Integer userId;
        private Integer money;
        private Long createTime;
    }

}