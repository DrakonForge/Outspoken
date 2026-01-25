package io.github.drakonforge.outspoken.rulebank;

import io.github.drakonforge.outspoken.response.Response;
import javax.annotation.Nonnull;

public final class RulebankQueryResult {

    public enum QueryReturnCode {
        SUCCESS,
        GENERIC_FAILURE,
        NO_VALID_ENTRY,
        MISSING_GROUP_CATEGORY,
    }

    public record BestMatch(QueryReturnCode code, @Nonnull Response response) {
        public static final BestMatch GENERIC_FAILURE = new BestMatch(QueryReturnCode.GENERIC_FAILURE, Response.EMPTY);
        public static final BestMatch NO_VALID_ENTRY = new BestMatch(QueryReturnCode.NO_VALID_ENTRY, Response.EMPTY);
        public static final BestMatch MISSING_GROUP_CATEGORY = new BestMatch(QueryReturnCode.MISSING_GROUP_CATEGORY, Response.EMPTY);
    }

    private RulebankQueryResult() {}
}
