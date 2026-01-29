package io.github.drakonforge.outspoken.ecs.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class CooldownSpeechStateSystem extends DelayedEntitySystem<EntityStore> {

    public CooldownSpeechStateSystem() {
        super(0.2f);
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        SpeechStateComponent speechStateComponent = archetypeChunk.getComponent(i, SpeechStateComponent.getComponentType());
        assert speechStateComponent != null;
        if (speechStateComponent.isBusy()) {
            speechStateComponent.decrementSpeechCooldown(v);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return SpeechStateComponent.getComponentType();
    }
}
