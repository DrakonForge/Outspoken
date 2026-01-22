package io.github.drakonforge.outspoken.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import javax.annotation.Nonnull;

public class ContextTable {
    private static final FactEntry NOT_FOUND = new FactEntry(FactType.NULL, -99);

    public enum FactType {
        NULL,
        STRING,
        NUMBER,
        BOOLEAN,
    }

    public record FactEntry(FactType type, float value) {}

    private final ContextManager contextManager;
    private final Map<String, FactEntry> context;

    public ContextTable(ContextManager contextManager) {
        this.contextManager = contextManager;
        this.context = new HashMap<>();
    }

    public ContextTable set(String key, String value) {
        int symbol = contextManager.getStringTable().cache(value);
        FactEntry entry = new FactEntry(FactType.STRING, symbol);
        context.put(key, entry);
        return this;
    }

    public ContextTable set(String key, float value) {
        FactEntry entry = new FactEntry(FactType.NUMBER, value);
        context.put(key, entry);
        return this;
    }

    public ContextTable set(String key, int value) {
        FactEntry entry = new FactEntry(FactType.NUMBER, value);
        context.put(key, entry);
        return this;
    }

    public ContextTable set(String key, boolean value) {
        FactEntry entry = new FactEntry(FactType.BOOLEAN, value ? 1 : 0);
        context.put(key, entry);
        return this;
    }

    public ContextTable remove(String key) {
        context.remove(key);
        return this;
    }

    @Nonnull
    public FactEntry get(String key) {
        return context.getOrDefault(key, NOT_FOUND);
    }

    @Nonnull
    public FactEntry getTyped(String key, FactType type) {
        FactEntry entry = get(key);
        if (entry.type() != type) {
            return NOT_FOUND;
        }
        return entry;
    }

    public boolean contains(String key) {
        return context.containsKey(key);
    }

    @Nonnull
    public FactType getType(String key) {
        return get(key).type();
    }

    public float getRawValue(String key) {
        return get(key).value();
    }

    public Optional<String> getString(String key) {
        FactEntry entry = get(key);
        if (entry.type() == FactType.STRING) {
            int symbol = (int) entry.value();
            return contextManager.getStringTable().lookup(symbol);
        }
        return Optional.empty();
    }

    public String getStringOrDefault(String key, String defaultValue) {
        return getString(key).orElse(defaultValue);
    }

    public OptionalInt getInt(String key) {
        FactEntry entry = get(key);
        if (entry.type() == FactType.NUMBER) {
            return OptionalInt.of((int) entry.value());
        }
        return OptionalInt.empty();
    }

    public int getIntOrDefault(String key, int defaultValue) {
        return getInt(key).orElse(defaultValue);
    }

    public float getFloatOrDefault(String key, float defaultValue) {
        FactEntry entry = get(key);
        if (entry.type() == FactType.NUMBER) {
            return entry.value();
        }
        return defaultValue;
    }

    public boolean getBooleanOrDefault(String key, boolean defaultValue) {
        FactEntry entry = get(key);
        if (entry.type() == FactType.BOOLEAN) {
            return entry.value() != 0.0f;
        }
        return defaultValue;
    }
}
