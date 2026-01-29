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
        return OutspokenPlugin.getInstance().getChatBubbleComponentType();
    }

    private Message fullText;
    private float age;
    private Ref<EntityStore> anchor;

    public SpeechBubbleComponent() {}

    public SpeechBubbleComponent(Message fullText, Ref<EntityStore> anchor) {
        this.fullText = fullText;
        this.anchor = anchor;
    }

    public void addAge(float deltaTime) {
        age += deltaTime;
    }

    public void setFullText(Message text) {
        this.fullText = text;
    }

    public void setAnchor(Ref<EntityStore> anchor) {
        this.anchor = anchor;
    }

    public void clear() {
        this.anchor = null;
        this.fullText = Message.empty();
    }

    public float getAge() {
        return age;
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
