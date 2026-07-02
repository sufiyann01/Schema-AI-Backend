package com.sqlai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class SqlModels {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequest {
        private String message;
        private List<ConversationMessage> history;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConversationMessage {
        private String role; // "user" or "assistant"
        private String content;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatResponse {
        private String message;       // AI explanation
        private String generatedSql;  // The SQL query generated
        private QueryResult queryResult; // Execution result if SQL was run
        private boolean success;
        private String error;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryResult {
        private List<String> columns;
        private List<Map<String, Object>> rows;
        private int rowCount;
        private long executionTimeMs;
        private String queryType; // SELECT, INSERT, UPDATE, DELETE, etc.
        private int affectedRows;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecuteSqlRequest {
        private String sql;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchemaInfo {
        private List<TableInfo> tables;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableInfo {
        private String tableName;
        private List<ColumnInfo> columns;
        private long rowCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnInfo {
        private String columnName;
        private String dataType;
        private boolean nullable;
        private boolean primaryKey;
        private boolean foreignKey;
        private String referencedTable;
    }
}
