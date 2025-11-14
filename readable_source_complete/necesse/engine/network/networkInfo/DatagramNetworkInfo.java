/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.networkInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Objects;
import necesse.engine.network.networkInfo.NetworkInfo;
import necesse.engine.util.GameRandom;

public class DatagramNetworkInfo
extends NetworkInfo {
    public final DatagramSocket socket;
    public final InetAddress address;
    public final int port;

    public DatagramNetworkInfo(DatagramSocket socket, InetAddress address, int port) {
        this.socket = socket;
        this.address = address;
        this.port = port;
    }

    @Override
    public void send(byte[] data) throws IOException {
        if (this.socket == null || this.socket.isClosed()) {
            return;
        }
        this.socket.send(new DatagramPacket(data, data.length, this.address, this.port));
    }

    @Override
    public String getDisplayName() {
        return this.address.getHostName() + ":" + this.port;
    }

    @Override
    public void closeConnection() {
    }

    @Override
    public void resetConnection() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DatagramNetworkInfo) {
            DatagramNetworkInfo other = (DatagramNetworkInfo)obj;
            return Objects.equals(this.address, other.address) && this.port == other.port;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = this.address == null ? 1337 : this.address.hashCode();
        hashCode = hashCode * GameRandom.prime(76) + this.port;
        return hashCode;
    }
}

