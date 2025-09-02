import java.sql.*;
import java.util.Scanner;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    // For Security point of view these private static and final are important

    private static final String url ="jdbc:mysql://127.0.0.1:3306/mydb1";
    // Why static because do need to the object to call the variable and use them
    // but by using the static by using the only the class name it is there
    // Do not need to change the password so there we can use the final keyword

    private static final String username = "root";
    private static final String password = "Waheguru\"071!";



    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Hello and welcome!");

        System.out.println("Performed the CRUD operations with help of Statements:- ");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            String query = "select * from students";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next())
            {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                double marks = resultSet.getDouble("marks");
                System.out.println("ID: "+id);
                System.out.println("Name: "+name);
                System.out.println("Age: "+age);
                System.out.println("Marks: "+marks);

            }

//            String query2 = String.format("insert into students(name,age,marks) values('%s',%o,%f)","Rahul",21,57.5);
//            int affectedrows = statement.executeUpdate(query2);
//            if(affectedrows > 0)
//            {
//                System.out.println("Data inserted Successfully");
//            }
//            else{
//                System.out.println("Data Not inserted");
//            }
//
//            String query3 = String.format("UPDATE students set marks = %f where id = %d",75.6,2);
//
//            int rowsAffeceted = statement.executeUpdate(query3);
//            if(rowsAffeceted> 0)
//            {
//                System.out.println("Data Updated Successfully");
//            }
//            else{
//                System.out.println("Data Not updated");
//            }

//            String query4 = "delete from students where id = 6";
//            int rowsAffected = statement.executeUpdate(query4);
//            if(rowsAffected > 0)
//            {
//                System.out.println("Data deleted successfully");
//            }
//            else{
//                System.out.println("Data not deleted");
//            }

            // Why to use the PreparedStatement instead of Statement
//            String query5 = "INSERT INTO students(name,age,marks) values(?,?,?)";
//            PreparedStatement preparedStatement = connection.prepareStatement(query5);
//            preparedStatement.setString(1,"Mohan");
//            preparedStatement.setInt(2,23);
//            preparedStatement.setDouble(3,67.7);
//            int rowsAffected1 = preparedStatement.executeUpdate();
//            if(rowsAffected1 > 0)
//            {
//                System.out.println("Data is updated successfully");
//            }
//            else{
//                System.out.println("Data is not updated");
//            }
//            PreparedStatement preaparedstatment23 = connection.prepareStatment(query);
//                preaparedstatment23.setInt(1,1);
//            ResultSet resultSet2 = preparedStatement.executeQuery();
//            if(resultSet2.next())
//            {
//                double marks = resultSet.getDouble("marks");
//                System.out.println("Name: "+resultSet2.getString("name"));
//            }

//        String query6 = "UPDATE students set marks = ? where id = ?";
//        PreparedStatement preparedStatement3 = connection.prepareStatement(query6);
//        preparedStatement3.setDouble(1,94.3);
//        preparedStatement3.setInt(2,4);
//        int rowsupdated = preparedStatement3.executeUpdate();
//
//        if(rowsupdated > 0)
//        {
//            System.out.println("Data is updated using Prepared Statement");
//        }
//        else{
//            System.out.println("Data is not updated!!");
//        }

//        String query7 = "UPDATE students set age = ? where id = ?";
//        PreparedStatement preparedStatement4 = connection.prepareStatement(query7);
//        preparedStatement4.setInt(1,22);
//        preparedStatement4.setInt(2,4);

//        int rowsupdated2  = preparedStatement4.executeUpdate();
//
//        if(rowsupdated2 > 0)
//        {
//            System.out.println("Data again updated using PreparedStatement");
//        }
//        else{
//            System.out.println("Data is not updated");
//        }



            Scanner scanner = new Scanner(System.in);
            Statement statement2 = connection.createStatement();
            String query8 = "insert into students(name,age,marks)";
            PreparedStatement statement3 = connection.prepareStatement(query8);
        while(true) {
            System.out.print("Enter name: ");
            String name = scanner.next();
            System.out.print("Enter the age: ");
            int age = scanner.nextInt();
            System.out.print("Enter the marks: ");
            double marks = scanner.nextDouble();
//            String query8 = String.format("insert into students(name,age,marks) values('%s',%d,%f)",name,age,marks);
//            Now by using the Preapared Statement
            statement3.setString(1,name);
            statement3.setInt(2,age);
            statement3.setDouble(3,marks);

            System.out.print("Enter more data(Y/N)");
//            statement2.addBatch(query8);
            statement3.addBatch();
            String choice = scanner.next();
            if (choice.toUpperCase().equals("N")) {
                break;
            }

        }
            int [] arr = statement2.executeBatch();
            for(int i=0;i<arr.length;i++)
            {
                if(arr[i] == 0)
                {
                    System.out.println("Query is "+i+" not executed successfully!!");
                }
            }

            int [] arr2 = statement3.executeBatch();
            for(int i=0;i<arr.length;i++)
            {
                if(arr[i] == 0)
                {
                    System.out.println("Query is "+i+" not executed successfully!!");
                }
            }

        }catch (SQLException e)
        {
            System.out.println(e.getMessage());
        }
    }
}