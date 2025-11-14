/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.packet;

import necesse.engine.GameLog;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.networkInfo.DatagramNetworkInfo;
import necesse.engine.network.packet.PacketServerStatus;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PacketServerStatusRequest
extends Packet {
    public final int respondPort;
    public final int state;

    public PacketServerStatusRequest(byte[] data) {
        super(data);
        this.respondPort = this.getShortUnsigned(0);
        this.state = this.getInt(2);
    }

    public PacketServerStatusRequest(int respondPort, int state) {
        this.respondPort = respondPort;
        this.state = state;
        this.putShortUnsigned(0, respondPort);
        this.putInt(2, state);
    }

    @Override
    public void processServer(NetworkPacket packet, Server server, ServerClient client) {
        if (packet.networkInfo instanceof DatagramNetworkInfo) {
            DatagramNetworkInfo networkInfo = (DatagramNetworkInfo)packet.networkInfo;
            int port = this.respondPort;
            if (port == 0) {
                port = networkInfo.port;
            }
            server.network.sendPacket(new NetworkPacket(new PacketServerStatus(server, this.state), new DatagramNetworkInfo(networkInfo.socket, networkInfo.address, port)));
        } else {
            GameLog.warn.println("Received status request packet from unknown connection: " + (packet.networkInfo == null ? "LOCAL" : packet.networkInfo.getDisplayName()));
        }
    }
}

