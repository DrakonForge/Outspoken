package io.github.drakonforge.outspoken.ecs.system.speechevent;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import io.github.drakonforge.outspoken.speech.SpeechResult;
import io.github.drakonforge.outspoken.util.ChatBubbleHelpers;
import org.bouncycastle.crypto.engines.SM2Engine.Mode;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ChatBubbleSpeechEventSystem extends SpeechEventSystem {
    @Override
    public void handleSpeechEvent(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl SpeechEvent speechEvent) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        SpeechResult result = speechEvent.getSpeechResult();
        if (result == null) {
            return;
        }

        // OutspokenConfig config = OutspokenPlugin.getInstance().getConfig().get();
        World world = store.getExternalData().getWorld();

        ModelComponent modelComponent = archetypeChunk.getComponent(i, ModelComponent.getComponentType());
        assert modelComponent != null;
        float eyeHeight = modelComponent.getModel().getEyeHeight();

        // TODO: Doing the model logic here in case we can move it, can add it to main QueryDatabaseSpeechSystem later if we want
        ChatBubbleHelpers.createChatBubble(world, ref, result.origin().clone().add(new Vector3d(0, eyeHeight + 0.5f, 0)), result.text());
    }

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return OutspokenPlugin.getInstance().getInspectSpeechEventGroup();
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(SpeechbankComponent.getComponentType(), EntityContextComponent.getComponentType(),
                ModelComponent.getComponentType());
    }
}
