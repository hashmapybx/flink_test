package com.zrys.window;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

/**
 * 基于时间的窗口函数操作案例
 *  信号灯编号和通过该信号灯的车的数量
 *  9,3
 * 9,2
 * 9,7
 * 4,9
 * 2,6
 * 1,5
 * 2,3
 * 5,7
 * 5,4
 * @author rocky
 * @create 2021 - 09 - 02 11:45
 *
 * 需求1：每5秒钟统计一次，最近5秒钟内，各个路口通过红绿灯汽车的数量--基于时间的滚动窗口
 * 需求2:每5秒钟统计一次，最近10秒钟内，各个路口通过红绿灯汽车的数量--基于时间的滑动窗口
 *
 */
public class WindowDemo01_TimeWindow {
    public static void main(String[] args) throws Exception {
        //todo 1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
        //todo 2.source
        DataStreamSource<String> inputDS = env.socketTextStream("47.116.75.250", 9000);
        //todo 3.transformation
        SingleOutputStreamOperator<CartInfo> carInfoDS = inputDS.map(new MapFunction<String, CartInfo>() {
            @Override
            public CartInfo map(String value) throws Exception {
                String[] arr = value.split(",");
                return new CartInfo(arr[0], Integer.parseInt(arr[1]));

            }
        });
        // * 需求1:每5秒钟统计一次，最近5秒钟内，各个路口/信号灯通过红绿灯汽车的数量--基于时间的滚动窗口
        //timeWindow(Time size窗口大小, Time slide滑动间隔)
        SingleOutputStreamOperator<CartInfo> count1 = carInfoDS.keyBy(CartInfo::getSensorId)
//                .window(TumblingEventTimeWindows.of(Time.seconds(5)))
                .timeWindow(Time.seconds(5))
                .sum("count");

        // * 需求2:每5秒钟统计一次，最近10秒钟内，各个路口/信号灯通过红绿灯汽车的数量--基于时间的滑动窗口
//        SingleOutputStreamOperator<CartInfo> count2 = carInfoDS.keyBy(CartInfo::getSensorId)
//                .window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5))).sum("count");

        //todo 4.sink
        count1.print();
        env.execute();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CartInfo {
        private String sensorId;//信号灯id
        private Integer count;//通过该信号灯的车的数量
    }
}