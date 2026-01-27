package io.github.drakonforge.outspoken.ecs.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.resource.WorldContextResource;
import io.github.drakonforge.outspoken.response.PlainTextResponse;
import io.github.drakonforge.outspoken.response.Response;
import io.github.drakonforge.outspoken.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.rulebank.RulebankQueryResult.BestMatch;
import io.github.drakonforge.outspoken.rulebank.RulebankQueryResult.QueryReturnCode;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EntityAmbientSpeechSystem extends DelayedEntitySystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    public EntityAmbientSpeechSystem() {
        super(5.0f);
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        EntityContextComponent contextComponent = archetypeChunk.getComponent(i, EntityContextComponent.getComponentType());
        assert contextComponent != null;
        ContextTable speaker = contextComponent.updateAndGetContext(store);

        WorldContextResource worldContextResource = store.getResource(WorldContextResource.getResourceType());
        ContextTable world = worldContextResource.updateAndGetContext(store);

        RulebankQuery query = new RulebankQuery("Example", "Greeting");
        query.addContextTable("World", world).addContextTable("Speaker", speaker);

        BestMatch bestMatch = OutspokenApi.getDatabase().queryBestMatch(query);
        if (bestMatch.code() != QueryReturnCode.SUCCESS) {
            LOGGER.atInfo().log("Query failed with status code: " + bestMatch.code().name());
        } else {
            Response response = bestMatch.response();
            if (response instanceof PlainTextResponse plainTextResponse) {
                LOGGER.atInfo().log(plainTextResponse.getRandomOption());
            } else {
                LOGGER.atInfo().log("Response type not yet supported: " + response.getType().name());
            }
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return EntityContextComponent.getComponentType();
    }
}
