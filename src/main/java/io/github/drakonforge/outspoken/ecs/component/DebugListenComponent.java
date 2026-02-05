package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class DebugListenComponent implements Component<EntityStore> {

    public static final BuilderCodec<DebugListenComponent> CODEC = BuilderCodec.builder(
            DebugListenComponent.class, DebugListenComponent::new).append(new KeyedCodec<>("Distance", Codec.FLOAT, true), (data, value) -> data.distance = value,
            DebugListenComponent::getDistance).add().append(new KeyedCodec<>("Limit", Codec.INTEGER, true), (data, value) -> data.limit = value, data -> data.limit).add().build();

    public static ComponentType<EntityStore, DebugListenComponent> getComponentType() {
        return OutspokenPlugin.getInstance().getDebugListenComponentType();
    }

    private float distance;
    private int limit;

    public DebugListenComponent() {}

    public DebugListenComponent(float distance) {
        this.distance = distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void decrementLimit() {
        if (this.limit > 0) {
            this.limit--;
        }
    }

    public float getDistance() {
        return distance;
    }

    public boolean shouldExpire() {
        return this.limit == 0;
    }


    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        DebugListenComponent clone = new DebugListenComponent();
        clone.distance = distance;
        return clone;
    }
}
