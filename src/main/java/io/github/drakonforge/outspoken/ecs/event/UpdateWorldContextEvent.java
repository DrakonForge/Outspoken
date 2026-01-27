package io.github.drakonforge.outspoken.ecs.event;

import com.hypixel.hytale.component.system.EcsEvent;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class UpdateWorldContextEvent extends EcsEvent {
    @Nonnull
    private final ContextTable worldContext;
    @Nonnull
    private final World world;
    private final boolean isInitial;

    public UpdateWorldContextEvent(@NonNullDecl ContextTable worldContext, @NonNullDecl World world, boolean isInitial) {
        this.worldContext = worldContext;
        this.world = world;
        this.isInitial = isInitial;
    }

    @Nonnull
    public ContextTable getWorldContext() {
        return worldContext;
    }

    @Nonnull
    public World getWorld() {
        return world;
    }

    public boolean isInitial() {
        return isInitial;
    }
}
