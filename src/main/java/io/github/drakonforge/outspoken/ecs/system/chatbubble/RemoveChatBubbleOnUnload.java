package io.github.drakonforge.outspoken.ecs.system.chatbubble;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RemoveChatBubbleOnUnload extends HolderSystem<EntityStore> {

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

        Ref<EntityStore> chatBubbleRef = speechStateComponent.getChatBubble();
        if (chatBubbleRef != null && chatBubbleRef.isValid()) {
            store.removeEntity(chatBubbleRef, RemoveReason.REMOVE);
        }
    }
}
