package io.github.drakonforge.outspoken;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import io.github.drakonforge.outspoken.asset.RulebankAsset;
import io.github.drakonforge.outspoken.command.OutspokenCommand;
import io.github.drakonforge.outspoken.context.ContextManager;
import io.github.drakonforge.outspoken.context.ContextTable;
import io.github.drakonforge.outspoken.rulebank.RuleDatabase;
import io.github.drakonforge.outspoken.rulebank.RuleDatabaseGenerator;
import io.github.drakonforge.outspoken.rulebank.RulebankQuery;
import io.github.drakonforge.outspoken.rulebank.RulebankQuery.PassthroughType;
import io.github.drakonforge.outspoken.rulebank.RulebankQueryResult;
import io.github.drakonforge.outspoken.rulebank.RulebankQueryResult.BestMatch;
import java.util.Map;
import java.util.Map.Entry;
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
        }

        this.getCommandRegistry().registerCommand(new OutspokenCommand());

        // TODO Can also set loadsAfter()
    }

    @Override
    protected void start() {
        OutspokenApi.createDatabase();
        LOGGER.atInfo().log("Outspoken database initialized.");
    }
}