package com.spring.jdbc;

import java.util.List;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.spring.jdbc.dao.StudentDao;
import com.spring.jdbc.entities.Student;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
//        ApplicationContext context = new ClassPathXmlApplicationContext("com/spring/jdbc/config.xml");
        
        ApplicationContext context = new AnnotationConfigApplicationContext(JdbcConfig.class);   
        StudentDao studentDao = context.getBean("studentDao",StudentDao.class);
		
        // to create 
        /*
		 * Student student = new Student(); 
		 * student.setId(107);
		 * student.setName("Niraj"); 
		 * student.setCity("Indore");
		 * 
		 * int resutl = studentDao.insert(student);
		 * System.out.println("student added "+resutl);
		 */
        
        
        // to update 
		/*
		 * Student student1 = new Student(); 
		 * student1.setId(106);
		 * student1.setName("Rohan Kapur"); 
		 * student1.setCity("Bhopal");
		 * 
		 * int updated = studentDao.change(student1);
		 * System.out.println("student updated..."+updated);
		 */
        
// 		to delete
//        Scanner sc = new Scanner(System.in);
//        int id = sc.nextInt();
//       int scannerDelete = studentDao.delete(id);
//        System.out.println("student deleted using scanner class object: "+scannerDelete);
        // sc.close();
        
//       Student student2 = new Student();
//		/* student2.setId(107); */
//       int deleted = studentDao.delete(107);
//       System.out.println("student deleted.. "+deleted);
       
// 		get all the records (reading all the records)
        Student studentInfo = studentDao.getStudent(107);
        System.out.println("Student information..."+studentInfo);
    
        
        System.out.println("Getting all the students");
       List<Student> students =  studentDao.getAllStudents();
       for(Student stud : students)
       {
    	   System.out.println(stud);
       }
    }
}
