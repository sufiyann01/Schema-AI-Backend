package com.sqlai.service;

import com.sqlai.model.SqlModels.QueryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SqlExecutionService {

    private final DataSource dataSource;

    // Keywords that indicate write operations
    private static final Set<String> WRITE_KEYWORDS = Set.of(
        "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER", "TRUNCATE"
    );

    public QueryResult executeQuery(String sql) {
        String normalizedSql = sql.trim().toUpperCase();
        String firstWord = normalizedSql.split("\\s+")[0];

        long start = System.currentTimeMillis();

        if ("SELECT".equals(firstWord) || firstWord.equals("WITH")) {
            return executeSelect(sql, start);
        } else if (WRITE_KEYWORDS.contains(firstWord)) {
            return executeUpdate(sql, firstWord, start);
        } else {
            return QueryResult.builder()
                    .queryType("UNKNOWN")
                    .rowCount(0)
                    .columns(Collections.emptyList())
                    .rows(Collections.emptyList())
                    .executionTimeMs(System.currentTimeMillis() - start)
                    .build();
        }
    }

    private QueryResult executeSelect(String sql, long start) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= colCount; i++) {
                columns.add(meta.getColumnLabel(i));
            }

            List<Map<String, Object>> rows = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    Object value = rs.getObject(i);
                    row.put(meta.getColumnLabel(i), value != null ? value.toString() : null);
                }
                rows.add(row);
            }

            return QueryResult.builder()
                    .queryType("SELECT")
                    .columns(columns)
                    .rows(rows)
                    .rowCount(rows.size())
                    .executionTimeMs(System.currentTimeMillis() - start)
                    .build();

        } catch (SQLException e) {
            log.error("SQL execution error", e);
            throw new RuntimeException("SQL Error: " + e.getMessage());
        }
    }

    private QueryResult executeUpdate(String sql, String queryType, long start) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int affected = ps.executeUpdate();

            return QueryResult.builder()
                    .queryType(queryType)
                    .columns(Collections.emptyList())
                    .rows(Collections.emptyList())
                    .rowCount(0)
                    .affectedRows(affected)
                    .executionTimeMs(System.currentTimeMillis() - start)
                    .build();

        } catch (SQLException e) {
            log.error("SQL update error", e);
            throw new RuntimeException("SQL Error: " + e.getMessage());
        }
    }

    public boolean isReadOnly(String sql) {
        String upper = sql.trim().toUpperCase();
        return upper.startsWith("SELECT") || upper.startsWith("WITH") || upper.startsWith("EXPLAIN");
    }
}
