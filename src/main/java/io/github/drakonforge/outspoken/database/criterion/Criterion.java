package io.github.drakonforge.outspoken.database.criterion;

import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;

public abstract class Criterion {
    public abstract boolean evaluate(String tableName, String key, RulebankQuery query);
    public abstract int getPriority();
}
