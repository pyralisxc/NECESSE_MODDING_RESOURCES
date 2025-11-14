/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.networkInfo;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.network.server.ServerClient;

public abstract class NetworkInfo {
    public ServerClient getClient(Stream<ServerClient> clientStream) {
        return clientStream.filter(Objects::nonNull).filter(c -> Objects.equals(c.networkInfo, this)).findFirst().orElse(null);
    }

    public abstract void send(byte[] var1) throws IOException;

    public abstract String getDisplayName();

    public abstract void closeConnection();

    public abstract void resetConnection();

    public abstract boolean equals(Object var1);

    public abstract int hashCode();
}

