package io.github.drakonforge.outspoken.database;

import io.github.drakonforge.outspoken.database.QueryResult.BestMatch;
import io.github.drakonforge.outspoken.database.Rule.CriteriaEntry;
import io.github.drakonforge.outspoken.database.RuleDatabase.QueryReturnCode;
import java.util.List;

public class RuleTable {

    private static boolean match(DatabaseQuery query, List<CriteriaEntry> criteria) {
        for (CriteriaEntry criteriaEntry : criteria) {
            if (!criteriaEntry.criterion().evaluate(criteriaEntry.tableName(), criteriaEntry.key(), query)) {
                return false;
            }
        }
        return true;
    }

    private final List<Rule> rules;

    public RuleTable(List<Rule> rules) {
        this.rules = rules;
    }

    public BestMatch queryBestMatch(final DatabaseQuery query) {
        for (Rule rule : rules) {
            if (match(query, rule.criteria())) {
                return new BestMatch(QueryReturnCode.SUCCESS, rule.response());
            }
        }
        return BestMatch.FAILURE;
    }
}
