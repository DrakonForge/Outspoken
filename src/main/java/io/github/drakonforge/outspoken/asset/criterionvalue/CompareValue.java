package io.github.drakonforge.outspoken.asset.criterionvalue;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;

public class CompareValue extends CriterionValue {

    public enum Operation {
        Equals, LessThan, LessThanEquals, GreaterThan, GreaterThanEquals, Includes,
    }

    public static final BuilderCodec<CompareValue> CODEC = BuilderCodec.builder(CompareValue.class,
                    CompareValue::new, CriterionValue.BASE_CODEC)
            .append(new KeyedCodec<>("Operation", new EnumCodec<>(Operation.class), true),
                    (pair, operation) -> pair.operation = operation, CompareValue::getOperation)
            .documentation("TODO - Operation")
            .add()
            .append(new KeyedCodec<>("Table", Codec.STRING, true),
                    (pair, tableName) -> pair.tableName = tableName, CompareValue::getTableName)
            .documentation("TODO - Table")
            .add()
            .append(new KeyedCodec<>("Key", Codec.STRING, true), (pair, key) -> pair.key = key,
                    CompareValue::getKey)
            .documentation("TODO - Key")
            .add()
            .build();

    private Operation operation;
    private String tableName;
    private String key;

    protected CompareValue() {}

    public Operation getOperation() {
        return operation;
    }

    public String getTableName() {
        return tableName;
    }

    public String getKey() {
        return key;
    }
}
