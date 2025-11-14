/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEWireHandler;

public class PacketWireHandlerUpdate
extends Packet {
    public final int tileX;
    public final int tileY;
    public final boolean[] outputs;

    public PacketWireHandlerUpdate(byte[] data) {
        super(data);
        PacketReader reader = new PacketReader(this);
        this.tileX = reader.getNextInt();
        this.tileY = reader.getNextInt();
        this.outputs = new boolean[4];
        for (int i = 0; i < this.outputs.length; ++i) {
            this.outputs[i] = reader.getNextBoolean();
        }
    }

    public PacketWireHandlerUpdate(OEWireHandler handler) {
        ObjectEntity oe = handler.getHandlerParent();
        this.tileX = oe.tileX;
        this.tileY = oe.tileY;
        this.outputs = new boolean[4];
        for (int i = 0; i < this.outputs.length; ++i) {
            this.outputs[i] = handler.getWireOutput(i);
        }
        PacketWriter writer = new PacketWriter(this);
        writer.putNextInt(this.tileX);
        writer.putNextInt(this.tileY);
        for (boolean output : this.outputs) {
            writer.putNextBoolean(output);
        }
    }

    @Override
    public void processClient(NetworkPacket packet, Client client) {
        if (client.getLevel() == null) {
            return;
        }
        ObjectEntity oe = client.getLevel().entityManager.getObjectEntity(this.tileX, this.tileY);
        if (oe instanceof OEWireHandler) {
            ((OEWireHandler)((Object)oe)).applyWireUpdate(this.outputs);
        }
    }
}

