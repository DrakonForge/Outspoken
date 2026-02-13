package io.github.drakonforge.outspoken.util;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.NonSerialized;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenConfig;
import io.github.drakonforge.outspoken.database.context.ContextTable;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.ecs.component.SpeechBubbleComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import io.github.drakonforge.outspoken.speech.SpeechResult;
import javax.annotation.Nullable;

public final class SpeechHelpers {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final float LINGER_AFTER_FINISHING_SECONDS = 2.0f;

    @Nullable
    public static String getListenerName(RulebankQuery query) {
        ContextTable listenerTable = query.getContextTable(ContextTables.LISTENER);
        if (listenerTable != null) {
            return listenerTable.getStringOrDefault("Name", null);
        }
        return null;
    }

    public static float getTimeToSpeakLine(Message text) {
        String textStr = text.getAnsiMessage();
        float defaultCharactersPerSecond = OutspokenConfig.get().getDefaultCharactersPerSecond();
        if (defaultCharactersPerSecond == 0) {
            LOGGER.atWarning().log("Default characters per second should not be 0");
            return 1.0f;
        }
        return textStr.length() / defaultCharactersPerSecond;
    }

    public static void createSpeechBubble(World world, Ref<EntityStore> anchor, @Nullable SpeechStateComponent speechStateComponent, SpeechResult result) {
        world.execute(() -> {
            Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
            ProjectileComponent projectileComponent = new ProjectileComponent("Projectile");
            holder.putComponent(ProjectileComponent.getComponentType(), projectileComponent);
            holder.putComponent(TransformComponent.getComponentType(), new TransformComponent(result.origin(), Vector3f.ZERO));
            holder.ensureComponent(UUIDComponent.getComponentType());
            holder.ensureComponent(Nameplate.getComponentType()); // TODO: Can initialize it with a message if we want
            holder.addComponent(EntityStore.REGISTRY.getNonSerializedComponentType(), NonSerialized.get()); // Prevent it from saving on chunk unload, like a particle

            if (projectileComponent.getProjectile() == null) {
                projectileComponent.initialize();
                if (projectileComponent.getProjectile() == null) {
                    return;
                }
            }

            holder.addComponent(NetworkId.getComponentType(), new NetworkId(world.getEntityStore().getStore().getExternalData().takeNextNetworkId()));
            float maxTime = result.timeToSpeakLine() + LINGER_AFTER_FINISHING_SECONDS;
            holder.addComponent(SpeechBubbleComponent.getComponentType(), new SpeechBubbleComponent(result.text(), anchor, result.timeToSpeakLine(), maxTime));

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
