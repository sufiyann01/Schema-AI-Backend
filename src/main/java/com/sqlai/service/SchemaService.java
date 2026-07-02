package com.sqlai.service;

import com.sqlai.model.SqlModels.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemaService {

    private final DataSource dataSource;

    public SchemaInfo getSchemaInfo() {
        List<TableInfo> tables = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            // Option 1 — match any schema
            ResultSet tableRs = meta.getTables(null, null, null, new String[]{"TABLE"});

            while (tableRs.next()) {
                String tableName = tableRs.getString("TABLE_NAME");
                if (tableName.startsWith("SYS") || tableName.startsWith("INFORMATION")) continue;

                List<ColumnInfo> columns = getColumns(conn, meta, tableName);
                long rowCount = getRowCount(conn, tableName);

                tables.add(TableInfo.builder()
                        .tableName(tableName)
                        .columns(columns)
                        .rowCount(rowCount)
                        .build());
            }
        } catch (SQLException e) {
            log.error("Error fetching schema info", e);
        }

        return SchemaInfo.builder().tables(tables).build();
    }

    private List<ColumnInfo> getColumns(Connection conn, DatabaseMetaData meta, String tableName) throws SQLException {
        List<ColumnInfo> columns = new ArrayList<>();

        // Get primary keys
        Set<String> primaryKeys = new HashSet<>();
        try (ResultSet pkRs = meta.getPrimaryKeys(null, null, tableName)) {
            while (pkRs.next()) {
                primaryKeys.add(pkRs.getString("COLUMN_NAME"));
            }
        }

        // Get foreign keys
        Map<String, String> foreignKeys = new HashMap<>();
        try (ResultSet fkRs = meta.getImportedKeys(null, null, tableName)) {
            while (fkRs.next()) {
                foreignKeys.put(fkRs.getString("FKCOLUMN_NAME"), fkRs.getString("PKTABLE_NAME"));
            }
        }

        try (ResultSet colRs = meta.getColumns(null, null, tableName, null)) {
            while (colRs.next()) {
                String colName = colRs.getString("COLUMN_NAME");
                columns.add(ColumnInfo.builder()
                        .columnName(colName)
                        .dataType(colRs.getString("TYPE_NAME"))
                        .nullable("YES".equals(colRs.getString("IS_NULLABLE")))
                        .primaryKey(primaryKeys.contains(colName))
                        .foreignKey(foreignKeys.containsKey(colName))
                        .referencedTable(foreignKeys.get(colName))
                        .build());
            }
        }

        return columns;
    }

    private long getRowCount(Connection conn, String tableName) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            log.warn("Could not get row count for {}", tableName);
        }
        return 0;
    }

    public String buildSchemaPrompt() {
        SchemaInfo schema = getSchemaInfo();
        StringBuilder sb = new StringBuilder();
        sb.append("DATABASE SCHEMA:\n\n");

        for (TableInfo table : schema.getTables()) {
            sb.append("TABLE: ").append(table.getTableName())
              .append(" (").append(table.getRowCount()).append(" rows)\n");

            for (ColumnInfo col : table.getColumns()) {
                sb.append("  - ").append(col.getColumnName())
                  .append(" (").append(col.getDataType()).append(")");
                if (col.isPrimaryKey()) sb.append(" [PK]");
                if (col.isForeignKey()) sb.append(" [FK -> ").append(col.getReferencedTable()).append("]");
                if (!col.isNullable()) sb.append(" NOT NULL");
                sb.append("\n");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
