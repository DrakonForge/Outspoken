package io.github.drakonforge.outspoken.ecs.system.speechtrigger;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.util.NpcHelpers;
import io.github.drakonforge.outspoken.util.SpeechEvents;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class AmbientSpeechSystem extends DelayedEntitySystem<EntityStore> {

    public AmbientSpeechSystem() {
        super(1.0f);
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        World world = store.getExternalData().getWorld();
        DamageDataComponent damageDataComponent = archetypeChunk.getComponent(i, DamageDataComponent.getComponentType());
        assert damageDataComponent != null;
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        // If not actively in combat, updates happen much more slowly
        if (NpcHelpers.isInCombat(damageDataComponent, world) || OutspokenPlugin.getInstance().getConfig().get().shouldSkipEvent(SpeechEvents.AMBIENT_IDLE_MODIFIER)) {
            OutspokenApi.triggerSpeechEvent(store, ref, SpeechEvents.AMBIENT);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(SpeechbankComponent.getComponentType(), DamageDataComponent.getComponentType(), Query.not(
                DeathComponent.getComponentType()));
    }
}
