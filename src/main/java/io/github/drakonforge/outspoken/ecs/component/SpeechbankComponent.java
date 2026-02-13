package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.drakonforge.outspoken.OutspokenPlugin;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SpeechbankComponent implements Component<EntityStore> {

    public static final BuilderCodec<SpeechbankComponent> CODEC = BuilderCodec.builder(
                    SpeechbankComponent.class, SpeechbankComponent::new)
            .append(new KeyedCodec<>("Group", Codec.STRING, true),
                    (data, value) -> data.groupName = value, SpeechbankComponent::getGroupName)
            .add()
            .build();

    public static ComponentType<EntityStore, SpeechbankComponent> getComponentType() {
        return OutspokenPlugin.get().getSpeechbankComponentType();
    }

    private String groupName;

    protected SpeechbankComponent() {
    }

    public SpeechbankComponent(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        SpeechbankComponent clone = new SpeechbankComponent();
        clone.groupName = groupName;
        return clone;
    }
}
