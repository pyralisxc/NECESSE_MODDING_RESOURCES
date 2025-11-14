/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import necesse.engine.network.Packet;
import necesse.engine.util.ByteIterator;

public class PacketIterator
extends ByteIterator {
    protected Packet packet;

    protected PacketIterator(Packet packet, int startIndex) {
        this.packet = packet;
        this.resetIndex(startIndex);
    }

    protected PacketIterator(Packet packet) {
        this(packet, 0);
    }

    public PacketIterator(PacketIterator copy) {
        super(copy);
        this.packet = copy.packet;
    }

    @Override
    public int getSizeOfData() {
        return this.packet.getSize();
    }

    public Packet getPacket() {
        return this.packet;
    }
}

