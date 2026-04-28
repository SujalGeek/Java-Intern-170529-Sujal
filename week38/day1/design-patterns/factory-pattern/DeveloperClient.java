public class DeveloperClient {
    public static void main(String[] args) {
      Employee employee = EmployeeFactory.getEmployee("ANDROID DEVELOPER");
       int salary =  employee.salary();
        System.out.println("Salary : "+salary);

        Employee employee1 = EmployeeFactory.getEmployee("WEB DEVELOPER");
        System.out.println("Salary : "+employee1.salary());
        
        // Factory Desgin Pattern
        /*
        When there is superclass and multiple sub classes and we want to get the object of the subclass
        based on input and requirements.

        advantages of the factory design pattern
        focus on the creating object for interface rather than implementation
        loose coupling, more robust code.
        */


    }
}
