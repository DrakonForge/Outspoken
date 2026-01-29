package io.github.drakonforge.outspoken.ecs.system.chatbubble;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.component.ChatBubbleComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ChatBubbleExpirySystem extends EntityTickingSystem<EntityStore> {
    private static final float EXPIRY_TIME = 5.0f;

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        ChatBubbleComponent chatBubble = archetypeChunk.getComponent(i, ChatBubbleComponent.getComponentType());
        assert chatBubble != null;

        chatBubble.addAge(v);
        if (chatBubble.getAge() > EXPIRY_TIME) {
            commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(ChatBubbleComponent.getComponentType());
    }
}
