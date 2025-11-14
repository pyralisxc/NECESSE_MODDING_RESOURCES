/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Dimension;
import necesse.level.gameObject.DecorDrawOffset;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;

public interface DecorationHolderInterface {
    public DecorDrawOffset getDecorationDrawOffset(Level var1, int var2, int var3, GameObject var4);

    public boolean canPlaceDecoration(Level var1, int var2, int var3);

    public Dimension getMaxDecorationSize(Level var1, int var2, int var3);
}

