package com.spring.jdbc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.spring.jdbc.dao.StudentDao;
import com.spring.jdbc.dao.StudentDaoImpl;

@Configuration
public class JdbcConfig {

	@Bean("ds")
	public DriverManagerDataSource getDataSource() {
		DriverManagerDataSource ds1 = new DriverManagerDataSource();
		ds1.setDriverClassName("com.mysql.cj.jdbc.Driver");
		ds1.setUrl("jdbc:mysql://localhost:3306/springjdbc");
		ds1.setUsername("root");
		ds1.setPassword("Waheguru\"071!");
		return ds1;
	}
	
	@Bean("jdbcTemplate")
	public JdbcTemplate getJdbcTemplate() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(getDataSource());
		return jdbcTemplate;
	}
	
	@Bean("studentDao")
	public StudentDao getStudentDao() {
		StudentDaoImpl studentDao = new StudentDaoImpl();
		studentDao.setJdbcTemplate(getJdbcTemplate());
		return studentDao;
	}
}
