package io.github.drakonforge.outspoken.ecs.system.speechevent;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.database.response.PlainTextResponse;
import io.github.drakonforge.outspoken.database.response.Response;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class LogSpeechEventSystem extends SpeechEventSystem {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl SpeechEvent speechEvent) {
        Response response = speechEvent.getResponse();
        if (response == null) {
            return;
        }

        if (response instanceof PlainTextResponse plainTextResponse) {
            LOGGER.atInfo().log(plainTextResponse.getRandomOption());
        } else {
            LOGGER.atInfo()
                    .log("Response type not yet supported: " + response.getType().name());
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
