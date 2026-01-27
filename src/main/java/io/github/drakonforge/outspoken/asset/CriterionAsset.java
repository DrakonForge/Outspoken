package io.github.drakonforge.outspoken.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import io.github.drakonforge.outspoken.asset.CriterionValue.ValueType;
import java.util.Set;
import javax.annotation.Nonnull;

public class CriterionAsset {
    public enum CriterionType {
        Equals(Set.of(ValueType.Float, ValueType.String, ValueType.IntArray, ValueType.StringArray, ValueType.Boolean), true),
        Exists(Set.of(ValueType.None), true),
        Pass(Set.of(ValueType.Float), false),
        Compare(Set.of(ValueType.Compare), true),
        Range(Set.of(ValueType.Range, ValueType.Float), true);

        private final Set<ValueType> validValueTypes;
        private final boolean invertible;

        CriterionType(Set<ValueType> validValueTypes, boolean invertible) {
            this.validValueTypes = validValueTypes;
            this.invertible = invertible;
        }

        public boolean isValidType(ValueType valueType) {
            return validValueTypes.contains(valueType);
        }

        public boolean canInvert() {
            return invertible;
        }
    }

    public static final BuilderCodec<CriterionAsset> CODEC = BuilderCodec.builder(
            CriterionAsset.class, CriterionAsset::new)
            .append(new KeyedCodec<>("Type", new EnumCodec<>(CriterionType.class), true), (obj, type) -> obj.type = type, CriterionAsset::getType).documentation("TODO").add()
            .append(new KeyedCodec<>("Invert", Codec.BOOLEAN), (obj, invert) -> obj.invert = invert, CriterionAsset::shouldInvert).documentation("TODO").add()
            .append(new KeyedCodec<>("Table", Codec.STRING), (obj, tableName) -> obj.tableName = tableName, CriterionAsset::getTableName).documentation("TODO").add()
            .append(new KeyedCodec<>("Key", Codec.STRING), (obj, key) -> obj.key = key, CriterionAsset::getKey).documentation("TODO").add()
            .append(new KeyedCodec<>("Value", CriterionValue.CODEC), (obj, value) -> obj.value = value, CriterionAsset::getValue).documentation("TODO").add()
            .documentation("TODO")
            .build();

    protected CriterionAsset() {}

    @Nonnull
    private CriterionType type = CriterionType.Equals;
    private boolean invert;
    private String tableName;
    private String key;
    @Nonnull
    private CriterionValue value = CriterionValue.NONE;

    @Nonnull
    public CriterionType getType() {
        return type;
    }

    public boolean shouldInvert() {
        return invert;
    }

    public String getTableName() {
        return tableName;
    }

    public String getKey() {
        return key;
    }

    @Nonnull
    public CriterionValue getValue() {
        return value;
    }
}
