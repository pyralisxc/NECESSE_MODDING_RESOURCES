/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.loading.ClientLoadingAutoPhase;
import necesse.engine.network.packet.PacketLevelData;
import necesse.engine.network.packet.PacketRequestPacket;

public class ClientLoadingLevelData
extends ClientLoadingAutoPhase {
    public ClientLoadingLevelData(ClientLoading loading) {
        super(loading, true);
    }

    public void submitLevelDataPacket(PacketLevelData packet) {
        if (this.client.getLevel() != null) {
            this.markDone();
        }
    }

    @Override
    public GameMessage getLoadingMessage() {
        return new LocalMessage("loading", "connectlevel");
    }

    @Override
    public void tick() {
        if (this.isWaiting()) {
            return;
        }
        this.client.network.sendPacket(new PacketRequestPacket(PacketRequestPacket.RequestType.LEVEL_DATA));
        this.setWait(200);
    }

    @Override
    public void end() {
    }

    @Override
    public void reset() {
        super.reset();
        this.client.levelManager.setLevel(null);
    }
}

