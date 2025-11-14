/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import necesse.engine.network.LatencyPacket;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.NetworkPacketList;
import necesse.engine.network.SizePacket;
import necesse.engine.network.StatPacket;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.network.packet.PacketNetworkUpdate;
import necesse.engine.registries.PacketRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;

public abstract class PacketManager {
    public static int networkTrackingTime = 5;
    private static Comparator<LatencyPacket> packetComparator = Comparator.comparingLong(p -> p.packet.timestamp == 0L ? Long.MIN_VALUE : p.packet.timestamp);
    public float dropPacketChance;
    public int minSimulatedLatency;
    public int maxSimulatedLatency;
    private final HashMap<NetworkInfo, Long> logPackets = new HashMap();
    private final NetworkPacketList incompletePackets = new NetworkPacketList(5000);
    private List<LatencyPacket> receivedPackets;
    private StatPacket[] totalInPacketTypes;
    private StatPacket[] totalOutPacketTypes;
    private long totalInBytes;
    private long totalOutBytes;
    private long totalInPackets;
    private long totalOutPackets;
    private long packetsIncomplete;
    private long packetsInLost;
    private long packetsOutLost;
    private long trackInBytes;
    private long trackOutBytes;
    private List<SizePacket> trackInPackets;
    private List<SizePacket> trackOutPackets;

    public PacketManager(float dropPacketChance, int minSimulatedLatency, int maxSimulatedLatency) {
        this.dropPacketChance = dropPacketChance;
        this.minSimulatedLatency = minSimulatedLatency;
        this.maxSimulatedLatency = maxSimulatedLatency;
        this.reset();
    }

    public PacketManager() {
        this(0.0f, 0, 0);
    }

    public void reset() {
        this.receivedPackets = Collections.synchronizedList(new LinkedList());
        this.resetTotalTracking();
        this.resetAverageTracking();
    }

    public void resetTotalTracking() {
        int packetTypes = PacketRegistry.getTotalRegistered();
        this.totalInPacketTypes = new StatPacket[packetTypes];
        this.totalOutPacketTypes = new StatPacket[packetTypes];
        for (int i = 0; i < this.totalInPacketTypes.length; ++i) {
            this.totalInPacketTypes[i] = new StatPacket(i);
            this.totalOutPacketTypes[i] = new StatPacket(i);
        }
        this.totalInBytes = 0L;
        this.totalOutBytes = 0L;
        this.totalInPackets = 0L;
        this.totalOutPackets = 0L;
        this.packetsIncomplete = 0L;
        this.packetsInLost = 0L;
        this.packetsOutLost = 0L;
    }

    public void resetAverageTracking() {
        this.trackInBytes = 0L;
        this.trackOutBytes = 0L;
        this.trackInPackets = Collections.synchronizedList(new ArrayList());
        this.trackOutPackets = Collections.synchronizedList(new ArrayList());
    }

    public void clearReceivedPackets() {
        this.receivedPackets.clear();
    }

