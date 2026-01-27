package io.github.drakonforge.outspoken.database.rulebank;

import com.hypixel.hytale.math.util.MathUtil;
import io.github.drakonforge.outspoken.database.response.Response;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQueryResult.BestMatch;
import io.github.drakonforge.outspoken.database.rulebank.Rule.CriteriaEntry;
import java.util.ArrayList;
import java.util.List;

public class RuleTable {

    private static boolean match(RulebankQuery query, List<CriteriaEntry> criteria) {
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

    public BestMatch queryBestMatch(final RulebankQuery query) {
        int bestPriority = -99;
        List<Response> matchingResponses = new ArrayList<>();
        for (Rule rule : rules) {
            if (bestPriority >= 0 && rule.priority() < bestPriority) {
                break;
            }
            if (match(query, rule.criteria())) {
                bestPriority = rule.priority();
                matchingResponses.add(rule.response());
            }
        }
        if (matchingResponses.isEmpty()) {
            return BestMatch.NO_VALID_ENTRY;
        }

        // Not sure if this is the best strategy, but it works
        Response randomMatchingResponse = matchingResponses.get(MathUtil.floor(Math.random() * matchingResponses.size()));
        return new BestMatch(RulebankQueryResult.QueryReturnCode.SUCCESS, randomMatchingResponse);
    }
}
