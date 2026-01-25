package io.github.drakonforge.outspoken.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SpeechbankComponent {
    private String groupName;

    public SpeechbankComponent(BuilderSpeechbank builder, BuilderSupport builderSupport) {
        this.groupName = builder.getGroupName(builderSupport);
    }

    public String getGroupName() {
        return groupName;
    }

}
