package io.github.drakonforge.outspoken.database.context;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContextTable {
    public enum FactType {
        NULL,
        STRING,
        NUMBER,
        BOOLEAN,
        INT_LIST,
        STRING_LIST;

        public boolean isArray() {
            return this == INT_LIST || this == STRING_LIST;
        }
    }

    private final ContextManager contextManager;
    private final Object2FloatMap<String> contextValueMap;
    private final Map<String, FactType> contextTypeMap;
    @Nullable
    private Map<String, IntSet> contextListMap = null;

    public ContextTable(ContextManager contextManager) {
        this.contextManager = contextManager;
        this.contextTypeMap = new HashMap<>();
        this.contextValueMap = new Object2FloatOpenHashMap<>();
    }

    private void setWithType(String key, FactType type, float value) {
        contextTypeMap.put(key, type);
        contextValueMap.put(key, value);
    }

    public ContextTable set(String key, String value) {
        int symbol = contextManager.getStringTable().cache(value);
        setWithType(key, FactType.STRING, symbol);
        return this;
    }

    public ContextTable set(String key, float value) {
        setWithType(key, FactType.NUMBER, value);
        return this;
    }

    public ContextTable set(String key, int value) {
        setWithType(key, FactType.NUMBER, value);
        return this;
    }

    public ContextTable set(String key, boolean value) {
        setWithType(key, FactType.BOOLEAN, value ? 1 : 0);
        return this;
    }

    public ContextTable set(String key, IntSet value) {
        if (contextListMap == null) {
            contextListMap = new HashMap<>();
        }
        contextTypeMap.put(key, FactType.INT_LIST);
        contextListMap.put(key, value);
        return this;
    }

    public ContextTable set(String key, Set<String> value) {
        if (contextListMap == null) {
            contextListMap = new HashMap<>();
        }
        IntSet set = new IntOpenHashSet(value.size());
        StringTable stringTable = contextManager.getStringTable();
        for (String item : value) {
            int symbol = stringTable.cache(item);
            set.add(symbol);
        }
        contextTypeMap.put(key, FactType.STRING_LIST);
        contextListMap.put(key, set);
        return this;
    }

    public ContextTable remove(String key) {
        contextValueMap.removeFloat(key);
        contextTypeMap.remove(key);
        if (contextListMap != null) {
            contextListMap.remove(key);
        }
        return this;
    }

    public boolean contains(String key) {
        return contextTypeMap.containsKey(key);
    }

    public boolean containsArray(String key) {
        FactType type = getType(key);
        return type == FactType.STRING_LIST || type == FactType.INT_LIST;
    }

    @Nonnull
    public FactType getType(String key) {
        return contextTypeMap.getOrDefault(key, FactType.NULL);
    }

    public float getRawValue(String key) {
        return contextValueMap.getFloat(key);
    }

    public Optional<String> getString(String key) {
        FactType type = getType(key);
        if (type == FactType.STRING) {
            int symbol = (int) contextValueMap.getFloat(key);
            return contextManager.getStringTable().lookup(symbol);
        }
        return Optional.empty();
    }

    public String getStringOrDefault(String key, String defaultValue) {
        return getString(key).orElse(defaultValue);
    }

    public OptionalInt getInt(String key) {
        FactType type = getType(key);
        if (type == FactType.NUMBER) {
            return OptionalInt.of((int) contextValueMap.getFloat(key));
        }
        return OptionalInt.empty();
    }

    public int getIntOrDefault(String key, int defaultValue) {
        return getInt(key).orElse(defaultValue);
    }

    public float getFloatOrDefault(String key, float defaultValue) {
        FactType type = getType(key);
        if (type == FactType.NUMBER) {
            return contextValueMap.getFloat(key);
        }
        return defaultValue;
    }

    public boolean getBooleanOrDefault(String key, boolean defaultValue) {
        FactType type = getType(key);
        if (type == FactType.BOOLEAN) {
            return contextValueMap.getFloat(key) != 0.0f;
        }
        return defaultValue;
    }

    public boolean doesListContainValue(String key, int value) {
        FactType type = getType(key);
        if (type.isArray() && contextListMap != null) {
            IntSet list = contextListMap.get(key);
            if (list != null) {
                return list.contains(value);
            }
        }
        return false;
    }

    public boolean doesListContainInteger(String key, int value) {
        FactType type = getType(key);
        if (type == FactType.INT_LIST && contextListMap != null) {
            IntSet list = contextListMap.get(key);
            if (list != null) {
                return list.contains(value);
            }
        }
        return false;
    }

    public boolean doesListContainString(String key, String value) {
        FactType type = getType(key);
        if (type == FactType.STRING_LIST && contextListMap != null) {
            int symbol = contextManager.getStringTable().cache(value);
            IntSet list = contextListMap.get(key);
            if (list != null) {
                return list.contains(symbol);
            }
        }
        return false;
    }
}
