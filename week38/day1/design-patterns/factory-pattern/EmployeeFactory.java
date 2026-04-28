public class EmployeeFactory {
    
    public static Employee getEmployee(String empType)
    {
        if(empType.trim().equalsIgnoreCase("ANDROID DEVELOPER"))
        {
            return new AndroidDevloper();
        }
        else if(empType.trim().equalsIgnoreCase("WEB DEVELOPER"))
        {
            return new WebDevloper();
        }
        else{
            return null;
            
        }
    }
}
