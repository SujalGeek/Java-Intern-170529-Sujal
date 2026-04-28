package builder;

public class Main {
    public static void main(String[] args) {
        
       User1 user1 = new User1.UserBuilder()
        .setEmailId("sujal@gmail.com")
        .setUserId("121")
        .setUserName("rvane")
        .build();
    
    System.out.println(user1);

    User1 user2 = User1.UserBuilder.builder()
                .setUserId("USER1234")
                .setEmailId("Ankit@gmail.com")

                .build();

        System.out.println(user2);

    }

}
