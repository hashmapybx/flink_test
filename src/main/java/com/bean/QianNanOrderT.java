package com.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QianNanOrder
 * @Description TODO
 * @Author oyc
 * @Date 2023/4/4 10:45
 * @Version

 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QianNanOrderT implements Comparable<QianNanOrderT>{
    private String orderId;
    private String treatShopId;
    private String customerId;
    private Integer status;
    private Integer isTaxInclusive;
    private Double charge;
    private String createTime;
    private String acceptTime;
    private String scene;
    private String partnerId;
    private String channelSource;

    @Override
    public String toString() {
        return "QianNanOrder{" +
                "orderId='" + orderId + '\'' +
                ", treatShopId='" + treatShopId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", status='" + status + '\'' +
                ", isTaxInclusive=" + isTaxInclusive +
                ", charge=" + charge +
                ", createTime=" + createTime +
                ", acceptTime=" + acceptTime +
                ", scene='" + scene + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", channelSource='" + channelSource + '\'' +
                '}';
    }

    @Override
    public int compareTo(QianNanOrderT o) {
        return (createTime.compareTo(o.getCreateTime()));
    }
}