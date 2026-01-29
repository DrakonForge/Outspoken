package io.github.drakonforge.outspoken.util;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
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
import io.github.drakonforge.outspoken.ecs.component.ChatBubbleComponent;

public final class ChatBubbleHelpers {
    public static void createChatBubble(World world, Ref<EntityStore> origin, Vector3d position, Message fullText) {
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
            holder.addComponent(ChatBubbleComponent.getComponentType(), new ChatBubbleComponent(fullText));

            world.getEntityStore().getStore().addEntity(holder, com.hypixel.hytale.component.AddReason.SPAWN);
        });
    }
}
