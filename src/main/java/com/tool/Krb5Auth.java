package com.tool;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;

import java.sql.*;

public class Krb5Auth {

    private static String DRIVER = "org.apache.hive.jdbc.HiveDriver";
    private static Connection conn;
    private static String USERNAME = "";
    private static String PASSWORD = "";
    private static Statement stat = null;
    private static ResultSet rs = null;
    private static String krbconf = "/ietl/krb5.conf";
    private static String keytab = "/ietl/gz_yl_pro_01.keytab";


    public static void main(String[] args) {
//        String principal = "e3base@EXAMPLE.COM";
//        String KeytabPath = "resource/e3base.keytab";
//        String URL = "jdbc:hive2://hbe3base09:15101/;principal=hive/hbe3base09@EXAMPLE.COM";
//        try {
//            Class.forName(DRIVER);
//            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            stat = conn.createStatement();
//            String sql = "show databases";
//            //PreparedStatement preparedStatement = conn.prepareStatement(sql);
//            //GetPreDriverInfo getPreDriverInfo = new GetPreDriverInfo();
//            //System.out.println(getPreDriverInfo);
//            rs = stat.executeQuery(sql);
//            while (rs.next()) {
//                System.out.println(rs.getString(1));
//            }
//            System.out.println("Show database successfully.");
//        } catch (SQLException e) {
//            System.out.println("Failed show data base.");
//        }
    }

    public static void getKerberosAuth() {
        Configuration conf = new Configuration();
        System.setProperty("java.security.krb5.conf", krbconf);
        conf.setBoolean("hadoop.security.authorization", true);
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hive.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab("gz_yl_pro_01@GZMEDICAL.COM",keytab );
            System.out.println("Kerberos 身份认证成功");
        } catch (Exception e) {
            System.out.println("Kerberos 身份认证失败：" + e);
        }
    }
}
