package io.github.drakonforge.outspoken.ecs.resource;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.event.UpdateWorldContextEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class WorldContextResource implements Resource<EntityStore> {

    public static ResourceType<EntityStore, WorldContextResource> getResourceType() {
        return OutspokenPlugin.get().getWorldContextResourceType();
    }

    @Nullable
    private ContextTable worldContext = null;

    public ContextTable updateAndGetContext(@Nonnull ComponentAccessor<EntityStore> entityStore) {
        boolean isInitial = worldContext == null;
        if (isInitial) {
            worldContext = OutspokenApi.createBlankContextTable();
        }
        UpdateWorldContextEvent event = new UpdateWorldContextEvent(worldContext, entityStore.getExternalData().getWorld(), isInitial);
        // Invoke world event
        entityStore.invoke(event);
        return worldContext;
    }

    @Nullable
    public ContextTable getContext() {
        return worldContext;
    }

    @NullableDecl
    @Override
    public Resource<EntityStore> clone() {
        WorldContextResource clone = new WorldContextResource();
        clone.worldContext = worldContext;
        return clone;
    }
}
