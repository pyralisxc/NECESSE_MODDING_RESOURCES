/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import necesse.engine.network.Packet;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.camera.GameCamera;

public interface ActiveBuffAbility {
    public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3);

    public void onActiveAbilityStarted(PlayerMob var1, ActiveBuff var2, Packet var3);

    public boolean tickActiveAbility(PlayerMob var1, ActiveBuff var2, boolean var3);

    public void onActiveAbilityUpdate(PlayerMob var1, ActiveBuff var2, Packet var3);

    public void onActiveAbilityStopped(PlayerMob var1, ActiveBuff var2);

    default public Packet getStartAbilityContent(PlayerMob player, ActiveBuff buff, GameCamera camera) {
        return new Packet();
    }

    default public Packet getRunningAbilityContent(PlayerMob player, ActiveBuff buff) {
        return new Packet();
    }
}

