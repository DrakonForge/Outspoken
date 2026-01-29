package io.github.drakonforge.outspoken.ecs.system.speechevent;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenConfig;
import io.github.drakonforge.outspoken.OutspokenConfig.ChatMessageMode;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import io.github.drakonforge.outspoken.speech.SpeechResult;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ChatSpeechEventSystem extends SpeechEventSystem {

    @Override
    public void handleSpeechEvent(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl SpeechEvent speechEvent) {
        SpeechResult result = speechEvent.getSpeechResult();
        if (result == null) {
            return;
        }

        OutspokenConfig config = OutspokenPlugin.getInstance().getConfig().get();
        World world = store.getExternalData().getWorld();
        Message speechLine = result.text();
        Message speakerName = result.displayName();
        // Message outputLine = Message.translation("chat.outspoken.speechLine").param("speaker", speakerName).param("message", speechLine);
        // TODO: Replace with translation once I figure out how to fix it
        Message outputLine = Message.join(speakerName, Message.raw(" (NPC): "), speechLine);
        if (config.getChatMessageMode() == ChatMessageMode.World) {
            world.sendMessage(outputLine);
        } else if (config.getChatMessageMode() == ChatMessageMode.Local) {
            float maxDistance = config.getSpeechMessageVisibleDistance();
            store.forEachEntityParallel(Query.and(PlayerRef.getComponentType(), TransformComponent.getComponentType()), (index, chunk, buffer) -> {
                TransformComponent transform = chunk.getComponent(index, TransformComponent.getComponentType());
                assert transform != null;
                if (transform.getPosition().distanceSquaredTo(result.origin()) <= maxDistance * maxDistance) {
                    PlayerRef playerRefComponent = chunk.getComponent(index, PlayerRef.getComponentType());
                    assert playerRefComponent != null;
                    playerRefComponent.sendMessage(outputLine);
                }
            });
        }
    }

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return OutspokenPlugin.getInstance().getInspectSpeechEventGroup();
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(SpeechbankComponent.getComponentType(), EntityContextComponent.getComponentType());
    }
}
