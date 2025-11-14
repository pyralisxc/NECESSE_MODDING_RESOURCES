/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.level.gameObject.ProcessObjectHandler;
import necesse.level.maps.Level;

public interface EggNestObjectInterface {
    public ProcessObjectHandler getLayEggHandler(Level var1, int var2, int var3);

    public ProcessObjectHandler getFertilizeEggHandler(Level var1, int var2, int var3);
}

