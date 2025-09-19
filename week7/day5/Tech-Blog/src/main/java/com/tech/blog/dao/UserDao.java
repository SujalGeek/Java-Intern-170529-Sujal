package com.tech.blog.dao;
import java.sql.*;


import com.tech.blog.entities.User;
import com.tech.blog.helper.PasswordHasher;

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
	// get user by email and password
	
	public User getUserbyEmailAndPassword(String email, String password) {
	    User user = null;
	    try {
	        String query = "SELECT * FROM user WHERE email = ? AND password = ?";
	        PreparedStatement psmt = conn.prepareStatement(query);
	        psmt.setString(1, email);
	        psmt.setString(2, password);  // already hashed in LoginServlet

	        ResultSet resultSet = psmt.executeQuery();
	        if (resultSet.next()) {
	            user = new User();
	            user.setId(resultSet.getInt("id"));
	            user.setName(resultSet.getString("name"));
	            user.setEmail(resultSet.getString("email"));
	            user.setPassword(resultSet.getString("password"));
	            user.setGender(resultSet.getString("gender"));
	            user.setAbout(resultSet.getString("about"));
	            user.setDateTime(resultSet.getTimestamp("registration"));
	            user.setProfile(resultSet.getString("profile"));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return user;
	}
	public boolean updateUser(User user) {
	    boolean f = false;
	    try {
	        String query = "update user set name = ? , email = ? , password = ? , gender = ? , about = ? , profile = ? where id = ?";
	        PreparedStatement p = conn.prepareStatement(query);

	        p.setString(1, user.getName());
	        p.setString(2, user.getEmail());
	        p.setString(3, user.getPassword()); // already hashed in EditServlet
	        p.setString(4, user.getGender());
	        p.setString(5, user.getAbout());
	        p.setString(6, user.getProfile());
	        p.setInt(7, user.getId());

	        int rows = p.executeUpdate();
	        if (rows > 0) f = true;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return f;
	}
}

