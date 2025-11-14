/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity.interfaces;

import necesse.engine.network.packet.PacketWireHandlerUpdate;
import necesse.entity.objectEntity.ObjectEntity;

public interface OEWireHandler {
    public ObjectEntity getHandlerParent();

    public boolean[] getWireOutputs();

    public boolean getWireOutput(int var1);

    default public void wireUpdateClients() {
        ObjectEntity oe = this.getHandlerParent();
        if (oe.isServer()) {
            oe.getLevel().getServer().network.sendToClientsWithTile(new PacketWireHandlerUpdate(this), oe.getLevel(), oe.tileX, oe.tileY);
        }
    }

    default public void applyWireUpdate(boolean[] outputs) {
        ObjectEntity oe = this.getHandlerParent();
        for (int i = 0; i < 4; ++i) {
            this.getWireOutputs()[i] = outputs[i];
            oe.getLevel().wireManager.updateWire(oe.tileX, oe.tileY, i, outputs[i]);
        }
    }

    default public void updateWireManager() {
        ObjectEntity oe = this.getHandlerParent();
        for (int i = 0; i < 4; ++i) {
            oe.getLevel().wireManager.updateWire(oe.tileX, oe.tileY, i, this.getWireOutputs()[i]);
        }
    }
}

