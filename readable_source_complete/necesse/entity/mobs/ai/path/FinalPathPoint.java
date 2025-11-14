/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.path;

import java.awt.Point;
import java.util.function.Supplier;

public class FinalPathPoint
extends Point {
    public Supplier<Boolean> checkValid;

    public FinalPathPoint(int x, int y, Supplier<Boolean> checkValid) {
        super(x, y);
        this.checkValid = checkValid;
    }

    public boolean checkValid() {
        return this.checkValid.get();
    }
}