    public void startVerboseLogging(NetworkInfo info, int milliseconds) {
        if (info == null) {
            return;
        }
        this.logPackets.put(info, System.currentTimeMillis() + (long)milliseconds);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void submitInPacket(NetworkPacket packet) {
        PacketManager packetManager = this;
        synchronized (packetManager) {
            long time = this.logPackets.getOrDefault(packet.networkInfo, 0L);
            if (time >= System.currentTimeMillis()) {
                int size = packet.isComplete() ? packet.getByteSize() : packet.getCurrentByteSize();
                System.out.println("VERBOSE PACKET IN - " + (packet.networkInfo == null ? "LOCAL" : packet.networkInfo.getDisplayName()) + " - Type: " + PacketRegistry.getPacketClassName(packet.type) + ", Size: " + GameUtils.getByteString(size));
            } else if (time != 0L) {
                this.logPackets.remove(packet.networkInfo);
            }
            packet = this.incompletePackets.submitPacket(packet, incompletePacket -> {
                ++this.packetsIncomplete;
                int byteSize = incompletePacket.getCurrentByteSize();
                this.totalInBytes += (long)byteSize;
                this.trackInPackets.add(new SizePacket(incompletePacket.type, byteSize));
                this.trackInBytes += (long)byteSize;
            });
            if (packet == null) {
                return;
            }
            ++this.totalInPackets;
            this.totalInBytes += (long)packet.getByteSize();
            ++this.totalInPacketTypes[packet.type].amount;
            this.totalInPacketTypes[packet.type].bytes += packet.getByteSize();
            this.trackInPackets.add(new SizePacket(packet.type, packet.getByteSize()));
            this.trackInBytes += (long)packet.getByteSize();
            int latency = this.minSimulatedLatency;
            if (this.maxSimulatedLatency > this.minSimulatedLatency) {
                latency += GameRandom.globalRandom.nextInt(this.maxSimulatedLatency - this.minSimulatedLatency);
            }
            if (latency == 0 && PacketRegistry.processInstantly(packet.type)) {
                this.processInstantly(packet);
            } else {
                GameUtils.insertSortedList(this.receivedPackets, new LatencyPacket(packet, latency), packetComparator);
            }
        }
    }

    public abstract void processInstantly(NetworkPacket var1);

    public void submitOutPacket(NetworkPacket packet) {
        long time = this.logPackets.getOrDefault(packet.networkInfo, 0L);
        if (time >= System.currentTimeMillis()) {
            int size = packet.isComplete() ? packet.getByteSize() : packet.getCurrentByteSize();
            System.out.println("VERBOSE PACKET OUT - " + (packet.networkInfo == null ? "LOCAL" : packet.networkInfo.getDisplayName()) + " - Type: " + PacketRegistry.getPacketClassName(packet.type) + ", Size: " + GameUtils.getByteString(size));
        } else if (time != 0L) {
            this.logPackets.remove(packet.networkInfo);
        }
        this.totalOutBytes += (long)packet.getByteSize();
        ++this.totalOutPackets;
        ++this.totalOutPacketTypes[packet.type].amount;
        this.totalOutPacketTypes[packet.type].bytes += packet.getByteSize();
        this.trackOutPackets.add(new SizePacket(packet.type, packet.getByteSize()));
        this.trackOutBytes += (long)packet.getByteSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public NetworkPacket nextPacket() {
        PacketManager packetManager = this;
        synchronized (packetManager) {
            if (this.receivedPackets.size() != 0) {
                LatencyPacket output = this.receivedPackets.get(0);
                if (output == null) {
                    this.receivedPackets.remove(0);
                    return null;
                }
                if (output.isReady()) {
                    this.receivedPackets.remove(0);
                    return output.packet;
                }
                return null;
            }
            return null;
        }
    }

    public long getTotalInPackets() {
        return this.totalInPackets;
    }

    public long getTotalOutPackets() {
        return this.totalOutPackets;
    }

    public long getTotalInBytes() {
        return this.totalInBytes;
    }

    public long getTotalOutBytes() {
        return this.totalOutBytes;
    }

    public String getTotalIn() {
        return GameUtils.getByteString(this.getTotalInBytes());
    }

    public String getTotalOut() {
        return GameUtils.getByteString(this.getTotalOutBytes());
    }

    public StatPacket getTotalInStats(int type) {
        return this.totalInPacketTypes[type];
    }

    public StatPacket getTotalOutStats(int type) {
        return this.totalOutPacketTypes[type];
    }

    public int getTotalInTypesAmount(int type) {
        return this.totalInPacketTypes[type].amount;
    }

    public int getTotalOutTypesAmount(int type) {
        return this.totalOutPacketTypes[type].amount;
    }

    public int getTotalInTypesBytes(int type) {
        return this.totalInPacketTypes[type].bytes;
    }

    public int getTotalOutTypesBytes(int type) {
        return this.totalOutPacketTypes[type].bytes;
    }

    public long getAverageInBytes() {
        return this.trackInBytes / (long)networkTrackingTime;
    }

    public long getAverageOutBytes() {
        return this.trackOutBytes / (long)networkTrackingTime;
    }

    public String getAverageOut() {
        return GameUtils.getByteString(this.getAverageOutBytes());
    }

    public String getAverageIn() {
        return GameUtils.getByteString(this.getAverageInBytes());
    }

    public long getAverageOutPackets() {
        return this.trackOutPackets.size();
    }

    public long getAverageInPackets() {
        return this.trackInPackets.size();
    }

    public Iterable<SizePacket> getRecentInPackets() {
        return this.trackInPackets;
    }

    public Iterable<SizePacket> getRecentOutPackets() {
        return this.trackOutPackets;
    }

    public Stream<SizePacket> streamRecentInPackets() {
        return this.trackInPackets.stream();
    }

    public Stream<SizePacket> streamRecentOutPackets() {
        return this.trackOutPackets.stream();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tickNetworkManager() {
        try {
            List<SizePacket> trackInPackets;
            List<SizePacket> trackOutPackets;
            List<SizePacket> list = trackOutPackets = this.trackOutPackets;
            synchronized (list) {
                SizePacket first;
                while (!trackOutPackets.isEmpty() && (first = trackOutPackets.get(0)).isExpired()) {
                    this.trackOutBytes -= (long)first.byteSize;
                    trackOutPackets.remove(0);
                }
            }
            List<SizePacket> list2 = trackInPackets = this.trackInPackets;
            synchronized (list2) {
                SizePacket first;
                while (!trackInPackets.isEmpty() && (first = trackInPackets.get(0)).isExpired()) {
                    this.trackInBytes -= (long)first.byteSize;
                    trackInPackets.remove(0);
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error ticking network tracking: " + e.getClass().getSimpleName() + " : " + e.getMessage());
        }
    }

    public void applyNetworkUpdate(PacketNetworkUpdate update) {
        this.packetsInLost = this.getTotalInPackets() - update.totalOutPackets - 1L;
        this.packetsOutLost = this.getTotalOutPackets() - update.totalInPackets - 1L;
    }

    public long getTotalIncompleteDropped() {
        return this.packetsIncomplete;
    }

    public long getLostInPackets() {
        return this.packetsInLost;
    }

    public long getLostOutPackets() {
        return this.packetsOutLost;
    }

    static {
        packetComparator = packetComparator.thenComparing(LatencyPacket::getReadyTime);
    }
}

