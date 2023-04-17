package com.cdc;

import com.bean.QianNanOrderT;
import org.apache.flink.api.common.functions.AggregateFunction;

/**
 * @ClassName MyAgg
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/12 8:54
 * @Version
 */
public class MyAgg implements AggregateFunction<QianNanOrderT,Double,Double> {

    /**
     * 合并数据，返回窗口的计算结果
     * @param a
     * @param b
     * @return
     */
    @Override
    public Double merge(Double a, Double b) {
        return a+b;
    }

    /**
     * 初始化累加器
     * @return
     */
    @Override
    public Double createAccumulator() {
        return 0D;
    }

    /**
     *
     将数据累加到累加器上
     */
    @Override
    public Double add(QianNanOrderT value, Double accumulator) {
        return value.getCharge() + accumulator;
    }

    @Override
    public Double getResult(Double accumulator) {
        return accumulator;
    }
}