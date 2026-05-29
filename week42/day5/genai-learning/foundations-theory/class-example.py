
print("The class is not working bro")

class Dog:
    def __init__(self,name):
        self.name = name
        
    
dog1 = Dog(name="Max")
dog2 = Dog(name="Mavick")

print("The Dog is working !!")

print(dog1.name)
print(dog2.name) 



class DataValidator:
    def __init__(self):
        self.errors = []
        pass
    

    def validate_email(self,email):
        if "@" in email:
            self.errors.append(f"Invalid email: {email}")
            return False
        return True
    
    def validate_age(self,age):
        if age < 0 or age > 150:
            self.errors.append(f"Invalid age: {age}")
            return False
        return True
    

    def get_errors(self):
        return self.errors
    

validator = DataValidator()

validator.validate_email(email="bad-email")
validator.validate_age(age=200)

print("The validating the result is displaying")
print(validator.get_errors())



age = 12
print(age)
print(type(age))
age = "Not able to display now"
print(len(age))
print(len(validator.get_errors()))