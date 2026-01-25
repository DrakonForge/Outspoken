package io.github.drakonforge.outspoken.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import javax.annotation.Nonnull;

public class CriterionValue {

    public static final BuilderCodec<CriterionValue> CODEC = BuilderCodec.builder(
                    CriterionValue.class, CriterionValue::new)
            .append(new KeyedCodec<>("Type", new EnumCodec<>(ValueType.class)),
                    (obj, valueType) -> obj.valueType = valueType, obj -> obj.valueType)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("Float", Codec.FLOAT), (obj, value) -> obj.floatValue = value,
                    obj -> obj.floatValue)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("Context", ContextValue.CODEC),
                    (obj, value) -> obj.contextValue = value, obj -> obj.contextValue)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("IntArray", Codec.INT_ARRAY),
                    (obj, value) -> obj.intArrayValue = value, obj -> obj.intArrayValue)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("StringArray", Codec.STRING_ARRAY),
                    (obj, value) -> obj.stringArrayValue = value, obj -> obj.stringArrayValue)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("String", Codec.STRING),
                    (obj, value) -> obj.stringValue = value, obj -> obj.stringValue)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("Boolean", Codec.BOOLEAN),
                    (obj, value) -> obj.booleanValue = value, obj -> obj.booleanValue)
            .documentation("TODO")
            .add()
            .append(new KeyedCodec<>("Range", Range.CODEC), (obj, value) -> obj.rangeValue = value, obj -> obj.rangeValue)
            .documentation("TODO")
            .add()
            .documentation("TODO")
            .build();
    public static final CriterionValue NONE = new CriterionValue();

    @Nonnull
    private ValueType valueType = ValueType.None;
    private float floatValue = -999.0f;
    private int[] intArrayValue;
    private String[] stringArrayValue;
    private String stringValue = "Invalid";
    private boolean booleanValue;
    private ContextValue contextValue;
    private Range rangeValue;
    protected CriterionValue() {}

    public ValueType getType() {
        return valueType;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public int[] getIntArrayValue() {
        return intArrayValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String[] getStringArrayValue() {
        return stringArrayValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public ContextValue getContextValue() {
        return contextValue;
    }

    public Range getRangeValue() {
        return rangeValue;
    }

    public enum ValueType {
        None, Float, IntArray, StringArray, String, Context, Boolean, Range
    }

    public static class ContextValue {

        public static final BuilderCodec<ContextValue> CODEC = BuilderCodec.builder(ContextValue.class,
                        ContextValue::new)
                .append(new KeyedCodec<>("Table", Codec.STRING, true),
                        (pair, tableName) -> pair.tableName = tableName, ContextValue::getTableName)
                .documentation("TODO - Table")
                .add()
                .append(new KeyedCodec<>("Key", Codec.STRING, true), (pair, key) -> pair.key = key,
                        ContextValue::getKey)
                .documentation("TODO - Key")
                .add()
                .documentation("TODO - TableKeyPair")
                .build();
        private String tableName;
        private String key;

        protected ContextValue() {}

        public String getTableName() {
            return tableName;
        }

        public String getKey() {
            return key;
        }
    }

    public static class Range {
        public static final BuilderCodec<Range> CODEC = BuilderCodec.builder(
                Range.class, Range::new)
                .append(new KeyedCodec<>("Min", Codec.FLOAT), (range, min) -> range.min = min, range -> range.min).documentation("TODO").add()
                .append(new KeyedCodec<>("Max", Codec.FLOAT), (range, max) -> range.max = max, range -> range.max).documentation("TODO").add()
                .append(new KeyedCodec<>("MinExclusive", Codec.BOOLEAN), (range, minExclusive) -> range.minExclusive = minExclusive, range -> range.minExclusive).documentation("TODO").add()
                .append(new KeyedCodec<>("MaxExclusive", Codec.BOOLEAN), (range, maxExclusive) -> range.maxExclusive = maxExclusive, range -> range.maxExclusive).documentation("TODO").add()
                .documentation("TODO")

                .build();

        private float min = Float.MIN_VALUE;
        private float max = Float.MAX_VALUE;
        private boolean minExclusive = false;
        private boolean maxExclusive = false;

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

        public boolean isMinExclusive() {
            return minExclusive;
        }

        public boolean isMaxExclusive() {
            return maxExclusive;
        }
    }
}
