package com.example.yiyou_newversion.model;

import android.util.Log;

import com.example.yiyou_newversion.bean.User;
import com.example.yiyou_newversion.utils.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author：LongSh1z
 * email：2674461089@qq.com
 * time：2019/07/17
 * desc:
 */
public class Data {
    private static final String TAG = "Data";
    public static String CurrenUerPhoneNum = "123";
    public static String CurrenUerUsername = "123";

    public User getUserByPhoneNumInLogin(String phoneNum){
        User user = null;
		Connection con = null;
		PreparedStatement stat = null;
		con = DBHelper.connect();
		String sql = "select * from User where phoneNum=?";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1, phoneNum);
            ResultSet rs = stat.executeQuery();
            if (rs.next()){
                user = new User();
                user.setUsername(rs.getString("username"));
                user.setPhoneNum(rs.getString("phoneNum"));
                user.setPassword(rs.getString("password"));
                user.setIdentity(rs.getInt("identity"));
                user.setGender(rs.getInt("gender"));
                user.setCompany(rs.getString("company"));
                user.setAvatar(rs.getBytes("avatar"));
            }else {
                return null;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBHelper.closePreparedStatement(stat);
            DBHelper.closeConneciton(con);
        }
        return user;
    }

    public boolean hasThisPhoneNum(String phoneNum){
        boolean result = false;
        Connection con = null;
        PreparedStatement stat = null;
        con = DBHelper.connect();
        String sql = "select * from User where phoneNum=?";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1, phoneNum);
            ResultSet rs = stat.executeQuery();
            if (rs.next()){
                result = true;
            }else {
                result = false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBHelper.closePreparedStatement(stat);
            DBHelper.closeConneciton(con);
        }
        return result;
    }

    public boolean isRegisterSuccessful(String username,String password, String company,
                                        int identity,int gender,byte[] avatar,String phoneNum){
        int rows = 0;
        Connection con = null;
        PreparedStatement stat = null;
        con = DBHelper.connect();
        String sql = "insert into User (username,phoneNum,gender,password,company,identity,avatar) " +
                "values(?,?,?,?,?,?,?)";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1, username);
            stat.setString(2, phoneNum);
            stat.setInt(3, gender);
            stat.setString(4, password);
            stat.setString(5, company);
            stat.setInt(6,identity);
            stat.setBytes(7,avatar);
            rows = stat.executeUpdate();
            Log.i(TAG, "isRegisterSuccessful: befor rows>>>"+rows);
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBHelper.closePreparedStatement(stat);
            DBHelper.closeConneciton(con);
        }
        Log.i(TAG, "isRegisterSuccessful: after rows>>>"+rows);
        if (rows == 1){
            return true;
        }else {
            return false;
        }
    }
}
