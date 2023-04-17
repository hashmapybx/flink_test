package com.kafka;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bean.QianNanOrder;
import com.bean.QianNanOrderT;
import com.tool.FixSizedPriorityQueue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;

public class KafkaConsumerTest {
    public static void main(String[] args) {
        Properties prop = new Properties();

        prop.put("bootstrap.servers","qn-flink01:9092,qn-flink02:9092,qn-flink03:9092");
        prop.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");

        prop.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        prop.put("group.id","con-1");
        prop.put("auto.offset.reset","latest");
        //自动提交偏移量
        prop.put("enable.auto.commit", "false"); //关闭自动提交offfset操作
        //自动提交时间
        prop.put("isolation.level","read_committed");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(prop);
        ArrayList<String> topics = new ArrayList<String>();

        //可以订阅多个消息
        topics.add("general_order");
        consumer.subscribe(topics);
        //消费订单数据来展示最大的10条数据 利用create_time来判断最近的10条订单数据展示。
        //选择小跟堆展示最近10小时的数据

        //创建小跟堆队列来保存数据
        FixSizedPriorityQueue<QianNanOrder> queue = new FixSizedPriorityQueue<QianNanOrder>(10);

        while(true){
            ConsumerRecords<String,String> poll = consumer.poll(Duration.ofSeconds(20));
            for(ConsumerRecord<String,String> consumerRecord :poll){
                //将消费者消费的数据写入到mysql里面 内存中维护一个小跟堆来更新最新的10条数据
                QianNanOrderT qianNanOrder = new QianNanOrderT();
                JSONObject jsonObject = JSON.parseObject(consumerRecord.value());
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
            }
            //异步提交 但是异步提交失败怎么办呢？
//            consumer.commitAsync();
            consumer.commitSync(); //同步提交偏移量


        }

    }
}
