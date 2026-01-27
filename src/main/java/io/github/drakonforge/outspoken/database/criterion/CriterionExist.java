package io.github.drakonforge.outspoken.database.criterion;

import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;

public class CriterionExist extends CriterionInvertible {

    public CriterionExist(boolean invert) {
        super(invert);
    }

    @Override
    public boolean evaluate(String tableName, String key, RulebankQuery query) {
        ContextTable contextTable = query.getContextTable(tableName);
        if (contextTable != null) {
            return invert != contextTable.contains(key);
        }
        return invert;
    }

    @Override
    public int getPriority() {
        return 4;
    }
}
