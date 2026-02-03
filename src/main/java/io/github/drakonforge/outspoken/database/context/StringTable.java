package io.github.drakonforge.outspoken.database.context;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;

public class StringTable {
    private static final int STARTING_ID = 1000;

    private final Object2IntOpenHashMap<String> cache = new Object2IntOpenHashMap<>();
    private final Int2ObjectOpenHashMap<String> lookup = new Int2ObjectOpenHashMap<>();
    private int nextId;
    private final boolean caseInsensitive; // Will preserve the casing of the original cache

    public StringTable(boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
        clear();
    }

    public int cache(String str) {
        if (str == null) {
            return 0;
        }
        return cache.computeIfAbsent(caseInsensitive ? str.toLowerCase() : str, (String s) -> {
            int id = nextId++;
            lookup.put(id, s);
            return id;
        });
    }

    public Optional<String> lookup(int symbol) {
        return Optional.ofNullable(lookup.getOrDefault(symbol, null));
    }

    public void clear() {
        nextId = STARTING_ID;
        cache.clear();
        lookup.clear();
    }
}
