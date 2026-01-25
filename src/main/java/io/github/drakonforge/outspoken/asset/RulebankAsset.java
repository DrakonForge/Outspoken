package io.github.drakonforge.outspoken.asset;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.AssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import java.util.HashMap;
import java.util.Map;

public class RulebankAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, RulebankAsset>> {
    private static final AssetBuilderCodec.Builder<String, RulebankAsset> CODEC_BUILDER = AssetBuilderCodec.builder(
                    RulebankAsset.class, RulebankAsset::new, Codec.STRING, (asset, id) -> asset.id = id,
                    RulebankAsset::getId, (asset, data) -> asset.extraData = data,
                    asset -> asset.extraData)
            .append(new KeyedCodec<>("Categories",
                            new MapCodec<>(new ArrayCodec<>(RuleAsset.CODEC, RuleAsset[]::new), HashMap::new, true), true),
                    (asset, value) -> asset.categoryMap = value,
                    RulebankAsset::getCategoryMap)
            .documentation("Each category defines a distinct set of rules and responses for a particular event. For example: Greeting, DamageTaken, DamageDealt")
            .add()
            .documentation("A rulebank containing categories of context-based rules. This can be used as a speechbank for NPCs, where each NPC may have at most 1 rulebank governing their dialogue barks.");
    public static final AssetCodec<String, RulebankAsset> CODEC = CODEC_BUILDER.build();
    private static AssetStore<String, RulebankAsset, DefaultAssetMap<String, RulebankAsset>> ASSET_STORE;
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(
            new AssetKeyValidator<>(RulebankAsset::getAssetStore));

    public static AssetStore<String, RulebankAsset, DefaultAssetMap<String, RulebankAsset>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(RulebankAsset.class);
        }

        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, RulebankAsset> getAssetMap() {
        return getAssetStore().getAssetMap();
    }

    protected String id;
    protected AssetExtraInfo.Data extraData;
    private Map<String, RuleAsset[]> categoryMap;

    @Override
    public String getId() {
        return id;
    }

    public Map<String, RuleAsset[]> getCategoryMap() {
        return categoryMap;
    }
}
