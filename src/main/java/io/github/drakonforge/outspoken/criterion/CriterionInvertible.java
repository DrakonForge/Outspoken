package io.github.drakonforge.outspoken.criterion;

public abstract class CriterionInvertible extends Criterion {

    protected final boolean invert;

    public CriterionInvertible(boolean invert) {
        this.invert = invert;
    }
}
