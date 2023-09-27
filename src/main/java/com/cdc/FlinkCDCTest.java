package com.cdc;

import com.tool.CustomDeserialization;
import com.tool.NewKafkaUtils;
import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.WebOptions;
import org.apache.flink.runtime.state.storage.FileSystemCheckpointStorage;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


/**
 * @ClassName FlinkCDCTest
 * @Description TODO
 * @Author oyc
 * @Date 2022/7/10 9:48
 * @Version
 */
public class FlinkCDCTest {
    private static final Logger log = LoggerFactory.getLogger(FlinkCDCTest.class);
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        Configuration conf = new Configuration();
        //在本地运行的时候需要设置日志信息 本地standalone模式
//        conf.setString(WebOptions.LOG_PATH,"D:/git_respo/flink_yiliao/src/main/resources/logs/job.log");
//        conf.setString(ConfigConstants.TASK_MANAGER_LOG_PATH_KEY,"D:/git_respo/flink_yiliao/src/main/resources/logs/job.log");
        conf.setBoolean("rest.flamegraph.enabled",true); //开启火焰图图来监控作业
//        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(conf);
        executionEnvironment.disableOperatorChaining();

        executionEnvironment.setParallelism(1);
//        开启ck FileSystemCheckpointStorage(文件存储)
        executionEnvironment.getCheckpointConfig().
                setCheckpointStorage(new FileSystemCheckpointStorage("hdfs://drmcluster/flink/checkpoints"));
//        file:///d:/cdc/ck
//        executionEnvironment.setStateBackend(new FsStateBackend("hdfs://drmcluster/flink/checkpoints"));
        //开启checkpoint 启用 checkpoint,设置触发间隔（两次执行开始时间间隔）
        executionEnvironment.enableCheckpointing(1000*10L); //测试5秒触发一次 生产环境10分钟
        executionEnvironment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//        超时时间，checkpoint没在时间内完成则丢弃
        executionEnvironment.getCheckpointConfig().setCheckpointTimeout(50000L); //10秒
        executionEnvironment.getCheckpointConfig().setMaxConcurrentCheckpoints(2);
        executionEnvironment.getCheckpointConfig().setTolerableCheckpointFailureNumber(1);
        //最小间隔时间（前一次结束时间，与下一次开始时间间隔）
        executionEnvironment.getCheckpointConfig().setMinPauseBetweenCheckpoints(1000);
//        当 Flink 任务取消时，保留外部保存的 checkpoint 信息
        executionEnvironment.getCheckpointConfig().
                setExternalizedCheckpointCleanup(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
//        executionEnvironment.getCheckpointConfig().setPreferCheckpointForRecovery(true);

        Properties prop = new Properties();
        prop.setProperty("autoReconnect", "true");
        //创建mysql bilog数据源
        MySqlSource<String> sourceFunction = MySqlSource.<String>builder().hostname("172.21.35.63")
//                .serverTimeZone("Asia/Shanghai")//表示东八区
                .port(3306)
                .username("qn_zczq")
                .password("Qn_zczq@2022")
                .databaseList("zczqdb")
                .tableList("zczqdb.general_order")
                .deserializer(new CustomDeserialization())
                .startupOptions(StartupOptions.initial()) // 从flink cdc 开始执行的时候获取到的最新插入的数据
                .jdbcProperties(prop)
                .build();

        DataStreamSource<String> streamSource = executionEnvironment
               .fromSource(sourceFunction, WatermarkStrategy.noWatermarks(), "MySQL Source");
//        streamSource.disableChaining();

        //在控制台上打印binlog日志信息
        streamSource.setParallelism(1)
        .print("最终数据是：").setParallelism(1);
        String topic = "general_order_01";
        //写入到kafka里面
        streamSource.sinkTo(NewKafkaUtils.kafkaSink(topic,"scene")).name("kafkaSink").setParallelism(1);
//        streamSource.addSink(MyKafkaUtil.getKafkaSink(topic)).setParallelism(1);
        executionEnvironment.execute("flinkcdc");
    }
}

