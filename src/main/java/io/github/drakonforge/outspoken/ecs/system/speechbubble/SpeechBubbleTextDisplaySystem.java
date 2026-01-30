package io.github.drakonforge.outspoken.ecs.system.speechbubble;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.component.SpeechBubbleComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SpeechBubbleTextDisplaySystem extends EntityTickingSystem<EntityStore> {

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        SpeechBubbleComponent speechBubble = archetypeChunk.getComponent(i, SpeechBubbleComponent.getComponentType());
        Nameplate nameplate = archetypeChunk.getComponent(i, Nameplate.getComponentType());
        assert speechBubble != null;
        assert nameplate != null;

        // TODO: Can move out to another system later if we want
        Ref<EntityStore> anchor = speechBubble.getAnchor();
        if (anchor != null && anchor.isValid()) {
            TransformComponent anchorTransform = store.getComponent(anchor, TransformComponent.getComponentType());
            ModelComponent anchorModel = store.getComponent(anchor, ModelComponent.getComponentType());
            if (anchorTransform != null && anchorModel != null) {
                TransformComponent transform = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
                assert transform != null;
                transform.setPosition(anchorTransform.getPosition().clone().add(new Vector3d(0, anchorModel.getModel().getEyeHeight() + 0.5f, 0)));
            }
        }

        String fullText = speechBubble.getFullText().getAnsiMessage();
        float progress = Math.clamp(speechBubble.getAge() / speechBubble.getFinishTime(), 0.0f, 1.0f);
        int numCharactersToShow = MathUtil.floor(fullText.length() * progress);
        String substring = fullText.substring(0, numCharactersToShow);
        nameplate.setText(substring);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(SpeechBubbleComponent.getComponentType(), Nameplate.getComponentType(), TransformComponent.getComponentType());
    }
}
