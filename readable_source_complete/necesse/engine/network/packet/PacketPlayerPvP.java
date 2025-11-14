/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketPlayerPvP
extends Packet {
    public final int slot;
    public final boolean pvpEnabled;

    public PacketPlayerPvP(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        int slot = reader.getNextByteUnsigned();
        if (slot > 250) {
            slot = (byte)slot;
        }
        this.slot = slot;
        this.pvpEnabled = reader.getNextBoolean();
    }

    public PacketPlayerPvP(int slot, boolean pvp) {
        this.slot = slot;
        this.pvpEnabled = pvp;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        writer.putNextBoolean(this.pvpEnabled);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (this.slot == client.slot) {
            if (!server.world.settings.forcedPvP && client.pvpSetCooldown <= System.currentTimeMillis()) {
                client.pvpEnabled = this.pvpEnabled;
                client.pvpSetCooldown = System.currentTimeMillis() + 4800L;
                System.out.println(client.getName() + " " + (this.pvpEnabled ? "enabled" : "disabled") + " PvP");
            }
            server.network.sendToAllClients(new PacketPlayerPvP(client.slot, client.pvpEnabled));
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (this.slot == -1) {
            client.worldSettings.forcedPvP = this.pvpEnabled;
        } else if (client.getClient(this.slot) != null && client.getClient((int)this.slot).pvpEnabled != this.pvpEnabled) {
            client.getClient((int)this.slot).pvpEnabled = this.pvpEnabled;
            if (client.loading.isDone()) {
                if (this.pvpEnabled) {
                    client.chat.addMessage(Localization.translate("misc", "pvpenable", "player", client.getClient(this.slot).getName()));
                } else {
                    client.chat.addMessage(Localization.translate("misc", "pvpdisable", "player", client.getClient(this.slot).getName()));
                }
            }
        }
    }
}

