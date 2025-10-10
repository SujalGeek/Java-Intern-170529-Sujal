package com.example.SpringSecurity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Welcome to the Home Page! <a href='/login'>Login</a>";
    }

    @GetMapping("/user/home")
    @ResponseBody
    public String userHome() {
        return "Welcome USER! <a href='/logout'>Logout</a>";
    }

    @GetMapping("/admin/home")
    @ResponseBody
    public String adminHome() {
        return "Welcome ADMIN! <a href='/logout'>Logout</a>";
    }

    @GetMapping("/login")
    @ResponseBody
    public String loginPage() {
        return "<form method='post' action='/login'>" +
                "Username: <input type='text' name='username'/><br/>" +
                "Password: <input type='password' name='password'/><br/>" +
                "<input type='submit' value='Login'/>" +
                "</form>";
    }
}
