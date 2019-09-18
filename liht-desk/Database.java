package com.dbcool.api.refresh.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
	private static final String jdbc = "jdbc:mysql://t-beijingrds.cohsevfejepo.rds.cn-north-1.amazonaws.com.cn:3306/dbcool?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull";
	private static final String username = "dbcool";
	private static final String password = "12345678";
	
	/**
	 * 查
	 * @param sql
	 * @return
	 */
	public static List<Map<String , Object>> getEntity(String sql){
		ResultSet rs = null;
		List<Map<String , Object>> list = new ArrayList<>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Connection con = DriverManager.getConnection(jdbc, username,password);
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData md = rs.getMetaData();
			int columnCount = md.getColumnCount();
			while (rs.next()) {
				Map<String, Object> rowData = new HashMap<>();  
				for (int i = 1 ; i <= columnCount; i++) {
					rowData.put(md.getColumnLabel(i),rs.getObject(i));
				}
				list.add(rowData);
			}
			rs.close();
			stmt.close();
			con.close();
		} catch (SQLException se) {
			System.out.println("数据库连接失败！");
			se.printStackTrace();
		}

		return list;
	}
	
	/**
	 * 增
	 * @param sql
	 */
	public static void updateEntity(String sql){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Connection con = DriverManager.getConnection(jdbc, username,password);
			Statement stmt = con.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
			con.close();
		} catch (SQLException se) {
			System.out.println("数据库连接失败！");
			se.printStackTrace();
		}
	}
	
	/**
	 * 插入
	 * @param sql
	 */
	public static void insertEntity(String sql){
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Connection con = DriverManager.getConnection(jdbc, username,password);
			Statement stmt = con.createStatement();
			stmt.execute(sql);
			stmt.close();
			con.close();
		} catch (SQLException se) {
			System.out.println("数据库连接失败！");
			se.printStackTrace();
		}
	}

}
