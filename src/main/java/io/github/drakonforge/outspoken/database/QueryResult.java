package io.github.drakonforge.outspoken.database;

import io.github.drakonforge.outspoken.database.RuleDatabase.QueryReturnCode;
import io.github.drakonforge.outspoken.response.Response;
import javax.annotation.Nullable;

public final class QueryResult {

    public record BestMatch(QueryReturnCode code, @Nullable Response response) {
        public static final BestMatch FAILURE = new BestMatch(QueryReturnCode.FAILURE, null);
    }

    private QueryResult() {}
}
