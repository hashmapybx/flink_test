package com.zrys.action;

import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.java.tuple.Tuple2;

/**
 * @author rocky
 * @create 2021 - 09 - 10 8:59
 *
 * AggregateFunction<Tuple2<String, Double>, Double, Double>
 *     接口的泛型说明是 输入数据结构  中间预聚合的数据结构  输出结果
 *
 *
 */
public class PriceAggregate implements AggregateFunction<Tuple2<String, Double>, Double, Double> {
    //初始化累加器
    @Override
    public Double createAccumulator() {
        return 0D;
    }

    //做累加计算
    @Override
    public Double add(Tuple2<String, Double> value, Double accumulator) {
        return accumulator + value.f1;
    }

    //获取累加结果
    @Override
    public Double getResult(Double accumulator) {
        return accumulator;
    }


    @Override
    public Double merge(Double a, Double b) {
        return a + b;
    }
}