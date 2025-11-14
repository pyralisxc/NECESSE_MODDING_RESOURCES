/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.loading.ClientLoadingAutoPhase;
import necesse.engine.network.packet.PacketRequestPlayerData;

public class ClientLoadingClient
extends ClientLoadingAutoPhase {
    private int oldSelectedInventory;

    public ClientLoadingClient(ClientLoading loading) {
        super(loading, true);
    }

    @Override
    public GameMessage getLoadingMessage() {
        return new LocalMessage("loading", "connectclient");
    }

    @Override
    public void tick() {
        ClientClient me = this.client.getClient();
        if (me == null || me.playerMob == null || !me.loadedPlayer) {
            if (this.isWaiting()) {
                return;
            }
            this.client.network.sendPacket(new PacketRequestPlayerData(this.client.getSlot()));
            this.setWait(200);
        } else {
            this.markDone();
        }
    }

    @Override
    public void end() {
        this.client.getPlayer().setSelectedSlot(this.oldSelectedInventory);
        this.client.initInventoryContainer();
    }

    @Override
    public void reset() {
        super.reset();
        this.oldSelectedInventory = this.client.getPlayer() != null ? this.client.getPlayer().getSelectedSlot() : 0;
    }
}

