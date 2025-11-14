/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketMountAbility;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;

public interface MountAbility {
    public void runMountAbility(PlayerMob var1, Packet var2);

    public boolean canRunMountAbility(PlayerMob var1, Packet var2);

    default public Packet getMountAbilityContent(PlayerMob player, GameCamera camera) {
        return new Packet();
    }

    default public void runAndSendMountAbility(Client client, PlayerMob player, Packet content) {
        this.runMountAbility(player, content);
        client.network.sendPacket(new PacketMountAbility(client.getSlot(), (Mob)((Object)this), content));
    }
}

