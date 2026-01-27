package io.github.drakonforge.outspoken.ecs.system.context;

import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.event.UpdateEntityContextEvent;

public abstract class EntityContextSystem extends EntityEventSystem<EntityStore, UpdateEntityContextEvent> {

    protected EntityContextSystem() {
        super(UpdateEntityContextEvent.class);
    }

}
