package io.github.drakonforge.outspoken.ecs.system;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.drakonforge.outspoken.OutspokenConfig;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import java.util.Objects;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class InitDefaultNpcSpeechbankSystem extends HolderSystem<EntityStore> {

    @Override
    public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder,
            @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {
        NPCEntity npcComponent = holder.getComponent(
                Objects.requireNonNull(NPCEntity.getComponentType()));
        assert npcComponent != null;

        String id = npcComponent.getNPCTypeId();
        String speechbankGroup = OutspokenConfig.get().getSpeechGroupFor(id);
        if (speechbankGroup != null) {
            holder.addComponent(SpeechbankComponent.getComponentType(), new SpeechbankComponent(speechbankGroup));
        }
    }

    @Override
    public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder,
            @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store) {

    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(NPCEntity.getComponentType(), Query.not(SpeechbankComponent.getComponentType()));
    }
}
