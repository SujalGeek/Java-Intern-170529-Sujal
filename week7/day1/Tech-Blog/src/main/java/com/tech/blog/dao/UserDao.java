package com.tech.blog.dao;
import java.sql.*;

import com.tech.blog.entities.User;

public class UserDao {
	private Connection conn;

	public UserDao(Connection conn) {
		super();
		this.conn = conn;
	}
	
	// method to insert into base;
	
	public boolean saveUser(User user) {
	    boolean f = false;
	    try {
	        String query = "insert into user(name,email,password,gender,about) values (?,?,?,?,?)";
	        PreparedStatement pstmt = this.conn.prepareStatement(query);
	        pstmt.setString(1, user.getName());
	        pstmt.setString(2, user.getEmail());
	        pstmt.setString(3, user.getPassword());
	        pstmt.setString(4, user.getGender());
	        pstmt.setString(5, user.getAbout());
	        
	        int rows = pstmt.executeUpdate();
	        if(rows > 0) {
	            f = true; // now it will return true if insert is successful
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	    return f;
	}
}

