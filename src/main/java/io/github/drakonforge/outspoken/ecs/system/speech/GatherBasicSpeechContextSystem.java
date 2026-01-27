package io.github.drakonforge.outspoken.ecs.system.speech;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import io.github.drakonforge.outspoken.ecs.resource.WorldContextResource;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class GatherBasicSpeechContextSystem extends SpeechEventSystem {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl SpeechEvent speechEvent) {
        RulebankQuery query = speechEvent.getQuery();
            Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(i);
            EntityContextComponent contextComponent = archetypeChunk.getComponent(i, EntityContextComponent.getComponentType());
            assert contextComponent != null;
            ContextTable speaker = contextComponent.updateAndGetContext(entityRef, store);

            WorldContextResource worldContextResource = store.getResource(WorldContextResource.getResourceType());
            ContextTable world = worldContextResource.updateAndGetContext(store);

            // TODO: Make common table names into constants
            Ref<EntityStore> listenerRef = speechEvent.getListener();
            if (listenerRef != null) {
                EntityContextComponent listenerContextComponent = store.getComponent(listenerRef, EntityContextComponent.getComponentType());
                if (listenerContextComponent != null) {
                    ContextTable listener = listenerContextComponent.updateAndGetContext(listenerRef, store);
                    query.addContextTable("Listener", listener);
                }
            }

            query.addContextTable("World", world).addContextTable("Speaker", speaker);
    }

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return OutspokenPlugin.getInstance().getGatherSpeechEventGroup();
    }

    @NullableDecl
    @Override
    // TODO: Filter to Speechbank Component & EntityContextComponent
    public Query<EntityStore> getQuery() {
        return EntityContextComponent.getComponentType();
    }
}
