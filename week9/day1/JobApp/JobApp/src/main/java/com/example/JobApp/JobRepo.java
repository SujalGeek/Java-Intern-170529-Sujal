package com.example.JobApp;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JobRepo {

    private final List<JobPost> jobs = Collections.synchronizedList(new ArrayList<>(Arrays.asList(
            new JobPost(1, "Java Developer", "Must have good experience in core Java and advanced Java", 2,
                    List.of("Core Java", "J2EE", "Spring Boot", "Hibernate")),
            new JobPost(2, "Frontend Developer", "Experience in building responsive web applications using React", 3,
                    List.of("HTML", "CSS", "JavaScript", "React")),
            new JobPost(3, "Data Scientist", "Strong background in machine learning and data analysis", 4,
                    List.of("Python", "Machine Learning", "Data Analysis")),
            new JobPost(4, "Network Engineer", "Design and implement computer networks for efficient data communication", 5,
                    List.of("Networking", "Cisco", "Routing", "Switching")),
            new JobPost(5, "Mobile App Developer", "Experience in mobile app development for iOS and Android", 3,
                    List.of("iOS Development", "Android Development", "Mobile App"))
    )));

    public List<JobPost> getAllJobs() {
        return jobs;
    }

    public Optional<JobPost> getJob(int postId) {
        return jobs.stream()
                .filter(job -> job.getPostId() == postId)
                .findFirst();
    }

    public void addJob(JobPost job) {
        jobs.add(job);
    }

    public void updateJob(JobPost jobPost) {
        getJob(jobPost.getPostId()).ifPresent(existing -> {
            existing.setPostProfile(jobPost.getPostProfile());
            existing.setPostDesc(jobPost.getPostDesc());
            existing.setReqExperience(jobPost.getReqExperience());
            existing.setPostTechStack(jobPost.getPostTechStack());
        });
    }

    public void deleteJob(int postId) {
        jobs.removeIf(job -> job.getPostId() == postId);
    }
}
