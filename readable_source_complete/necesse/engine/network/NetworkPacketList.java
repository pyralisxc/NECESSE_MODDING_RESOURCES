/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Consumer;
import necesse.engine.network.NetworkPacket;

public class NetworkPacketList {
    public final int timeout;
    private LinkedList<IncompletePacket> packets = new LinkedList();

    public NetworkPacketList(int timeout) {
        this.timeout = timeout;
    }

    public NetworkPacket submitPacket(NetworkPacket packet, Consumer<NetworkPacket> onTimeout) {
        if (packet.isComplete()) {
            return packet;
        }
        this.tickTimeout(onTimeout);
        ListIterator it = this.packets.listIterator();
        while (it.hasNext()) {
            IncompletePacket next = (IncompletePacket)it.next();
            if (!next.packet.canMerge(packet)) continue;
            next.packet = next.packet.mergePackets(packet);
            next.expireTime = System.currentTimeMillis() + (long)this.timeout;
            if (next.packet.isComplete()) {
                it.remove();
                return next.packet;
            }
            return null;
        }
        this.packets.add(new IncompletePacket(System.currentTimeMillis() + (long)this.timeout, packet));
        return null;
    }

    public void tickTimeout(Consumer<NetworkPacket> onTimeout) {
        while (!this.packets.isEmpty() && this.packets.getFirst().expireTime < System.currentTimeMillis()) {
            IncompletePacket removed = this.packets.removeFirst();
            if (onTimeout == null) continue;
            onTimeout.accept(removed.packet);
        }
    }

    private class IncompletePacket {
        public long expireTime;
        public NetworkPacket packet;

        public IncompletePacket(long expireTime, NetworkPacket packet) {
            this.expireTime = expireTime;
            this.packet = packet;
        }
    }
}

