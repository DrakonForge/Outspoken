package io.github.drakonforge.outspoken.asset;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import javax.annotation.Nullable;

public class RuleAsset {

    public static final BuilderCodec<RuleAsset> CODEC = BuilderCodec.builder(RuleAsset.class,
                    RuleAsset::new)
            .append(new KeyedCodec<>("Id", Codec.STRING), (rule, id) -> rule.id = id,
                    RuleAsset::getId)
            .documentation("TODO - Id")
            .add()
            .append(new KeyedCodec<>("Criteria",
                            new ArrayCodec<>(CriterionAsset.CODEC, CriterionAsset[]::new)),
                    (rule, criteria) -> rule.criteria = criteria, RuleAsset::getCriteria)
            .documentation("TODO - Criteria")
            .add()
            .append(new KeyedCodec<>("Responses",
                            new ArrayCodec<>(ResponseAsset.CODEC, ResponseAsset[]::new)),
                    (rule, criteria) -> rule.responses = criteria, RuleAsset::getResponses)
            .documentation("TODO - Responses")
            .add()
            .append(new KeyedCodec<>("Priority", Codec.INTEGER),
                    (rule, priority) -> rule.priority = priority, RuleAsset::getPriority)
            .documentation("TODO - Priority")
            .add()
            .documentation("TODO - Rule")
            .build();

    @Nullable
    private String id;
    private CriterionAsset[] criteria;
    private ResponseAsset[] responses;
    private int priority;

    protected RuleAsset() {}

    @Nullable
    public String getId() {
        return id;
    }

    public CriterionAsset[] getCriteria() {
        return criteria;
    }

    public ResponseAsset[] getResponses() {
        return responses;
    }

    public int getPriority() {
        return priority;
    }
}
