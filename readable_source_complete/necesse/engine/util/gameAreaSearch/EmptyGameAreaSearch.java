/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util.gameAreaSearch;

import necesse.engine.util.gameAreaSearch.GameAreaSearch;

public class EmptyGameAreaSearch<T>
extends GameAreaSearch<T> {
    public EmptyGameAreaSearch() {
        super(0, 0, 0, 0, 0, 0, 0);
        this.isDone = true;
    }

    @Override
    protected T get(int x, int y) {
        return null;
    }
}

