package com.example.sre_remediation_service.service;


import org.kohsuke.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class GitHubAutomationService {

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.repo}")
    private String repoName;

    @Value("${github.main-branch}")
    private String mainBranch;

    public String createAutoHotfixPR(String filePath, String newContent, String bugReport) throws IOException {
        // 1. Connect to GitHub (Local se direct connection)
        GitHub github = new GitHubBuilder().withOAuthToken(githubToken).build();
        GHRepository repository = github.getRepository(repoName);

        // 2. Create New Branch from Main
        String newBranchName = "ai-fix-" + System.currentTimeMillis();
        String mainSha = repository.getRef("heads/" + mainBranch).getObject().getSha();
        repository.createRef("refs/heads/" + newBranchName, mainSha);

        // 3. Prepare the Content Builder
        GHContentBuilder contentBuilder = repository.createContent()
                .branch(newBranchName)
                .path(filePath)
                .content(newContent)
                .message("AI-Ops: Remediation for " + bugReport);

        // 4. Check if file exists to get SHA (Update vs Create)
        try {
            GHContent existingFile = repository.getFileContent(filePath, mainBranch);
            contentBuilder.sha(existingFile.getSha()); // Found! Setting SHA for Update
        } catch (GHFileNotFoundException e) {
            // Not Found! Builder will automatically 'Create' without SHA
            System.out.println("DEBUG: File not found on main, creating fresh on new branch: " + filePath);
        }

        // 5. Commit & Push to GitHub
        contentBuilder.commit();

        // 6. Generate Pull Request
        GHPullRequest pr = repository.createPullRequest(
            "Self-Healing: " + bugReport,
            newBranchName,
            mainBranch,
            "### AI-SRE Remediation Report\n\n**Diagnosis:** " + bugReport
        );

        return pr.getHtmlUrl().toString();
    }
}