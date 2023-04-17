package com.tool;

import com.alibaba.fastjson.JSONObject;
import org.apache.flink.api.common.serialization.SerializationSchema;

import javax.print.DocFlavor;

/**
 * @ClassName MyKeySerializationSchema
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/9 13:39
 * @Version
 */
public class MyKeySerializationSchema implements SerializationSchema<String> {

    @Override
    public byte[] serialize(String element) {
        String afterJson = JSONObject.parseObject(element).get("after").toString();
        String json = JSONObject.parseObject(afterJson).get("order_id").toString();
        return json.getBytes();
    }
}