package io.github.drakonforge.outspoken;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.codecs.map.Object2FloatMapCodec;
import io.github.drakonforge.outspoken.util.SpeechEvents;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class OutspokenConfig {

    public static final BuilderCodec<OutspokenConfig> CODEC = BuilderCodec.builder(
                    OutspokenConfig.class, OutspokenConfig::new)
            .append(new KeyedCodec<>("ContextThrottleCooldown", Codec.FLOAT),
                    (config, value) -> config.contextThrottleCooldown = value,
                    OutspokenConfig::getContextThrottleCooldown)
            .add()
            .append(new KeyedCodec<>("SpeechGroups", new MapCodec<>(Codec.STRING, HashMap::new)),
                    (config, value) -> config.speechGroupMap = value,
                    OutspokenConfig::getSpeechGroupMap)
            .add()
            .append(new KeyedCodec<>("SpeechEventFrequency", new Object2FloatMapCodec<>(Codec.STRING, Object2FloatOpenHashMap::new)),
                    (config, value) -> config.speechEventFrequencyMap = value,
                    OutspokenConfig::getSpeechEventFrequencyMap)
            .add()
            .append(new KeyedCodec<>("SpeechMessageVisibleDistance", Codec.FLOAT),
                    (config, value) -> config.speechMessageVisibleDistance = value,
                    OutspokenConfig::getSpeechMessageVisibleDistance)
            .add()
            .append(new KeyedCodec<>("ChatMessageMode", new EnumCodec<>(ChatMessageMode.class)),
                    (config, value) -> config.chatMessageMode = value,
                    OutspokenConfig::getChatMessageMode)
            .add()
            .append(new KeyedCodec<>("ChatBubbleMode", new EnumCodec<>(ChatBubbleMode.class)),
                    (config, value) -> config.chatBubbleMode = value,
                    OutspokenConfig::getChatBubbleMode)
            .add()
            .build();

    private static Map<String, String> createDefaultSpeechGroupMap() {
        Map<String, String> speechGroupMap = new HashMap<>();
        speechGroupMap.put("Kweebec_Razorleaf", "Generic");
        return speechGroupMap;
    }

    private static Object2FloatMap<String> createDefaultSpeechEventFrequencyMap() {
        Object2FloatMap<String> eventFrequencyMap = new Object2FloatOpenHashMap<>();
        eventFrequencyMap.put(SpeechEvents.AMBIENT_IDLE_MODIFIER, 0.05f);
        eventFrequencyMap.put(SpeechEvents.AMBIENT, 0.05f);
        eventFrequencyMap.put(SpeechEvents.GREETING, 0.25f);
        eventFrequencyMap.put(SpeechEvents.STATE_CHANGE, 1.0f);
        eventFrequencyMap.put(SpeechEvents.DAMAGE_TAKEN, 0.5f);
        return eventFrequencyMap;
    }

    public enum ChatMessageMode {
        Never,
        Local,
        World;
    }

    public enum ChatBubbleMode {
        Never,
        Always
    }

    private float contextThrottleCooldown = 1.0f;
    private Map<String, String> speechGroupMap = createDefaultSpeechGroupMap();
    private Object2FloatMap<String> speechEventFrequencyMap = createDefaultSpeechEventFrequencyMap();
    private float speechMessageVisibleDistance = 64.0f;
    private ChatMessageMode chatMessageMode = ChatMessageMode.Local;
    private ChatBubbleMode chatBubbleMode = ChatBubbleMode.Always;

    public OutspokenConfig() {}

    protected Map<String, String> getSpeechGroupMap() {
        return speechGroupMap;
    }

    protected Object2FloatMap<String> getSpeechEventFrequencyMap() {
        return speechEventFrequencyMap;
    }

    public float getSpeechMessageVisibleDistance() {
        return speechMessageVisibleDistance;
    }

    public ChatBubbleMode getChatBubbleMode() {
        return chatBubbleMode;
    }

    public ChatMessageMode getChatMessageMode() {
        return chatMessageMode;
    }

    @Nullable
    public String getSpeechGroupFor(String entityId) {
        return speechGroupMap.get(entityId);
    }

    public boolean shouldSkipEvent(String speechEvent) {
        float chance = speechEventFrequencyMap.getOrDefault(speechEvent, 1.0f);
        return Math.random() >= chance;
    }

    public float getContextThrottleCooldown() {
        return contextThrottleCooldown;
    }
}
