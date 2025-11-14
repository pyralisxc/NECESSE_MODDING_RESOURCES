/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs;

import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketBuffAbility;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.gfx.camera.GameCamera;

public interface BuffAbility {
    public void runAbility(PlayerMob var1, ActiveBuff var2, Packet var3);

    public boolean canRunAbility(PlayerMob var1, ActiveBuff var2, Packet var3);

    default public Packet getAbilityContent(PlayerMob player, ActiveBuff buff, GameCamera camera) {
        return new Packet();
    }

    default public void runAndSendAbility(Client client, PlayerMob player, ActiveBuff buff, Packet content) {
        this.runAbility(player, buff, content);
        client.network.sendPacket(new PacketBuffAbility(client.getSlot(), buff.buff, content));
    }
}

