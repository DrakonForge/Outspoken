package io.github.drakonforge.outspoken.asset.criterionvalue;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class BooleanValue extends CriterionValue {

    public static final BuilderCodec<BooleanValue> CODEC = BuilderCodec.builder(BooleanValue.class,
            BooleanValue::new, CriterionValue.BASE_CODEC).append(new KeyedCodec<>("Value", Codec.BOOLEAN, true), (obj, value) -> obj.value = value, BooleanValue::getValue).documentation("TODO").add().build();

    private boolean value = false;

    public boolean getValue() {
        return value;
    }
}
