package com.springmvc.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.springmvc.model.User;

@Repository
public class UserDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Transactional
	public int saveUser(User user)
	{
		Session s = this.sessionFactory.getCurrentSession();
		Integer r = (Integer)s.save(user);
		return r;
	}
}