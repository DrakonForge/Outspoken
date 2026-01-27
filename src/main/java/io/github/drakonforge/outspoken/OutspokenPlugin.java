package io.github.drakonforge.outspoken;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import io.github.drakonforge.outspoken.asset.RulebankAsset;
import io.github.drakonforge.outspoken.command.OutspokenCommand;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.resource.WorldContextResource;
import io.github.drakonforge.outspoken.ecs.system.InitDefaultNpcSpeechbankSystem;
import io.github.drakonforge.outspoken.ecs.system.speechtrigger.DamageTakenSpeechSystem;
import io.github.drakonforge.outspoken.ecs.system.speechtrigger.NearbyEntityAmbientSpeechSystem;
import io.github.drakonforge.outspoken.ecs.system.EntityContextUpdateThrottleSystem;
import io.github.drakonforge.outspoken.ecs.system.InitEntityContextSystem;
import io.github.drakonforge.outspoken.ecs.system.InitResourceSystem;
import io.github.drakonforge.outspoken.ecs.system.context.UpdateBasicEntityContextSystems;
import io.github.drakonforge.outspoken.ecs.system.speechevent.GatherBasicSpeechContextSystem;
import io.github.drakonforge.outspoken.ecs.system.context.UpdateBasicWorldContextSystem;
import io.github.drakonforge.outspoken.ecs.system.speechevent.QuerySpeechSystem;
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

    private Config<OutspokenConfig> config;
    private ComponentType<EntityStore, EntityContextComponent> entityContextComponentType;
    private ComponentType<EntityStore, SpeechbankComponent> speechbankComponent;
    private ResourceType<EntityStore, WorldContextResource> worldContextResourceType;
    private SystemGroup<EntityStore> gatherSpeechEventGroup; // Gathers the context
    private SystemGroup<EntityStore> filterSpeechEventGroup; // Filters the context
    private SystemGroup<EntityStore> inspectSpeechEventGroup; // After the speech event has fired


    public OutspokenPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        config = this.withConfig("Outspoken", OutspokenConfig.CODEC);
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

        ComponentRegistryProxy<EntityStore> entityStoreRegistry = this.getEntityStoreRegistry();
        this.worldContextResourceType = entityStoreRegistry.registerResource(WorldContextResource.class, WorldContextResource::new);
        this.entityContextComponentType = entityStoreRegistry.registerComponent(EntityContextComponent.class, EntityContextComponent::new);
        this.speechbankComponent = entityStoreRegistry.registerComponent(SpeechbankComponent.class, "Speechbank", SpeechbankComponent.CODEC);
        this.gatherSpeechEventGroup = entityStoreRegistry.registerSystemGroup();
        this.filterSpeechEventGroup = entityStoreRegistry.registerSystemGroup();
        this.inspectSpeechEventGroup = entityStoreRegistry.registerSystemGroup();
        entityStoreRegistry.registerSystem(new InitEntityContextSystem());
        entityStoreRegistry.registerSystem(new InitResourceSystem(this.worldContextResourceType));
        entityStoreRegistry.registerSystem(new InitDefaultNpcSpeechbankSystem());
        entityStoreRegistry.registerSystem(new EntityContextUpdateThrottleSystem(this.entityContextComponentType));
        entityStoreRegistry.registerSystem(new GatherBasicSpeechContextSystem());
        entityStoreRegistry.registerSystem(new QuerySpeechSystem());

        // Triggers speech queries
        entityStoreRegistry.registerSystem(new NearbyEntityAmbientSpeechSystem());
        entityStoreRegistry.registerSystem(new DamageTakenSpeechSystem());

        // Populates context table with basic context
        entityStoreRegistry.registerSystem(new UpdateBasicWorldContextSystem());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdateEntityStats());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdatePosition());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdateNpc());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdatePlayer());

        // BuilderFactory<SpeechbankComponent> speechbankFactory = new BuilderFactory<>(SpeechbankComponent.class, "Type", BuilderSpeechbank::new);
        // NPCPlugin.get().getBuilderManager().registerFactory(speechbankFactory);
        // NPCPlugin.get().registerCoreComponentType("Speechbank", BuilderSpeechbank::new);

        this.getCommandRegistry().registerCommand(new OutspokenCommand());

        config.save();
    }

    @Override
    protected void start() {
        OutspokenApi.createDatabase();
        LOGGER.atInfo().log("Outspoken database initialized.");
    }

    public ResourceType<EntityStore, WorldContextResource> getWorldContextResourceType() {
        return worldContextResourceType;
    }

    public ComponentType<EntityStore, EntityContextComponent> getEntityContextComponentType() {
        return entityContextComponentType;
    }

    public Config<OutspokenConfig> getConfig() {
        return config;
    }

    public SystemGroup<EntityStore> getGatherSpeechEventGroup() {
        return gatherSpeechEventGroup;
    }

    public SystemGroup<EntityStore> getFilterSpeechEventGroup() {
        return filterSpeechEventGroup;
    }

    public SystemGroup<EntityStore> getInspectSpeechEventGroup() {
        return inspectSpeechEventGroup;
    }

    public ComponentType<EntityStore, SpeechbankComponent> getSpeechbankComponentType() {
        return speechbankComponent;
    }
}