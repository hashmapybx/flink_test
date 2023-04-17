package com.window;

import com.bean.DailyData;
import com.cdc.FlinkCDCTest;
import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.WebOptions;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.Iterator;
import java.util.Random;

/**
 * @ClassName DailyDataWindow
 * @Description TODO 利用滚动窗口计算
 * @Author oyc
 * @Date 2023/4/13 17:06
 * @Version
 */
public class DailyDataWindow {
    private static final Logger log = LoggerFactory.getLogger(DailyDataWindow.class);

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
//        conf.setString(String.valueOf(WebOptions.PORT), "8082");
        conf.setString(WebOptions.LOG_PATH, "D:/git_respo/flink_yiliao/src/main/resources/logs/1.log");
        conf.setString(ConfigConstants.TASK_MANAGER_LOG_PATH_KEY, "D:/git_respo/flink_yiliao/src/main/resources/logs/1.log");
        conf.setBoolean("rest.flamegraph.enabled", true);
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
//        executionEnvironment.disableOperatorChaining();
//        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        executionEnvironment.setParallelism(1);
//        开启ck FileSystemCheckpointStorage(文件存储)
        executionEnvironment.getCheckpointConfig().
                setCheckpointStorage(new FileSystemCheckpointStorage("file:///d:/cdc/read_kafka/"));
//        executionEnvironment.setStateBackend(new FsStateBackend("hdfs://drmcluster/flink/checkpoints"));
        //开启checkpoint 启用 checkpoint,设置触发间隔（两次执行开始时间间隔）
        executionEnvironment.enableCheckpointing(1000 * 10L); //测试5秒触发一次 生产环境10分钟
        executionEnvironment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//        超时时间，checkpoint没在时间内完成则丢弃
        executionEnvironment.getCheckpointConfig().setCheckpointTimeout(50000L); //10秒
        executionEnvironment.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        executionEnvironment.getCheckpointConfig().setTolerableCheckpointFailureNumber(1);
        //最小间隔时间（前一次结束时间，与下一次开始时间间隔）
        executionEnvironment.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000);
//        当 Flink 任务取消时，保留外部保存的 checkpoint 信息
        executionEnvironment.getCheckpointConfig().setExternalizedCheckpointCleanup(
                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        executionEnvironment.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 10000));
        executionEnvironment.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        DataStreamSource<DailyData> streamSource = executionEnvironment.addSource(new DataSource());

        SingleOutputStreamOperator<DailyData> mapStream = streamSource.map(new MapFunction<DailyData, DailyData>() {
            @Override
            public DailyData map(DailyData value) throws Exception {
                return value;
            }
            //指定时间戳和水位线
        }).assignTimestampsAndWatermarks(WatermarkStrategy.<DailyData>forBoundedOutOfOrderness(Duration.ofSeconds(3)) //允许延时的时间是3秒
                .withTimestampAssigner(new SerializableTimestampAssigner<DailyData>() { //提取时间戳的函数
                    //时间戳提取器
                    @Override
                    public long extractTimestamp(DailyData element, long recordTimestamp) {
                        return element.getTimestamp();
                    }
                }));

        //窗口还是聚合一天长度的滚动窗口
        SingleOutputStreamOperator<String> processStream = mapStream.keyBy(DailyData::getDaily)
                .window(TumblingEventTimeWindows.of(Time.days(1)))
                .trigger(CountTrigger.of(4))
                .process(new ProcessWindowFunction<DailyData, String, String, TimeWindow>() {
                    @Override
                    public void process(String s, Context context, Iterable<DailyData> elements, Collector<String> out) throws Exception {
                        Iterator<DailyData> iterator = elements.iterator();
                        int sum = 0;
                        while (iterator.hasNext()) {
                            sum += iterator.next().getMessage();
                        }
                        out.collect("总数" + sum);
                    }
                });//一个窗口结束的时候调用一次在一个并行度中

        //将process 打印
        processStream.print().setParallelism(1);
        executionEnvironment.execute();

    }


    private static class DataSource extends RichParallelSourceFunction<DailyData> {
        private volatile boolean isRunning = true;

        public void run(SourceContext<DailyData> ctx) throws Exception {
            Random random = new Random();
            while (isRunning) {
                Thread.sleep((getRuntimeContext().getIndexOfThisSubtask() + 1) * 1000 * 5);
                String key = "类别" + (char) ('A' + random.nextInt(3));
//                int value = random.nextInt(10) + 1;
                if (key.equals("A")) {
                    DailyData dailyData = new DailyData();
                    dailyData.setDaily(key);
                    dailyData.setMessage(random.nextInt(10) + 5);
                    dailyData.setTimestamp(System.currentTimeMillis()-2000);
                    log.info(dailyData.toString());
                    ctx.collect(dailyData);
                }else {
                    DailyData dailyData = new DailyData();
                    dailyData.setDaily(key);
                    dailyData.setMessage(random.nextInt(10));
                    dailyData.setTimestamp(System.currentTimeMillis());
                    log.info(dailyData.toString());
                    ctx.collect(dailyData);
                }
            }
        }
        public void cancel() {
            isRunning = false;
        }
    }
}

