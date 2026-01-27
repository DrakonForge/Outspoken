package io.github.drakonforge.outspoken.ecs.system;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.event.UpdateEntityContextEvent;
import java.util.Objects;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UpdateBasicNpcContextSystem  extends
        EntityEventSystem<EntityStore, UpdateEntityContextEvent> {

    public UpdateBasicNpcContextSystem() {
        super(UpdateEntityContextEvent.class);
    }

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl UpdateEntityContextEvent updateEntityContextEvent) {
        ContextTable context = updateEntityContextEvent.getEntityContext();
        boolean isInitial = updateEntityContextEvent.isInitial();

        NPCEntity npcEntityComponent = archetypeChunk.getComponent(i,
                Objects.requireNonNull(NPCEntity.getComponentType()));
        assert npcEntityComponent != null;

        context.set("Type", npcEntityComponent.getNPCTypeId());
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(EntityContextComponent.getComponentType(), NPCEntity.getComponentType());
    }
}
