/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.awt.Color;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;

public class PacketStatusMessage
extends Packet {
    public final GameMessage message;
    public final Color color;
    public final float seconds;

    public PacketStatusMessage(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.message = GameMessage.fromContentPacket(reader.getNextContentPacket());
        this.color = new Color(reader.getNextInt());
        this.seconds = reader.getNextFloat();
    }

    public PacketStatusMessage(GameMessage message, Color col, float seconds) {
        this.message = message;
        this.color = col;
        this.seconds = seconds;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextContentPacket(message.getContentPacket());
        writer.putNextInt(col.getRGB());
        writer.putNextFloat(seconds);
    }

    public PacketStatusMessage(String message, Color col, float seconds) {
        this(new StaticMessage(message), col, seconds);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        client.setMessage(this.message, this.color, this.seconds);
    }
}

