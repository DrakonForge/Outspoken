package io.github.drakonforge.outspoken.rulebank;

import io.github.drakonforge.outspoken.context.ContextTable;
import java.util.HashMap;
import java.util.Map;
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

    public PassthroughType getPassthroughType() {
        return passthroughType;
    }
}
