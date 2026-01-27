package io.github.drakonforge.outspoken.ecs.system.context;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.event.UpdateEntityContextEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UpdateBasicPlayerContextSystem extends
        EntityContextSystem {

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

        Inventory inventory = player.getInventory();
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

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(EntityContextComponent.getComponentType(), Player.getComponentType());
    }
}
