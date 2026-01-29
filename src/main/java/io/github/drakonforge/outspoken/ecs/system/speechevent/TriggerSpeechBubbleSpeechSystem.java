package io.github.drakonforge.outspoken.ecs.system.speechevent;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenConfig;
import io.github.drakonforge.outspoken.OutspokenConfig.SpeechBubbleMode;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import io.github.drakonforge.outspoken.speech.SpeechResult;
import io.github.drakonforge.outspoken.util.SpeechHelpers;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class TriggerSpeechBubbleSpeechSystem extends SpeechEventSystem {
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

        OutspokenConfig config = OutspokenPlugin.getInstance().getConfig().get();
        if (config.getSpeechBubbleMode() == SpeechBubbleMode.Never) {
            return;
        }

        World world = store.getExternalData().getWorld();

        SpeechStateComponent speechStateComponent = archetypeChunk.getComponent(i, SpeechStateComponent.getComponentType());
        assert speechStateComponent != null;

        // TODO: Doing the model logic here in case we can move it, can add it to main QueryDatabaseSpeechSystem later if we want
        // TODO: See if we can remove - .clone().add(new Vector3d(0, eyeHeight + 0.5f, 0))
        SpeechHelpers.createSpeechBubble(world, ref, speechStateComponent, result.origin(), result.text());
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
                SpeechStateComponent.getComponentType());
    }
}
