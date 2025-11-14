/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import necesse.engine.GlobalData;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.loading.ClientLoadingUtil;
import necesse.engine.state.MainMenu;
import necesse.gfx.forms.FormResizeWrapper;

public abstract class ClientLoadingPhase
extends ClientLoadingUtil {
    public final boolean resetOnLevelChange;
    private boolean isDone;

    public ClientLoadingPhase(ClientLoading loading, boolean resetOnLevelChange) {
        super(loading);
        this.resetOnLevelChange = resetOnLevelChange;
    }

    public abstract FormResizeWrapper start();

    public abstract GameMessage getLoadingMessage();

    public abstract void tick();

    public abstract void end();

    public void reset() {
        this.isDone = false;
    }

    public final boolean isDone() {
        return this.isDone;
    }

    protected final void markDone() {
        this.isDone = true;
    }

    public final void cancelConnection() {
        if (GlobalData.getCurrentState() instanceof MainMenu) {
            ((MainMenu)GlobalData.getCurrentState()).cancelConnection();
        }
    }
}

