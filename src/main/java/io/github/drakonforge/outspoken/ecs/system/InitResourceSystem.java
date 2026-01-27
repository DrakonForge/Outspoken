package io.github.drakonforge.outspoken.ecs.system;

import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.StoreSystem;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.ecs.resource.WorldContextResource;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class InitResourceSystem extends StoreSystem<EntityStore> {

    @Nonnull
    private final ResourceType<EntityStore, WorldContextResource> worldContextResourceType;

    public InitResourceSystem(@Nonnull ResourceType<EntityStore, WorldContextResource> worldContextResourceType) {
        this.worldContextResourceType = worldContextResourceType;
    }

    @Override
    public void onSystemAddedToStore(@NonNullDecl Store<EntityStore> store) {
        World world = store.getExternalData().getWorld();
        WorldContextResource worldContextResource = store.getResource(worldContextResourceType);
        // Retrieve context once
        world.execute(() -> worldContextResource.updateAndGetContext(store));
    }

    @Override
    public void onSystemRemovedFromStore(@NonNullDecl Store<EntityStore> store) {

    }
}
