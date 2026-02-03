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
            DebugListenComponent::getDistance).add().build();

    public static ComponentType<EntityStore, DebugListenComponent> getComponentType() {
        return OutspokenPlugin.getInstance().getDebugListenComponentType();
    }

    private float distance;

    public DebugListenComponent() {}

    public DebugListenComponent(float distance) {
        this.distance = distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        DebugListenComponent clone = new DebugListenComponent();
        clone.distance = distance;
        return clone;
    }
}
