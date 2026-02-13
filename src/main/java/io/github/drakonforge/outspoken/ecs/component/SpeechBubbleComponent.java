package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SpeechBubbleComponent implements Component<EntityStore> {
    public static ComponentType<EntityStore, SpeechBubbleComponent> getComponentType() {
        return OutspokenPlugin.get().getSpeechBubbleComponentType();
    }

    private Message fullText;
    private float age;
    private float finishTime;
    private float maxAge;
    private Ref<EntityStore> anchor;

    public SpeechBubbleComponent() {}

    public SpeechBubbleComponent(Message fullText, Ref<EntityStore> anchor, float finishTime, float maxAge) {
        this.fullText = fullText;
        this.anchor = anchor;
        this.finishTime = finishTime;
        this.maxAge = maxAge;
    }

    public void addAge(float deltaTime) {
        age += deltaTime;
    }

    public void clear() {
        this.anchor = null;
        this.fullText = Message.empty();
    }

    public float getAge() {
        return age;
    }

    public float getFinishTime() {
        return finishTime;
    }

    public float getMaxAge() {
        return maxAge;
    }

    public Message getFullText() {
        return fullText;
    }

    public Ref<EntityStore> getAnchor() {
        return anchor;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        SpeechBubbleComponent clone = new SpeechBubbleComponent();
        clone.age = age;
        clone.fullText = fullText;
        clone.anchor = anchor;
        return clone;
    }
}
