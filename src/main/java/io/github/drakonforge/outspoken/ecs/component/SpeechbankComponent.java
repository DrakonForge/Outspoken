package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class SpeechbankComponent {

    public static final BuilderCodec<SpeechbankComponent> CODEC = BuilderCodec.builder(
                    SpeechbankComponent.class, SpeechbankComponent::new)
            .append(new KeyedCodec<>("Group", Codec.STRING, true),
                    (data, value) -> data.groupName = value, SpeechbankComponent::getGroupName)
            .documentation("TODO")
            .add()
            .documentation("TODO")
            .build();

    private String groupName;

    public SpeechbankComponent() {
    }

    public String getGroupName() {
        return groupName;
    }

}
