/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.util.Map;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketCharacterSelectError;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.HumanLook;

public class PacketPlayerAppearance
extends Packet {
    public final int slot;
    public final int characterUniqueID;
    public final HumanLook look;
    public final String name;

    public PacketPlayerAppearance(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.slot = reader.getNextByteUnsigned();
        this.characterUniqueID = reader.getNextInt();
        this.look = new HumanLook(reader);
        this.name = reader.getNextString();
    }

    public PacketPlayerAppearance(ServerClient client) {
        this.slot = client.slot;
        this.characterUniqueID = client.getCharacterUniqueID();
        this.look = client.playerMob.look;
        this.name = client.getName();
        this.putData();
    }

    public PacketPlayerAppearance(int slot, int characterUniqueID, PlayerMob player) {
        this.slot = slot;
        this.characterUniqueID = characterUniqueID;
        this.look = player.look;
        this.name = player.getDisplayName();
        this.putData();
    }

    private void putData() {
        PacketWriter writer = new PacketWriter(this);
        writer.putNextByteUnsigned(this.slot);
        writer.putNextInt(this.characterUniqueID);
        this.look.setupContentPacket(writer, true);
        writer.putNextString(this.name);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (this.slot != client.slot) {
            System.out.print("Player " + client.authentication + " (\"" + client.getName() + "\", slot " + client.slot + ") tried to change wrong client slot appearance: " + this.slot);
            return;
        }
        if (client.needAppearance()) {
            GameMessage validName = GameUtils.isValidPlayerName(this.name);
            if (validName != null) {
                client.sendPacket(new PacketCharacterSelectError(this.look, validName));
            } else {
                for (Map.Entry<Long, String> entry : server.usedNames.entrySet()) {
                    Long auth = entry.getKey();
                    String usedName = entry.getValue();
                    if (auth == client.authentication || !usedName.equalsIgnoreCase(this.name)) continue;
                    client.sendPacket(new PacketCharacterSelectError(new HumanLook(), new LocalMessage("ui", "characternameinuse", "name", this.name)));
                    return;
                }
                client.applyAppearancePacket(this);
                if (client.getName().equalsIgnoreCase(Settings.serverOwnerName)) {
                    client.setPermissionLevel(PermissionLevel.OWNER, false);
                }
                client.sendConnectingMessage();
            }
        } else if (client.getPermissionLevel().getLevel() < PermissionLevel.ADMIN.getLevel()) {
            System.out.println(client.getName() + " tried to change appearance, but isn't admin");
            server.network.sendPacket((Packet)new PacketPlayerAppearance(client), client);
        } else if (!client.getName().equalsIgnoreCase(this.name)) {
            System.out.println(client.getName() + " tried to change appearance with wrong name");
            server.network.sendPacket((Packet)new PacketPlayerAppearance(client), client);
        } else if (!server.world.settings.cheatsAllowedOrHidden()) {
            System.out.println(client.getName() + " tried to change appearance, but cheats aren't allowed");
            server.network.sendPacket((Packet)new PacketPlayerAppearance(client), client);
        } else {
            client.applyAppearancePacket(this);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        ClientClient target = client.getClient(this.slot);
        if (target == null) {
            client.network.sendPacket(new PacketRequestPlayerData(this.slot));
        } else {
            target.applyAppearancePacket(this);
        }
        client.loading.createCharPhase.submitPlayerAppearancePacket(this);
    }
}

