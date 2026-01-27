package io.github.drakonforge.outspoken.database.context;

public class ContextManager {
    // TODO: Record validator issues
    private final StringTable stringTable = new StringTable();

    public StringTable getStringTable() {
        return stringTable;
    }
}
