package io.github.drakonforge.outspoken.ecs.system.speechevent;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import io.github.drakonforge.outspoken.database.response.Response;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQueryResult.BestMatch;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQueryResult.QueryReturnCode;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class QuerySpeechSystem extends SpeechEventSystem {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void handleSpeechEvent(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl SpeechEvent speechEvent) {
        LOGGER.atFine().log("Starting query");
        BestMatch bestMatch = OutspokenApi.getDatabase().queryBestMatch(speechEvent.getQuery());
        LOGGER.atFine().log("Finished query");

        RulebankQuery query = speechEvent.getQuery();

        // TODO: Leverage the entity's speech state to display text
        if (bestMatch.code() != QueryReturnCode.SUCCESS) {
            LOGGER.atInfo().log("Query failed with status code: " + bestMatch.code().name() + " for " + query.getGroup() + "." + query.getCategory());
            speechEvent.setCancelled(true);
        } else {
            Response response = bestMatch.response();
            speechEvent.setResponse(response);
        }
    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemGroupDependency<>(Order.AFTER,
                OutspokenPlugin.getInstance().getInitSpeechEventGroup()), new SystemGroupDependency<>(Order.AFTER,
                        OutspokenPlugin.getInstance().getGatherSpeechEventGroup()),
                new SystemGroupDependency<>(Order.BEFORE,
                        OutspokenPlugin.getInstance().getInspectSpeechEventGroup()));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(SpeechbankComponent.getComponentType(), EntityContextComponent.getComponentType());
    }
}
