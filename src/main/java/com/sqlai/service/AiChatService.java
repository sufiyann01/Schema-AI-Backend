package com.sqlai.service;

import com.sqlai.model.SqlModels.ChatRequest;
import com.sqlai.model.SqlModels.ChatResponse;
import com.sqlai.model.SqlModels.ConversationMessage;
import com.sqlai.model.SqlModels.QueryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiChatService {

    private final ChatClient chatClient;
    private final SchemaService schemaService;
    private final SqlExecutionService sqlExecutionService;

    private static final String SYSTEM_TEMPLATE = """
            You are an expert SQL assistant. Your job is to help users query and understand their database.

            {schema}

            INSTRUCTIONS:
            1. When the user asks a question about data, generate the appropriate SQL query.
            2. Always wrap SQL queries in ```sql ... ``` code blocks.
            3. After the SQL block, briefly explain what the query does.
            4. If the user asks for explanations instead of SQL, answer conversationally.
            5. Use standard SQL compatible with H2/MySQL/PostgreSQL.
            6. Always use table aliases for JOINs.
            7. Prefer SELECT queries unless the user explicitly asks to modify data.
            8. Keep explanations concise.
            """;

    public ChatResponse chat(ChatRequest request) {

        try {

            String schemaInfo = schemaService.buildSchemaPrompt();

            String systemPrompt = SYSTEM_TEMPLATE.replace("{schema}", schemaInfo);

            ChatClient.ChatClientRequestSpec prompt = chatClient.prompt();

            prompt.system(systemPrompt);

            // Previous conversation
            if (request.getHistory() != null) {

                for (ConversationMessage message : request.getHistory()) {

                    if ("user".equalsIgnoreCase(message.getRole())) {
                        prompt.user(message.getContent());
                    }

                    // Current ChatClient API doesn't expose assistant()
                    // Ignore assistant messages
                }
            }

            // Current question
            prompt.user(request.getMessage());

            String aiResponse = prompt
                    .call()
                    .content();

            String generatedSql = extractSql(aiResponse);

            QueryResult queryResult = null;

            if (generatedSql != null &&
                    sqlExecutionService.isReadOnly(generatedSql)) {

                try {
                    queryResult = sqlExecutionService.executeQuery(generatedSql);
                }
                catch (Exception ex) {
                    log.warn("Auto execution failed: {}", ex.getMessage());
                }
            }

            return ChatResponse.builder()
                    .message(aiResponse)
                    .generatedSql(generatedSql)
                    .queryResult(queryResult)
                    .success(true)
                    .build();

        }
        catch (Exception ex) {

            log.error("Chat Error", ex);

            return ChatResponse.builder()
                    .success(false)
                    .error(ex.getMessage())
                    .build();
        }
    }

    private String extractSql(String response) {

        Pattern pattern = Pattern.compile(
                "```sql\\s*([\\s\\S]*?)```",
                Pattern.CASE_INSENSITIVE);

        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }
}