package com.example.sre_remediation_service.service;

import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class GitHubAutomationService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubAutomationService.class);

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.repo}")
    private String repoName;

    @Value("${github.main-branch}")
    private String mainBranch;

    // --- NEW METHOD: Read live code from GitHub ---
    public String getFileContent(String filePath) {
        try {
            GitHub github = new GitHubBuilder().withOAuthToken(githubToken).build();
            GHRepository repository = github.getRepository(repoName);
            
            GHContent content = repository.getFileContent(filePath, mainBranch);
            try (InputStream is = content.read()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            logger.error("Error fetching file from GitHub: {}", e.getMessage());
            return null; // Context nahi mila toh null bhejenge
        }
    }

    public String createAutoHotfixPR(String filePath, String newContent, String bugReport) throws IOException {
        GitHub github = new GitHubBuilder().withOAuthToken(githubToken).build();
        GHRepository repository = github.getRepository(repoName);

        String newBranchName = "ai-fix-" + System.currentTimeMillis();
        String mainSha = repository.getRef("heads/" + mainBranch).getObject().getSha();
        repository.createRef("refs/heads/" + newBranchName, mainSha);

        GHContentBuilder contentBuilder = repository.createContent()
                .branch(newBranchName)
                .path(filePath)
                .content(newContent)
                .message("AI-Ops: Surgical Remediation for " + bugReport);

        try {
            GHContent existingFile = repository.getFileContent(filePath, mainBranch);
            contentBuilder.sha(existingFile.getSha());
        } catch (GHFileNotFoundException e) {
            logger.warn("File not found on main, creating fresh: {}", filePath);
        }

        contentBuilder.commit();

        GHPullRequest pr = repository.createPullRequest(
            "Self-Healing: " + bugReport,
            newBranchName,
            mainBranch,
            "### AI-SRE Surgical Fix Report\n\n**Diagnosis:** Logic bug detected and fixed via Gemini Reasoning."
        );

        return pr.getHtmlUrl().toString();
    }
}