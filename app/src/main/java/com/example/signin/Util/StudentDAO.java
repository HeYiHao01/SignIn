package com.example.signin.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class StudentDAO {
    public static ResultSet query_stu(Connection con) throws SQLException {
        String sql="select * from account.student";
        PreparedStatement pstmt=con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        return rs;
    }

    public static void create_sign(Connection con,String table_name) throws SQLException {
        String sql = "create table "+table_name+" (\n" +
                "Sname varchar(8) primary key,\n" +
                "Sta varchar(2));";
        PreparedStatement ps = con.prepareStatement(sql);
        //ps.setString(1,table_name);
        ps.execute();
    }

    public static boolean insert_sign(Connection con, String table_name, ArrayList<String> name, HashMap<Integer,Boolean> hmap) throws SQLException {
        int i = 0;
        for (String stu_name:name){
            String sql="insert into "+table_name+" values(?,?);";
            PreparedStatement ps = con.prepareStatement(sql);
            //ps.setString(1,table_name);
            ps.setString(1,stu_name);
            if (hmap.get(i++)){
                ps.setString(2,"Y");
            }else {
                ps.setString(2,"N");
            }
            ps.execute();
        }
        return true;
    }

    public static boolean insert_date(Connection con,String date,String table_name) throws SQLException {
        String sql = "insert into date values(?,?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1,date);
        ps.setString(2,table_name);
        return ps.execute();
    }

    public static ResultSet query_date(Connection con) throws SQLException {
        String sql="select * from account.date";
        PreparedStatement pstmt=con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        return rs;
    }

    public static ResultSet query_count(Connection con,String table_name) throws SQLException{
        String sql = "select count(*) from "+table_name+" where Sta = 'Y'";
        PreparedStatement ps = con.prepareStatement(sql);
        //ps.setString(1,table_name);
        ResultSet rs = ps.executeQuery();
        if (rs.next()){
            return rs;
        }else {
            return null;
        }
    }
}
