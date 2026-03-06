package io.github.drakonforge.outspoken.asset.criterionvalue;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class StringArrayValue extends CriterionValue {

    private static final String[] EMPTY = new String[0];
    public static final BuilderCodec<StringArrayValue> CODEC = BuilderCodec.builder(
            StringArrayValue.class,
            StringArrayValue::new, CriterionValue.BASE_CODEC).append(new KeyedCodec<>("Value", Codec.STRING_ARRAY, true), (obj, value) -> obj.value = value, StringArrayValue::getValue).documentation("TODO").add().build();

    private String[] value = EMPTY;

    public String[] getValue() {
        return value;
    }
}
