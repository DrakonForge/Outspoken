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
import com.hypixel.hytale.server.core.util.message.MessageFormat;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.ecs.component.DebugListenComponent;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import io.github.drakonforge.outspoken.speech.SpeechResult;
import io.github.drakonforge.outspoken.util.StringHelpers;
import java.util.Map;
import java.util.Map.Entry;
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

        World world = store.getExternalData().getWorld();
        world.execute(() -> store.forEachEntityParallel(Query.and(PlayerRef.getComponentType(), TransformComponent.getComponentType(),
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

                debugListenComponent.decrementLimit();
                if (debugListenComponent.shouldExpire()) {
                    buffer.removeComponent(chunk.getReferenceTo(index), DebugListenComponent.getComponentType());
                }
            }
        }));
    }

    private Message createDebugOutput(SpeechEvent speechEvent) {
        SpeechResult result = speechEvent.getSpeechResult();
        RulebankQuery query = speechEvent.getQuery();

        String group = query.getGroup();
        String category = query.getCategory();
        Map<String, ContextTable> contexts = query.getContexts();
        Message[] messages = new Message[contexts.size() + 1];
        int index = 0;

        messages[0] = Message.join(
                Message.raw(String.format("Received query result for %s.%s:\n", group, category)),
                Message.raw("DisplayName="),
                result.displayName(),
                Message.raw(", Result="),
                result.text(),
                Message.raw(", TimeToSpeakLine=" + result.timeToSpeakLine() + ", Origin=" + StringHelpers.vector3dToString(result.origin()) + "\n=== Context Tables ===")
        );

        for (Entry<String, ContextTable> entry : query.getContexts().entrySet()) {
            String tableName = entry.getKey();
            ContextTable table = entry.getValue();
            messages[++index] = MessageFormat.list(Message.raw("\n" + tableName + ":"), table.toMessages());
        }
        return Message.join(messages);
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
