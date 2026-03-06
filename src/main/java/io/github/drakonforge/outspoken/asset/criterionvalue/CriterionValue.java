package io.github.drakonforge.outspoken.asset.criterionvalue;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import javax.annotation.Nonnull;

public abstract class CriterionValue {

    public static final CodecMapCodec<CriterionValue> CODEC = new CodecMapCodec<>("Type");
    protected static final BuilderCodec<CriterionValue> BASE_CODEC = BuilderCodec.abstractBuilder(
            CriterionValue.class).documentation("TODO").build();

    protected CriterionValue() {}
}
