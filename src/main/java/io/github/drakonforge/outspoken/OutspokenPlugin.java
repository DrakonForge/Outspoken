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
import io.github.drakonforge.outspoken.ecs.component.SpeechBubbleComponent;
import io.github.drakonforge.outspoken.ecs.component.EntityContextComponent;
import io.github.drakonforge.outspoken.ecs.component.PreviousStateComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechStateComponent;
import io.github.drakonforge.outspoken.ecs.component.SpeechbankComponent;
import io.github.drakonforge.outspoken.ecs.resource.WorldContextResource;
import io.github.drakonforge.outspoken.ecs.system.CooldownSpeechStateSystem;
import io.github.drakonforge.outspoken.ecs.system.InitDefaultNpcSpeechbankSystem;
import io.github.drakonforge.outspoken.ecs.system.InitSpeechStateSystem;
import io.github.drakonforge.outspoken.ecs.system.context.UpdateBasicEntityContextSystems.UpdateLocation;
import io.github.drakonforge.outspoken.ecs.system.speechbubble.RemoveSpeechBubbleOnUnload;
import io.github.drakonforge.outspoken.ecs.system.speechbubble.SpeechBubbleExpirySystem;
import io.github.drakonforge.outspoken.ecs.system.speechbubble.SpeechBubbleTextDisplaySystem;
import io.github.drakonforge.outspoken.ecs.system.speechbubble.RemoveSpeechBubbleOnDeath;
import io.github.drakonforge.outspoken.ecs.system.speechevent.TriggerSpeechBubbleSpeechSystem;
import io.github.drakonforge.outspoken.ecs.system.speechevent.TriggerChatMessageSpeechSystem;
import io.github.drakonforge.outspoken.ecs.system.speechevent.LogSpeechEventSystem;
import io.github.drakonforge.outspoken.ecs.system.speechevent.SkipSpeechEventIfBusySystem;
import io.github.drakonforge.outspoken.ecs.system.speechtrigger.DamageTakenSpeechSystem;
import io.github.drakonforge.outspoken.ecs.system.speechtrigger.AmbientSpeechSystem;
import io.github.drakonforge.outspoken.ecs.system.EntityContextUpdateThrottleSystem;
import io.github.drakonforge.outspoken.ecs.system.InitEntityContextSystem;
import io.github.drakonforge.outspoken.ecs.system.InitResourceSystem;
import io.github.drakonforge.outspoken.ecs.system.context.UpdateBasicEntityContextSystems;
import io.github.drakonforge.outspoken.ecs.system.speechevent.GatherBasicSpeechContextSystem;
import io.github.drakonforge.outspoken.ecs.system.context.UpdateBasicWorldContextSystem;
import io.github.drakonforge.outspoken.ecs.system.speechevent.QueryDatabaseSpeechSystem;
import io.github.drakonforge.outspoken.ecs.system.ChangeNpcStateSystem;
import io.github.drakonforge.outspoken.ecs.system.speechtrigger.StateChangeSpeechSystem;
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

    private final Config<OutspokenConfig> config;
    private ComponentType<EntityStore, EntityContextComponent> entityContextComponentType;
    private ComponentType<EntityStore, SpeechbankComponent> speechbankComponentType;
    private ResourceType<EntityStore, WorldContextResource> worldContextResourceType;
    private SystemGroup<EntityStore> gatherSpeechEventGroup; // Gathers the context
    private SystemGroup<EntityStore> initSpeechEventGroup; // Filters the context
    private SystemGroup<EntityStore> inspectSpeechEventGroup; // After the speech event has fired
    private ComponentType<EntityStore, PreviousStateComponent> previousStateComponentType;
    private ComponentType<EntityStore, SpeechStateComponent> speechStateComponentType;
    private ComponentType<EntityStore, SpeechBubbleComponent> speechBubbleComponentType;


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
        this.speechbankComponentType = entityStoreRegistry.registerComponent(SpeechbankComponent.class, "Speechbank", SpeechbankComponent.CODEC);
        this.previousStateComponentType = entityStoreRegistry.registerComponent(PreviousStateComponent.class, PreviousStateComponent::new);
        this.speechBubbleComponentType = entityStoreRegistry.registerComponent(SpeechBubbleComponent.class, SpeechBubbleComponent::new);  // TODO: Remove chat bubble entity on world restart
        this.speechStateComponentType = entityStoreRegistry.registerComponent(SpeechStateComponent.class, SpeechStateComponent::new);

        this.gatherSpeechEventGroup = entityStoreRegistry.registerSystemGroup();
        this.initSpeechEventGroup = entityStoreRegistry.registerSystemGroup();
        this.inspectSpeechEventGroup = entityStoreRegistry.registerSystemGroup();
        entityStoreRegistry.registerSystem(new InitEntityContextSystem());
        entityStoreRegistry.registerSystem(new InitResourceSystem(this.worldContextResourceType));
        entityStoreRegistry.registerSystem(new InitDefaultNpcSpeechbankSystem());
        entityStoreRegistry.registerSystem(new ChangeNpcStateSystem());
        entityStoreRegistry.registerSystem(new EntityContextUpdateThrottleSystem(this.entityContextComponentType));
        entityStoreRegistry.registerSystem(new CooldownSpeechStateSystem());
        entityStoreRegistry.registerSystem(new InitSpeechStateSystem());

        // Speech event - Init context
        entityStoreRegistry.registerSystem(new SkipSpeechEventIfBusySystem());
        // entityStoreRegistry.registerSystem(new SkipSpeechEventSystem());

        // Speech event - Gather context
        entityStoreRegistry.registerSystem(new GatherBasicSpeechContextSystem());

        // Speech event - Query and inspect
        entityStoreRegistry.registerSystem(new QueryDatabaseSpeechSystem());
        entityStoreRegistry.registerSystem(new LogSpeechEventSystem());
        entityStoreRegistry.registerSystem(new TriggerChatMessageSpeechSystem());
        entityStoreRegistry.registerSystem(new TriggerSpeechBubbleSpeechSystem());

        // Triggers speech queries
        entityStoreRegistry.registerSystem(new AmbientSpeechSystem());
        entityStoreRegistry.registerSystem(new DamageTakenSpeechSystem());
        entityStoreRegistry.registerSystem(new StateChangeSpeechSystem());

        // Context update systems
        entityStoreRegistry.registerSystem(new UpdateBasicWorldContextSystem());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdateEntityStats());
        entityStoreRegistry.registerSystem(new UpdateLocation());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdateNpc());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdatePlayer());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdateInCombat());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdateMovementState());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdateEffects());
        entityStoreRegistry.registerSystem(new UpdateBasicEntityContextSystems.UpdateName());

        // BuilderFactory<SpeechbankComponent> speechbankFactory = new BuilderFactory<>(SpeechbankComponent.class, "Type", BuilderSpeechbank::new);
        // NPCPlugin.get().getBuilderManager().registerFactory(speechbankFactory);
        // NPCPlugin.get().registerCoreComponentType("Speechbank", BuilderSpeechbank::new);

        // Chat Bubble
        entityStoreRegistry.registerSystem(new SpeechBubbleExpirySystem());
        entityStoreRegistry.registerSystem(new SpeechBubbleTextDisplaySystem());
        entityStoreRegistry.registerSystem(new RemoveSpeechBubbleOnDeath());
        entityStoreRegistry.registerSystem(new RemoveSpeechBubbleOnUnload());

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

    public SystemGroup<EntityStore> getInitSpeechEventGroup() {
        return initSpeechEventGroup;
    }

    public SystemGroup<EntityStore> getGatherSpeechEventGroup() {
        return gatherSpeechEventGroup;
    }

    public SystemGroup<EntityStore> getInspectSpeechEventGroup() {
        return inspectSpeechEventGroup;
    }

    public ComponentType<EntityStore, SpeechbankComponent> getSpeechbankComponentType() {
        return speechbankComponentType;
    }

    public ComponentType<EntityStore, PreviousStateComponent> getPreviousStateComponentType() {
        return previousStateComponentType;
    }

    public ComponentType<EntityStore, SpeechBubbleComponent> getSpeechBubbleComponentType() {
        return speechBubbleComponentType;
    }

    public ComponentType<EntityStore, SpeechStateComponent> getSpeechStateComponentType() {
        return speechStateComponentType;
    }
}