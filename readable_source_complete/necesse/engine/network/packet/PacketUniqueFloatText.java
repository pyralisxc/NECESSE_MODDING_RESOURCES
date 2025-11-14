/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import java.util.Objects;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;

public class PacketUniqueFloatText
extends Packet {
    public int levelX;
    public int levelY;
    public final GameMessage message;
    public String uniqueType;
    public int hoverTime;

    public PacketUniqueFloatText(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.levelX = reader.getNextInt();
        this.levelY = reader.getNextInt();
        this.message = GameMessage.fromPacket(reader);
        this.uniqueType = reader.getNextBoolean() ? reader.getNextString() : null;
        this.hoverTime = reader.getNextInt();
    }

    public PacketUniqueFloatText(int levelX, int levelY, GameMessage message, String uniqueType, int hoverTime) {
        this.levelX = levelX;
        this.levelY = levelY;
        this.message = Objects.requireNonNull(message);
        this.uniqueType = uniqueType;
        this.hoverTime = hoverTime;
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(levelX);
        writer.putNextInt(levelY);
        message.writePacket(writer);
        writer.putNextBoolean(uniqueType != null);
        if (uniqueType != null) {
            writer.putNextString(uniqueType);
        }
        writer.putNextInt(hoverTime);
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        String message = this.message.translate();
        UniqueFloatText text = new UniqueFloatText(this.levelX, this.levelY, message, new FontOptions(16).outline(), this.uniqueType);
        text.hoverTime = this.hoverTime;
        client.getLevel().hudManager.addElement(text);
    }
}

