package io.github.drakonforge.outspoken.asset.criterionvalue;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class FloatValue extends CriterionValue {

    public static final BuilderCodec<FloatValue> CODEC = BuilderCodec.builder(FloatValue.class,
            FloatValue::new, CriterionValue.BASE_CODEC).append(new KeyedCodec<>("Value", Codec.FLOAT, true), (obj, value) -> obj.value = value, FloatValue::getValue).documentation("TODO").add().build();

    private float value;

    public float getValue() {
        return value;
    }
}
