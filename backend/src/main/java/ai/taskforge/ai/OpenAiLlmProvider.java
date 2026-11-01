package ai.taskforge.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiLlmProvider implements LlmProvider {

    private final String key;
    private final ObjectMapper json;

    public OpenAiLlmProvider(
            @Value("${taskforge.openai-key:}") String key,
            ObjectMapper json
    ) {
        this.key = key;
        this.json = json;
    }

    @Override
    public Analysis analyze(String text) {

        if (key.isBlank()) {
            throw new IllegalStateException(
                    "OPENAI_API_KEY is not configured"
            );
        }

        try {

            Map<String, Object> taskSchema = Map.of(
                    "type", "object",
                    "additionalProperties", false,
                    "properties", Map.of(
                            "title", Map.of(
                                    "type", "string"
                            ),
                            "description", Map.of(
                                    "type", "string"
                            ),
                            "category", Map.of(
                                    "type", "string"
                            ),
                            "priority", Map.of(
                                    "type", "string",
                                    "enum", List.of(
                                            "LOW",
                                            "MEDIUM",
                                            "HIGH",
                                            "CRITICAL"
                                    )
                            ),
                            "deadline", Map.of(
                                    "type", List.of("string", "null")
                            ),
                            "estimatedEffort", Map.of(
                                    "type", "string"
                            ),
                            "dependencies", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "string"
                                    )
                            )
                    ),
                    "required", List.of(
                            "title",
                            "description",
                            "category",
                            "priority",
                            "deadline",
                            "estimatedEffort",
                            "dependencies"
                    )
            );

            Map<String, Object> schema = Map.of(
                    "type", "object",
                    "additionalProperties", false,

                    "properties", Map.of(

                            "summary", Map.of(
                                    "type", "string"
                            ),

                            "category", Map.of(
                                    "type", "string"
                            ),

                            "priority", Map.of(
                                    "type", "string",
                                    "enum", List.of(
                                            "LOW",
                                            "MEDIUM",
                                            "HIGH",
                                            "CRITICAL"
                                    )
                            ),

                            "priorityScore", Map.of(
                                    "type", "integer",
                                    "minimum", 0,
                                    "maximum", 100
                            ),

                            "deadline", Map.of(
                                    "type", List.of("string", "null")
                            ),

                            "entities", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "string"
                                    )
                            ),

                            "tasks", Map.of(
                                    "type", "array",
                                    "items", taskSchema
                            ),

                            "reasons", Map.of(
                                    "type", "array",
                                    "items", Map.of(
                                            "type", "string"
                                    )
                            )
                    ),

                    "required", List.of(
                            "summary",
                            "category",
                            "priority",
                            "priorityScore",
                            "deadline",
                            "entities",
                            "tasks",
                            "reasons"
                    )
            );

            Map<String, Object> body = Map.of(
                    "model", "gpt-4.1-mini",

                    "input",
                    """
                    Analyze the following enterprise work item.

                    Extract actionable work from emails, documents, requests,
                    instructions, meeting notes, and other business text.

                    Rules:
                    - Identify every meaningful actionable task.
                    - Extract explicit deadlines when present.
                    - If a deadline is not present, return null.
                    - Identify dependencies between extracted tasks.
                    - Estimate effort using a concise value such as:
                      "30 minutes", "2 hours", "1 day", "3 days", etc.
                    - Assign category and priority to each task.
                    - Use CRITICAL only when the text clearly indicates
                      severe urgency or business impact.
                    - priorityScore must be between 0 and 100.
                    - Extract important people, organizations, projects,
                      systems, products, or other entities.
                    - Do not invent deadlines or dependencies.
                    - Return only JSON matching the supplied schema.

                    Input:
                    """ + text,

                    "text", Map.of(
                            "format", Map.of(
                                    "type", "json_schema",
                                    "name", "task_analysis",
                                    "strict", true,
                                    "schema", schema
                            )
                    )
            );

            String responseBody = RestClient
                    .create("https://api.openai.com")
                    .post()
                    .uri("/v1/responses")
                    .header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode raw = json.readTree(responseBody);

            String output = raw
                    .path("output")
                    .get(0)
                    .path("content")
                    .get(0)
                    .path("text")
                    .asText();

            if (output == null || output.isBlank()) {
                throw new IllegalStateException(
                        "OpenAI returned an empty structured response"
                );
            }

            JsonNode n = json.readTree(output);

            List<ExtractedTask> tasks = new ArrayList<>();

            for (JsonNode task : n.path("tasks")) {

                tasks.add(
                        new ExtractedTask(
                                required(task, "title"),
                                required(task, "description"),
                                required(task, "category"),
                                required(task, "priority"),
                                readDate(task, "deadline"),
                                required(task, "estimatedEffort"),
                                readStrings(task, "dependencies")
                        )
                );
            }

            return new Analysis(
                    required(n, "summary"),
                    required(n, "category"),
                    required(n, "priority"),
                    n.path("priorityScore").asInt(),
                    readDate(n, "deadline"),
                    readStrings(n, "entities"),
                    tasks,
                    readStrings(n, "reasons")
            );

        } catch (Exception e) {

            throw new IllegalStateException(
                    "OpenAI analysis failed without a valid structured response",
                    e
            );
        }
    }

    private String required(JsonNode node, String field) {

        String value = node.path(field).asText();

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing required field: " + field
            );
        }

        return value;
    }

    private LocalDate readDate(JsonNode node, String field) {

        JsonNode value = node.path(field);

        if (value.isMissingNode()
                || value.isNull()
                || value.asText().isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(value.asText());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid date for field " + field + ": "
                            + value.asText()
            );
        }
    }

    private List<String> readStrings(JsonNode node, String field) {

        List<String> result = new ArrayList<>();

        JsonNode values = node.path(field);

        if (values.isArray()) {
            values.forEach(value -> {
                if (!value.asText().isBlank()) {
                    result.add(value.asText());
                }
            });
        }

        return result;
    }
}