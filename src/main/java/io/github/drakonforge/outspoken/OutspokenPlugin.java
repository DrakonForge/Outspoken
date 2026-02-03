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
import java.util.HashMap;
import java.util.Map;
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
    private ResourceType<EntityStore, WorldContextResource> worldContextResourceType;

    // System Groups
    private SystemGroup<EntityStore> initSpeechEventGroup; // Can cancel the event before context is gathered
    private SystemGroup<EntityStore> gatherSpeechEventGroup; // Gathers the context
    private SystemGroup<EntityStore> inspectSpeechEventGroup; // After the speech event has fired

    // Components
    private ComponentType<EntityStore, EntityContextComponent> entityContextComponentType;
    private ComponentType<EntityStore, SpeechbankComponent> speechbankComponentType;
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
        // entityStoreRegistry.registerSystem(new RemoveSpeechBubbleOnUnload());

        registerDefaultSpeechGroups();

        this.getCommandRegistry().registerCommand(new OutspokenCommand());

        config.save();
    }

    private void registerDefaultSpeechGroups() {
        Map<String, String> speechGroupMap = new HashMap<>();
        // Kweebecs
        registerSpeechGroup(speechGroupMap, "Generic", new String[] {
                "Kweebec_Elder",
                "Kweebec_Merchant",
                "Kweebec_Razorleaf",
                "Kweebec_Razorleaf_Patrol",
                "Kweebec_Rootling",
                "Kweebec_Sapling",
                "Kweebec_Sapling_Orange",
                "Kweebec_Sapling_Pink",
                // "Kweebec_Seedling", // Cannot talk
                "Kweebec_Sproutling",
                "Kweebec_Sproutling_Patrol",
                "Temple_Kweebec",
                "Temple_Kweebec_Elder",
                "Temple_Kweebec_Merchant",
                "Temple_Kweebec_Razorleaf_Patrol",
                "Temple_Kweebec_Razorleaf_Patrol1",
                "Temple_Kweebec_Razorleaf_Patrol2",
                "Temple_Kweebec_Razorleaf_Patrol3",
                "Temple_Kweebec_Razorleaf_Patrol4",
        });
        // Goblins
        registerSpeechGroup(speechGroupMap, "Generic", new String[] {
                "Goblin_Duke",
                "Goblin_Duke_Phase_2",
                "Goblin_Duke_Phase_3_Fast",
                "Goblin_Duke_Phase_3_Slow",
                "Goblin_Hermit",
                "Goblin_Lobber",
                "Goblin_Lobber_Patrol",
                "Goblin_Miner",
                "Goblin_Miner_Patrol",
                "Goblin_Ogre",
                "Goblin_Scavenger",
                "Goblin_Scavenger_Battleaxe",
                "Goblin_Scavenger_Sword",
                "Goblin_Scrapper",
                "Goblin_Scrapper_Patrol",
                "Goblin_Thief",
                "Goblin_Thief_Patrol",
        });
        // Trorks
        registerSpeechGroup(speechGroupMap, "Generic", new String[] {
                "Trork_Brawler",
                "Trork_Chieftain",
                "Trork_Doctor_Witch",
                "Trork_Guard",
                "Trork_Hunter",
                "Trork_Mauler",
                "Trork_Sentry",
                "Trork_Sentry_Patrol",
                "Trork_Shaman",
                "Trork_Unarmed",
                "Trork_Warrior",
                "Trork_Warrior_Patrol",
        });
        // Outlanders
        registerSpeechGroup(speechGroupMap, "Generic", new String[] {
                "Outlander_Berserker",
                "Outlander_Brute",
                "Outlander_Cultist",
                "Outlander_Hunter",
                "Outlander_Marauder",
                // "Outlander_Peon", // Cannot talk
                "Outlander_Priest",
                "Outlander_Sorcerer",
                "Outlander_Stalker",
        });
        // Ferans
        registerSpeechGroup(speechGroupMap, "Generic", new String[] {
                "Feran_Burrower",
                "Feran_Civilian",
                "Feran_Cub",
                "Feran_Longtooth",
                "Feran_Sharptooth",
                "Feran_Windwalker",
                "Temple_Feran",
                "Temple_Feran_Longtooth",
        });
        // Klops!
        registerSpeechGroup(speechGroupMap, "Generic", new String[] {
                "Klops_Gentleman",
                "Klops_Merchant",
                "Klops_Merchant_Patrol",
                "Klops_Merchant_Wandering",
                "Klops_Miner",
                "Klops_Miner_Patrol",
                "Temple_Klops",
                "Temple_Klop_Merchant",
        });
        // Intelligent but we likely cannot understand them: Scarak, Skeleton

        config.get().mergeSpeechGroups(speechGroupMap, false);
    }

    private void registerSpeechGroup(Map<String, String> speechGroupMap, String group, String[] entityIds) {
        for (String entityId : entityIds) {
            speechGroupMap.put(entityId, group);
        }
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