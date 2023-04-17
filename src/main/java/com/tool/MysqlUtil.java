package com.tool;

import java.sql.*;

/**
 * @ClassName OjdbcUtil
 * @Description TODO
 * @Author oyc
 * @Date 2022/8/11 21:03
 * @Version
 */
public class MysqlUtil {
    static {
        try {
            // 注册驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // 第二步： 获取与oracle的连接 Connection
    static String url = "jdbc:mysql://xxxx:3306/flink_cdc?characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    static String user = "root";
    static String password = "1qazZAQ!";


    // localhost
    static String local_url = "jdbc:mysql://localhost:3306/test?characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    static String local_user = "root";
    static String local_password = "1qazZAQ!";


    static String qiannan_url = "jdbc:mysql://xxxx:3306/zczqdb?characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    static String qiannan_user = "qn_zczq";
    static String qiannan_password = "Qn_zczq@2022";


    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(qiannan_url, qiannan_user, qiannan_password);
    }

    public static void closeAll(ResultSet resultSet, PreparedStatement stmt, Connection conn) {
        System.out.println("关闭资源");
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException sqlex) {
                // ignore, as we can't do anything about it here
            }
            resultSet = null;
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException sqlex) {
                // ignore, as we can't do anything about it here
            }
            stmt = null;
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException sqlex) {
                // ignore, as we can't do anything about it here
            }
            conn = null;
        }
    }

}