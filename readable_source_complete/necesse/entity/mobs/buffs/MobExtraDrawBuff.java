/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import java.util.LinkedList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;

public interface MobExtraDrawBuff {
    public void addBackDrawOptions(ActiveBuff var1, LinkedList<DrawOptions> var2, int var3, int var4, TickManager var5, GameCamera var6, PlayerMob var7);

    public void addFrontDrawOptions(ActiveBuff var1, LinkedList<DrawOptions> var2, int var3, int var4, TickManager var5, GameCamera var6, PlayerMob var7);
}

