/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.save.CharacterSave;
import necesse.engine.save.CharacterSaveNetworkData;

public class PacketDownloadCharacterResponse
extends Packet {
    public final int characterUniqueID;
    public final CharacterSaveNetworkData networkData;

    public PacketDownloadCharacterResponse(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.characterUniqueID = reader.getNextInt();
        this.networkData = new CharacterSaveNetworkData(reader);
    }

    public PacketDownloadCharacterResponse(int characterUniqueID, CharacterSave character) {
        this.characterUniqueID = characterUniqueID;
        this.networkData = new CharacterSaveNetworkData(character);
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(characterUniqueID);
        this.networkData.write(writer);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.loading.createCharPhase.submitDownloadedCharacter(this);
    }
}

