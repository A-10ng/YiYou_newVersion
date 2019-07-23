package com.example.yiyou_newversion.model;

import android.util.Log;

import com.example.yiyou_newversion.bean.Team;
import com.example.yiyou_newversion.bean.User;
import com.example.yiyou_newversion.utils.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * author：LongSh1z
 * email：2674461089@qq.com
 * time：2019/07/17
 * desc:
 */
public class Data {
    private static final String TAG = "Data";
    public static String CurrenUserPhoneNum = "none";
    public static String CurrenUserUsername = "none";
    public static String CurrentTeamName = "none";
    public static String CurrentRoute = "none";

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

    public List<Team> getCurTeamsInTourist(){
        List<Team> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stat = null;
        con = DBHelper.connect();
        String sql = "select * from Team where teamatePhoneNum = ? order by teamId desc";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1, Data.CurrenUserPhoneNum);
            ResultSet rs = stat.executeQuery();
            while (rs.next()){
                Team team = new Team();
                team.setTeamName(rs.getString("teamName"));
                list.add(team);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBHelper.closePreparedStatement(stat);
            DBHelper.closeConneciton(con);
        }
        return list;
    }

    public List<Team> getCurTeamsInGuide(){
        List<Team> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stat = null;
        con = DBHelper.connect();
        String sql = "select * from Team where guidePhoneNum = ? and teamatePhoneNum = ? order by teamId desc";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1, Data.CurrenUserPhoneNum);
            stat.setString(2, "0");
            ResultSet rs = stat.executeQuery();
            while (rs.next()){
                Team team = new Team();
                team.setTeamName(rs.getString("teamName"));
                list.add(team);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBHelper.closePreparedStatement(stat);
            DBHelper.closeConneciton(con);
        }
        return list;
    }

    public List<Object> joinTeamByTeamCode(String teamCode){
        List<Object> list = new ArrayList<>();
        int rows = 0;
        Connection con = null;
        PreparedStatement stat = null;
        con = DBHelper.connect();
        String sql1 = "select * from Team where teamCode = ?";
        String sql2 = "insert into Team(guidePhoneNum,travelDate,teamName,teamIntro,teamatePhoneNum,teamCode,qrCode) " +
                "values(?,?,?,?,?,?,?)";

        try {
            stat = con.prepareStatement(sql1);
            stat.setString(1,teamCode);
            ResultSet rs = stat.executeQuery();
            //先根据队伍码判断有没有该队伍
            if (rs.next()){
                //有的话list的第一个元素就放true
                list.add(true);
                PreparedStatement stat2 = con.prepareStatement(sql2);
                stat2.setString(1,rs.getString("guidePhoneNum"));
                stat2.setString(2,rs.getString("travelDate"));
                stat2.setString(3,rs.getString("teamName"));
                stat2.setString(4,rs.getString("teamIntro"));
                stat2.setString(5,Data.CurrenUserPhoneNum);
                stat2.setString(6,rs.getString("teamCode"));
                stat2.setBytes(7,rs.getBytes("qrCode"));
                rows = stat2.executeUpdate();
                //如果加入队伍成功就将list的第二个元素放1，否则为0
                if (rows == 1){
                    list.add(1);
                }else {
                    list.add(0);
                }
            }else {
                list.add(false);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBHelper.closePreparedStatement(stat);
            DBHelper.closeConneciton(con);
        }
        return list;
    }

    public List<Team> findTeamsByTeamCode(String teamCode) {
        List<Team> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stat = null;
        con = DBHelper.connect();
        String sql = "select * from Team where teamCode = ?";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1,teamCode);
            ResultSet rs = stat.executeQuery();
            while (rs.next()){
                Team team = new Team();
                team.setTeamCode(rs.getString("teamCode"));
                list.add(team);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBHelper.closePreparedStatement(stat);
            DBHelper.closeConneciton(con);
        }
        return list;
    }

    public boolean crtTeamSuccessful(String teamName, String travelDate, String teamIntro,
                                     String teamCode, String teamatePhoneNum, byte[] qrCode) {
        int result = 0;
        Connection con = null;
        PreparedStatement stat = null;
        con = DBHelper.connect();
        String sql = "insert into Team (guidePhoneNum,teamName, travelDate, teamIntro, teamCode, teamatePhoneNum, qrCode) " +
                "values(?,?,?,?,?,?,?)";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1,Data.CurrenUserPhoneNum);
            stat.setString(2,teamName);
            stat.setString(3,travelDate);
            stat.setString(4,teamIntro);
            stat.setString(5,teamCode);
            stat.setString(6,teamatePhoneNum);
            stat.setBytes(7,qrCode);
            result = stat.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBHelper.closePreparedStatement(stat);
            DBHelper.closeConneciton(con);
        }
        if (result == 1){
            return true;
        }else {
            return false;
        }
    }

    public User getCurUserInfo() {
        User user = null;
        Connection con = null;
        PreparedStatement stat = null;

        con = DBHelper.connect();
        String sql = "select * from User where phoneNum = ?";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1,Data.CurrenUserPhoneNum);
            ResultSet rs = stat.executeQuery();
            if (rs.next()){
                user = new User();
                user.setUsername(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean isUpdateTeamIntroSuccessful(String teamIntro) {
        int result = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        connection = DBHelper.connect();
        String sql = "update Team set teamIntro = ? where teamName = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,teamIntro);
            statement.setString(2,Data.CurrentTeamName);
            result = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (result == 1){
            return true;
        }else {
            return false;
        }
    }

    public Team getCurrentTeamInGuideFun() {
        Team team = null;
        PreparedStatement statement = null;
        Connection connection = DBHelper.connect();
        String sql = "select * from Team where teamName = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,Data.CurrentTeamName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                team = new Team();
                team.setTeamIntro(rs.getString("teamIntro"));
                team.setTravelDate(rs.getString("travelDate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return team;
    }

    public User getCurGuideInGuideFun() {
        User user = null;
        PreparedStatement statement = null;
        Connection connection = DBHelper.connect();
        String sql = "select * from User where phoneNum = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,Data.CurrenUserPhoneNum);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                user = new User();
                user.setUsername(rs.getString("username"));
                user.setAvatar(rs.getBytes("avatar"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<Object> getCurTeamAndGuideInTourFun() {
        List<Object> list = new ArrayList<>();
        Team team = null;
        User user = null;
        PreparedStatement statement = null;
        Connection connection = DBHelper.connect();
        String sql1 = "select * from Team where teamName = ?";
        String sql2 = "select * from User where phoneNum = ?";


        try {
            statement = connection.prepareStatement(sql1);
            statement.setString(1,Data.CurrentTeamName);
            ResultSet rs = statement.executeQuery();
            if (rs.next()){
                team = new Team();
                team.setGuidePhoneNum(rs.getString("guidePhoneNum"));
                team.setTravelDate(rs.getString("travelDate"));
                team.setTeamIntro(rs.getString("teamIntro"));
                list.add(team);
                PreparedStatement statement1 = connection.prepareStatement(sql2);
                statement1.setString(1,team.getGuidePhoneNum());
                ResultSet resultSet = statement1.executeQuery();
                if (resultSet.next()){
                    user = new User();
                    user.setUsername(resultSet.getString("username"));
                    user.setAvatar(resultSet.getBytes("avatar"));
                    list.add(user);
                }
            }else {
                list.add(null);
                list.add(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Team> getCurrentTeam() {
        List<Team> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stat = null;
        con = DBHelper.connect();
        String sql = "select * from Team where teamName = ? and teamatePhoneNum != ?";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1,Data.CurrentTeamName);
            stat.setString(2,"0");
            ResultSet rs = stat.executeQuery();
            while (rs.next()){
                Team team = new Team();
                team.setTeamatePhoneNum(rs.getString("teamatePhoneNum"));
                list.add(team);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBHelper.closePreparedStatement(stat);
            DBHelper.closeConneciton(con);
        }
        return list;
    }

    public List<Team> getNoPeopleTeam() {
        List<Team> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stat = null;
        con = DBHelper.connect();
        String sql = "select * from Team where teamName = ? and teamatePhoneNum = ?";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1,Data.CurrentTeamName);
            stat.setString(2,"0");
            ResultSet rs = stat.executeQuery();
            while (rs.next()){
                Team team = new Team();
                team.setTeamCode(rs.getString("teamCode"));
                team.setQrCode(rs.getBytes("qrCode"));
                list.add(team);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            DBHelper.closePreparedStatement(stat);
            DBHelper.closeConneciton(con);
        }
        return list;
    }

    public List<User> getCurUsersInfo(List<Team> teams) {
        List<User> list = new ArrayList<>();
        User user = null;
        Connection con = null;
        PreparedStatement stat = null;

        con = DBHelper.connect();
        String sql = "select * from User where phoneNum = ?";

        try {
            stat = con.prepareStatement(sql);
            stat.setString(1,teams.get(0).getTeamatePhoneNum());
            ResultSet rs = stat.executeQuery();
            while (rs.next()){
                user = new User();
                user.setUsername(rs.getString("username"));
                user.setPhoneNum(rs.getString("phoneNum"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteTeamSuccessful() {
        int result = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        connection = DBHelper.connect();
        String sql = "delete from Team where teamName = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,Data.CurrentTeamName);
            result = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (result == 1)
            return true;
        else
            return false;
    }

    public boolean QuitThisTeam() {
        int result = 0;
        Connection connection = null;
        PreparedStatement statement = null;
        connection = DBHelper.connect();
        String sql = "delete from Team where teamatePhoneNum = ? and teamName = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,Data.CurrenUserPhoneNum);
            statement.setString(2,Data.CurrentTeamName);
            result = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (result == 1)
            return true;
        else
            return false;
    }
}
