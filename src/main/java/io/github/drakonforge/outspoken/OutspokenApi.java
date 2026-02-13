package io.github.drakonforge.outspoken;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.asset.RuleDatabaseFactory;
import io.github.drakonforge.outspoken.asset.RulebankAsset;
import io.github.drakonforge.outspoken.database.context.ContextManager;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.database.rulebank.RuleDatabase;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class OutspokenApi {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static RuleDatabase database;

    public static void createDatabase() {
        Map<String, RulebankAsset> assetMap = RulebankAsset.getAssetMap().getAssetMap();
        ContextManager contextManager = new ContextManager();
        database = RuleDatabaseFactory.createFromAssetMap(assetMap, contextManager);
    }

    // This library doesn't only work on speech, but we provide some methods to support it

    public static void triggerSpeechEvent(@Nonnull Store<EntityStore> store,
            @Nonnull String category, @Nonnull Ref<EntityStore> originRef, @Nullable Ref<EntityStore> listenerRef,
            @Nullable Consumer<RulebankQuery> modifyQuery) {
        if (OutspokenConfig.get().shouldSkipEvent(category)) {
            return;
        }
        SpeechbankComponent speechbank = store.getComponent(originRef,
                SpeechbankComponent.getComponentType());
        if (speechbank == null) {
            // TODO: Log warning
            return;
        }
        String group = speechbank.getGroupName();
        RulebankQuery query = new RulebankQuery(group, category);
        if (modifyQuery != null) {
            modifyQuery.accept(query);
        }
        SpeechEvent event = new SpeechEvent(query);
        if (listenerRef != null && listenerRef.isValid()) {
            event.setListener(listenerRef);
        }
        store.invoke(originRef, event);
    }

    public static void triggerSpeechEvent(@Nonnull Store<EntityStore> store, String category, @Nonnull Ref<EntityStore> originRef,
            @Nullable Ref<EntityStore> listenerRef) {
        triggerSpeechEvent(store, category, originRef, listenerRef, null);
    }

    public static RuleDatabase getDatabase() {
        return database;
    }

    public static ContextManager getContextManager() {
        return database.getContextManager();
    }

    public static ContextTable createBlankContextTable() {
        return database.getContextManager().createBlankContextTable();
    }

    private OutspokenApi() {}
}
