package com.example.JobApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class JobController {

    @Autowired
    private JobService jobService;

    @RequestMapping({"/","home"})
    public String home(){
        return "home";
    }

    @RequestMapping("addjob")
    public String addJob(){
        return "addjob";
    }

    @PostMapping("handleForm")
    public String handleForm(JobPost jobPost)
    {
    jobService.addJob(jobPost);
    return "success";
    }

    @GetMapping("viewalljobs")
    public String viewJobs(Model model){
        List<JobPost> jobs = jobService.getAllJobs();
        model.addAttribute("jobPosts",jobs);
        return "viewalljobs";
    }
}
