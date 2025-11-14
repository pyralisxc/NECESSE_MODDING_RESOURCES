/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GlobalData;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPermissionUpdate;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.state.MainGame;
import necesse.engine.world.WorldSettings;

public class PacketSettings
extends Packet {
    public PacketSettings(byte[] data) {
        super(data);
    }

    public PacketSettings(WorldSettings settings) {
        PacketWriter writer = new PacketWriter(this);
        settings.setupContentPacket(writer);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (client.getPermissionLevel().getLevel() >= PermissionLevel.ADMIN.getLevel()) {
            server.world.settings.applyContentPacket(new PacketReader(this));
            server.network.sendToAllClients(this);
            server.world.settings.saveSettings();
        } else {
            System.out.println(client.getName() + " tried to change world settings without permissions");
            server.network.sendPacket((Packet)new PacketSettings(server.world.settings), client);
            server.network.sendPacket((Packet)new PacketPermissionUpdate(client.getPermissionLevel()), client);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.worldSettings == null) {
            client.worldSettings = new WorldSettings(client, new PacketReader(this), false);
        } else {
            client.worldSettings.applyContentPacket(new PacketReader(this));
        }
        if (GlobalData.getCurrentState() instanceof MainGame) {
            MainGame state = (MainGame)GlobalData.getCurrentState();
            if (!client.worldSettings.achievementsEnabled()) {
                state.formManager.pauseMenu.disableAchievements();
            }
        }
    }
}

