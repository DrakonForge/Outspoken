package io.github.drakonforge.outspoken;

import io.github.drakonforge.outspoken.asset.RulebankAsset;
import io.github.drakonforge.outspoken.context.ContextManager;
import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.rulebank.RuleDatabase;
import io.github.drakonforge.outspoken.rulebank.RuleDatabaseFactory;
import java.util.Map;

public final class OutspokenApi {
    private static RuleDatabase database;

    public static void createDatabase() {
        Map<String, RulebankAsset> assetMap = RulebankAsset.getAssetMap().getAssetMap();
        ContextManager contextManager = new ContextManager();
        database = RuleDatabaseFactory.createFromAssetMap(assetMap, contextManager);
    }

    public static RuleDatabase getDatabase() {
        return database;
    }

    public static ContextManager getContextManager() {
        return database.getContextManager();
    }

    public static ContextTable createBlankContextTable() {
        return new ContextTable(database.getContextManager());
    }

    private OutspokenApi() {}
}
