package io.github.drakonforge.outspoken.ecs.system.context;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.DisplayNameComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.support.MarkedEntitySupport;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.event.UpdateEntityContextEvent;
import io.github.drakonforge.outspoken.util.NpcHelpers;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class UpdateBasicEntityContextSystems {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private UpdateBasicEntityContextSystems() {}

    public static class UpdateEntityStats extends EntityContextSystem {
        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                @NonNullDecl Store<EntityStore> store,
                @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                @NonNullDecl UpdateEntityContextEvent updateEntityContextEvent) {
            ContextTable context = updateEntityContextEvent.getEntityContext();
            EntityStatMap entityStatMap = archetypeChunk.getComponent(i, EntityStatMap.getComponentType());
            assert entityStatMap != null;

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

    public static class UpdateInCombat extends EntityContextSystem {

        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                @NonNullDecl Store<EntityStore> store,
                @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                @NonNullDecl UpdateEntityContextEvent updateEntityContextEvent) {
            World world = store.getExternalData().getWorld();
            DamageDataComponent damageData = archetypeChunk.getComponent(i, DamageDataComponent.getComponentType());
            assert damageData != null;
            updateEntityContextEvent.getEntityContext().set("InCombat", NpcHelpers.isInCombat(damageData, world));

        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(EntityContextComponent.getComponentType(), DamageDataComponent.getComponentType());
        }
    }

    public static class UpdateLocation extends EntityContextSystem {
        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                @NonNullDecl Store<EntityStore> store,
                @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                @NonNullDecl UpdateEntityContextEvent updateEntityContextEvent) {
            ContextTable context = updateEntityContextEvent.getEntityContext();
            TransformComponent transform = archetypeChunk.getComponent(i, TransformComponent.getComponentType());
            assert transform != null;

            Vector3d position = transform.getPosition();
            context.set("X", (float) position.getX());
            context.set("Y", (float) position.getY());
            context.set("Z", (float) position.getZ());

            // TODO: Can we grab region info too?
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(EntityContextComponent.getComponentType(), TransformComponent.getComponentType());
        }
    }

    public static class UpdateEffects extends EntityContextSystem {
        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                @NonNullDecl Store<EntityStore> store,
                @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                @NonNullDecl UpdateEntityContextEvent updateEntityContextEvent) {
            ContextTable context = updateEntityContextEvent.getEntityContext();
            EffectControllerComponent effectControllerComponent = archetypeChunk.getComponent(i, EffectControllerComponent.getComponentType());
            assert effectControllerComponent != null;

            ActiveEntityEffect[] effects = effectControllerComponent.getAllActiveEntityEffects();
            if (effects == null) {
                return;
            }
            Set<String> activeEffectIds = new HashSet<>();
            for (ActiveEntityEffect effect : effects) {
                int effectIndex = effect.getEntityEffectIndex();
                EntityEffect entityEffect = EntityEffect.getAssetMap().getAsset(effectIndex);
                if (entityEffect != null) {
                    activeEffectIds.add(entityEffect.getId());
                }
            }

            context.set("ActiveEffects", activeEffectIds);
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(EntityContextComponent.getComponentType(), EffectControllerComponent.getComponentType());
        }
    }

    public static class UpdateName extends EntityContextSystem {
        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                @NonNullDecl Store<EntityStore> store,
                @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                @NonNullDecl UpdateEntityContextEvent updateEntityContextEvent) {
            ContextTable context = updateEntityContextEvent.getEntityContext();

            if (updateEntityContextEvent.isInitial()) {
                DisplayNameComponent displayNameComponent = archetypeChunk.getComponent(i, DisplayNameComponent.getComponentType());
                assert displayNameComponent != null;
                Message displayName = displayNameComponent.getDisplayName();
                if (displayName != null) {
                    context.set("Name", displayName.getAnsiMessage());
                }
            }
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(EntityContextComponent.getComponentType(), DisplayNameComponent.getComponentType());
        }
    }

    public static class UpdateMovementState extends EntityContextSystem {
        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                @NonNullDecl Store<EntityStore> store,
                @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                @NonNullDecl UpdateEntityContextEvent updateEntityContextEvent) {
            ContextTable context = updateEntityContextEvent.getEntityContext();
            MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(i, MovementStatesComponent.getComponentType());
            assert movementStatesComponent != null;

            MovementStates movementStates = movementStatesComponent.getMovementStates();
            context.set("Crouching", movementStates.crouching || movementStates.forcedCrouching);
            context.set("Walking", movementStates.walking);
            context.set("Idle", movementStates.idle);
            context.set("Flying", movementStates.flying);
            context.set("Running", movementStates.running);
            context.set("Sprinting", movementStates.sprinting);
            context.set("Swimming", movementStates.swimming);
            context.set("Sitting", movementStates.sitting);
            context.set("Sleeping", movementStates.sleeping);
            context.set("Gliding", movementStates.gliding);
            context.set("Climbing", movementStates.climbing || movementStates.mantling);
            context.set("InFluid", movementStates.inFluid);
            context.set("OnGround", movementStates.onGround);
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(EntityContextComponent.getComponentType(), MovementStatesComponent.getComponentType());
        }
    }

    public static class UpdateNpc extends EntityContextSystem {

        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                @NonNullDecl Store<EntityStore> store,
                @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                @NonNullDecl UpdateEntityContextEvent updateEntityContextEvent) {
            ContextTable context = updateEntityContextEvent.getEntityContext();

            NPCEntity npcEntityComponent = archetypeChunk.getComponent(i,
                    Objects.requireNonNull(NPCEntity.getComponentType()));
            assert npcEntityComponent != null;

            context.set("Type", npcEntityComponent.getNPCTypeId());

            Inventory inventory = npcEntityComponent.getInventory();
            addInventoryContext(context, inventory);

            Role role = npcEntityComponent.getRole();
            if (role != null) {
                context.set("Role", role.getRoleName());
                String state = role.getStateSupport().getStateName();
                int separator = state.indexOf('.');
                if (separator > -1) {
                    context.set("State", state.substring(0, separator));
                    context.set("Substate", state.substring(separator + 1));
                } else {
                    context.set("State", state);
                    context.remove("Substate");
                }
                MotionController motionController = role.getActiveMotionController();
                context.set("NavState", motionController.getNavState().name());
                context.set("TargetDeltaSquared", (float) motionController.getTargetDeltaSquared());


                // TODO: Get entity targets?
                // MarkedEntitySupport markedEntitySupport = role.getMarkedEntitySupport();
                // Ref<EntityStore>[] entityTargets = markedEntitySupport.getEntityTargets();
                // for (int j = 0; j < entityTargets.length; ++j) {
                //     String slotName = markedEntitySupport.getSlotName(j);
                //     Ref<EntityStore> ref = markedEntitySupport.getMarkedEntityRef(j);
                //     if (ref != null && ref.isValid() && slotName != null) {
                //         DisplayNameComponent refName = store.getComponent(ref, DisplayNameComponent.getComponentType());
                //         String markedEntityName = refName == null ? "Unknown" : (refName.getDisplayName() == null ? "Unknown" : refName.getDisplayName().getAnsiMessage());
                //         LOGGER.atInfo().log("Found marked entity context for " + role.getRoleName() + " in state " + role.getStateSupport().getStateName() + ": " + slotName + " = " + markedEntityName);
                //     }
                // }
            }
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(EntityContextComponent.getComponentType(), NPCEntity.getComponentType());
        }
    }

    public static class UpdatePlayer extends EntityContextSystem {

        @Override
        public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                @NonNullDecl Store<EntityStore> store,
                @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                @NonNullDecl UpdateEntityContextEvent updateEntityContextEvent) {
            ContextTable context = updateEntityContextEvent.getEntityContext();
            boolean isInitial = updateEntityContextEvent.isInitial();

            Player player = archetypeChunk.getComponent(i, Player.getComponentType());
            assert player != null;
            if (isInitial) {
                context.set("Name", player.getDisplayName());
            }

            // TODO: Make this a helper and also add it in NPC entity
            Inventory inventory = player.getInventory();
            addInventoryContext(context, inventory);
        }

        @NullableDecl
        @Override
        public Query<EntityStore> getQuery() {
            return Query.and(EntityContextComponent.getComponentType(), Player.getComponentType());
        }
    }

    private static void addInventoryContext(ContextTable context, Inventory inventory) {
        ItemStack mainhandStack = inventory.getActiveHotbarItem();
        ItemStack offhandStack = inventory.getUtilityItem();
        if (mainhandStack != null) {
            context.set("Mainhand", mainhandStack.getItemId());
        } else {
            context.remove("Mainhand");
        }
        if (offhandStack != null) {
            context.set("Offhand", offhandStack.getItemId());
        } else {
            context.remove("Offhand");
        }
    }


}
