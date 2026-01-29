package io.github.drakonforge.outspoken.ecs.system.speechtrigger;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage.EntitySource;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage.EnvironmentSource;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage.ProjectileSource;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage.Source;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.util.ContextTables;
import io.github.drakonforge.outspoken.util.SpeechEvents;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class DamageTakenSpeechSystem extends DamageEventSystem {

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(i);

        Source damageSource = damage.getSource();
        Ref<EntityStore> listenerRef = null;
        if (damageSource instanceof EntitySource entitySource) {
            listenerRef = entitySource.getRef();
        }

        if (damage.getAmount() > 0.0f) {
            OutspokenApi.triggerSpeechEvent(store, SpeechEvents.DAMAGE_TAKEN, ref, listenerRef, query -> {
                ContextTable contextTable = query.getOrCreateContextTable(ContextTables.EVENT);
                contextTable.set("DamageAmount", damage.getAmount());
                if (damageSource instanceof EntitySource) {
                    contextTable.set("IsProjectileDamage", damageSource instanceof ProjectileSource);
                    contextTable.set("DamageType", "Entity");
                }
                if (damageSource instanceof EnvironmentSource environmentSource) {
                    contextTable.set("DamageType", environmentSource.getType());
                }
            });
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(SpeechbankComponent.getComponentType(), Query.not(
                DeathComponent.getComponentType()));
    }
}
