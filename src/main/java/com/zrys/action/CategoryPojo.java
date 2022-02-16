package com.zrys.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author rocky
 * @create 2021 - 09 - 10 9:55
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryPojo {
    private String category;//分类名称
    private double totalPrice;//该分类总销售额
    private String dateTime;// 截止到当前时间的时间,本来应该是EventTime,但是我们这里简化了直接用当前系统时间即可
}