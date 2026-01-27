package io.github.drakonforge.outspoken.ecs.component;

import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;

public class SpeechbankComponent {
    private String groupName;

    public SpeechbankComponent(BuilderSpeechbank builder, BuilderSupport builderSupport) {
        this.groupName = builder.getGroupName(builderSupport);
    }

    public String getGroupName() {
        return groupName;
    }

}
