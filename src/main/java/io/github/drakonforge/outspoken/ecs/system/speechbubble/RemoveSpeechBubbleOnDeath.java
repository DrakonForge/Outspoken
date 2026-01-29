package io.github.drakonforge.outspoken.ecs.system.speechbubble;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RemoveSpeechBubbleOnDeath extends DeathSystems.OnDeathSystem {

    @Override
    public void onComponentAdded(@NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl DeathComponent deathComponent, @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        SpeechStateComponent speechStateComponent = store.getComponent(ref, SpeechStateComponent.getComponentType());
        assert speechStateComponent != null;

        Ref<EntityStore> speechBubbleRef = speechStateComponent.getSpeechBubble();
        if (speechBubbleRef != null && speechBubbleRef.isValid()) {
            commandBuffer.removeEntity(speechBubbleRef, RemoveReason.REMOVE);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return SpeechStateComponent.getComponentType();
    }
}
