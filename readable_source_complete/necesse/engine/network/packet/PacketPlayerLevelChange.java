/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainMenu;
import necesse.engine.util.LevelIdentifier;

public class PacketPlayerLevelChange
extends Packet {
    public final int slot;
    public final LevelIdentifier identifier;
    public final boolean mountFollow;

    public PacketPlayerLevelChange(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.identifier = new LevelIdentifier(reader);
        this.mountFollow = reader.getNextBoolean();
    }

    public PacketPlayerLevelChange(int slot, LevelIdentifier identifier, boolean mountFollow) {
        this.slot = slot;
        this.identifier = identifier;
        this.mountFollow = mountFollow;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(slot);
        identifier.writePacket(writer);
        writer.putNextBoolean(mountFollow);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
            return;
        }
        if (!server.world.settings.cheatsAllowedOrHidden()) {
            return;
        }
        if (client.slot == this.slot) {
            client.changeLevel(this.identifier, null, this.mountFollow);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target = client.getClient(this.slot);
        if (this.slot == client.getSlot()) {
            if (target != null) {
                target.applyLevelChangePacket(this);
            }
            if (Settings.instantLevelChange) {
                client.reset();
            } else if (GlobalData.getCurrentState() instanceof MainMenu) {
                ((MainMenu)GlobalData.getCurrentState()).changeLevel(client);
            } else {
                GlobalData.setCurrentState(new MainMenu(client));
            }
        } else if (target == null) {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        } else {
            target.applyLevelChangePacket(this);
        }
    }
}

