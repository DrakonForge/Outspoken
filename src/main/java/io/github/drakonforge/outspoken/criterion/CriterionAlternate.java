package io.github.drakonforge.outspoken.criterion;

import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.database.DatabaseQuery;
import it.unimi.dsi.fastutil.ints.IntList;

public class CriterionAlternate extends CriterionInvertible {

    private final IntList options;

    public CriterionAlternate(IntList options, boolean invert) {
        super(invert);
        this.options = options;
    }

    @Override
    public boolean evaluate(String tableName, String key, DatabaseQuery query) {
        ContextTable contextTable = query.getContextTable(tableName);
        if (contextTable != null) {
            float value = contextTable.getRawValue(key);
            return compare(value);
        }
        return false;
    }

    private boolean compare(float value) {
        return invert != options.contains((int) value);
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
