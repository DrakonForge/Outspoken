package io.github.drakonforge.outspoken.database.criterion;

import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.database.context.ContextTable.FactType;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import java.util.Optional;

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
            FactType type = contextTable.getType(key);
            if (type == FactType.STRING) {
                Optional<String> contextString = contextTable.getString(key);
                Optional<String> lookupString = OutspokenApi.getContextManager().getStringTable().lookup(value);
                if (contextString.isPresent() && lookupString.isPresent()) {
                    return contextString.get().contains(lookupString.get());
                }
            } else if (type.isArray()) {
                return invert != contextTable.doesListContainValue(key, value);
            }
        }
        return invert;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
