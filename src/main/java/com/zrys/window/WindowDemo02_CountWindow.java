package com.zrys.window;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * 基于数量的滚动和滑动窗口
 *
 * @author rocky
 * @create 2021 - 09 - 02 13:12
 *
 * 需求1:统计在最近5条消息中,各自路口通过的汽车数量,相同的key每出现5次进行统计--基于数量的滚动窗口
 * 需求2:统计在最近5条消息中,各自路口通过的汽车数量,相同的key每出现3次进行统计--基于数量的滑动窗口
 *
 *
 *
 */
public class WindowDemo02_CountWindow {

    public static void main(String[] args) throws Exception {
        //todo 1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //todo 2.Source
        DataStreamSource<String> socketDS = env.socketTextStream("node1", 9999);
        SingleOutputStreamOperator<CartInfo> cartInfoDS = socketDS.map(new MapFunction<String, CartInfo>() {
            @Override
            public CartInfo map(String value) throws Exception {
                String[] arr = value.split(",");
                return new CartInfo(arr[0], Integer.parseInt(arr[1]));
            }
        });

        //todo 3.分组

        // * 需求1:统计在最近5条消息中,各自路口通过的汽车数量,相同的key每出现5次进行统计--基于数量的滚动窗口
        SingleOutputStreamOperator<CartInfo> result1 = cartInfoDS
                .keyBy(CartInfo::getSensorId)
                //.countWindow(5L, 5L)
                .countWindow( 5L)
                .sum("count");

        // * 需求2:统计在最近5条消息中,各自路口通过的汽车数量,相同的key每出现3次进行统计--基于数量的滑动窗口
        //countWindow(long size, long slide)
        SingleOutputStreamOperator<CartInfo> result2 = cartInfoDS
                .keyBy(CartInfo::getSensorId)
                .countWindow(5L, 3L)
                .sum("count");


        //todo 4.sink
        result1.print();
//        result2.print();

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