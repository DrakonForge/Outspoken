package io.github.drakonforge.outspoken.asset.criterionvalue;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class IntArrayValue extends CriterionValue {

    private static final int[] EMPTY = new int[0];
    public static final BuilderCodec<IntArrayValue> CODEC = BuilderCodec.builder(IntArrayValue.class,
            IntArrayValue::new, CriterionValue.BASE_CODEC).append(new KeyedCodec<>("Value", Codec.INT_ARRAY, true), (obj, value) -> obj.value = value, IntArrayValue::getValue).documentation("TODO").add().build();

    private int[] value = EMPTY;

    public int[] getValue() {
        return value;
    }
}
