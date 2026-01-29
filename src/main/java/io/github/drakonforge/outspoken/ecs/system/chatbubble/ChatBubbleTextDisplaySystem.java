package io.github.drakonforge.outspoken.ecs.system.chatbubble;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.component.ChatBubbleComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ChatBubbleTextDisplaySystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        ChatBubbleComponent chatBubble = archetypeChunk.getComponent(i, ChatBubbleComponent.getComponentType());
        Nameplate nameplate = archetypeChunk.getComponent(i, Nameplate.getComponentType());
        assert chatBubble != null;
        assert nameplate != null;

        // TODO: Can move out to another system later if we want
        Ref<EntityStore> anchor = chatBubble.getAnchor();
        if (anchor != null && anchor.isValid()) {
            TransformComponent anchorTransform = store.getComponent(anchor, TransformComponent.getComponentType());
            ModelComponent anchorModel = store.getComponent(anchor, ModelComponent.getComponentType());
            if (anchorTransform != null && anchorModel != null) {
                TransformComponent transform = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
                assert transform != null;
                transform.setPosition(anchorTransform.getPosition().clone().add(new Vector3d(0, anchorModel.getModel().getEyeHeight() + 0.5f, 0)));
            }
        }

        nameplate.setText(chatBubble.getFullText().getAnsiMessage());
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(ChatBubbleComponent.getComponentType(), Nameplate.getComponentType());
    }
}
