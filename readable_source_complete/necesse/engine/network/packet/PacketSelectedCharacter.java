/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.util.Map;
import necesse.engine.Settings;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketCharacterSelectError;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.CharacterSave;
import necesse.engine.save.CharacterSaveNetworkData;
import necesse.engine.util.GameUtils;
import necesse.gfx.HumanLook;

public class PacketSelectedCharacter
extends Packet {
    public final int characterUniqueID;
    public final CharacterSaveNetworkData networkData;

    public PacketSelectedCharacter(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.characterUniqueID = reader.getNextInt();
        this.networkData = reader.getNextBoolean() ? new CharacterSaveNetworkData(reader) : null;
    }

    public PacketSelectedCharacter(int characterUniqueID, CharacterSave save) {
        this.characterUniqueID = characterUniqueID;
        this.networkData = save == null ? null : new CharacterSaveNetworkData(save);
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(characterUniqueID);
        if (this.networkData != null) {
            writer.putNextBoolean(true);
            this.networkData.write(writer);
        } else {
            writer.putNextBoolean(false);
        }
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!server.world.settings.allowOutsideCharacters) {
            client.sendPacket(new PacketCharacterSelectError(new HumanLook(), new LocalMessage("ui", "outsidecharactersnotallowed")));
            return;
        }
        if (!client.hasSubmittedCharacter()) {
            if (this.networkData != null) {
                GameMessage validName = GameUtils.isValidPlayerName(this.networkData.playerName);
                if (validName != null) {
                    client.sendPacket(new PacketCharacterSelectError(new HumanLook(), validName));
                    return;
                }
                for (Map.Entry<Long, String> entry : server.usedNames.entrySet()) {
                    Long auth = entry.getKey();
                    String usedName = entry.getValue();
                    if (auth == client.authentication || !usedName.equalsIgnoreCase(this.networkData.playerName)) continue;
                    client.sendPacket(new PacketCharacterSelectError(new HumanLook(), new LocalMessage("ui", "characternameinuse", "name", this.networkData.playerName)));
                    return;
                }
                if (this.networkData.playerName.equalsIgnoreCase(Settings.serverOwnerName)) {
                    client.setPermissionLevel(PermissionLevel.OWNER, false);
                }
                if (this.networkData.cheatsEnabled && !server.world.settings.cheatsAllowedOrHidden()) {
                    if (client.getPermissionLevel().getLevel() < PermissionLevel.OWNER.getLevel()) {
                        client.sendPacket(new PacketCharacterSelectError(new HumanLook(), new LocalMessage("ui", "characterhascheats")));
                        return;
                    }
                    server.world.settings.enableCheats();
                    System.out.println(this.networkData.playerName + " enabled cheats by joining with a cheats enabled character");
                }
                if (this.networkData.creativeEnabled) {
                    if (!server.world.settings.creativeMode && client.getPermissionLevel().getLevel() < PermissionLevel.OWNER.getLevel()) {
                        client.sendPacket(new PacketCharacterSelectError(new HumanLook(), new StaticMessage("Cannot join a survival world with a creative character")));
                        return;
                    }
                    server.world.settings.enableCreativeMode(true);
                    System.out.println(this.networkData.playerName + " enabled creative by joining with a creative enabled character");
                }
            }
            client.applyLoadedCharacterPacket(this);
            client.sendConnectingMessage();
        }
    }
}

