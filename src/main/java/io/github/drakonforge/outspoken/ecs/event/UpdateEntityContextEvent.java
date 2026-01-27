package io.github.drakonforge.outspoken.ecs.event;

import com.hypixel.hytale.component.system.EcsEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.drakonforge.outspoken.context.ContextTable;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class UpdateEntityContextEvent extends EcsEvent {
    @Nonnull
    private final ContextTable entityContext;
    @Nonnull
    private final World world;
    private final boolean isInitial;

    public UpdateEntityContextEvent(@NonNullDecl ContextTable entityContext, @NonNullDecl World world, boolean isInitial) {
        this.entityContext = entityContext;
        this.world = world;
        this.isInitial = isInitial;
    }

    @NonNullDecl
    public ContextTable getEntityContext() {
        return entityContext;
    }

    @Nonnull
    public World getWorld() {
        return world;
    }

    public boolean isInitial() {
        return isInitial;
    }
}
