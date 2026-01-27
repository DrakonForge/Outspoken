package io.github.drakonforge.outspoken;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import java.util.HashMap;
import java.util.Map;

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
            .build();

    private static Map<String, String> createDefaultSpeechGroupMap() {
        Map<String, String> speechGroupMap = new HashMap<>();
        speechGroupMap.put("Kweebec_Razorleaf", "Example");
        return speechGroupMap;
    }

    private float contextThrottleCooldown = 1.0f;
    private Map<String, String> speechGroupMap = createDefaultSpeechGroupMap();

    public OutspokenConfig() {}

    public Map<String, String> getSpeechGroupMap() {
        return speechGroupMap;
    }

    public float getContextThrottleCooldown() {
        return contextThrottleCooldown;
    }
}
