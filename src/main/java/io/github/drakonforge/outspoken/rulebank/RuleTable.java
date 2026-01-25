package io.github.drakonforge.outspoken.rulebank;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import io.github.drakonforge.outspoken.rulebank.RulebankQueryResult.BestMatch;
import io.github.drakonforge.outspoken.rulebank.Rule.CriteriaEntry;
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
        for (Rule rule : rules) {
            if (match(query, rule.criteria())) {
                return new BestMatch(RulebankQueryResult.QueryReturnCode.SUCCESS, rule.response());
            }
        }
        return BestMatch.FAILURE;
    }
}
