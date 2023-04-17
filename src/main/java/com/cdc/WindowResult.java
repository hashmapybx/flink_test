package com.cdc;

import com.bean.CategoryPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.api.scala.function.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import scala.collection.Iterable;

/**
 * @ClassName WindowResult
 * @Description TODO 自定义窗口函数,指定窗口数据收集规则
 * @Author oyc
 * @Date 2023/4/12 8:59
 * @Version
 */
public class WindowResult implements WindowFunction<Double, Double,String, TimeWindow> {

    @Override
    public void apply(String s, TimeWindow window, Iterable<Double> input, Collector<Double> out) {
        Double totalPrice = input.iterator().next();
        out.collect(totalPrice);
    }

}



