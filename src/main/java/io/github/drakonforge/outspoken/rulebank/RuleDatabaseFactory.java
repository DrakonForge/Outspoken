package io.github.drakonforge.outspoken.rulebank;

import com.hypixel.hytale.logger.HytaleLogger;
import io.github.drakonforge.outspoken.asset.CriterionAsset;
import io.github.drakonforge.outspoken.asset.CriterionAsset.CriterionType;
import io.github.drakonforge.outspoken.asset.CriterionValue;
import io.github.drakonforge.outspoken.asset.CriterionValue.CompareValue;
import io.github.drakonforge.outspoken.asset.CriterionValue.CompareValue.Operation;
import io.github.drakonforge.outspoken.asset.CriterionValue.Range;
import io.github.drakonforge.outspoken.asset.CriterionValue.ValueType;
import io.github.drakonforge.outspoken.asset.ResponseAsset;
import io.github.drakonforge.outspoken.asset.RuleAsset;
import io.github.drakonforge.outspoken.asset.RulebankAsset;
import io.github.drakonforge.outspoken.context.ContextManager;
import io.github.drakonforge.outspoken.criterion.Criterion;
import io.github.drakonforge.outspoken.criterion.CriterionAlternate;
import io.github.drakonforge.outspoken.criterion.CriterionCompare;
import io.github.drakonforge.outspoken.criterion.CriterionExist;
import io.github.drakonforge.outspoken.criterion.CriterionIncludes;
import io.github.drakonforge.outspoken.criterion.CriterionPass;
import io.github.drakonforge.outspoken.criterion.CriterionStatic;
import io.github.drakonforge.outspoken.response.PlainTextResponse;
import io.github.drakonforge.outspoken.response.Response;
import io.github.drakonforge.outspoken.response.Response.ResponseType;
import io.github.drakonforge.outspoken.rulebank.Rule.CriteriaEntry;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public final class RuleDatabaseFactory {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final float EPSILON = 1e-6f;

    private static void sortRules(List<Rule> rules) {
        // Sort by descending priority
        rules.sort(Comparator.comparingInt(Rule::priority).reversed());
    }

    private static void sortCriteria(List<CriteriaEntry> criteria) {
        // Sort by descending priority
        criteria.sort(Comparator.comparingInt((CriteriaEntry entry) -> entry.criterion().getPriority())
                .reversed());
    }

    private static class Ref<T> {
        private T value;

        public Ref() {
            this.value = null;
        }

        public void set(T value) {
            this.value = value;
        }

        @Nullable
        public T get() {
            return value;
        }

        public T getOrElse(T defaultValue) {
            if (value == null) {
                return defaultValue;
            }
            return value;
        }
    }

    public enum Status {
        SUCCESS,
        FAILURE;
    }

    private record Result(Status status, String message) {
        public static final Result SUCCESS = new Result(Status.SUCCESS, "");

        public static Result error(String message) {
            return new Result(Status.FAILURE, message);
        }

        public boolean failed() {
            return status != Status.SUCCESS;
        }
    }

    public static RuleDatabase createFromAssetMap(Map<String, RulebankAsset> assetMap, ContextManager contextManager) {
        LOGGER.atInfo().log("Found " + assetMap.size() + " rulebank assets");
        RuleDatabase database = new RuleDatabase(contextManager);
        List<String> failedRulebanks = new ArrayList<>();
        for (Entry<String, RulebankAsset> entry : assetMap.entrySet()) {
            RulebankAsset asset = entry.getValue();;
            String id = asset.getId();
            LOGGER.atFine().log("Processing rulebank " + id);
            // TODO: Check dependencies
            Result result = processRulebank(id, asset, database);
            if (result.failed()) {
                failedRulebanks.add(result.message());
            }
        }
        if (!failedRulebanks.isEmpty()) {
            LOGGER.atSevere().log("Some rulebanks failed to parse: " + String.join(", ", failedRulebanks));
        }
        return database;
    }

    private static Result processRulebank(String id, RulebankAsset asset, RuleDatabase database) {
        Map<String, RuleAsset[]> categoryMap = asset.getCategoryMap();
        int numCategoryErrors = 0;
        for (Entry<String, RuleAsset[]> category : categoryMap.entrySet()) {
            List<Rule> rules = new ArrayList<>();
            LOGGER.atInfo().log("Found id " + id + ", category " + category.getKey());
            for (RuleAsset ruleAsset : category.getValue()) {
                Result result = collectRuleFromAsset(rules, ruleAsset, database.getContextManager());
                if (result.failed()) {
                    numCategoryErrors += 1;
                    LOGGER.atSevere().log("Failed to parse rule in " + id + "." + category.getKey() + " at index " + rules.size() + ": " + result.message());
                }
            }
            sortRules(rules);
            RuleTable ruleTable = new RuleTable(rules);
            database.addRuleTable(id, category.getKey(), ruleTable);
        }
        if (numCategoryErrors > 0) {
            return Result.error(id + " (" + numCategoryErrors + ")");
        }
        return Result.SUCCESS;
    }

    private static Result collectRuleFromAsset(List<Rule> rules, RuleAsset ruleAsset, ContextManager contextManager) {
        int priority = ruleAsset.getPriority();

        CriterionAsset[] criteriaAssets = ruleAsset.getCriteria();
        List<CriteriaEntry> criteriaEntries;
        if (criteriaAssets != null) {
            criteriaEntries = new ArrayList<>();
            Result result = collectCriteriaFromAsset(criteriaEntries, criteriaAssets, contextManager);
            if (result.failed()) {
                return result;
            }
            sortCriteria(criteriaEntries);
        } else {
            criteriaEntries = Collections.emptyList();
        }

        ResponseAsset responseAsset = ruleAsset.getResponse();
        Ref<Response> responseRef = new Ref<>();
        if (responseAsset != null) {
            Result result = collectResponseFromAsset(responseRef, responseAsset, contextManager);
            if (result.failed()) {
                return result;
            }
        }

        rules.add(new Rule(criteriaEntries, responseRef.getOrElse(Response.EMPTY), priority));
        return Result.SUCCESS;
    }

    private static Result collectCriteriaFromAsset(List<CriteriaEntry> criteriaEntries, CriterionAsset[] criteriaAssets, ContextManager contextManager) {
        for (CriterionAsset criterionAsset : criteriaAssets) {
            CriterionType type = criterionAsset.getType();
            CriterionValue value = criterionAsset.getValue();
            ValueType valueType = value.getType();
            boolean invert = criterionAsset.shouldInvert();
            Ref<Criterion> criterionRef = new Ref<>();

            if(!type.isValidType(value.getType())) {
                return Result.error("Invalid value type: " + valueType + " is not a valid type for " + type);
            }

            if (invert && !type.canInvert()) {
                return Result.error("Criterion type " + type + " cannot be inverted");
            }

            Result result;
            switch (criterionAsset.getType()) {
                case Equals -> result = parseEqualsCriterion(criterionRef, value, valueType, invert, contextManager);
                case Exists -> result = parseExistsCriterion(criterionRef, invert);
                case Pass -> result = parsePassCriterion(criterionRef, value);
                case Compare ->
                        result = parseCompareCriterion(criterionRef, value.getCompareValue(), invert);
                case Range ->
                        result = parseRangeCriterion(criterionRef, value.getRangeValue(), invert);
                case Includes ->
                    result = parseIncludesCriterion(criterionRef, value, valueType, invert, contextManager);
                default -> {
                    return Result.error("Unknown criterion type: " + criterionAsset.getType());
                }
            }
            if (result.failed()) {
                return result;
            }
            Criterion criterion = criterionRef.get();
            if (criterion == null) {
                return Result.error("Value is null");
            }
            String tableName = criterionAsset.getTableName();
            String key = criterionAsset.getKey();
            criteriaEntries.add(new CriteriaEntry(tableName, key, criterion));
        }
        return Result.SUCCESS;
    }

    private static Result parseEqualsCriterion(Ref<Criterion> criterionRef, CriterionValue value, ValueType valueType, boolean invert, ContextManager contextManager) {
        if (valueType == ValueType.Float) {
            float floatValue = value.getFloatValue();
            criterionRef.set(new CriterionStatic(floatValue - EPSILON, floatValue + EPSILON, invert));
            return Result.SUCCESS;
        }
        if (valueType == ValueType.String) {
            String stringValue = value.getStringValue();
            int symbol = contextManager.getStringTable().cache(stringValue);
            criterionRef.set(new CriterionStatic(symbol - EPSILON, symbol + EPSILON, invert));
            return Result.SUCCESS;
        }
        if (valueType == ValueType.IntArray) {
            IntSet options = new IntArraySet();
            for (int item : value.getIntArrayValue()) {
                options.add(item);
            }
            criterionRef.set(new CriterionAlternate(options, invert));
            return Result.SUCCESS;
        }
        if (valueType == ValueType.StringArray) {
            IntSet options = new IntArraySet();
            for (String item : value.getStringArrayValue()) {
                int symbol = contextManager.getStringTable().cache(item);
                options.add(symbol);
            }
            criterionRef.set(new CriterionAlternate(options, invert));
            return Result.SUCCESS;
        }
        if (valueType == ValueType.Boolean) {
            if (value.getBooleanValue()) {
                criterionRef.set(CriterionStatic.IS_TRUE);
            } else {
                criterionRef.set(CriterionStatic.IS_FALSE);
            }
            return Result.SUCCESS;
        }
        return Result.error("Invalid type");
    }

    private static Result parseExistsCriterion(Ref<Criterion> criterionRef, boolean invert) {
        criterionRef.set(new CriterionExist(invert));
        return Result.SUCCESS;
    }

    private static Result parsePassCriterion(Ref<Criterion> criterionRef, CriterionValue value) {
        float floatValue = value.getFloatValue();
        if (floatValue < 0 || floatValue > 1) {
            return Result.error("Pass criterion chance must be between 0 and 1");
        }
        criterionRef.set(new CriterionPass(value.getFloatValue()));
        return Result.SUCCESS;
    }

    private static Result parseCompareCriterion(Ref<Criterion> criterionRef, CompareValue compareValue, boolean invert) {
        Operation operation = compareValue.getOperation();
        switch (operation) {
            case Equals -> criterionRef.set(new CriterionCompare(-EPSILON, EPSILON, compareValue.getTableName(), compareValue.getKey(), invert));
            case LessThan ->
                    criterionRef.set(new CriterionCompare(Float.MIN_VALUE, -EPSILON, compareValue.getTableName(), compareValue.getKey(), invert));
            case LessThanEquals ->
                    criterionRef.set(new CriterionCompare(Float.MIN_VALUE, EPSILON, compareValue.getTableName(), compareValue.getKey(), invert));
            case GreaterThan ->
                    criterionRef.set(new CriterionCompare(EPSILON, Float.MAX_VALUE, compareValue.getTableName(), compareValue.getKey(), invert));
            case GreaterThanEquals ->
                    criterionRef.set(new CriterionCompare(-EPSILON, Float.MAX_VALUE, compareValue.getTableName(), compareValue.getKey(), invert));
            default -> {
                return Result.error("Unsupported operation: " + operation);
            }
        }
        return Result.SUCCESS;
    }


    private static Result parseRangeCriterion(Ref<Criterion> criterionRef, Range rangeValue, boolean invert) {
        if (rangeValue == null) {
            return Result.error("Range value is null");
        }

        float min = rangeValue.getMin();
        float max = rangeValue.getMax();
        if (rangeValue.isMinExclusive()) {
            min += EPSILON;
        }
        if (rangeValue.isMaxExclusive()) {
            max -= EPSILON;
        }

        criterionRef.set(new CriterionStatic(min, max, invert));
        return Result.SUCCESS;
    }

    private static Result parseIncludesCriterion(Ref<Criterion> criterionRef, CriterionValue value, ValueType valueType, boolean invert, ContextManager contextManager) {
        if (valueType == ValueType.Float) {
            float floatValue = value.getFloatValue();
            criterionRef.set(new CriterionIncludes((int) floatValue, invert));
            return Result.SUCCESS;
        }
        if (valueType == ValueType.String) {
            String stringValue = value.getStringValue();
            int symbol = contextManager.getStringTable().cache(stringValue);
            criterionRef.set(new CriterionIncludes(symbol, invert));
            return Result.SUCCESS;
        }
        return Result.error("Invalid type");
    }

    private static Result collectResponseFromAsset(Ref<Response> responseRef, ResponseAsset responseAsset, ContextManager contextManager) {
        ResponseType type = responseAsset.getResponseType();
        String[] entries = responseAsset.getEntries();
        if (type == ResponseType.None) {
            return Result.SUCCESS;
        }
        if (type == ResponseType.PlainText) {
            if (entries == null) {
                return Result.error("PlainText response must have entries");
            }
            responseRef.set(new PlainTextResponse(entries));
            return Result.SUCCESS;
        }
        // TODO Support speech type
        return Result.error("Response type not supported");
    }

    private RuleDatabaseFactory() {}
}
