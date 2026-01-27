package io.github.drakonforge.outspoken.ecs.system.context;

import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.event.UpdateWorldContextEvent;

public abstract class WorldContextSystem extends EntityEventSystem<EntityStore, UpdateWorldContextEvent> {

    public WorldContextSystem() {
        super(UpdateWorldContextEvent.class);
    }
}
