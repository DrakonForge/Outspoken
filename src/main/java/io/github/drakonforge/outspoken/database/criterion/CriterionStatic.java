package io.github.drakonforge.outspoken.database.criterion;

import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;

public class CriterionStatic extends CriterionInvertible {

    public static final CriterionStatic IS_TRUE = new CriterionStatic(0.0f, 0.0f, true);
    public static final CriterionStatic IS_FALSE = new CriterionStatic(0.0f, 0.0f, false);

    private final float min;
    private final float max;

    public CriterionStatic(float min, float max, boolean invert) {
        super(invert);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean evaluate(String tableName, String key, RulebankQuery query) {
        ContextTable contextTable = query.getContextTable(tableName);
        if (contextTable != null) {
            float value = contextTable.getRawValue(key);
            return compare(value);
        }
        return false;
    }

    @Override
    public int getPriority() {
        return 3;
    }

    private boolean compare(float value) {
        return invert != (min <= value && value <= max);
    }
}
