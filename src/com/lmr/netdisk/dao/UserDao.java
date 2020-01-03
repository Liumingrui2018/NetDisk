package com.lmr.netdisk.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import com.lmr.netdisk.model.User;

public class UserDao {
	
	private Connection conn;
	private ServletContext context;
	
	public UserDao(String dbUrl,String dbUser,String password,ServletContext context) throws SQLException {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn = DriverManager.getConnection(dbUrl, dbUser, password);
		this.context = context;
	}
	public boolean existUser(String userName) {
		String sql = "select * from Users where name=?";
		boolean exist = false;
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userName);
			ResultSet resultSet = statement.executeQuery();
			exist = resultSet.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return exist;
	}
	public boolean registerUser(User user) {
		boolean result = false;
		if(user==null)
			throw new IllegalArgumentException("Argument user must not equals null");
		String sql = "insert into Users values (?,?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, user.getName());
			statement.setString(2, user.getPassword());
			result = statement.executeUpdate()>0;
			if(result) {
				String root = context.getInitParameter("filesRoot");
				Path rootPath = Paths.get(root);
				Path userRoot = rootPath.resolve(user.getName());
				Files.createDirectories(userRoot);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public User login(String userName,String password) {
		String sql = "select * from Users where name=? and password=?";
		User user = null;
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userName);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();
			if(resultSet.next()) {
				user = new User();
				user.setName(resultSet.getString("name"));
				user.setPassword(resultSet.getString("password"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return user;
	}
	public boolean changePassword(String userName,String newPassword) {
		String sql = "update Users set password=? where name=?";
		boolean success = false;
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, newPassword);
			statement.setString(2,userName);
			int row = statement.executeUpdate();
			success = row>0;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}
}
