/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.network.Packet;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;

public interface ActiveMountAbility {
    public boolean canRunMountAbility(PlayerMob var1, Packet var2);

    public void onActiveMountAbilityStarted(PlayerMob var1, Packet var2);

    public boolean tickActiveMountAbility(PlayerMob var1, boolean var2);

    public void onActiveMountAbilityUpdate(PlayerMob var1, Packet var2);

    public void onActiveMountAbilityStopped(PlayerMob var1);

    default public Packet getStartMountAbilityContent(PlayerMob player, GameCamera camera) {
        return new Packet();
    }

    default public Packet getRunningMountAbilityContent(PlayerMob player) {
        return new Packet();
    }
}

