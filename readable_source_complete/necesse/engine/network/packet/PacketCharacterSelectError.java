/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.gfx.HumanLook;

public class PacketCharacterSelectError
extends Packet {
    public final HumanLook look;
    public final GameMessage error;

    public PacketCharacterSelectError(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.look = new HumanLook(reader);
        this.error = GameMessage.fromContentPacket(reader.getNextContentPacket());
    }

    public PacketCharacterSelectError(HumanLook look, GameMessage error) {
        this.look = look;
        this.error = error;
        PacketWriter writer = new PacketWriter(this);
        look.setupContentPacket(writer, true);
        writer.putNextContentPacket(error.getContentPacket());
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.loading.createCharPhase.submitError(this.look, this.error);
    }
}

