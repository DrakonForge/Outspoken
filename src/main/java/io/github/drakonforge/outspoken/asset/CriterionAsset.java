package io.github.drakonforge.outspoken.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import io.github.drakonforge.outspoken.asset.criterionvalue.BooleanValue;
import io.github.drakonforge.outspoken.asset.criterionvalue.CompareValue;
import io.github.drakonforge.outspoken.asset.criterionvalue.CriterionValue;
import io.github.drakonforge.outspoken.asset.criterionvalue.FloatValue;
import io.github.drakonforge.outspoken.asset.criterionvalue.IntArrayValue;
import io.github.drakonforge.outspoken.asset.criterionvalue.NullValue;
import io.github.drakonforge.outspoken.asset.criterionvalue.RangeValue;
import io.github.drakonforge.outspoken.asset.criterionvalue.StringArrayValue;
import io.github.drakonforge.outspoken.asset.criterionvalue.StringValue;
import java.util.Set;
import javax.annotation.Nonnull;

public class CriterionAsset {
    public enum CriterionType {
        Equals(Set.of(FloatValue.class, StringValue.class, IntArrayValue.class, StringArrayValue.class, BooleanValue.class), true),
        Exists(Set.of(NullValue.class), true),
        Pass(Set.of(FloatValue.class), false),
        Compare(Set.of(CompareValue.class), true),
        Range(Set.of(RangeValue.class, FloatValue.class), true),
        Includes(Set.of(StringValue.class, FloatValue.class), true),
        Reference(Set.of(StringValue.class), false);

        private final Set<Class<? extends CriterionValue>> validValueTypes;
        private final boolean invertible;

        CriterionType(Set<Class<? extends CriterionValue>> validValueTypes, boolean invertible) {
            this.validValueTypes = validValueTypes;
            this.invertible = invertible;
        }

        public boolean isValidType(Class<? extends CriterionValue> valueType) {
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
    private CriterionValue value = NullValue.INSTANCE;

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
