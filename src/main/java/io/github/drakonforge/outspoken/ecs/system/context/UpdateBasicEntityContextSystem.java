package io.github.drakonforge.outspoken.ecs.system.context;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.event.UpdateEntityContextEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UpdateBasicEntityContextSystem extends EntityContextSystem {

    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
            @NonNullDecl UpdateEntityContextEvent updateEntityContextEvent) {
        ContextTable context = updateEntityContextEvent.getEntityContext();
        boolean isInitial = updateEntityContextEvent.isInitial();

        // Stats
        EntityStatMap entityStatMap = archetypeChunk.getComponent(i, EntityStatMap.getComponentType());
        assert entityStatMap != null;
        addEntityStatMapContext(context, entityStatMap);
    }

    private void addEntityStatMapContext(ContextTable context, EntityStatMap entityStatMap) {
        EntityStatValue healthStat = entityStatMap.get(DefaultEntityStatTypes.getHealth());
        if (healthStat != null) {
            float maxHealth = healthStat.getMax();
            float health = healthStat.get();
            context.set("MaxHealth", maxHealth);
            context.set("Health", health);
            context.set("HealthPercentage", health / maxHealth);
        }
        EntityStatValue staminaStat = entityStatMap.get(DefaultEntityStatTypes.getStamina());
        if (staminaStat != null) {
            float maxStamina = staminaStat.getMax();
            float stamina = staminaStat.get();
            context.set("MaxStamina", maxStamina);
            context.set("Stamina", stamina);
            context.set("StaminaPercentage", stamina / maxStamina);
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(EntityContextComponent.getComponentType(), EntityStatMap.getComponentType());
    }
}
