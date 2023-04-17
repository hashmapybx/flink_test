package com.udf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flink.table.functions.ScalarFunction;

/**
 * @ClassName FormatJsonUDF2
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/10 18:50
 * @Version
 */
public class FormatJsonUDF2 extends ScalarFunction {
    /**
     * 解析json格式数据
     * @param json
     * @return
     */
    public String eval(String json) {
        JSONObject jsonObject = JSON.parseObject(json);
        String database = jsonObject.getString("database");
        String after = jsonObject.getString("after");
        String before = jsonObject.getString("before");
        String type = jsonObject.getString("type");
        String tableName = jsonObject.getString("tableName");

        return database+"|"+after+"|"+before+"|"+type+"|"+tableName;

    }
}