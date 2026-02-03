package io.github.drakonforge.outspoken.database.context;

public class ContextManager {
    // TODO: Record validator issues
    // TODO: Move case insensitive to config later
    private final StringTable stringTable = new StringTable(true);

    public StringTable getStringTable() {
        return stringTable;
    }

    public ContextTable createBlankContextTable() {
        // TODO: Consider object pooling
        return new ContextTable(this);
    }
}
