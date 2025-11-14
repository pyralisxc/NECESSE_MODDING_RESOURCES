/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.networkInfo;

import java.io.IOException;
import necesse.engine.network.networkInfo.NetworkInfo;

public class InvalidNetworkInfo
extends NetworkInfo {
    @Override
    public void send(byte[] data) throws IOException {
    }

    @Override
    public String getDisplayName() {
        return "INVALID";
    }

    @Override
    public void closeConnection() {
    }

    @Override
    public void resetConnection() {
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return -1;
    }
}

