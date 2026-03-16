package com.demo.healthchecker.checker;

import com.demo.healthchecker.client.GitHubApiClient;
import com.demo.healthchecker.model.AiReadinessReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Evaluates how well a GitHub repository is prepared for AI-assisted development.
 *
 * <p>The checker inspects seven indicators and produces a score from 0 to 7:
 * <ol>
 *   <li>Copilot instructions ({@code .github/copilot-instructions.md})</li>
 *   <li>Custom agents ({@code .github/copilot/agents.md})</li>
 *   <li>Custom skills ({@code .github/copilot/skills/} directory)</li>
 *   <li>Prompt files ({@code *.prompt.md} under {@code .github/})</li>
 *   <li>{@code .gitignore} file</li>
 *   <li>Folder-level instructions ({@code *.instructions.md} under {@code src/})</li>
 *   <li>Prompt library ({@code .github/prompts/} directory)</li>
 * </ol>
 */
public class AiReadinessChecker {

    private static final Logger logger = LoggerFactory.getLogger(AiReadinessChecker.class);
    private static final int MAX_SCORE = 7;

    private final GitHubApiClient client;

    /**
     * Creates a new AI-readiness checker backed by the given API client.
     *
     * @param client the GitHub API client used for all repository queries
     */
    public AiReadinessChecker(GitHubApiClient client) {
        this.client = client;
    }

    /**
     * Runs the AI-readiness check against the specified repository.
     *
     * @param owner repository owner (user or organization)
     * @param repo  repository name
     * @return an {@link AiReadinessReport} summarizing the results
     * @throws IOException if any GitHub API call fails
     */
    public AiReadinessReport check(String owner, String repo) throws IOException {
        logger.info("Starting AI readiness check for {}/{}", owner, repo);

        boolean hasCopilotInstructions = checkCopilotInstructions(owner, repo);
        boolean hasCustomAgents = checkCustomAgents(owner, repo);
        boolean hasCustomSkills = checkCustomSkills(owner, repo);
        boolean hasPromptFiles = checkPromptFiles(owner, repo);
        boolean hasGitignore = checkGitignore(owner, repo);
        boolean hasFolderInstructions = checkFolderInstructions(owner, repo);
        boolean hasPromptLibrary = checkPromptLibrary(owner, repo);

        int score = 0;
        if (hasCopilotInstructions) score++;
        if (hasCustomAgents) score++;
        if (hasCustomSkills) score++;
        if (hasPromptFiles) score++;
        if (hasGitignore) score++;
        if (hasFolderInstructions) score++;
        if (hasPromptLibrary) score++;

        logger.info("AI readiness check complete for {}/{}: score={}/{}", owner, repo, score, MAX_SCORE);

        return new AiReadinessReport(
                hasCopilotInstructions,
                hasCustomAgents,
                hasCustomSkills,
                hasPromptFiles,
                hasGitignore,
                hasFolderInstructions,
                hasPromptLibrary,
                score,
                MAX_SCORE
        );
    }

    private boolean checkCopilotInstructions(String owner, String repo) throws IOException {
        logger.info("Checking .github/copilot-instructions.md...");
        boolean exists = client.checkFileExists(owner, repo, ".github/copilot-instructions.md");
        logger.info("copilot-instructions.md exists: {}", exists);
        return exists;
    }

    private boolean checkCustomAgents(String owner, String repo) throws IOException {
        logger.info("Checking .github/copilot/agents.md...");
        boolean exists = client.checkFileExists(owner, repo, ".github/copilot/agents.md");
        logger.info("agents.md exists: {}", exists);
        return exists;
    }

    private boolean checkCustomSkills(String owner, String repo) throws IOException {
        logger.info("Checking .github/copilot/skills/ directory...");
        boolean exists = client.hasDirectory(owner, repo, ".github/copilot/skills");
        logger.info("skills directory exists: {}", exists);
        return exists;
    }

    private boolean checkPromptFiles(String owner, String repo) throws IOException {
        logger.info("Checking for *.prompt.md files under .github/...");
        boolean exists = hasFileWithSuffix(owner, repo, ".github", ".prompt.md");
        logger.info("Found .prompt.md file under .github/: {}", exists);
        return exists;
    }

    private boolean checkGitignore(String owner, String repo) throws IOException {
        logger.info("Checking .gitignore...");
        boolean exists = client.checkFileExists(owner, repo, ".gitignore");
        logger.info(".gitignore exists: {}", exists);
        return exists;
    }

    private boolean checkFolderInstructions(String owner, String repo) throws IOException {
        logger.info("Checking for *.instructions.md files under src/...");
        boolean exists = hasFileWithSuffix(owner, repo, "src", ".instructions.md");
        logger.info("Found .instructions.md file under src/: {}", exists);
        return exists;
    }

    private boolean checkPromptLibrary(String owner, String repo) throws IOException {
        logger.info("Checking for .github/prompts/ prompt library directory...");
        boolean exists = client.hasDirectory(owner, repo, ".github/prompts");
        logger.info(".github/prompts/ directory exists: {}", exists);
        return exists;
    }

    private boolean hasFileWithSuffix(String owner, String repo, String rootPath, String suffix) throws IOException {
        List<Map<String, Object>> entries = client.listDirectoryEntries(owner, repo, rootPath);
        for (Map<String, Object> entry : entries) {
            Object typeObj = entry.get("type");
            Object nameObj = entry.get("name");
            Object pathObj = entry.get("path");
            if (!(typeObj instanceof String type) || !(nameObj instanceof String name) || !(pathObj instanceof String path)) {
                continue;
            }
            if ("file".equals(type) && name.endsWith(suffix)) {
                return true;
            }
            if ("dir".equals(type) && hasFileWithSuffix(owner, repo, path, suffix)) {
                return true;
            }
        }
        return false;
    }
}

