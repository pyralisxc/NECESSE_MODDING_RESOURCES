/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.network;

import java.util.function.BiConsumer;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;

public abstract class ClientNetwork {
    public abstract boolean openConnection();

    public abstract String getOpenError();

    public abstract boolean isOpen();

    public abstract void sendPacket(Packet var1);

    public abstract void close();

    public abstract String getDebugString();

    public abstract LocalMessage getPlayingMessage();

    public String getRichPresenceGroup() {
        return null;
    }

    public abstract void writeLobbyConnectInfo(BiConsumer<String, String> var1);
}

