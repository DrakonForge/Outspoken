package io.github.drakonforge.outspoken.database.rulebank;

import io.github.drakonforge.outspoken.database.context.ContextManager;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQueryResult.BestMatch;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
            return BestMatch.MISSING_GROUP_CATEGORY;
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

    public Set<String> getAllGroups() {
        Set<String> results = new HashSet<>();
        for (GroupCategory groupCategory : groupCategoryToTable.keySet()) {
            results.add(groupCategory.group());
        }
        return results;
    }

    public Set<String> getCategoriesForGroup(String group) {
        Set<String> results = new HashSet<>();
        for (GroupCategory groupCategory : groupCategoryToTable.keySet()) {
            if (groupCategory.group().equals(group)) {
                results.add(groupCategory.category());
            }
        }
        return results;
    }

    public ContextManager getContextManager() {
        return contextManager;
    }
}
