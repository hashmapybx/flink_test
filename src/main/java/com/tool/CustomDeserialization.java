package com.tool;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.source.SourceRecord;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Field;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Schema;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.data.Struct;
import com.ververica.cdc.connectors.shaded.org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

/**
 * @ClassName CustomDeserialization
 * @Description TODO
 * @Author oyc
 * @Date 2022/7/10 11:31
 * @Version
 */
public class CustomDeserialization implements DebeziumDeserializationSchema<String> {
    /**
     * 反序列化 自定义分装的数据格式 json
     * {
     * "":"",
     * "":"",
     * "":""
     * }
     */
    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {
        //创建json对象
        JSONObject jsonObject = new JSONObject();

        //获取库表和表名
        String topic = sourceRecord.topic();
        String[] strings = topic.split("\\.");
        String database = strings[1];
        String tableName = strings[2];

        Struct value = (Struct) sourceRecord.value();
        //获取before 数据
        Struct before = value.getStruct("before");
        JSONObject beforeJson = new JSONObject();
        if (before != null) {
            Schema beforeSchema = before.schema();
            //根据schema循环
            List<Field> fields = beforeSchema.fields();
            for (Field f : fields
            ) {
                Object beforeValue = before.get(f);
                beforeJson.put(f.name(), beforeValue);
            }
        }

        //获取after数据
        Struct after = value.getStruct("after");
        JSONObject afterJson = new JSONObject();
        if (after != null) {
            Schema afterSchema = after.schema();
            //根据schema循环
            List<Field> fields = afterSchema.fields();
            for (Field f : fields
            ) {
                Object afterValue = after.get(f);
                afterJson.put(f.name(), afterValue);
            }
        }
        //获取操作类型 CREATE
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
        String type = operation.toString().toLowerCase();
        if ("create".equals(type)) {
            type = "insert";
        }
        //将字段写入json对象
        jsonObject.put("database", database);
        jsonObject.put("tableName", tableName);
        jsonObject.put("before", beforeJson);
        jsonObject.put("after", afterJson);
        jsonObject.put("type", type);

        //输出json数据
        collector.collect(jsonObject.toJSONString());
    }

    public TypeInformation<String> getProducedType() {
        return TypeInformation.of(String.class);
    }


}