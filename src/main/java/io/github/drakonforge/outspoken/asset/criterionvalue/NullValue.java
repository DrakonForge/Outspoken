package io.github.drakonforge.outspoken.asset.criterionvalue;

import com.hypixel.hytale.codec.builder.BuilderCodec;

public class NullValue extends CriterionValue {

    public static final NullValue INSTANCE = new NullValue();

    public static final BuilderCodec<NullValue> CODEC = BuilderCodec.builder(NullValue.class,
            NullValue::new, CriterionValue.BASE_CODEC).build();
}
