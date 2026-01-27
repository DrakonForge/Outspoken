package io.github.drakonforge.outspoken.database.rulebank;

import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RulebankQuery {
    public enum PassthroughType {
        CHANCE, NEVER
    }

    private final String group;
    private final String category;
    private final Map<String, ContextTable> contexts = new HashMap<>();
    private final PassthroughType passthroughType;

    public RulebankQuery(String group, String category) {
        this(group, category, PassthroughType.CHANCE);
    }

    public RulebankQuery(String group, String category, PassthroughType passthroughType) {
        this.group = group;
        this.category = category;
        this.passthroughType = passthroughType;
    }

    public RulebankQuery addContextTable(String tableName, ContextTable contextTable) {
        contexts.put(tableName, contextTable);
        return this;
    }

    @Nullable
    public ContextTable getContextTable(String tableName) {
        return contexts.get(tableName);
    }

    @Nonnull
    public ContextTable getOrCreateContextTable(String tableName) {
        ContextTable contextTable = contexts.get(tableName);
        if (contextTable == null) {
            ContextTable newContextTable = OutspokenApi.createBlankContextTable();
            contexts.put(tableName, newContextTable);
            return newContextTable;
        }
        return contextTable;
    }

    public String getGroup() {
        return group;
    }

    public String getCategory() {
        return category;
    }

    public PassthroughType getPassthroughType() {
        return passthroughType;
    }
}
