package com.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.partitioner.FlinkFixedPartitioner;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

/**
 * @ClassName MyKafkaUtil
 * @Description TODO
 * @Author oyc
 * @Date 2022/7/12 10:52
 * @Version
 */
public class MyKafkaUtil {
    //    private static String KAFKA_SERVER = "192.168.56.102:9092";
    private static String KAFKA_SERVER = "qn-flink01:9092,qn-flink02:9092,qn-flink03:9092";
    //    private static String KAFKA_SERVER = "192.168.56.102:9092";
    private static Properties properties = new Properties();

    static {
        properties.setProperty("bootstrap.servers", KAFKA_SERVER);
//        properties.setProperty()
    }

    public static FlinkKafkaProducer<String> getKafkaSink(String topic) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", KAFKA_SERVER);
        properties.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
//        properties.put("acks", "all");
//        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,true);
//        properties.put("retries", 0); //当请求失败不会自动重新连接请求发送数据
//        properties.put("batch.size", 16384);
//        properties.put("linger.ms", 1);
//        properties.put("buffer.memory", 33554432);

        //配置分区自定义发送数据
        return new FlinkKafkaProducer<String>(topic, new SimpleStringSchema(), properties, new MyDefinePartitioner(),
                FlinkKafkaProducer.Semantic.EXACTLY_ONCE,2
                );
    }

    public static FlinkKafkaConsumer<String> getKafkaSource(String topic, String groupId) {
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        return new FlinkKafkaConsumer<String>(topic, new SimpleStringSchema(), properties);
    }
}

// 自定义分区函数
class MyDefinePartitioner extends FlinkFixedPartitioner<String> {

    @Override
    public int partition(String record, byte[] key, byte[] value, String targetTopic, int[] partitions) {
        JSONObject jsonObject = JSONObject.parseObject(record);
        String afterJson = jsonObject.get("after").toString();
        Object json = JSONObject.parseObject(afterJson).get("scene");
        System.out.println("json"+json);
        return Math.abs(json.hashCode() % partitions.length);
    }
}

