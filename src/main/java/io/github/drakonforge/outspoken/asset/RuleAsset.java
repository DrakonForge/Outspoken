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
            .append(new KeyedCodec<>("Response",
                            ResponseAsset.CODEC),
                    (rule, criteria) -> rule.response = criteria, RuleAsset::getResponse)
            .documentation("TODO - Responses")
            .add()
            .append(new KeyedCodec<>("ModifyPriority", Codec.INTEGER),
                    (rule, priority) -> rule.priorityModifier = priority, RuleAsset::getPriorityModifier)
            .documentation("TODO - Priority")
            .add()
            .documentation("TODO - Rule")
            .build();

    @Nullable
    private String id;
    @Nullable
    private CriterionAsset[] criteria;
    @Nullable
    private ResponseAsset response;
    private int priorityModifier = 0;

    protected RuleAsset() {}

    @Nullable
    public String getId() {
        return id;
    }

    public CriterionAsset[] getCriteria() {
        return criteria;
    }

    public ResponseAsset getResponse() {
        return response;
    }

    public int getPriorityModifier() {
        return priorityModifier;
    }
}
