package io.github.drakonforge.outspoken.ecs.system;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialStructure;
import com.hypixel.hytale.component.system.DelayedSystem;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenApi;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class NearbyEntityAmbientSpeechSystem extends DelayedSystem<EntityStore> {
    private static final float MIN_DISTANCE_FROM_OTHER_PLAYERS = 32.0f;
    private static final float SEARCH_DISTANCE = 32.0f;

    public NearbyEntityAmbientSpeechSystem() {
        super(5.0f);
    }

    @Override
    public void delayedTick(float v, int i, @NonNullDecl Store<EntityStore> store) {
        World world = store.getExternalData().getWorld();
        List<Vector3d> queriedPositions = new ArrayList<>();
        // TODO: Make a new spatial structure specifically for entities with speechbank components
        SpatialStructure<Ref<EntityStore>> entitySpatialStructure = store.getResource(EntityModule.get().getEntitySpatialResourceType()).getSpatialStructure();
        world.getPlayerRefs().forEach(playerRef ->
                processPlayer(playerRef, store, queriedPositions, entitySpatialStructure));


    }

    private void processPlayer(PlayerRef playerRef, @NonNullDecl Store<EntityStore> store,
            List<Vector3d> queriedPositions,
            SpatialStructure<Ref<EntityStore>> entitySpatialStructure) {
            Ref<EntityStore> ref = playerRef.getReference();
            if (ref == null) {
                // This should never happen
                return;
            }
            TransformComponent transform = store.getComponent(ref,
                    TransformComponent.getComponentType());
            assert transform != null;
            Vector3d position = transform.getPosition();
            if (areOtherPlayersNearby(position, queriedPositions)) {
                return;
            }

            queriedPositions.add(position);
            Ref<EntityStore> nearbyEntityRef = chooseRandomNearbyEntity(position, entitySpatialStructure);
            if (nearbyEntityRef != null) {
                OutspokenApi.triggerSpeechEvent(store, nearbyEntityRef, "Ambient");
            }
    }

    // Might not actually need this, depending on game feel
    // Maybe just rely on fallthrough chance
    private boolean areOtherPlayersNearby(Vector3d position, List<Vector3d> queriedPositions) {
        for (Vector3d otherPosition : queriedPositions) {
            if (position.distanceSquaredTo(otherPosition) < MIN_DISTANCE_FROM_OTHER_PLAYERS * MIN_DISTANCE_FROM_OTHER_PLAYERS) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private Ref<EntityStore> chooseRandomNearbyEntity(Vector3d position, SpatialStructure<Ref<EntityStore>> entitySpatialStructure) {
        List<Ref<EntityStore>> nearby = new ArrayList<>();
        entitySpatialStructure.collect(position, SEARCH_DISTANCE, nearby);
        if (nearby.isEmpty()) {
            return null;
        }
        return nearby.get(MathUtil.floor(Math.random() * nearby.size()));
    }
}
