/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketPlayerHunger
extends Packet {
    public final int slot;
    public final float hunger;

    public PacketPlayerHunger(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.hunger = reader.getNextFloat();
    }

    public PacketPlayerHunger(int slot, float hunger) {
        this.slot = slot;
        this.hunger = hunger;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextFloat(hunger);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            if (client.slot != this.slot) {
                return;
            }
            if (server.world.settings.cheatsAllowedOrHidden()) {
                client.playerMob.hungerLevel = this.hunger;
            } else {
                System.out.println(client.getName() + " tried to change hunger, but cheats aren't allowed");
                server.network.sendPacket((Packet)new PacketPlayerHunger(client.slot, client.playerMob.hungerLevel), client);
            }
        } else {
            System.out.println(client.getName() + " tried to change hunger, but isn't admin");
            server.network.sendPacket((Packet)new PacketPlayerHunger(client.slot, client.playerMob.hungerLevel), client);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null) {
            player.hungerLevel = this.hunger;
        }
    }
}

