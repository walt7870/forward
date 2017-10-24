package com.ntech.util;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ntech.model.LibraryKey;
import com.ntech.service.inf.ICustomerService;
import com.ntech.service.inf.ILibraryService;
import org.apache.log4j.Logger;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import org.springframework.beans.factory.annotation.Autowired;

public class Mysql {

	private static Logger logger  = Logger.getLogger(Mysql.class);

	private static Statement statement = null;
	static{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connection =  (Connection) DriverManager.getConnection(
					"jdbc:mysql://127.0.0.1:3306/test","root","ntech");
			statement = (Statement) connection.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> getGalleries(String inputToken) throws SQLException{
		List<String> galleryList = new ArrayList<String>();
		String sql = "select gallery from user where token='"+inputToken+"'";
			ResultSet resultSet = statement.executeQuery(sql);
			while(resultSet.next()) {
				String gallery = resultSet.getString("gallery");
				logger.info("user-hold-gallery: "+gallery);
				galleryList.add(gallery);
			}
		return galleryList;
	}

	public String getUserName(String inputToken) throws SQLException {
		String sql = "select userName from user where token='"+inputToken+"'";
		ResultSet resultSet = statement.executeQuery(sql);
		resultSet.next();
		return resultSet.getString("userName");
	}

	public boolean isToken(String inputToken) throws SQLException {
		boolean flag = false;
		String sql = "select count(*) from user where token='"+inputToken+"'";
		ResultSet resultSet = statement.executeQuery(sql);
		resultSet.next();
		int count = resultSet.getInt("count(*)");
		if(count!=0)
			flag = true;
		logger.info("sql: "+sql);
		logger.info("count(*): "+count);
		return flag;
	}

	public boolean createGallery(String inputToken,String userName,String galleryName) throws SQLException {

		String sql = "insert into `user` (token,userName,gallery) values('"+inputToken+"','"+userName+"','"+galleryName+"')";
		return statement.execute(sql);
	}
	public boolean deleteGallery(String galleryName) throws SQLException {
		
		String sql = "delete from user where gallery='"+galleryName+"'";
		return statement.execute(sql);
	}
	public boolean timesCount(String userName) throws SQLException {
		boolean flag = false;
		String sql1 = "update user set times=times+1 where userName='"+userName+"'";
		int affect1 =  statement.executeUpdate(sql1);
		String sql2 = "update user set balance=balance-1 where userName='"+userName+"'";
		int affect2 = statement.executeUpdate(sql2);
		if(affect1!=0&&affect2!=0)
			flag = true;
		return flag;
	}
}
