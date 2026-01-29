package io.github.drakonforge.outspoken.ecs.system.speechbubble;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.component.SpeechBubbleComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SpeechBubbleExpirySystem extends EntityTickingSystem<EntityStore> {
    private static final float EXPIRY_TIME = 5.0f;

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        SpeechBubbleComponent speechBubble = archetypeChunk.getComponent(i, SpeechBubbleComponent.getComponentType());
        assert speechBubble != null;

        speechBubble.addAge(v);
        Ref<EntityStore> anchor = speechBubble.getAnchor();
        if (speechBubble.getAge() > EXPIRY_TIME || anchor == null || !anchor.isValid()) {
            commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(SpeechBubbleComponent.getComponentType());
    }
}
