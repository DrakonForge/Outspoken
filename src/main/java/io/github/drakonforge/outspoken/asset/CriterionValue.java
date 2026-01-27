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
            .append(new KeyedCodec<>("Context", CompareValue.CODEC),
                    (obj, value) -> obj.compareValue = value, obj -> obj.compareValue)
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
    private CompareValue compareValue;
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

    public CompareValue getCompareValue() {
        return compareValue;
    }

    public Range getRangeValue() {
        return rangeValue;
    }

    public enum ValueType {
        None, Float, IntArray, StringArray, String, Compare, Boolean, Range
    }

    public static class CompareValue {

        public enum Operation {
            Equals,
            LessThan,
            LessThanEquals,
            GreaterThan,
            GreaterThanEquals,
            Includes,
        }

        public static final BuilderCodec<CompareValue> CODEC = BuilderCodec.builder(CompareValue.class,
                        CompareValue::new)
                .append(new KeyedCodec<>("Operation", new EnumCodec<>(Operation.class), true),
                        (pair, operation) -> pair.operation = operation, CompareValue::getOperation)
                .documentation("TODO - Operation")
                .add()
                .append(new KeyedCodec<>("Table", Codec.STRING, true),
                        (pair, tableName) -> pair.tableName = tableName, CompareValue::getTableName)
                .documentation("TODO - Table")
                .add()
                .append(new KeyedCodec<>("Key", Codec.STRING, true), (pair, key) -> pair.key = key,
                        CompareValue::getKey)
                .documentation("TODO - Key")
                .add()
                .documentation("TODO - Compare")
                .build();

        private Operation operation;
        private String tableName;
        private String key;

        protected CompareValue() {}

        public Operation getOperation() {
            return operation;
        }

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
