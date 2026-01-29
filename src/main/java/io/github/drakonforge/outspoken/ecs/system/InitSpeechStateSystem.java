package io.github.drakonforge.outspoken.ecs.system;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class InitSpeechStateSystem extends HolderSystem<EntityStore> {

    @Override
    public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder,
            @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store) {
        holder.ensureComponent(SpeechStateComponent.getComponentType());
    }

    @Override
    public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder,
            @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store) {

    }

    @NonNullDecl
    @Override
    public Set<Dependency<EntityStore>> getDependencies() {
        return Set.of(new SystemDependency<>(Order.AFTER, InitDefaultNpcSpeechbankSystem.class));
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return SpeechbankComponent.getComponentType();
    }
}
