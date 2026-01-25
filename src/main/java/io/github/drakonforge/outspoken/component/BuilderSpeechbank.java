package io.github.drakonforge.outspoken.component;

import com.google.gson.JsonElement;
import com.hypixel.hytale.server.npc.asset.builder.Builder;
import com.hypixel.hytale.server.npc.asset.builder.BuilderBase;
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState;
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport;
import com.hypixel.hytale.server.npc.asset.builder.holder.StringHolder;
import com.hypixel.hytale.server.npc.asset.builder.validators.StringNullOrNotEmptyValidator;
import com.hypixel.hytale.server.npc.util.expression.ExecutionContext;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BuilderSpeechbank extends BuilderBase<SpeechbankComponent> {

    private final StringHolder groupName = new StringHolder();

    @NullableDecl
    @Override
    public String getShortDescription() {
        return "Short description";
    }

    @NullableDecl
    @Override
    public String getLongDescription() {
        return "Long description";
    }

    public String getGroupName(BuilderSupport builderSupport) {
        return this.groupName.get(builderSupport.getExecutionContext());
    }

    @NullableDecl
    @Override
    public SpeechbankComponent build(BuilderSupport builderSupport) {
        return new SpeechbankComponent(this, builderSupport);
    }

    @Override
    public Class<SpeechbankComponent> category() {
        return SpeechbankComponent.class;
    }

    @NullableDecl
    @Override
    public BuilderDescriptorState getBuilderDescriptorState() {
        return BuilderDescriptorState.WorkInProgress;
    }

    @Override
    public Builder<SpeechbankComponent> readConfig(JsonElement data) {
        this.requireString(data, "Group", this.groupName, StringNullOrNotEmptyValidator.get(),
                BuilderDescriptorState.WorkInProgress, "Short description", "Long description");
        // TODO: Should probably be only nonempty, don't allow null later
        return super.readConfig(data);
    }

    @Override
    public boolean isEnabled(ExecutionContext executionContext) {
        return true;
    }
}
