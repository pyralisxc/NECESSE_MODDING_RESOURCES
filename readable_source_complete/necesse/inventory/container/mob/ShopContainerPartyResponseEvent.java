/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.mob;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.inventory.container.events.ContainerEvent;

public class ShopContainerPartyResponseEvent
extends ContainerEvent {
    public GameMessage error;

    public ShopContainerPartyResponseEvent(GameMessage error) {
        this.error = error;
    }

    public ShopContainerPartyResponseEvent(PacketReader reader) {
        super(reader);
        this.error = reader.getNextBoolean() ? GameMessage.fromPacket(reader) : null;
    }

    @Override
    public void write(PacketWriter writer) {
        if (this.error != null) {
            writer.putNextBoolean(true);
            this.error.writePacket(writer);
        } else {
            writer.putNextBoolean(false);
        }
    }
}

