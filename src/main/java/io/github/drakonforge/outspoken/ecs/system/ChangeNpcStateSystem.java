package io.github.drakonforge.outspoken.ecs.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import io.github.drakonforge.outspoken.ecs.component.PreviousStateComponent;
import io.github.drakonforge.outspoken.ecs.event.ChangeNpcStateEvent;
import java.util.Objects;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

// TODO This system and related components are pretty generic, could be moved to a library
public class ChangeNpcStateSystem extends DelayedEntitySystem<EntityStore> {

    public ChangeNpcStateSystem() {
        super(0.5f);
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        NPCEntity npcEntity = archetypeChunk.getComponent(i,
                Objects.requireNonNull(NPCEntity.getComponentType()));
        assert npcEntity != null;

        Role role = npcEntity.getRole();
        if (role == null) {
            return;
        }
        PreviousStateComponent prevStateComponent = commandBuffer.ensureAndGetComponent(ref, PreviousStateComponent.getComponentType());

        String currentState = role.getStateSupport().getStateName();
        String prevState = prevStateComponent.getPrevState();

        if (!currentState.equals(prevState)) {
            store.invoke(ref, new ChangeNpcStateEvent(prevState, currentState));
        }

        prevStateComponent.setPrevState(currentState);
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return NPCEntity.getComponentType();
    }
}
