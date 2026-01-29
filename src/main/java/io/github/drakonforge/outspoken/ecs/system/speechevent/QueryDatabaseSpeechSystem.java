package io.github.drakonforge.outspoken.ecs.system.speechevent;

import com.hypixel.hytale.common.util.FormatUtil;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.database.response.PlainTextResponse;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import io.github.drakonforge.outspoken.database.response.Response;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQueryResult.BestMatch;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQueryResult.QueryReturnCode;
import io.github.drakonforge.outspoken.speech.SpeechResult;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// This is the critical system that makes speech work. Upon receiving a speech event and gathering
// all the context, it queries the database and creates the result. Then inspect events can
// handle how the result is displayed.
public class QueryDatabaseSpeechSystem extends SpeechEventSystem {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void handleSpeechEvent(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl SpeechEvent speechEvent) {
        LOGGER.atFine().log("Starting query");
        long startTime = System.nanoTime();
        BestMatch bestMatch = OutspokenApi.getDatabase().queryBestMatch(speechEvent.getQuery());
        long endTime = System.nanoTime();
        LOGGER.atFine().log("Finished query in " + FormatUtil.nanosToString(endTime - startTime));

        RulebankQuery query = speechEvent.getQuery();

        // TODO: Leverage the entity's speech state to display text
        if (bestMatch.code() != QueryReturnCode.SUCCESS) {
            LOGGER.atInfo().log("Query failed with status code: " + bestMatch.code().name() + " for " + query.getGroup() + "." + query.getCategory());
            speechEvent.setCancelled(true);
            return;
        }

        TransformComponent transform = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
        assert transform != null;
        DisplayNameComponent displayNameComponent = archetypeChunk.getComponent(i, DisplayNameComponent.getComponentType());
        assert displayNameComponent != null;
        SpeechStateComponent speechStateComponent = archetypeChunk.getComponent(i, SpeechStateComponent.getComponentType());
        assert speechStateComponent != null;
        Response response = bestMatch.response();
        // TODO: Eventually support fancier speech responses
        if (response instanceof PlainTextResponse plainTextResponse) {
            String option = plainTextResponse.getRandomOption();
            // TODO: Do text replacement and other parsing here
            // TODO: Also add on the NPC's name, possibly as a separate option;
            SpeechResult speechResult = new SpeechResult(displayNameComponent.getDisplayName(), Message.raw(option), transform.getPosition().clone());
            speechEvent.setSpeechResult(speechResult);
            speechStateComponent.setSpeechCooldown(3.0f); // TODO: Calculate how long the line should take
        } else {
            LOGGER.atWarning().log("Speech response type currently not supported: " + response.getType());
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
        return Query.and(SpeechbankComponent.getComponentType(), EntityContextComponent.getComponentType(),
                TransformComponent.getComponentType(), DisplayNameComponent.getComponentType(),
                SpeechStateComponent.getComponentType());
    }
}
