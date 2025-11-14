/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketCreativeCheck;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;

public class PacketCreativePlayerSettings
extends PacketCreativeCheck {
    public final int slot;
    public final Packet content;

    public PacketCreativePlayerSettings(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextShortUnsigned();
        this.content = reader.getNextContentPacket();
    }

    public PacketCreativePlayerSettings(PlayerMob player) {
        this.slot = player.getPlayerSlot();
        this.content = new Packet();
        player.setupCreativeSettingsPacket(new PacketWriter(this.content));
        PacketWriter writer = new PacketWriter(this);
        writer.putNextShortUnsigned(this.slot);
        writer.putNextContentPacket(this.content);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!PacketCreativePlayerSettings.checkCreativeAndSendUpdate(server, client)) {
            return;
        }
        if (this.slot != client.slot) {
            return;
        }
        client.playerMob.applyCreativeSettingsPacket(new PacketReader(this.content));
        server.network.sendToClientsWithEntityExcept(this, client.playerMob, client);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        PlayerMob player = client.getPlayer(this.slot);
        if (player != null) {
            player.applyCreativeSettingsPacket(new PacketReader(this.content));
        }
    }
}

