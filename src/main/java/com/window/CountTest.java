package com.window;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.HashMap;
import java.util.Random;

/**
 * @ClassName CountTest
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/12 14:17
 * @Version
 */
public class CountTest {
    private static class DataSource extends RichParallelSourceFunction<Tuple2<String, Integer>> {
        private volatile boolean isRunning = true;

        public void run(SourceContext<Tuple2<String, Integer>> ctx) throws Exception {
            Random random = new Random();
            while (isRunning) {
                Thread.sleep((getRuntimeContext().getIndexOfThisSubtask() + 1) * 1000 * 5);
                String key = "类别" + (char) ('A' + random.nextInt(3));
                int value = random.nextInt(10) + 1;

                System.out.println(String.format("Emits\t(%s, %d)", key, value));
                ctx.collect(new Tuple2<String,Integer>(key, value));
            }
        }

        public void cancel() {
            isRunning = false;
        }
    }

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(2);
        //结构：类别、成交量
        DataStream<Tuple2<String, Integer>> ds = env.addSource(new DataSource());
        //通过keyBy算子进行类别分组
        KeyedStream<Tuple2<String, Integer>, Tuple> keyedStream = ds.keyBy(0);
        /**
         * 通过sum算子，统计分组之后的各类别成交量之和
         * 通过keyBy算子，再次进行分组
         */
        KeyedStream<Tuple2<String, Integer>, Object> keyBy = ds.keyBy(new KeySelector<Tuple2<String, Integer>, Object>() {

            public Object getKey(Tuple2<String, Integer> value) throws Exception {
                return "";
            }
        });

        SingleOutputStreamOperator<String> streamOperator = keyBy.sum(1).map(new MapFunction<Tuple2<String, Integer>, String>() {
            @Override
            public String map(Tuple2<String, Integer> value) throws Exception {
                return "总数量:" + value.f1;
            }
        });
        streamOperator.print().setParallelism(1);

        env.execute();
    }
}

//