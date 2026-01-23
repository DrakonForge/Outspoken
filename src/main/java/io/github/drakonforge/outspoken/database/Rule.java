package io.github.drakonforge.outspoken.database;

import io.github.drakonforge.outspoken.criterion.Criterion;
import io.github.drakonforge.outspoken.response.Response;
import java.util.Comparator;
import java.util.List;

public record Rule(String id, List<CriteriaEntry> criteria, Response response, int priority) {
    public record CriteriaEntry(String tableName, String key, Criterion criterion) {}

    public static void sortRules(List<Rule> rules) {
        // Sort by descending priority
        rules.sort(Comparator.comparingInt(Rule::priority).reversed());
    }

    public static void sortCriteria(List<CriteriaEntry> criteria) {
        // Sort by descending priority
        criteria.sort(Comparator.comparingInt((CriteriaEntry entry) -> entry.criterion().getPriority()).reversed());
    }
}
