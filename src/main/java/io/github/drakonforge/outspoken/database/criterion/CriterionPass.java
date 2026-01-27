package io.github.drakonforge.outspoken.database.criterion;

import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery.PassthroughType;

public class CriterionPass extends Criterion {

    private final float chanceToPassthrough;

    public CriterionPass(float chanceToPassthrough) {
        this.chanceToPassthrough = chanceToPassthrough;
    }

    @Override
    public boolean evaluate(String tableName, String key, RulebankQuery query) {
        RulebankQuery.PassthroughType passthroughType = query.getPassthroughType();
        if (passthroughType == PassthroughType.NEVER) {
            return true;
        }
        if (passthroughType == PassthroughType.CHANCE) {
            return ((float) Math.random()) >= chanceToPassthrough;
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 5;
    }
}
