package com.tech.blog.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tech.blog.entities.Category;
import com.tech.blog.entities.Post;

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

public boolean savePost(Post p)
{
	boolean f=false;

	try{
		
	String query = "insert into posts(pTitle,pContent,pCode,pPic,catId,userId) values (?,?,?,?,?,?)";
	PreparedStatement statement = conn.prepareStatement(query);
	statement.setString(1, p.getpTitle());
	statement.setString(2, p.getpContent());
	statement.setString(3, p.getpCode());
	statement.setString(4, p.getpPic());
	statement.setInt(5, p.getCatId());
	statement.setInt(6, p.getUserId());
	
	statement.executeUpdate();
	f=true;
	}
	
	catch(Exception e) {
		e.printStackTrace();
	}
	
	
	return f;
}

public List<Post> getAllPosts(){
	List<Post> list = new ArrayList<>();
	try {
	
		PreparedStatement psmt = conn.prepareStatement("select * from posts");
		
		ResultSet resultset = psmt.executeQuery();
		
		while(resultset.next())
		{
			int pid = resultset.getInt("pid");
			String pTitle = resultset.getString("pTitle");
			String pContent = resultset.getString("pContent");
			String pCode = resultset.getString("pCode");
			String pPic = resultset.getString("pPic");
			Timestamp date = resultset.getTimestamp("pDate");
			int catId = resultset.getInt("catId");
			int userId = resultset.getInt("userId");
			Post post = new Post(pid,pTitle,pContent,pCode,pPic,date,catId,userId);
			
			list.add(post);
			
		}	
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	
	return list;
}
public List<Post> getPostByCatId(int catId)
{
	List<Post> list = new ArrayList<>();
	try {
		
		PreparedStatement psmt = conn.prepareStatement("select * from posts where catId = ?");
		psmt.setInt(1, catId);
		ResultSet resultset = psmt.executeQuery();
		
		while(resultset.next())
		{
			int pid = resultset.getInt("pid");
			String pTitle = resultset.getString("pTitle");
			String pContent = resultset.getString("pContent");
			String pCode = resultset.getString("pCode");
			String pPic = resultset.getString("pPic");
			Timestamp date = resultset.getTimestamp("pDate");
			int userId = resultset.getInt("userId");
			Post post = new Post(pid,pTitle,pContent,pCode,pPic,date,catId,userId);
			list.add(post);
		}	
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return list;
}

public Post getPostById(int pid)
{
Post post = null;
try {
	
	PreparedStatement psmt = conn.prepareStatement("select * from posts where pid=?");
	psmt.setInt(1, pid);
	ResultSet rst = psmt.executeQuery();
	if(rst.next())
	{
		String pTitle = rst.getString("pTitle");
		String pContent = rst.getString("pContent");
		String pCode = rst.getString("pCode");
		String pPic = rst.getString("pDate");
		Timestamp date = rst.getTimestamp("pDate");
		int catId = rst.getInt("catId");
		int userId = rst.getInt("userId");
		
		post = new Post(pid,pTitle,pContent,pCode,pPic,date,catId,userId);
	}
}
catch(Exception e)
{
	e.printStackTrace();
}
return post;
}

public boolean addLike(int pid,int userId)
{
try {
	PreparedStatement psmt = conn.prepareStatement("select * from likes where postId = ? and userId = ?");
	psmt.setInt(1, pid);
	psmt.setInt(2, userId);
	
	ResultSet rst = psmt.executeQuery();
	
	if(rst.next())
	{
		return false;
	}
	
	PreparedStatement psmt2 = conn.prepareStatement("insert into likes (postId,userId) values (?,?)");
	psmt2.setInt(1, pid);
	psmt2.setInt(2, userId);
	psmt2.executeUpdate();
	return true;
}

catch(Exception e)
{
	e.printStackTrace();
}
return false;
}

public int countLikes(int pid)
{
	int count=0;
	try {
		PreparedStatement ps = conn.prepareStatement("select count(*) from likes where postId = ?");
		ps.setInt(1, pid);
		ResultSet rst3 = ps.executeQuery();
		if(rst3.next())
		{
			count = rst3.getInt(1);
		}
		}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return count;
}

public boolean addComment(int pid,int userId,String content)
{
	try {
		PreparedStatement psmt = conn.prepareStatement("insert into comments(postId, userId, content) values (?,?,?)");
		psmt.setInt(1, pid);
		psmt.setInt(2, userId);
		psmt.setString(3, content);
		psmt.executeUpdate();
		return true;
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	return false;
}

public List<Map<String,String>> getComments(int pid)
{
List<Map<String,String>> comments = new ArrayList<>();
try {
	
	PreparedStatement ps = conn.prepareStatement("SELECT c.content, c.commentDate, u.name\r\n"
			+ "FROM comments c\r\n"
			+ "JOIN user u ON c.userId = u.id\r\n"
			+ "WHERE c.postId = ?\r\n"
			+ "ORDER BY c.commentDate DESC\r\n"
			+ "");
	ps.setInt(1, pid);
	ResultSet rs = ps.executeQuery();
	while(rs.next())
	{
		Map<String,String> map1 = new HashMap<>();
		map1.put("name", rs.getString("name"));
		map1.put("content", rs.getString("content"));
		map1.put("date", rs.getTimestamp("commentDate").toString());
		comments.add(map1);
	}
}
catch(Exception e)
{
	e.printStackTrace();
}
return comments;
}
}

