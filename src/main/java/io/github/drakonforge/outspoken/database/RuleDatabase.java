package io.github.drakonforge.outspoken.database;

import io.github.drakonforge.outspoken.context.ContextManager;
import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.response.Response;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class RuleDatabase {
    public enum QueryReturnCode {
        SUCCESS, FAILURE
    }

    private record GroupCategory(String group, String category) {}

    public record BestMatchQueryResult(QueryReturnCode code, @Nullable Response response) {}

    private final ContextManager contextManager;
    private final Map<GroupCategory, RuleTable> groupCategoryToTable = new HashMap<>();

    public RuleDatabase(ContextManager contextManager) {
        this.contextManager = contextManager;
    }

    public ContextTable createBlankContextTable() {
        return new ContextTable(contextManager);
    }
}
