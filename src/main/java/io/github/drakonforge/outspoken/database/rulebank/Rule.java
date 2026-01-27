package io.github.drakonforge.outspoken.database.rulebank;

import io.github.drakonforge.outspoken.database.criterion.Criterion;
import io.github.drakonforge.outspoken.database.response.Response;
import java.util.List;
import javax.annotation.Nonnull;

public record Rule(List<CriteriaEntry> criteria, @Nonnull Response response, int priority) {

    public record CriteriaEntry(String tableName, String key, Criterion criterion) {}
}
