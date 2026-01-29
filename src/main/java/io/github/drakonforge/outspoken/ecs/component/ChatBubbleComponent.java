package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class ChatBubbleComponent implements Component<EntityStore> {
    public static ComponentType<EntityStore, ChatBubbleComponent> getComponentType() {
        return OutspokenPlugin.getInstance().getChatBubbleComponentType();
    }

    private Message fullText;
    private float age;

    public ChatBubbleComponent() {}

    public ChatBubbleComponent(Message fullText) {
        this.fullText = fullText;
    }

    public void addAge(float deltaTime) {
        age += deltaTime;
    }

    public void setFullText(Message text) {
        this.fullText = text;
    }

    public float getAge() {
        return age;
    }

    public Message getFullText() {
        return fullText;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        ChatBubbleComponent clone = new ChatBubbleComponent();
        clone.age = age;
        clone.fullText = fullText;
        return clone;
    }
}
