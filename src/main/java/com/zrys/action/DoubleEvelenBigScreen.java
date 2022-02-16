package com.zrys.action;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.ContinuousProcessingTimeTrigger;

import java.util.Random;

/**
 * 模拟双十一交易大屏
 *
 * @author rocky
 * @create 2021 - 09 - 08 23:56
 * 模拟双十一淘宝建议数据实时统计
 *  1.实时计算出当天零点截止到当前时间的销售总额 11月11日 00:00:00 ~ 23:59:59
 *  2.计算出各个分类的销售top3
 *  3.每秒钟更新一次统计结果
 *
 */
public class DoubleEvelenBigScreen {


    public static void main(String[] args) throws Exception {
        //todo 1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //todo 2.source
        DataStreamSource<Tuple2<String, Double>> source = env.addSource(new MyDoubleElevenSource());
        //todo 3.transformation
        SingleOutputStreamOperator<CategoryPojo> aggregateResult = source.keyBy(t -> t.f0)
                .window(TumblingProcessingTimeWindows.of(Time.days(1), Time.hours(-8)))
                .trigger(ContinuousProcessingTimeTrigger.of(Time.seconds(1))) //每秒钟触发计算数据
                .aggregate(new PriceAggregate(), new WindowResult());

        //初步聚合结果打印出来
        //todo 4.sink
        aggregateResult.print("初步预聚合结果打印出来");

        //todo 5.执行操作
        env.execute();

        /**
         * 1.env
         * 2.source
         * 3.transformation--预聚合
         * 3.1定义大小为一天的窗口,第二个参数表示中国使用的UTC+08:00时区比UTC时间早
         * keyBy(t->t.f0)
         * window(TumblingProcessingTimeWindows.of(Time.days(1), Time.hours(-8))
         * 3.2定义一个1s的触发器
         * .trigger(ContinuousProcessingTimeTrigger.of(Time.seconds(1)))
         * 3.3聚合结果.aggregate(new PriceAggregate(), new WindowResult());
         * 3.4看一下聚合的结果
         * CategoryPojo(category=男装, totalPrice=17225.26, dateTime=2020-10-20 08:04:12)
         * 4.sink-使用上面预聚合的结果,实现业务需求:
         * tempAggResult.keyBy(CategoryPojo::getDateTime)
         * //每秒钟更新一次统计结果
         *                 .window(TumblingProcessingTimeWindows.of(Time.seconds(1)))
         * //在ProcessWindowFunction中实现该复杂业务逻辑
         *              	.process(new WindowResultProcess());
         * 4.1.实时计算出当天零点截止到当前时间的销售总额
         * 4.2.计算出各个分类的销售top3
         * 4.3.每秒钟更新一次统计结果
         * 5.execute
         */
    }


    /**
     * 自定义数据源 订单数据源
     */
    public static class MyDoubleElevenSource implements SourceFunction<Tuple2<String, Double>> {
        private boolean flag = true;
        private String[] categorys = {"女装", "男装", "图书", "家电", "洗护", "美妆", "运动", "游戏", "户外", "家具", "乐器", "办公"};
        private Random random = new Random();
        @Override
        public void run(SourceContext<Tuple2<String, Double>> sourceContext) throws Exception {
            while (flag) {
                //随机生成分类和金额
                int index = random.nextInt(categorys.length);
                String category = categorys[index];//获取的随机分类
                double price = random.nextDouble() * 100;
                //注意nextDouble生成的是[0~1)之间的随机小数,*100之后表示[0~100)的随机小数
                sourceContext.collect(Tuple2.of(category, price));
                Thread.sleep(20);
            }
        }

        @Override
        public void cancel() {
            flag = false;
        }
    }



}