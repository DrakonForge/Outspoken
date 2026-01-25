package io.github.drakonforge.outspoken.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import io.github.drakonforge.outspoken.response.Response;
import io.github.drakonforge.outspoken.response.Response.ResponseType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class ResponseAsset {
    public static final BuilderCodec<ResponseAsset> CODEC = BuilderCodec.builder(
            ResponseAsset.class, ResponseAsset::new)
            .append(new KeyedCodec<>("Type", new EnumCodec<>(
                    ResponseType.class), true), (asset, responseType) -> asset.responseType = responseType, ResponseAsset::getResponseType).documentation("TODO").add()
            .append(new KeyedCodec<>("Entries", Codec.STRING_ARRAY), (asset, data) -> asset.entries = data, ResponseAsset::getEntries).documentation("TODO").add()
            .documentation("TODO")
            .build();

    // TODO: Can add other response logic here later
    @Nonnull
    private ResponseType responseType = Response.ResponseType.None;
    @Nullable
    private String[] entries;

    protected ResponseAsset() {}

    @NonNullDecl
    public ResponseType getResponseType() {
        return responseType;
    }

    @Nullable
    public String[] getEntries() {
        return entries;
    }
}
