package io.github.drakonforge.outspoken.criterion;

import io.github.drakonforge.outspoken.database.DatabaseQuery;

public abstract class Criterion {
    public abstract boolean evaluate(String tableName, String key, DatabaseQuery query);
    public abstract int getPriority();
}
