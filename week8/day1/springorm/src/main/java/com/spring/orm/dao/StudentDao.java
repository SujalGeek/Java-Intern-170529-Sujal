package com.spring.orm.dao;

import java.util.List;

import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.spring.orm.entities.Student;

public class StudentDao {

	private HibernateTemplate hibernateTemplate;
	
	@Transactional
	public int insert(Student student)
	{
		Integer i = (Integer) this.hibernateTemplate.save(student);
		return i;
	}
	
	public Student getStudent(int studentID)
	{
		Student student = this.hibernateTemplate.get(Student.class, studentID);
		return student;
	}

	//get all students(all rows)
	public List<Student> getAllStudents()
	{
		List<Student> students = this.hibernateTemplate.loadAll(Student.class);
		return students;
	}
	
	// deleting the rows
	public void deleteStudent(int studentId)
	{
		Student student = this.hibernateTemplate.get(Student.class, studentId);
		this.hibernateTemplate.delete(student);
	}
	
	// updating the data.....
	
	public void updateStudent(Student student)
	{
		this.hibernateTemplate.update(student);
	}
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	
}
