package io.github.drakonforge.outspoken.rulebank;

import io.github.drakonforge.outspoken.criterion.Criterion;
import io.github.drakonforge.outspoken.response.Response;
import java.util.List;

public record Rule(List<CriteriaEntry> criteria, Response response, int priority) {

    public record CriteriaEntry(String tableName, String key, Criterion criterion) {}
}
