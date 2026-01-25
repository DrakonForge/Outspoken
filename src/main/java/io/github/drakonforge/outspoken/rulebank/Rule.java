package io.github.drakonforge.outspoken.rulebank;

import io.github.drakonforge.outspoken.criterion.Criterion;
import io.github.drakonforge.outspoken.response.Response;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public record Rule(List<CriteriaEntry> criteria, Response response, int priority) {

    public static void sortRules(Rule[] rules) {
        // Sort by descending priority
        Arrays.sort(rules, Comparator.comparingInt(Rule::priority).reversed());
    }

    public static void sortCriteria(List<CriteriaEntry> criteria) {
        // Sort by descending priority
        criteria.sort(Comparator.comparingInt((CriteriaEntry entry) -> entry.criterion().getPriority())
                .reversed());
    }

    public record CriteriaEntry(String tableName, String key, Criterion criterion) {}
}
