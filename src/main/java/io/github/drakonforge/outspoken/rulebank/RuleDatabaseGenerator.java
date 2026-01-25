package io.github.drakonforge.outspoken.rulebank;

import com.hypixel.hytale.logger.HytaleLogger;
import io.github.drakonforge.outspoken.asset.CriterionAsset;
import io.github.drakonforge.outspoken.asset.CriterionAsset.CriterionType;
import io.github.drakonforge.outspoken.asset.CriterionValue;
import io.github.drakonforge.outspoken.asset.CriterionValue.ContextValue;
import io.github.drakonforge.outspoken.asset.CriterionValue.ValueType;
import io.github.drakonforge.outspoken.asset.ResponseAsset;
import io.github.drakonforge.outspoken.asset.RuleAsset;
import io.github.drakonforge.outspoken.asset.RulebankAsset;
import io.github.drakonforge.outspoken.context.ContextManager;
import io.github.drakonforge.outspoken.criterion.Criterion;
import io.github.drakonforge.outspoken.criterion.CriterionAlternate;
import io.github.drakonforge.outspoken.criterion.CriterionDynamic;
import io.github.drakonforge.outspoken.criterion.CriterionExist;
import io.github.drakonforge.outspoken.criterion.CriterionPass;
import io.github.drakonforge.outspoken.criterion.CriterionStatic;
import io.github.drakonforge.outspoken.response.NoneResponse;
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

public final class RuleDatabaseGenerator {
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
        LOGGER.atFine().log("Found " + assetMap.size() + " rulebank assets");
        RuleDatabase database = new RuleDatabase(contextManager);
        for (Entry<String, RulebankAsset> entry : assetMap.entrySet()) {
            RulebankAsset asset = entry.getValue();;
            String id = asset.getId();
            LOGGER.atFine().log("Processing rulebank " + id);
            // TODO: Check dependencies
            Result result = processRulebank(id, asset, database);
            if (result.failed()) {
                LOGGER.atWarning().log("Some rulebanks failed to parse: " + result.message());
            }
        }
        return database;
    }

    private static Result processRulebank(String id, RulebankAsset asset, RuleDatabase database) {
        Map<String, RuleAsset[]> categoryMap = asset.getCategoryMap();
        for (Entry<String, RuleAsset[]> category : categoryMap.entrySet()) {
            List<Rule> rules = new ArrayList<>();
            for (RuleAsset ruleAsset : category.getValue()) {
                Result result = collectRuleFromAsset(rules, ruleAsset, database.getContextManager());
                if (result.failed()) {
                    return result;
                }
            }
            sortRules(rules);
            RuleTable ruleTable = new RuleTable(rules);
            database.addRuleTable(id, category.getKey(), ruleTable);
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

            switch (criterionAsset.getType()) {
                case CriterionType.Equals -> {
                    Result result = parseEqualsCriterion(criterionRef, value, valueType, invert, contextManager);
                    if (result.failed()) {
                        return result;
                    }
                }
                case CriterionType.Exists -> {
                    Result result = parseExistsCriterion(criterionRef, invert);
                    if (result.failed()) {
                        return result;
                    }
                }
                case Pass -> {
                    Result result = parsePassCriterion(criterionRef, value);
                    if (result.failed()) {
                        return result;
                    }
                }
                case GreaterThan -> {
                    Result result = parseGreaterThanCriterion(criterionRef, value, valueType, invert);
                    if (result.failed()) {
                        return result;
                    }
                }
                case GreaterThanEquals -> {
                    Result result = parseGreaterThanEqualsCriterion(criterionRef, value, valueType, invert);
                    if (result.failed()) {
                        return result;
                    }
                }
                case LessThan -> {
                    Result result = parseLessThanCriterion(criterionRef, value, valueType, invert);
                    if (result.failed()) {
                        return result;
                    }
                }
                case LessThanEquals -> {
                    Result result = parseLessThanEqualsCriterion(criterionRef, value, valueType, invert);
                    if (result.failed()) {
                        return result;
                    }
                }
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
        if (valueType == ValueType.Context) {
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

    private static Result parseGreaterThanCriterion(Ref<Criterion> criterionRef, CriterionValue value, ValueType valueType, boolean invert) {
        if (valueType == ValueType.Float) {
            criterionRef.set(new CriterionStatic(value.getFloatValue() + EPSILON, Float.MAX_VALUE, invert));
            return Result.SUCCESS;
        }

        if (valueType == ValueType.Context) {
            ContextValue context = value.getContextValue();
            criterionRef.set(new CriterionDynamic(EPSILON, Float.MAX_VALUE, context.getTableName(), context.getKey(), invert));
            return Result.SUCCESS;
        }

        return Result.error("Invalid type");
    }

    private static Result parseGreaterThanEqualsCriterion(Ref<Criterion> criterionRef, CriterionValue value, ValueType valueType, boolean invert) {
        if (valueType == ValueType.Float) {
            criterionRef.set(new CriterionStatic(value.getFloatValue() - EPSILON, Float.MAX_VALUE, invert));
            return Result.SUCCESS;
        }

        if (valueType == ValueType.Context) {
            ContextValue context = value.getContextValue();
            criterionRef.set(new CriterionDynamic(-EPSILON, Float.MAX_VALUE, context.getTableName(), context.getKey(), invert));
            return Result.SUCCESS;
        }

        return Result.error("Invalid type");
    }

    private static Result parseLessThanCriterion(Ref<Criterion> criterionRef, CriterionValue value, ValueType valueType, boolean invert) {
        if (valueType == ValueType.Float) {
            criterionRef.set(new CriterionStatic(Float.MIN_VALUE, value.getFloatValue() - EPSILON, invert));
            return Result.SUCCESS;
        }

        if (valueType == ValueType.Context) {
            ContextValue context = value.getContextValue();
            criterionRef.set(new CriterionDynamic(Float.MIN_VALUE, -EPSILON, context.getTableName(), context.getKey(), invert));
            return Result.SUCCESS;
        }

        return Result.error("Invalid type");
    }

    private static Result parseLessThanEqualsCriterion(Ref<Criterion> criterionRef, CriterionValue value, ValueType valueType, boolean invert) {
        if (valueType == ValueType.Float) {
            criterionRef.set(new CriterionStatic(Float.MIN_VALUE, value.getFloatValue() + EPSILON, invert));
            return Result.SUCCESS;
        }

        if (valueType == ValueType.Context) {
            ContextValue context = value.getContextValue();
            criterionRef.set(new CriterionDynamic(Float.MIN_VALUE, EPSILON, context.getTableName(), context.getKey(), invert));
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

    private RuleDatabaseGenerator() {}
}
