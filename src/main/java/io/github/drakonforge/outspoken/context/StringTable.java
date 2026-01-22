package io.github.drakonforge.outspoken.context;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Optional;

public class StringTable {
    private static final int STARTING_ID = 1000;

    private final Object2IntOpenHashMap<String> cache = new Object2IntOpenHashMap<>();
    private final Int2ObjectOpenHashMap<String> lookup = new Int2ObjectOpenHashMap<>();
    private int nextId;

    public StringTable() {
        clear();
    }

    public int cache(String str) {
        return cache.computeIfAbsent(str, (String s) -> {
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
