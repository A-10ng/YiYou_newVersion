package com.example.yiyou_newversion.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * author：LongSh1z
 * email：2674461089@qq.com
 * time：2019/07/17
 * desc:
 */
public class DBHelper {
    public static Connection connect() {
        String driverClassName = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://120.78.219.119:3306/TeamMaker?useUnicode=true&characterEncoding=utf8";

        try {
            Class.forName(driverClassName);
            Properties pro = new Properties();

            Connection con = DriverManager.getConnection(url, "root", "EDB0bd8cb80d");

            return con;
        } catch (Exception ex) {
            System.out.println("  " + ex.toString());
            return null;
        }
    }

    public static void closeResult(ResultSet rs) {
        try {
            rs.close();
        } catch (SQLException e) {
        }
    }

    public static void closePreparedStatement(PreparedStatement ps) {
        try {
            ps.close();
        } catch (SQLException e) {
        }
    }

    public static void closeConneciton(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
        }
    }
}
