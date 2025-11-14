/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.loading.ClientLoadingAutoPhase;
import necesse.gfx.forms.FormResizeWrapper;

public class ClientLoadingLevelPreload
extends ClientLoadingAutoPhase {
    public ClientLoadingLevelPreload(ClientLoading loading) {
        super(loading, true);
    }

    @Override
    public GameMessage getLoadingMessage() {
        return new LocalMessage("loading", "connectmap", "percent", (int)(this.client.levelManager.loading().getPercentPreloaded() * 100.0f));
    }

    @Override
    public FormResizeWrapper start() {
        this.client.levelManager.loading().start(this.client.getPlayer());
        return super.start();
    }

    @Override
    public void tick() {
        if (this.client.levelManager.loading().isPreloadingDone()) {
            this.markDone();
        } else {
            this.setWait(50);
        }
    }

    @Override
    public void end() {
        this.client.levelManager.finishUp();
    }
}

