package io.github.drakonforge.outspoken.database;

import io.github.drakonforge.outspoken.context.ContextManager;
import io.github.drakonforge.outspoken.context.ContextTable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class RuleDatabase {
    public enum QueryReturnCode {
        SUCCESS, FAILURE
    }

    private record GroupCategory(String group, String category) {}

    private final ContextManager contextManager;
    private final Map<GroupCategory, RuleTable> groupCategoryToTable = new HashMap<>();

    public RuleDatabase(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    public QueryResult.BestMatch queryBestMatch(DatabaseQuery query) {
        RuleTable table = getRuleTable(query.getGroup(), query.getCategory());
        if (table == null) {
            return QueryResult.BestMatch.FAILURE;
        }
        return table.queryBestMatch(query);
    }

    @Nullable
    private RuleTable getRuleTable(String group, String category) {
        GroupCategory key = new GroupCategory(group, category);
        return groupCategoryToTable.get(key);
    }

    public ContextTable createBlankContextTable() {
        return new ContextTable(contextManager);
    }
}
