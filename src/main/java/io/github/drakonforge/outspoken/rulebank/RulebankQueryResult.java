package io.github.drakonforge.outspoken.rulebank;

import io.github.drakonforge.outspoken.response.Response;
import javax.annotation.Nullable;

public final class RulebankQueryResult {

    public enum QueryReturnCode {
        SUCCESS, FAILURE
    }

    public record BestMatch(QueryReturnCode code, @Nullable Response response) {
        public static final BestMatch FAILURE = new BestMatch(QueryReturnCode.FAILURE, null);
    }

    private RulebankQueryResult() {}
}
