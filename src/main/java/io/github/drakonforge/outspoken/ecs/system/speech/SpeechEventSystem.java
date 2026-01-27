package io.github.drakonforge.outspoken.ecs.system.speech;

import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;

public abstract class SpeechEventSystem extends EntityEventSystem<EntityStore, SpeechEvent> {

    protected SpeechEventSystem() {
        super(SpeechEvent.class);
    }
}
