package io.github.drakonforge.outspoken.ecs.system.speechevent;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.event.SpeechEvent;
import io.github.drakonforge.outspoken.ecs.resource.WorldContextResource;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.util.ContextTables;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class GatherBasicSpeechContextSystem extends SpeechEventSystem {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static void addDistanceContext(TransformComponent speaker, TransformComponent listener, ContextTable table) {
        Vector3d speakerPos = speaker.getPosition();
        Vector3d listenerPos = listener.getPosition();
        double deltaX = listenerPos.getX() - speakerPos.getX();
        double deltaY = listenerPos.getY() - speakerPos.getY();
        double deltaZ = listenerPos.getZ() - speakerPos.getZ();
        table.set("Distance", (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
        table.set("HeightDifference", (float) deltaY);
    }

    @Override
    public void handleSpeechEvent(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl SpeechEvent speechEvent) {
        RulebankQuery query = speechEvent.getQuery();
            Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(i);
            EntityContextComponent contextComponent = archetypeChunk.getComponent(i, EntityContextComponent.getComponentType());
            assert contextComponent != null;

            ContextTable speakerTable = contextComponent.updateAndGetContext(entityRef, store);
            query.addContextTable(ContextTables.SPEAKER, speakerTable);

            WorldContextResource worldContextResource = store.getResource(WorldContextResource.getResourceType());
            ContextTable worldTable = worldContextResource.updateAndGetContext(store);
            query.addContextTable(ContextTables.WORLD, worldTable);

            // TODO: Make common table names into constants
            Ref<EntityStore> listenerRef = speechEvent.getListener();
            if (listenerRef != null) {
                EntityContextComponent listenerContextComponent = store.getComponent(listenerRef, EntityContextComponent.getComponentType());
                if (listenerContextComponent != null) {
                    ContextTable listenerTable = listenerContextComponent.updateAndGetContext(listenerRef, store);
                    query.addContextTable(ContextTables.LISTENER, listenerTable);
                }

                TransformComponent speakerTransform = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
                TransformComponent listenerTransform = store.getComponent(listenerRef, TransformComponent.getComponentType());
                if (speakerTransform != null && listenerTransform != null) {
                    ContextTable eventTable = query.getOrCreateContextTable("Event");
                    addDistanceContext(speakerTransform, listenerTransform, eventTable);
                }
            }
    }



    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemGroupDependency<>(Order.AFTER, OutspokenPlugin.get().getInitSpeechEventGroup()), new SystemGroupDependency<>(Order.BEFORE, OutspokenPlugin.get().getGatherSpeechEventGroup()), new SystemGroupDependency<>(Order.BEFORE, OutspokenPlugin.get().getInspectSpeechEventGroup()));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return EntityContextComponent.getComponentType();
    }
}
