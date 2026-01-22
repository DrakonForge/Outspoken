package io.github.drakonforge.outspoken.database;

import io.github.drakonforge.outspoken.context.ContextTable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class DatabaseQuery {
    private final String group;
    private final String category;
    private final Map<String, ContextTable> contexts = new HashMap<>();

    public DatabaseQuery(String group, String category) {
        this.group = group;
        this.category = category;
    }

    public void addContextTable(String tableName, ContextTable contextTable) {
        contexts.put(tableName, contextTable);
    }

    @Nullable
    public ContextTable getContextTable(String tableName) {
        return contexts.get(tableName);
    }

    public String getGroup() {
        return group;
    }

    public String getCategory() {
        return category;
    }
}
