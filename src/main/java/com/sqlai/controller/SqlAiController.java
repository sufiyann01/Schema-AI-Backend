package com.sqlai.controller;

import com.sqlai.model.SqlModels;
import com.sqlai.service.AiChatService;
import com.sqlai.service.SchemaService;
import com.sqlai.service.SqlExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SqlAiController {

    private final AiChatService aiChatService;
    private final SchemaService schemaService;
    private final SqlExecutionService sqlExecutionService;

    /**
     * Main chat endpoint — accepts natural language, returns AI response + SQL + results
     */
    @PostMapping("/chat")
    public ResponseEntity<SqlModels.ChatResponse> chat(@RequestBody SqlModels.ChatRequest request) {
        log.debug("Chat request: {}", request.getMessage());
        SqlModels.ChatResponse response = aiChatService.chat(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Execute raw SQL query directly
     */
    @PostMapping("/execute")
    public ResponseEntity<?> executeSql(@RequestBody SqlModels.ExecuteSqlRequest request) {
        log.debug("Execute SQL: {}", request.getSql());
        try {
            SqlModels.QueryResult result = sqlExecutionService.executeQuery(request.getSql());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    SqlModels.ChatResponse.builder()
                            .success(false)
                            .error(e.getMessage())
                            .build()
            );
        }
    }

    /**
     * Get database schema information
     */
    @GetMapping("/schema")
    public ResponseEntity<SqlModels.SchemaInfo> getSchema() {
        return ResponseEntity.ok(schemaService.getSchemaInfo());
    }

    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("SQL AI Assistant is running!");
    }
}
