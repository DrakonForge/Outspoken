package io.github.drakonforge.outspoken.rulebank;

import io.github.drakonforge.outspoken.context.ContextManager;
import io.github.drakonforge.outspoken.context.ContextTable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class RuleDatabase {
    private record GroupCategory(String group, String category) {}

    private final ContextManager contextManager;
    private final Map<GroupCategory, RuleTable> groupCategoryToTable = new HashMap<>();

    public RuleDatabase(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    public RulebankQueryResult.BestMatch queryBestMatch(RulebankQuery query) {
        RuleTable table = getRuleTable(query.getGroup(), query.getCategory());
        if (table == null) {
            return RulebankQueryResult.BestMatch.FAILURE;
        }
        return table.queryBestMatch(query);
    }

    @Nullable
    public RuleTable getRuleTable(String group, String category) {
        GroupCategory key = new GroupCategory(group, category);
        return groupCategoryToTable.get(key);
    }

    public void addRuleTable(String group, String category, RuleTable ruleTable) {
        GroupCategory key = new GroupCategory(group, category);
        groupCategoryToTable.put(key, ruleTable);
    }

    public ContextTable createBlankContextTable() {
        return new ContextTable(contextManager);
    }

    public ContextManager getContextManager() {
        return contextManager;
    }
}
