package com.tool;

import com.alibaba.fastjson.JSON;
import org.apache.flink.api.common.serialization.SerializationSchema;

/**
 * @ClassName MyValueSerializationSchema
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/10 18:59
 * @Version
 */
public class MyValueSerializationSchema implements SerializationSchema<String> {
    @Override
    public byte[] serialize(String element) {
//        JSON.parseObject(element).getBytes()
        return new byte[0];
    }
}