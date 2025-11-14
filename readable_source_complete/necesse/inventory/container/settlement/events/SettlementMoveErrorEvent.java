/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.events;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class SettlementMoveErrorEvent
extends ContainerEvent {
    public final GameMessage error;

    public SettlementMoveErrorEvent(GameMessage error) {
        this.error = error;
    }

    public SettlementMoveErrorEvent(PacketReader reader) {
        super(reader);
        this.error = GameMessage.fromContentPacket(reader.getNextContentPacket());
    }

    @Override
    public void write(PacketWriter writer) {
        writer.putNextContentPacket(this.error.getContentPacket());
    }
}

