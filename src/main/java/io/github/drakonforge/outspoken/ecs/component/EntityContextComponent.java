package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.event.UpdateEntityContextEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EntityContextComponent implements Component<EntityStore> {
    public static ComponentType<EntityStore, EntityContextComponent> getComponentType() {
        return OutspokenPlugin.getInstance().getEntityContextComponentType();
    }

    private ContextTable contextTable = null;
    private float contextUpdateCooldown = 0.0f;
    // TODO: Maybe want different tiers of updates, inventory that only updates every 5-10s, etc.

    public EntityContextComponent() {}

    public EntityContextComponent(@Nonnull ContextTable contextTable) {
        this.contextTable = contextTable;
    }

    public void decrementUpdateCooldown(float deltaTime) {
        contextUpdateCooldown = Math.max(contextUpdateCooldown - deltaTime, 0.0f);
    }

    public boolean isUpdateThrottled() {
        return contextUpdateCooldown > 0.0f;
    }

    public void setContext(ContextTable contextTable) {
        this.contextTable = contextTable;
    }

    public ContextTable updateAndGetContext(@Nonnull Ref<EntityStore> entityRef, @Nonnull ComponentAccessor<EntityStore> entityStore) {
        boolean isInitial = contextTable == null;
        if (isInitial) {
            contextTable = OutspokenApi.createBlankContextTable();
        }
        if (isInitial || !isUpdateThrottled()) {
            UpdateEntityContextEvent event = new UpdateEntityContextEvent(contextTable, entityStore.getExternalData().getWorld(), isInitial);
            entityStore.invoke(entityRef, event);
            contextUpdateCooldown = OutspokenPlugin.getInstance().getConfig().get().getContextThrottleCooldown();
        }
        return contextTable;
    }

    @Nullable
    public ContextTable getContext() {
        return contextTable;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        // TODO: Probably actually clone the context table
        EntityContextComponent clone = new EntityContextComponent(contextTable);
        return clone;
    }
}
