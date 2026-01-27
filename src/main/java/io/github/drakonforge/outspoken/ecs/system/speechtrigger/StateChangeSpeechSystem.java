package io.github.drakonforge.outspoken.ecs.system.speechtrigger;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.event.ChangeNpcStateEvent;
import io.github.drakonforge.outspoken.util.ContextTables;
import io.github.drakonforge.outspoken.util.SpeechEvents;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class StateChangeSpeechSystem extends EntityEventSystem<EntityStore, ChangeNpcStateEvent> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public StateChangeSpeechSystem() {
        super(ChangeNpcStateEvent.class);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl ChangeNpcStateEvent changeNpcStateEvent) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        LOGGER.atInfo().log("State change from " + changeNpcStateEvent.getPrevState() + " to " + changeNpcStateEvent.getCurrentState());
        OutspokenApi.triggerSpeechEvent(store, ref, SpeechEvents.STATE_CHANGE, query -> {
            ContextTable contextTable = OutspokenApi.createBlankContextTable();
            contextTable.set("State", changeNpcStateEvent.getCurrentState());
            contextTable.set("PrevState", changeNpcStateEvent.getPrevState());
            query.addContextTable(ContextTables.EVENT, contextTable);
        });
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(NPCEntity.getComponentType(), SpeechbankComponent.getComponentType());
    }
}
