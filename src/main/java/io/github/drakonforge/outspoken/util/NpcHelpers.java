package io.github.drakonforge.outspoken.util;

import com.hypixel.hytale.builtin.npccombatactionevaluator.CombatActionEvaluatorSystems.CombatConstructionData;
import com.hypixel.hytale.common.util.TimeUtil;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.support.MarkedEntitySupport;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public final class NpcHelpers {

    // See OutOfCombatCondition
    public static boolean isInCombat(@Nonnull DamageDataComponent damageDataComponent, @Nonnull World world) {
        Instant lastCombatAction = damageDataComponent.getLastCombatAction();
        Duration delay = world.getGameplayConfig().getCombatConfig().getOutOfCombatDelay();
        return TimeUtil.compareDifference(lastCombatAction, world.getWorldConfig().getGameTime(), delay) < 0;
    }

    @Nullable
    public static Ref<EntityStore> getLockedTarget(@NonNullDecl Ref<EntityStore> originRef, @NonNullDecl Store<EntityStore> store) {
        NPCEntity npcEntityComponent = store.getComponent(originRef,
                Objects.requireNonNull(NPCEntity.getComponentType()));
        // CombatConstructionData combatConstructionData = store.getComponent(originRef, CombatConstructionData.getComponentType());
        // if (npcEntityComponent == null || combatConstructionData == null) {
        if (npcEntityComponent == null) {
            return null;
        }
        Role role = npcEntityComponent.getRole();
        if (role == null) {
            return null;
        }
        MarkedEntitySupport markedEntitySupport = role.getMarkedEntitySupport();
        // int targetSlot = combatConstructionData.getMarkedTargetSlot();
        // if (targetSlot < 0 || targetSlot >= markedEntitySupport.getMarkedEntitySlotCount()) {
        //     return null;
        // }
        Ref<EntityStore> listenerRef = markedEntitySupport.getMarkedEntityRef("LockedTarget");
        if (listenerRef == null || !listenerRef.isValid()) {
            return null;
        }
        return listenerRef;
    }

    private NpcHelpers() {}


}
