package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ActivityAIService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity) {
        // 1. LOGGING
        log.info("------------------------------------------------");
        log.info("AI SERVICE: Processing Activity ID: {}", activity != null ? activity.getId() : "NULL");
        log.info("Raw Activity Data: {}", activity);

        // 2. SAFETY CHECKS
        if (activity == null) {
            return createErrorRecommendation(null, "Error: Activity data not found");
        }
        if (activity.getType() == null) {
            return createErrorRecommendation(activity, "Error: Activity Type is missing.");
        }

        try {
            // 3. SAFE TYPE CONVERSION
            String safeActivityType = activity.getType().toString().toUpperCase();

            // 4. GENERATE PROMPT
            String prompt = createPromptForActivity(activity, safeActivityType);
            log.info("AI SERVICE: Sending Prompt to AI: {}", prompt);

            // 5. CALL AI
            String aiResponse = geminiService.getRecommendations(prompt);

            return processAIResponse(activity, aiResponse, safeActivityType);

        } catch (Exception e) {
            log.error("AI SERVICE CRASHED: ", e);
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation processAIResponse(Activity activity, String aiResponse, String safeActivityType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);

            // Handle AI errors or empty responses
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isMissingNode() || candidates.isEmpty()) {
                log.warn("AI returned no candidates. Raw response: {}", aiResponse);
                return createDefaultRecommendation(activity);
            }

            JsonNode textNode = candidates.get(0).path("content").path("parts").get(0).path("text");

            // Clean up Markdown code blocks if present
            String jsonContent = textNode.asText()
                    .replaceAll("```json\\n","")
                    .replaceAll("```json","")
                    .replaceAll("\\n```","")
                    .replaceAll("```","")
                    .trim();

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");

            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis, analysisNode, "overall", "Overall:");
            addAnalysisSection(fullAnalysis, analysisNode, "pace", "Pace:");
            addAnalysisSection(fullAnalysis, analysisNode, "heartRate", "Heart Rate:");
            addAnalysisSection(fullAnalysis, analysisNode, "caloriesBurned", "Calories:");

            List<String> improvements = extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions"));
            List<String> safety = extractSafetyGuidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(safeActivityType)
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse AI JSON response", e);
            return createDefaultRecommendation(activity);
        }
    }

    private Recommendation createErrorRecommendation(Activity activity, String errorMessage) {
        return Recommendation.builder()
                .activityId(activity != null ? activity.getId() : "Unknown")
                .userId(activity != null ? activity.getUserId() : "Unknown")
                .type("Unknown")
                .recommendation(errorMessage)
                .improvements(Collections.emptyList())
                .suggestions(Collections.emptyList())
                .safety(Collections.emptyList())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        String safeType = (activity != null && activity.getType() != null) ? activity.getType().toString() : "Unknown";
        return Recommendation.builder()
                .activityId(activity != null ? activity.getId() : "Unknown")
                .userId(activity != null ? activity.getUserId() : "Unknown")
                .type(safeType)
                .recommendation("Unable to generate detailed analysis. Please try again later.")
                .improvements(Collections.singletonList("Keep active and consistent."))
                .suggestions(Collections.singletonList("Try varying your intensity."))
                .safety(Arrays.asList("Stay hydrated", "Warm up properly"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if (safetyNode.isArray()) {
            safetyNode.forEach(item -> safety.add(item.asText()));
        }
        return safety.isEmpty() ? Collections.singletonList("Follow general safety guidelines") : safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(s -> suggestions.add(s.path("workout").asText() + ": " + s.path("description").asText()));
        }
        return suggestions.isEmpty() ? Collections.singletonList("No specific suggestions") : suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if (improvementsNode.isArray()) {
            improvementsNode.forEach(i -> improvements.add(i.path("area").asText() + ": " + i.path("recommendation").asText()));
        }
        return improvements.isEmpty() ? Collections.singletonList("No specific improvements") : improvements;
    }

    private void addAnalysisSection(StringBuilder sb, JsonNode node, String key, String prefix) {
        if (!node.path(key).isMissingNode() && !node.path(key).isNull()) {
            sb.append(prefix).append(" ").append(node.path(key).asText()).append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity, String safeActivityType) {
        // FIX: Handle nulls for numeric values using "0" as default
        int duration = activity.getDuration() != null ? activity.getDuration() : 0;
        int calories = activity.getCaloriesBurned() != null ? activity.getCaloriesBurned() : 0;
        String metrics = activity.getAdditionalMetrics() != null ? activity.getAdditionalMetrics().toString() : "None";

        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Brief overall summary",
            "pace": "Analysis of pace",
            "caloriesBurned": "Analysis of calorie burn"
          },
          "improvements": [ { "area": "Area", "recommendation": "Tip" } ],
          "suggestions": [ { "workout": "Name", "description": "Details" } ],
          "safety": [ "Tip 1", "Tip 2" ]
        }

        Activity Data:
        Type: %s
        Duration: %d minutes
        Calories: %d
        Metrics: %s
        """,
                safeActivityType,
                duration, // Now safe from NullPointerException
                calories, // Now safe from NullPointerException
                metrics
        );
    }
}