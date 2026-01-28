package io.github.drakonforge.outspoken.util;

import com.hypixel.hytale.common.util.TimeUtil;
import com.hypixel.hytale.server.core.entity.damage.DamageDataComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nonnull;

public final class NpcHelpers {

    // See OutOfCombatCondition
    public static boolean isInCombat(@Nonnull DamageDataComponent damageDataComponent, @Nonnull World world) {
        Instant lastCombatAction = damageDataComponent.getLastCombatAction();
        Duration delay = world.getGameplayConfig().getCombatConfig().getOutOfCombatDelay();
        return TimeUtil.compareDifference(lastCombatAction, world.getWorldConfig().getGameTime(), delay) < 0;
    }

    private NpcHelpers() {}


}
