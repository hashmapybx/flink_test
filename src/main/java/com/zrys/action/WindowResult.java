package com.zrys.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * @author rocky
 * @create 2021 - 09 - 10 9:52
 *
 * 窗口的聚合操作
 * 自定义窗口函数,指定窗口数据收集规则
 *
 */
public class WindowResult implements WindowFunction<Double, CategoryPojo, String, TimeWindow> {
    private FastDateFormat df = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
    @Override
    public void apply(String s, TimeWindow window, Iterable<Double> input, Collector<CategoryPojo> out) throws Exception {
        long currentTimeMillis = System.currentTimeMillis();
        String dateTime = df.format(currentTimeMillis);
        Double totalPrice = input.iterator().next();
        out.collect(new CategoryPojo(s, totalPrice, dateTime));

    }
}


