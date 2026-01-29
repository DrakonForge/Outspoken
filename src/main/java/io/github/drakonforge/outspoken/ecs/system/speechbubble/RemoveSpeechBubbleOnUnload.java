package io.github.drakonforge.outspoken.ecs.system.speechbubble;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RemoveSpeechBubbleOnUnload extends HolderSystem<EntityStore> {

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return SpeechStateComponent.getComponentType();
    }

    @Override
    public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder,
            @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {

    }

    @Override
    public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder,
            @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store) {
        SpeechStateComponent speechStateComponent = holder.getComponent(SpeechStateComponent.getComponentType());
        assert speechStateComponent != null;

        Ref<EntityStore> speechBubbleRef = speechStateComponent.getSpeechBubble();
        if (speechBubbleRef != null && speechBubbleRef.isValid()) {
            // TODO: This needs to be run in command buffer
            // store.removeEntity(speechBubbleRef, RemoveReason.REMOVE);
        }
    }
}
