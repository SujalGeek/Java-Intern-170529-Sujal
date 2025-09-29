package com.example.JobApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class JobRestController {

    @Autowired
    private JobService jobService;

    @GetMapping("/jobPosts")
    public List<JobPost> getAllJobs() {
        return jobService.getAllJobs();
    }

    @GetMapping("/jobPosts/{postId}")
    public JobPost getJob(@PathVariable int postId) {
        return jobService.getJob(postId);
    }

    @PostMapping("/jobPost")
    public JobPost addJob(@RequestBody JobPost jobPost) {
        jobService.addJob(jobPost);
        return jobService.getJob(jobPost.getPostId());
    }

    @PutMapping("/jobPost")
    public JobPost updateJob(@RequestBody JobPost jobPost) {
        jobService.updateJob(jobPost);
        return jobService.getJob(jobPost.getPostId());
    }

    @DeleteMapping("/jobPost/{postId}")
    public String deleteJob(@PathVariable int postId) {
        jobService.deleteJob(postId);
        return "Deleted job with ID: " + postId;
    }
}
