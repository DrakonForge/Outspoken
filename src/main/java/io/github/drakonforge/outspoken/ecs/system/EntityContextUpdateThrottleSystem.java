package io.github.drakonforge.outspoken.ecs.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EntityContextUpdateThrottleSystem extends DelayedEntitySystem<EntityStore> {

    private final ComponentType<EntityStore, EntityContextComponent> entityContextComponentType;

    public EntityContextUpdateThrottleSystem(ComponentType<EntityStore, EntityContextComponent> entityContextComponentType) {
        super(0.2f);
        this.entityContextComponentType = entityContextComponentType;
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        EntityContextComponent entityContext = archetypeChunk.getComponent(i, entityContextComponentType);
        assert entityContext != null;
        if (entityContext.isUpdateThrottled()) {
            entityContext.decrementUpdateCooldown(v);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return entityContextComponentType;
    }
}
