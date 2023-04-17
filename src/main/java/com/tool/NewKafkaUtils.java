package com.tool;

import com.alibaba.fastjson.JSONObject;
import com.cdc.FlinkCDCTest;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.json.JsonRowSerializationSchema;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkKafkaPartitioner;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @ClassName NewKafkaUtils
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/7 9:45
 * @Version
 */
public class NewKafkaUtils {

    /**
     * 返回新版中kafkaSink
     *
     * @param topic 指定topic
     * @param filed 具体更加filed字段来分发数据到某一个partition
     * @return
     */
    private static final Logger log = LoggerFactory.getLogger(NewKafkaUtils.class);
    private static Object RowTypeInfo;

    public static KafkaSink<String> kafkaSink(String topic, String filed) {
        //设置压缩属性
        Properties properties = new Properties();
        properties.setProperty("transaction.timeout.ms", 1000 * 60 * 2+ "");//设置事务时间 5分钟提交事务
        return KafkaSink.<String>builder()
                .setBootstrapServers("qn-flink01:9092,qn-flink02:9092,qn-flink03:9092")
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(topic)
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .setKeySerializationSchema(new MyKeySerializationSchema())
                        .setPartitioner(new FlinkKafkaPartitioner<String>() {
                            //数据分区，按照scene字段的hash值来分发数据到3个分区
                            @Override
                            public int partition(String record, byte[] key, byte[] value, String targetTopic, int[] partitions) {
                                JSONObject jsonObject = JSONObject.parseObject(record);
                                String afterJson = jsonObject.get("after").toString();
                                Object json = JSONObject.parseObject(afterJson).get(filed);
                                log.info("scene: " + json);
                                return Math.abs(json.hashCode() % partitions.length);
                            }
                        }).build()
                )
                .setDeliverGuarantee(DeliveryGuarantee.EXACTLY_ONCE) //精确一次消费
                .setKafkaProducerConfig(properties)
//                .setTransactionalIdPrefix("scene")
                .build();
    }

    public static KafkaSource<String> kafkaSource(String topic, String groupId, OffsetsInitializer offset) {
        Properties properties = new Properties();
        properties.setProperty("isolation.level","read_committed");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setProperty("partition.discovery.interval.ms", "60000")
                .setBootstrapServers("qn-flink01:9092,qn-flink02:9092,qn-flink03:9092")
                .setTopics(topic)
                .setGroupId(groupId)
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(offset)
                .setProperties(properties)

                .build();
        return source;
    }
}

