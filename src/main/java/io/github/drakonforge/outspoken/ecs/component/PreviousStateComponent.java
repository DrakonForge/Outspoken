package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PreviousStateComponent implements Component<EntityStore> {
    public static ComponentType<EntityStore, PreviousStateComponent> getComponentType() {
        return OutspokenPlugin.get().getPreviousStateComponentType();
    }

    @Nullable
    private String prevState;

    public void setPrevState(@Nullable String prevState) {
        this.prevState = prevState;
    }

    @Nullable
    public String getPrevState() {
        return prevState;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        PreviousStateComponent clone = new PreviousStateComponent();
        clone.prevState = prevState;
        return clone;
    }
}
