/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketCharacterSelectError;
import necesse.engine.network.packet.PacketDownloadCharacterResponse;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.CharacterSave;
import necesse.gfx.HumanLook;

public class PacketDownloadCharacter
extends Packet {
    public final int characterUniqueID;

    public PacketDownloadCharacter(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.characterUniqueID = reader.getNextInt();
    }

    public PacketDownloadCharacter(int characterUniqueID) {
        this.characterUniqueID = characterUniqueID;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(characterUniqueID);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (!server.world.settings.allowOutsideCharacters) {
            client.sendPacket(new PacketCharacterSelectError(new HumanLook(), new LocalMessage("ui", "outsidecharactersnotallowed")));
            return;
        }
        if (!client.hasSubmittedCharacter()) {
            if (client.getCharacterUniqueID() == this.characterUniqueID) {
                client.sendPacket(new PacketDownloadCharacterResponse(this.characterUniqueID, new CharacterSave(client)));
            } else {
                GameLog.warn.println(client.getName() + " tried to download unknown character with uniqueID " + this.characterUniqueID);
            }
        }
    }
}

