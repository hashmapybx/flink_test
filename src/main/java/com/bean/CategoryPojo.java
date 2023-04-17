package com.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName CategoryPojo
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/12 9:06
 * @Version
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryPojo {

        private String category;//分类名称
        private double totalPrice;//该分类总销售额
//        private String dateTime;// 截止到当前时间的时间,本来应该是EventTime,但是我们这里简化了直接用当前系统时间即可

}