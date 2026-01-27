package io.github.drakonforge.outspoken.database.criterion;

import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import it.unimi.dsi.fastutil.ints.IntSet;

public class CriterionAlternate extends CriterionInvertible {

    private final IntSet options;

    public CriterionAlternate(IntSet options, boolean invert) {
        super(invert);
        this.options = options;
    }

    @Override
    public boolean evaluate(String tableName, String key, RulebankQuery query) {
        ContextTable contextTable = query.getContextTable(tableName);
        if (contextTable != null) {
            float value = contextTable.getRawValue(key);
            return compare((int) value);
        }
        return false;
    }

    private boolean compare(int value) {
        return invert != options.contains(value);
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
