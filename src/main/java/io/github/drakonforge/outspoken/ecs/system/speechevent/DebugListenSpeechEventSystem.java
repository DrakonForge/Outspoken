package io.github.drakonforge.outspoken.ecs.system.speechevent;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.message.MessageFormat;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.ecs.component.DebugListenComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import io.github.drakonforge.outspoken.speech.SpeechResult;
import java.util.List;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class DebugListenSpeechEventSystem extends SpeechEventSystem {

    @Override
    public void handleSpeechEvent(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl SpeechEvent speechEvent) {
        SpeechResult result = speechEvent.getSpeechResult();
        if (result == null) {
            return;
        }

        store.forEachEntityParallel(Query.and(PlayerRef.getComponentType(), TransformComponent.getComponentType(),
                DebugListenComponent.getComponentType()),  (index, chunk, buffer) -> {
            TransformComponent transform = chunk.getComponent(index, TransformComponent.getComponentType());
            DebugListenComponent debugListenComponent = chunk.getComponent(index, DebugListenComponent.getComponentType());
            assert transform != null;
            assert debugListenComponent != null;
            float maxDistanceSq = debugListenComponent.getDistance() * debugListenComponent.getDistance();
            if (transform.getPosition().distanceSquaredTo(result.origin()) <= maxDistanceSq) {
                PlayerRef playerRefComponent = chunk.getComponent(index, PlayerRef.getComponentType());
                assert playerRefComponent != null;
                playerRefComponent.sendMessage(createDebugOutput(speechEvent));
            }
        });
    }

    private Message createDebugOutput(SpeechEvent speechEvent) {
        SpeechResult result = speechEvent.getSpeechResult();
        RulebankQuery query = speechEvent.getQuery();
        // List<Message> contexts =
        //         Message.join(MessageFormat.list(Message.raw("CONTEXT"), ))
        return Message.raw("Nothing yet");
    }

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        return OutspokenPlugin.getInstance().getInspectSpeechEventGroup();
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return null;
    }
}
