package io.github.drakonforge.outspoken.ecs.system.context;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.event.UpdateWorldContextEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UpdateBasicWorldContextSystem extends WorldContextSystem {

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl UpdateWorldContextEvent updateWorldContextEvent) {
        ContextTable context = updateWorldContextEvent.getWorldContext();
        World world = updateWorldContextEvent.getWorld();
        WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());

        // Update context
        context.set("Hour", worldTimeResource.getCurrentHour());
        context.set("Name", world.getName());
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.empty();
    }
}
