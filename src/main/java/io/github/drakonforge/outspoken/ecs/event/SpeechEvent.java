package io.github.drakonforge.outspoken.ecs.event;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.system.CancellableEcsEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.database.rulebank.RulebankQuery;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpeechEvent extends CancellableEcsEvent {
    @Nullable
    private Ref<EntityStore> listener;

    @Nonnull
    private final RulebankQuery query;


    public SpeechEvent(@Nonnull RulebankQuery query) {
        this.query = query;
    }

    public void setListener(@Nullable Ref<EntityStore> listener) {
        this.listener = listener;
    }

    @Nonnull
    public RulebankQuery getQuery() {
        return query;
    }

    @Nullable
    public Ref<EntityStore> getListener() {
        return listener;
    }
}
