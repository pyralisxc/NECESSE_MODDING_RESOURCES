/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.loading.ClientLoadingAutoPhase;
import necesse.engine.network.packet.PacketRequestPlayerData;
import necesse.gfx.forms.FormResizeWrapper;

public class ClientLoadingPlayers
extends ClientLoadingAutoPhase {
    private boolean[] loaded;

    public ClientLoadingPlayers(ClientLoading loading) {
        super(loading, true);
    }

    public void submitLoadedPlayer(int slot) {
        if (this.loaded == null) {
            return;
        }
        this.loaded[slot] = true;
        this.updateLoadingMessage();
        for (boolean b : this.loaded) {
            if (b) continue;
            return;
        }
        this.markDone();
    }

    @Override
    public GameMessage getLoadingMessage() {
        int count = 0;
        for (boolean b : this.loaded) {
            if (!b) continue;
            ++count;
        }
        float percent = (float)count / (float)this.client.getSlots();
        return new LocalMessage("loading", "connectplayers", "percent", (int)(percent * 100.0f));
    }

    @Override
    public FormResizeWrapper start() {
        if (this.loaded == null) {
            this.loaded = new boolean[this.client.getSlots()];
            if (Settings.instantLevelChange) {
                for (int i = 0; i < this.client.getSlots(); ++i) {
                    ClientClient current = this.client.getClient(i);
                    this.client.network.sendPacket(new PacketRequestPlayerData(i));
                    if (current == null || !current.loadedPlayer) continue;
                    this.loaded[i] = true;
                }
            }
        }
        return super.start();
    }

    @Override
    public void tick() {
        if (this.isWaiting()) {
            return;
        }
        boolean done = true;
        for (int i = 0; i < this.loaded.length; ++i) {
            if (this.loaded[i]) continue;
            this.client.network.sendPacket(new PacketRequestPlayerData(i));
            done = false;
        }
        if (done) {
            this.markDone();
        } else {
            this.setWait(250);
        }
    }

    @Override
    public void end() {
        this.loaded = null;
    }

    @Override
    public void reset() {
        super.reset();
        if (this.loaded != null && !Settings.instantLevelChange) {
            this.loaded[this.client.getSlot()] = false;
        }
    }
}

