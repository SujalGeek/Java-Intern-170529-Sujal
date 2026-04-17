package builder;

class User1 {
    
    private final String userId;
    private final String userName;
    private final String emailId;

    private User1(UserBuilder builder)
    {
        this.userId=builder.userId;
        this.userName=builder.userName;
        this.emailId=builder.emailId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailId() {
        return emailId;
    }

    @Override
    public String toString() {
        return this.userName + " : "+ this.emailId+ " : "+ this.userId;
        //return sb.toString();
    }


    static class UserBuilder{
        private String userId;
        private String userName;
        private String emailId;

        public UserBuilder() {
        }

        public UserBuilder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public UserBuilder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public UserBuilder setEmailId(String emailId) {
            this.emailId = emailId;
            return this;
        }

        
        public User1 build() {
            User1 user = new User1(this);
            return user;
        }

        
    }

    
}
