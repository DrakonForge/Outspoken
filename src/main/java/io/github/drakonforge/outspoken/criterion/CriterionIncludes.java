package io.github.drakonforge.outspoken.criterion;

import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.rulebank.RulebankQuery;

public class CriterionIncludes extends CriterionInvertible {

    private final int value;

    public CriterionIncludes(int value, boolean invert) {
        super(invert);
        this.value = value;
    }

    @Override
    public boolean evaluate(String tableName, String key, RulebankQuery query) {
        ContextTable contextTable = query.getContextTable(tableName);
        if (contextTable != null) {
            return invert != contextTable.doesListContainValue(key, value);
        }
        return invert;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
