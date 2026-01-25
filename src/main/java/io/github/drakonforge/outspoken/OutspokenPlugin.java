package io.github.drakonforge.outspoken;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderFactory;
import io.github.drakonforge.outspoken.asset.RulebankAsset;
import io.github.drakonforge.outspoken.command.OutspokenCommand;
import io.github.drakonforge.outspoken.component.BuilderSpeechbank;
import io.github.drakonforge.outspoken.component.SpeechbankComponent;
import javax.annotation.Nonnull;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class OutspokenPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static OutspokenPlugin instance;

    public static OutspokenPlugin getInstance() {
        return instance;
    }

    public OutspokenPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        instance = this;
        LOGGER.atInfo().log("Setting up plugin " + this.getName() + " version " + this.getManifest().getVersion().toString());

        // TODO: For hot reloading
        if (AssetRegistry.getAssetStore(RulebankAsset.class) == null) {
            AssetRegistry.register(HytaleAssetStore.builder(RulebankAsset.class, new DefaultAssetMap<>()).setPath("Rulebank").setCodec(RulebankAsset.CODEC).setKeyFunction(RulebankAsset::getId).build());
            // TODO Can also set loadsAfter()
        }

        BuilderFactory<SpeechbankComponent> speechbankFactory = new BuilderFactory<>(SpeechbankComponent.class, "Type", BuilderSpeechbank::new);
        NPCPlugin.get().getBuilderManager().registerFactory(speechbankFactory);
        NPCPlugin.get().registerCoreComponentType("Speechbank", BuilderSpeechbank::new);

        this.getCommandRegistry().registerCommand(new OutspokenCommand());

    }

    @Override
    protected void start() {
        OutspokenApi.createDatabase();
        LOGGER.atInfo().log("Outspoken database initialized.");
    }
}