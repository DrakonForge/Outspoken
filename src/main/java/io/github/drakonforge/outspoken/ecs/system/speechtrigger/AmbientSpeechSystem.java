package io.github.drakonforge.outspoken.ecs.system.speechtrigger;

import com.hypixel.hytale.common.util.TimeUtil;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.util.SpeechEvents;
import java.time.Duration;
import java.time.Instant;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class AmbientSpeechSystem extends DelayedEntitySystem<EntityStore> {

    public AmbientSpeechSystem() {
        super(5.0f);
    }

    @Override
    public void tick(float v, int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        World world = store.getExternalData().getWorld();
        DamageDataComponent damageDataComponent = archetypeChunk.getComponent(i, DamageDataComponent.getComponentType());
        assert damageDataComponent != null;
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);
        if (isOutOfCombat(damageDataComponent, world)) {
            OutspokenApi.triggerSpeechEvent(store, ref, SpeechEvents.AMBIENT_IDLE);
        } else {
            OutspokenApi.triggerSpeechEvent(store, ref, SpeechEvents.AMBIENT_COMBAT);
        }
    }

    // See OutOfCombatCondition
    private boolean isOutOfCombat(DamageDataComponent damageDataComponent, World world) {
        Instant lastCombatAction = damageDataComponent.getLastCombatAction();
        Duration delay = world.getGameplayConfig().getCombatConfig().getOutOfCombatDelay();
        return TimeUtil.compareDifference(lastCombatAction, world.getWorldConfig().getGameTime(), delay) >= 0;
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(SpeechbankComponent.getComponentType(), DamageDataComponent.getComponentType());
    }
}
