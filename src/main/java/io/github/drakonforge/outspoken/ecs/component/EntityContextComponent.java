package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.event.UpdateEntityContextEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EntityContextComponent implements Component<EntityStore> {
    private ContextTable contextTable = null;

    public static ComponentType<EntityStore, EntityContextComponent> getComponentType() {
        return OutspokenPlugin.getInstance().getEntityContextComponentType();
    }

    public EntityContextComponent() {}

    public EntityContextComponent(@Nonnull ContextTable contextTable) {
        this.contextTable = contextTable;
    }

    public void setContext(ContextTable contextTable) {
        this.contextTable = contextTable;
    }

    public ContextTable updateAndGetContext(@Nonnull ComponentAccessor<EntityStore> entityStore) {
        boolean isInitial = contextTable == null;
        if (isInitial) {
            contextTable = OutspokenApi.createBlankContextTable();
        }
        UpdateEntityContextEvent event = new UpdateEntityContextEvent(contextTable, entityStore.getExternalData().getWorld(), isInitial);
        entityStore.invoke(event);
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
