package com.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @ClassName DailyData
 * @Description TODO 对于每天的数据做实时汇总
 * @Author oyc
 * @Date 2023/4/13 17:03
 * @Version
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyData {
    private String daily;
    private Integer message;
    private Long timestamp;

}

