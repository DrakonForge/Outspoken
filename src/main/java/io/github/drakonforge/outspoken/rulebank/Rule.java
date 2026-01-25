package io.github.drakonforge.outspoken.rulebank;

import io.github.drakonforge.outspoken.criterion.Criterion;
import io.github.drakonforge.outspoken.response.Response;
import java.util.List;
import javax.annotation.Nonnull;

public record Rule(List<CriteriaEntry> criteria, @Nonnull Response response, int priority) {

    public record CriteriaEntry(String tableName, String key, Criterion criterion) {}
}
