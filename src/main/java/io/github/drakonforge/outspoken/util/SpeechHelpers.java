package io.github.drakonforge.outspoken.util;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.ecs.component.SpeechBubbleComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import javax.annotation.Nullable;

public final class SpeechHelpers {
    @Nullable
    public static String getListenerName(RulebankQuery query) {
        ContextTable listenerTable = query.getContextTable(ContextTables.LISTENER);
        if (listenerTable != null) {
            return listenerTable.getStringOrDefault("Name", null);
        }
        return null;
    }

    public static void createSpeechBubble(World world, Ref<EntityStore> anchor, @Nullable SpeechStateComponent speechStateComponent, Vector3d position, Message fullText) {
        world.execute(() -> {
            Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
            ProjectileComponent projectileComponent = new ProjectileComponent("Projectile");
            holder.putComponent(ProjectileComponent.getComponentType(), projectileComponent);
            holder.putComponent(TransformComponent.getComponentType(), new TransformComponent(position, Vector3f.ZERO));
            holder.ensureComponent(UUIDComponent.getComponentType());
            holder.ensureComponent(Nameplate.getComponentType()); // TODO: Can initialize it with a message if we want

            if (projectileComponent.getProjectile() == null) {
                projectileComponent.initialize();
                if (projectileComponent.getProjectile() == null) {
                    return;
                }
            }

            holder.addComponent(NetworkId.getComponentType(), new NetworkId(world.getEntityStore().getStore().getExternalData().takeNextNetworkId()));
            // TODO: Set origin
            holder.addComponent(SpeechBubbleComponent.getComponentType(), new SpeechBubbleComponent(fullText, anchor));

            Store<EntityStore> store = world.getEntityStore().getStore();
            Ref<EntityStore> ref = store.addEntity(holder, AddReason.SPAWN);

            if (speechStateComponent != null) {
                Ref<EntityStore> oldRef = speechStateComponent.getSpeechBubble();
                if (oldRef != null && oldRef.isValid()) {
                    store.removeEntity(oldRef, RemoveReason.REMOVE);
                }
                speechStateComponent.setSpeechBubble(ref);
            }
        });
    }
}
