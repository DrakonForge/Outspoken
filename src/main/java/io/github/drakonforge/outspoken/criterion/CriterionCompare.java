package io.github.drakonforge.outspoken.criterion;

import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.rulebank.RulebankQuery;

public class CriterionCompare extends CriterionInvertible {

    private final float minDelta;
    private final float maxDelta;
    private final String otherTable;
    private final String otherKey;

    public CriterionCompare(float minDelta, float maxDelta, String otherTable, String otherKey,
            boolean invert) {
        super(invert);
        this.minDelta = minDelta;
        this.maxDelta = maxDelta;
        this.otherTable = otherTable;
        this.otherKey = otherKey;
    }

    @Override
    public boolean evaluate(String tableName, String key, RulebankQuery query) {
        ContextTable table1 = query.getContextTable(tableName);
        if (table1 == null) {
            return false;
        }
        ContextTable table2 = query.getContextTable(otherTable);
        if (table2 == null) {
            return false;
        }
        float value1 = table1.getRawValue(key);
        float value2 = table2.getRawValue(otherKey);
        return compare(value1 - value2);
    }

    private boolean compare(float delta) {
        return invert != (minDelta <= delta && delta <= maxDelta);
    }

    @Override
    public int getPriority() {
        return 3;
    }
}
