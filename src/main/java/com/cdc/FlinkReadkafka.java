package com.cdc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bean.CategoryPojo;
import com.bean.QianNanOrderT;
import com.tool.NewKafkaUtils;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.WebOptions;
import org.apache.flink.connector.jdbc.*;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @ClassName FlinkReadkafka
 * @Description TODO 从kafka读取数据做计算
 * @Author oyc
 * @Date 2023/4/6 14:29
 * @Version
 */
public class FlinkReadkafka {
    public static Logger logger = LoggerFactory.getLogger(FlinkReadkafka.class);

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
//        conf.setString(String.valueOf(WebOptions.PORT), "8082");
//        conf.setString(WebOptions.LOG_PATH, "D:/git_respo/flink_yiliao/src/main/resources/logs/read_kafka.log");
//        conf.setString(ConfigConstants.TASK_MANAGER_LOG_PATH_KEY, "D:/git_respo/flink_yiliao/src/main/resources/logs/read_kafka.log");
        conf.setBoolean("rest.flamegraph.enabled", true);
//        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
//        executionEnvironment.disableOperatorChaining();
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
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

        executionEnvironment.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,10000));
        //读取kafka里面的数据
        DataStreamSource<String> dataStreamSource = executionEnvironment.fromSource(NewKafkaUtils.kafkaSource("general_order_01",
                "test_001", OffsetsInitializer.earliest())
                //  OffsetsInitializer.earliest() 从earliest开始 从earliest开始消费数据 中间程序断开出现问题的时候，
                //  是会根据checkpoint中记录信息来从上次消费的offset开始消费数据
                // latest从offset的最新位置开始
                //timestamp 指定时间戳来消费kafka中的数据.
                , WatermarkStrategy.noWatermarks(), "kafkasource");


        //过滤delete操作的数据
        SingleOutputStreamOperator<String> filterDataStream = dataStreamSource.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String value) throws Exception {
                JSONObject jsonObject = JSON.parseObject(value);
                return jsonObject.get("type").toString().equals("insert") ||
                        jsonObject.get("type").toString().equals("read")
                        || jsonObject.get("type").toString().equals("update");
            }
        });

        // 解析数据
        SingleOutputStreamOperator<QianNanOrderT> mapDataStream =
                dataStreamSource.map(new MapFunction<String, QianNanOrderT>() {
                    @Override
                    public QianNanOrderT map(String value) throws Exception {
                        JSONObject jsonObject = JSON.parseObject(value);
                        QianNanOrderT qianNanOrder = new QianNanOrderT();
                        //解析after insert update的操作的数据
                        JSONObject after = jsonObject.getJSONObject("after");
                        Integer is_tax_inclusive = Integer.valueOf(after.get("is_tax_inclusive").toString());
                        Double charge = Double.valueOf(after.get("charge").toString());
                        String create_time = after.get("create_time").toString();
                        String treat_shop_id = after.get("treat_shop_id").toString();
                        String scene = after.get("scene").toString();
                        String partner_id = after.get("partner_id").toString();
                        String channel_source = after.get("channel_source").toString();
                        String customer_id = after.get("customer_id").toString();
                        String order_id = after.get("order_id").toString();
                        String accept_time = after.get("accept_time").toString();
                        Integer status = Integer.valueOf(after.get("status").toString());
                        qianNanOrder.setOrderId(order_id);
                        qianNanOrder.setTreatShopId(treat_shop_id);
                        qianNanOrder.setCustomerId(customer_id);
                        qianNanOrder.setStatus(status);
                        qianNanOrder.setIsTaxInclusive(is_tax_inclusive);
                        qianNanOrder.setCharge(charge);
                        qianNanOrder.setCreateTime(create_time);
                        qianNanOrder.setAcceptTime(accept_time);
                        qianNanOrder.setScene(scene);
                        qianNanOrder.setPartnerId(partner_id);
                        qianNanOrder.setChannelSource(channel_source);
                        return qianNanOrder;
                    }
                });

        SingleOutputStreamOperator<CategoryPojo> aggregate = mapDataStream.keyBy(order -> order.getCustomerId())
                .window(SlidingProcessingTimeWindows.of(Time.seconds(3), Time.seconds(1)))
                .aggregate(new MyAgg(), new WindowFunction<Double, CategoryPojo, String, TimeWindow>() {
                    @Override
                    public void apply(String s, TimeWindow window, Iterable<Double> input, Collector<CategoryPojo> out) throws Exception {
                        Double totalPrice = input.iterator().next();
                        out.collect(new CategoryPojo(s, totalPrice));
                    }
                });

        //keyby+sum实现累计销售额计算

        SingleOutputStreamOperator<QianNanOrderT> reduce = mapDataStream.keyBy(new KeySelector<QianNanOrderT, String>() {
            @Override
            public String getKey(QianNanOrderT value) throws Exception {
                return "";
            }
        }).sum("charge");

        SingleOutputStreamOperator<String> map = reduce.map(new MapFunction<QianNanOrderT, String>() {
            @Override
            public String map(QianNanOrderT value) throws Exception {
                Double aDouble = Double.valueOf(String.format("%.2f", value.getCharge()));
                return "总销售额：" + aDouble;
            }
        }); //todo 实时计算累计销售额的指标数据
        map.print().setParallelism(1);


        executionEnvironment.execute();

    }

}