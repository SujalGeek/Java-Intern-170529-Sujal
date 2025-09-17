package com.tech.blog.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.tech.blog.entities.Category;

public class PostDao {
Connection conn;

public PostDao(Connection conn) {
	super();
	this.conn = conn;
}

public ArrayList<Category> getAllCategories(){
	ArrayList<Category> list = new ArrayList<>();
	try {
		String query = "select * from categories";
		Statement st = conn.createStatement();
		ResultSet rst = st.executeQuery(query);
		while(rst.next())
		{
			int cid= rst.getInt("catId");
			String name=rst.getString("catName");
			String description = rst.getString("description");
			Category c = new Category(cid,name,description);
			list.add(c);
		}
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return list;
}

}
