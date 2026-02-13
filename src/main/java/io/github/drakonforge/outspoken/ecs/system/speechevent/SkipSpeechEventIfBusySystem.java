package io.github.drakonforge.outspoken.ecs.system.speechevent;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// TODO: See if we can skip in the API instead
public class SkipSpeechEventIfBusySystem extends SpeechEventSystem {

    @Override
    public void handleSpeechEvent(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl SpeechEvent speechEvent) {
        SpeechStateComponent speechStateComponent = archetypeChunk.getComponent(i, SpeechStateComponent.getComponentType());
        assert speechStateComponent != null;
        if (speechStateComponent.isBusy()) {
            speechEvent.setCancelled(true);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(EntityContextComponent.getComponentType(), SpeechStateComponent.getComponentType());
    }

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return OutspokenPlugin.get().getInitSpeechEventGroup();
    }
}
