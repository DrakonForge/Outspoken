package io.github.drakonforge.outspoken.database;

public class RuleTable {

    public boolean sortEntries() {
        if (isSorted) {
            return false;
        }
        // TODO: Sort
        isSorted = true;
        return true;
    }

    private boolean isSorted = false;
}
