/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object.fallenAltar;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.level.maps.incursion.IncursionData;

public class ContainerIncursionData {
    public final IncursionData data;
    public final GameMessage unavailableError;

    public ContainerIncursionData(IncursionData data, GameMessage unavailableError) {
        this.data = data;
        this.unavailableError = unavailableError;
    }

    public ContainerIncursionData(PacketReader reader) {
        this.data = IncursionData.fromPacket(reader);
        this.unavailableError = reader.getNextBoolean() ? GameMessage.fromPacket(reader) : null;
    }

    public void writePacket(PacketWriter writer) {
        IncursionData.writePacket(this.data, writer);
        if (this.unavailableError != null) {
            writer.putNextBoolean(true);
            this.unavailableError.writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }
    }
}

