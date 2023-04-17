//package com.cdc;
//
//import com.alibaba.ververica.cdc.connectors.mysql.MySQLSource;
//import com.alibaba.ververica.cdc.connectors.mysql.table.StartupOptions;
//import com.alibaba.ververica.cdc.debezium.DebeziumSourceFunction;
//import com.tool.CustomDeserialization;
//import com.tool.MyKafkaUtil;
//import org.apache.flink.api.common.eventtime.WatermarkStrategy;
//import org.apache.flink.contrib.streaming.state.RocksDBStateBackend;
//import org.apache.flink.runtime.state.filesystem.FsStateBackend;
//import org.apache.flink.streaming.api.CheckpointingMode;
//import org.apache.flink.streaming.api.datastream.DataStreamSource;
//import org.apache.flink.streaming.api.environment.CheckpointConfig;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//
///**
// * @ClassName FlinkCDCTest
// * @Description TODO
// * @Author oyc
// * @Date 2022/7/10 9:48
// * @Version
// */
//public class FlinkCDCReadMysql {
//    public static void main(String[] args) throws Exception{
//        //获取执行环境
//        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
//        executionEnvironment.setParallelism(1);
////        开启ck 并制定状态后端 memory FS rocksdb
////        executionEnvironment.setStateBackend(new RocksDBStateBackend("file:///d:/cdc/ck"));
////hdfs://drmcluster/flink/checkpoints
//        executionEnvironment.setStateBackend(new FsStateBackend("file:///d:/cdc/ck"));
////
////        //开启checkpoint
//        executionEnvironment.enableCheckpointing(5000L); //测试5秒触发一次 生产环境10分钟
//        executionEnvironment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//        executionEnvironment.getCheckpointConfig().setCheckpointTimeout(60000L); //10秒
//        executionEnvironment.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
//        executionEnvironment.getCheckpointConfig().setTolerableCheckpointFailureNumber(3);
//        executionEnvironment.getCheckpointConfig().setMinPauseBetweenCheckpoints(2000);
//////        当 Flink 任务取消时，保留外部保存的 checkpoint 信息
//        executionEnvironment.getCheckpointConfig().enableExternalizedCheckpoints(
//                CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
////        executionEnvironment.getCheckpointConfig().enableUnalignedCheckpoints();
////        executionEnvironment.getCheckpointConfig().setPreferCheckpointForRecovery(true);
////  两个checkpoint 头尾间隔2秒
//        //checkPoint的重启策略 在flink-1.12之前需要设置的，但是在1-12之后不需要设计了 在1.10的版本 里面默认重启int的最大值
//        //创建mysql bilog数据源
//        DebeziumSourceFunction<String> sourceFunction = MySQLSource.<String>builder().hostname("172.21.35.63")
//                .port(3306)
//                .username("qn_zczq")
//                .password("Qn_zczq@2022")
//                .databaseList("zczqdb")
//                .tableList("zczqdb.general_order")
//                .deserializer(new CustomDeserialization())
//                .startupOptions(StartupOptions.initial()) // 从flink cdc 开始执行的时候获取到的最新插入的数据
//                .build();
//
//        DataStreamSource<String> streamSource = executionEnvironment.addSource(sourceFunction);
//        streamSource.addSink(MyKafkaUtil.getKafkaSink("ods_mysql_db"));
//        executionEnvironment.execute();
//
//    }
//}