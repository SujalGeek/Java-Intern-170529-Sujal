package com.spring.orm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.spring.orm.dao.StudentDao;
import com.spring.orm.entities.Student;

public class App {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ApplicationContext context = new ClassPathXmlApplicationContext("com/spring/orm/config.xml");
		StudentDao studentDao = context.getBean("studentDao",StudentDao.class);
		
		/*
		 * Student student = new Student(110,"Sujal Morwani","MP"); int r =
		 * studentDao.insert(student); System.out.println("added..."+r);
		 */
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean go=true;
		while(go)
		{
		System.out.println("Press 1 for add new Student");
		System.out.println("Press 2 for display all students");
		System.out.println("Press 3 for get detail of single student");
		System.out.println("Press 4 for delete students");
		System.out.println("Press 5 for update students");
		System.out.println("Press 6 for exit");
		
		
		try {
			int input = Integer.parseInt(br.readLine());
			switch(input)
			{
			case 1:
				// add a new student
				
				System.out.println("Enter Student ID:");
				int id = Integer.parseInt(br.readLine());
				
				System.out.println("Enter the Student Name");
				String name = br.readLine();
				
				System.out.println("Enter the Student city");
				String city = br.readLine();
				
				Student st = new Student(id,name,city);
				int r = studentDao.insert(st);
				System.out.println("Student added successfully with ID: "+r);
			
				break;
			case 2:
				// display all student
				List<Student> students = studentDao.getAllStudents();
				System.out.println("----- All Students ----");
				for(Student s: students)
				{
					System.out.println("ID: "+s.getStudentId()+" Name: "+s.getStudentName()+" City: "+s.getStudentCity());
				}
				
				break;
			case 3:
				// get single student data
				
				System.out.println("Enter the Student ID:");
				int sid = Integer.parseInt(br.readLine());
				Student std = studentDao.getStudent(sid);
				if( std != null)
				{
					System.out.println("ID: " + std.getStudentId()+ " Name: "+ std.getStudentName() + " City: "+std.getStudentCity());
				}
				else {
					System.out.println("No Student found with ID "+sid);
				}
				break;
			case 4:
				// delete student
				
				System.out.println("Enter the StudentId to delete");
				int studentId = Integer.parseInt(br.readLine());
				studentDao.deleteStudent(studentId);
				System.out.println("Student with Id "+studentId+" deleted!!");
				break;
			case 5:
				// update the student
				System.out.println("Enter the StudentId to update: ");
				int uid = Integer.parseInt(br.readLine());
				
				System.out.println("Enter the Student Name: ");
				String uname = br.readLine();
				
				System.out.println("Enter the Student City: ");
				String ucity = br.readLine();
				
				Student updateStudent = new Student(uid,uname,ucity);
				studentDao.updateStudent(updateStudent);
				System.out.println("Student with Id "+uid+" is updated.");
				break;
			case 6:
				go=false;
				break;
			default:
				System.out.println("Invalid choice! Try again.");
				break;
			}
		}
		
		catch(Exception e)
		{
			System.out.println("Invalid Input Try with other one!!");
//			e.printStackTrace();
			e.getMessage();
		}
		}
		System.out.println("Thank you for using my Application");
		System.out.println("See you soon!!");
	}

}
