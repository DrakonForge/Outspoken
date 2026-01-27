package io.github.drakonforge.outspoken;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.asset.RulebankAsset;
import io.github.drakonforge.outspoken.database.context.ContextManager;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import io.github.drakonforge.outspoken.database.rulebank.RuleDatabase;
import io.github.drakonforge.outspoken.asset.RuleDatabaseFactory;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import java.util.Map;
import javax.annotation.Nonnull;

public final class OutspokenApi {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static RuleDatabase database;

    public static void createDatabase() {
        Map<String, RulebankAsset> assetMap = RulebankAsset.getAssetMap().getAssetMap();
        ContextManager contextManager = new ContextManager();
        database = RuleDatabaseFactory.createFromAssetMap(assetMap, contextManager);
    }

    // This library doesn't only work on speech, but
    public static void triggerSpeechEvent(@Nonnull Store<EntityStore> store, @Nonnull Ref<EntityStore> originRef, String category) {
        SpeechbankComponent speechbank = store.getComponent(originRef, SpeechbankComponent.getComponentType());
        if (speechbank == null) {
            // TODO: Log warning
            return;
        }
        String group = speechbank.getGroupName();
        RulebankQuery query = new RulebankQuery(group, category);
        SpeechEvent event = new SpeechEvent(query);
        store.invoke(originRef, event);
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
