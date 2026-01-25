package io.github.drakonforge.outspoken.asset;

import com.hypixel.hytale.codec.builder.BuilderCodec;

public class ResponseAsset {
    public static final BuilderCodec<ResponseAsset> CODEC = BuilderCodec.builder(
            ResponseAsset.class, ResponseAsset::new).build();

    public enum Type {
        Text
    }

    protected ResponseAsset() {}
}
