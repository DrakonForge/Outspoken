package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SpeechStateComponent implements Component<EntityStore> {

    public static ComponentType<EntityStore, SpeechStateComponent> getComponentType() {
        return OutspokenPlugin.getInstance().getSpeechStateComponentType();
    }

    private float speechCooldown = 0.0f;
    @Nullable
    private Ref<EntityStore> speechBubble = null;

    public void decrementSpeechCooldown(float deltaTime) {
        speechCooldown = Math.max(0.0f, speechCooldown - deltaTime);
    }

    public void setSpeechCooldown(float speechCooldown) {
        this.speechCooldown = speechCooldown;
    }

    public void setSpeechBubble(@Nullable Ref<EntityStore> speechBubble) {
        this.speechBubble = speechBubble;
    }

    @Nullable
    public Ref<EntityStore> getSpeechBubble() {
        return speechBubble;
    }

    public boolean isBusy() {
        return speechCooldown > 0.0f;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        SpeechStateComponent clone = new SpeechStateComponent();
        return clone;
    }
}
