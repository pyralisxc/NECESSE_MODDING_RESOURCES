/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.loading.ClientLoadingAutoPhase;
import necesse.engine.network.packet.PacketRequestPacket;
import necesse.engine.network.packet.PacketWorldData;

public class ClientLoadingWorld
extends ClientLoadingAutoPhase {
    public ClientLoadingWorld(ClientLoading loading) {
        super(loading, false);
    }

    public void submitWorldDataPacket(PacketWorldData packet) {
        if (this.client.worldEntity != null) {
            this.markDone();
        }
    }

    @Override
    public GameMessage getLoadingMessage() {
        return new LocalMessage("loading", "connectworld");
    }

    @Override
    public void tick() {
        if (this.isWaiting()) {
            return;
        }
        this.client.network.sendPacket(new PacketRequestPacket(PacketRequestPacket.RequestType.WORLD_DATA));
        this.setWait(200);
    }

    @Override
    public void end() {
    }
}

