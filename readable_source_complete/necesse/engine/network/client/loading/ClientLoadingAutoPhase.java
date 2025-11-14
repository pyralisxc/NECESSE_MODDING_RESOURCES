/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.network.client.loading;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.loading.ClientLoading;
import necesse.engine.network.client.loading.ClientLoadingPhase;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.FormResizeWrapper;
import necesse.gfx.forms.presets.NoticeForm;

public abstract class ClientLoadingAutoPhase
extends ClientLoadingPhase {
    private NoticeForm form;

    public ClientLoadingAutoPhase(ClientLoading loading, boolean resetOnLevelChange) {
        super(loading, resetOnLevelChange);
    }

    @Override
    public FormResizeWrapper start() {
        this.form = new NoticeForm("loading", 400, 120);
        this.form.setupNotice(this.getLoadingMessage(), (GameMessage)new LocalMessage("ui", "connectcancel"));
        this.form.onContinue(this::cancelConnection);
        return new FormResizeWrapper(this.form, () -> {
            GameWindow window = WindowManager.getWindow();
            this.form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        });
    }

    public final void updateLoadingMessage() {
        if (this.form != null && !this.form.isDisposed()) {
            GameWindow window = WindowManager.getWindow();
            this.form.setupNotice(this.getLoadingMessage());
            this.form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        }
    }
}

