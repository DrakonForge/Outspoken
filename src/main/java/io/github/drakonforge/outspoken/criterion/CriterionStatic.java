package io.github.drakonforge.outspoken.criterion;

import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.database.DatabaseQuery;

public class CriterionStatic extends CriterionInvertible {

    private final float min;
    private final float max;

    public CriterionStatic(float min, float max, boolean invert) {
        super(invert);
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean evaluate(String tableName, String key, DatabaseQuery query) {
        ContextTable contextTable = query.getContextTable(tableName);
        if (contextTable != null) {
            float value = contextTable.getRawValue(key);
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
