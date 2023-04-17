package com.mysql;


import com.bean.QianNanOrder;
import com.tool.MysqlUtil;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.UUID;

/**
 * @ClassName Generator
 * @Description TODO 实时往数据库中产生订单信息
 * @Author oyc
 * @Date 2023/4/4 10:54
 * @Version
 */
public class Generator {
    public static void main(String[] args) throws SQLException, InterruptedException {
        //销售方id
        String[] treat_shop_id = {"11001","11002","11003","11004","11005","11006","11007","11008","11009","110010"};
        //采购方id
        String[] customer_id = {"33001","33002","33003","33004","33005","33006","33007","33008","33009","330010"};
        String[] channel_source = {"GRJY(贵人家园)" ,"WXXCX（微信小程序）","WXGZH（微信公众号） 停车场",
                "GRJY(贵人家园)","WXZF（微信支付）"};

        String[] area_code = {"520102000000","520181000000","520122000000","520222000000"};


        Connection connection = MysqlUtil.connect();

        //准备数据
        String sql = "INSERT INTO `general_order` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        Random random = new Random();
        DecimalFormat df1 = new DecimalFormat("0.0000");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        connection.setAutoCommit(false);
        //实时写入数据
        int i =0;
        while (true) {
            i++;
            QianNanOrder qianNanOrder = new QianNanOrder();
            //订单编号
            preparedStatement.setString(1, UUID.randomUUID().toString());
            //销售方id
            preparedStatement.setString(2,treat_shop_id[random.nextInt(treat_shop_id.length)]);
            preparedStatement.setString(3,treat_shop_id[random.nextInt(customer_id.length)]);
            preparedStatement.setInt(4,random.nextInt(8)+1);
            preparedStatement.setInt(5,random.nextInt(2));
            preparedStatement.setDouble(6,Double.parseDouble(df1.format(random.nextDouble())) *1000);
            preparedStatement.setString(7, simpleDateFormat.format(new Date(System.currentTimeMillis())));
            preparedStatement.setString(8, simpleDateFormat.format(new Date(System.currentTimeMillis()+ random.nextInt(10000))));
            preparedStatement.setString(9,"");
            preparedStatement.setString(10, String.valueOf(random.nextInt(4))); // 四个场景
            preparedStatement.setInt(11,0);
            preparedStatement.setString(12,"");
            preparedStatement.setString(13,area_code[random.nextInt(area_code.length)]);
            preparedStatement.setString(14,channel_source[random.nextInt(channel_source.length)]);
            preparedStatement.execute();
            System.out.println("插入第"+i+"条数据");
            Thread.sleep(30000);
        }

    }

    @Test
    public void test() {
        Random random = new Random();
        DecimalFormat df1 = new DecimalFormat("0.0000");
        System.out.println(Double.parseDouble(df1.format(random.nextDouble())) *1000);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        System.out.println(simpleDateFormat.format(new Date(System.currentTimeMillis()-random.nextInt(10000))));
    }

}