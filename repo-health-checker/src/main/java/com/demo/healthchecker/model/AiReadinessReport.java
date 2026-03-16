package com.demo.healthchecker.model;

/**
 * Immutable data carrier for the results of an AI-readiness check.
 *
 * <p>The scoring system awards one point for each of seven indicators:
 * <ol>
 *   <li>{@code hasCopilotInstructions} — Copilot instructions file present</li>
 *   <li>{@code hasCustomAgents} — custom agents definition present</li>
 *   <li>{@code hasCustomSkills} — custom skills directory present</li>
 *   <li>{@code hasPromptFiles} — at least one {@code *.prompt.md} file found</li>
 *   <li>{@code hasGitignore} — {@code .gitignore} file present</li>
 *   <li>{@code hasFolderInstructions} — at least one {@code *.instructions.md} file found</li>
 *   <li>{@code hasPromptLibrary} — dedicated {@code .github/prompts/} prompt library directory present</li>
 * </ol>
 *
 * @param hasCopilotInstructions whether {@code .github/copilot-instructions.md} exists
 * @param hasCustomAgents        whether {@code .github/copilot/agents.md} exists
 * @param hasCustomSkills        whether {@code .github/copilot/skills/} directory exists
 * @param hasPromptFiles         whether any {@code *.prompt.md} files exist under {@code .github/}
 * @param hasGitignore           whether a {@code .gitignore} file exists
 * @param hasFolderInstructions  whether any {@code *.instructions.md} files exist under {@code src/}
 * @param hasPromptLibrary       whether the dedicated {@code .github/prompts/} directory exists
 * @param score                  total AI-readiness score (0–{@code maxScore})
 * @param maxScore               maximum possible score (currently 7)
 */
public record AiReadinessReport(
        boolean hasCopilotInstructions,
        boolean hasCustomAgents,
        boolean hasCustomSkills,
        boolean hasPromptFiles,
        boolean hasGitignore,
        boolean hasFolderInstructions,
        boolean hasPromptLibrary,
        int score,
        int maxScore
) {}