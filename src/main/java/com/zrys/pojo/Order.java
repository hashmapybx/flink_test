package com.zrys.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单类
 *
 * @author rocky
 * @create 2021 - 09 - 02 8:36
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    public Long user;
    public String product;
    public int amount;

}