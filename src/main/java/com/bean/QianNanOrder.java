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
 *
 *
 * CREATE TABLE `general_order`  (
 *   `order_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单编号',
 *   `treat_shop_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '销售方id',
 *   `customer_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '采购方id',
 *   `status` int(0) DEFAULT NULL COMMENT '订单状态:1:已创建 2:待签订合同(权益商城)=3:已支付  4：已发货 5：已签收 6:已签订合同(商户待发货) 7:订单关闭 8:已退货（POS）',
 *   `is_tax_inclusive` tinyint(0) DEFAULT NULL COMMENT '是否含税:\r\n0: 不含税      1:含税',
 *   `charge` decimal(10, 2) DEFAULT NULL COMMENT '订单总金额',
 *   `create_time` datetime(0) DEFAULT NULL COMMENT '创建时间',
 *   `accept_time` datetime(0) DEFAULT NULL COMMENT '供应方接单时间',
 *   `association_contract` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '关联合同号',
 *   `scene` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '场景id(区分不同业务场景):\r\nB2B 大消费场景',
 *   `is_delete` tinyint(1) UNSIGNED ZEROFILL DEFAULT 0 COMMENT '是否已删除:\r\n0:未删除\r\n1:已删除',
 *   `field1` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '备用字段1 ',
 *   `partner_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '区域编码',
 *   `channel_source` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '权益商城： GRJY(贵人家园) 、WXXCX（微信小程序）、WXGZH（微信公众号） 停车场： GRJY(贵人家园) 、WXZF（微信支付）',
 *   PRIMARY KEY (`order_id`) USING BTREE
 * ) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QianNanOrder implements Comparable<QianNanOrder>{
    private String orderId;
    private String treatShopId;
    private String customerId;
    private Integer status;
    private Integer isTaxInclusive;
    private Double charge;
    private String createTime;
    private String acceptTime;
    private String associationContract;
    private String scene;
    private Integer isDelete;
    private String field1;
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
                ", associationContract='" + associationContract + '\'' +
                ", scene='" + scene + '\'' +
                ", isDelete=" + isDelete +
                ", field1='" + field1 + '\'' +
                ", partnerId='" + partnerId + '\'' +
                ", channelSource='" + channelSource + '\'' +
                '}';
    }

    @Override
    public int compareTo(QianNanOrder o) {
        return (createTime.compareTo(o.getCreateTime()));
    }
}