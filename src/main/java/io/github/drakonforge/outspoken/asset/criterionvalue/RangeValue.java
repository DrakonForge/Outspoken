package io.github.drakonforge.outspoken.asset.criterionvalue;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class RangeValue extends CriterionValue {
    public static final BuilderCodec<RangeValue> CODEC = BuilderCodec.builder(
            RangeValue.class, RangeValue::new, CriterionValue.BASE_CODEC)
            .append(new KeyedCodec<>("Min", Codec.FLOAT), (range, min) -> range.min = min, range -> range.min).documentation("TODO").add()
            .append(new KeyedCodec<>("Max", Codec.FLOAT), (range, max) -> range.max = max, range -> range.max).documentation("TODO").add()
            .append(new KeyedCodec<>("MinExclusive", Codec.BOOLEAN), (range, minExclusive) -> range.minExclusive = minExclusive, range -> range.minExclusive).documentation("TODO").add()
            .append(new KeyedCodec<>("MaxExclusive", Codec.BOOLEAN), (range, maxExclusive) -> range.maxExclusive = maxExclusive, range -> range.maxExclusive).documentation("TODO").add()
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
