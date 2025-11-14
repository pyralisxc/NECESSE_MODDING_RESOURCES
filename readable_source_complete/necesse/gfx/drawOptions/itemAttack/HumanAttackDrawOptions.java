/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawOptions.itemAttack;

import necesse.gfx.drawOptions.DrawOptions;
import necesse.level.maps.light.GameLight;

public interface HumanAttackDrawOptions {
    public HumanAttackDrawOptions setOffsets(int var1, int var2, int var3, int var4, float var5, int var6, int var7, int var8, int var9, int var10);

    public HumanAttackDrawOptions light(GameLight var1);

    public DrawOptions pos(int var1, int var2);
}

