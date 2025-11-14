/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import necesse.engine.input.Input;

public interface MouseDraggingElement {
    public boolean draw(int var1, int var2);

    default public boolean isKeyDown(Input input) {
        return input.isKeyDown(-100);
    }
}

