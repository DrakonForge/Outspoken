package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SpeechStateComponent implements Component<EntityStore> {

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return null;
    }
}
