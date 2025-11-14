/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.level.gameObject.DecorDrawOffset;
import necesse.level.maps.Level;

public interface TorchHolderInterface {
    public DecorDrawOffset getTorchDrawOffset(Level var1, int var2, int var3);

    default public boolean canPlaceTorch(Level level, int tileX, int tileY) {
        return true;
    }
}

