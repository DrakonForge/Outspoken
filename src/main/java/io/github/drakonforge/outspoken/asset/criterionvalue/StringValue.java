package io.github.drakonforge.outspoken.asset.criterionvalue;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class StringValue extends CriterionValue {

    public static final BuilderCodec<StringValue> CODEC = BuilderCodec.builder(StringValue.class,
            StringValue::new, CriterionValue.BASE_CODEC).append(new KeyedCodec<>("Value", Codec.STRING, true), (obj, value) -> obj.value = value, StringValue::getValue).documentation("TODO").add().build();

    private String value;

    public String getValue() {
        return value;
    }
}
