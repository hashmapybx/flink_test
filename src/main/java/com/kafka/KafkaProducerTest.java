package com.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @ClassName KafkaProducer
 * @Description TODO
 * @Author oyc
 * @Date 2023/3/23 16:04
 * @Version
 */
public class KafkaProducerTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "qn-flink01:9092,qn-flink02:9092,qn-flink03:9092");
        properties.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("acks", "all");

        properties.put("retries", 0); //当请求失败不会自动重新连接请求发送数据
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);

        String topic = "general_order";
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        Future<RecordMetadata> sendResult = producer.send(new ProducerRecord<String, String>(topic, Integer.toString(2), "Hello,kafka"));

        RecordMetadata recordMetadata = sendResult.get();
        System.out.println("server记录的元数据 topic:" + recordMetadata.topic()
                    + ", offset: " + recordMetadata.offset() + ", timestampe: " + recordMetadata.timestamp()
                + ", partition: " + recordMetadata.partition()
                );

        producer.close();

    }
}