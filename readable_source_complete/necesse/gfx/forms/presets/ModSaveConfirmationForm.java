/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.window.WindowManager;
import necesse.gfx.forms.presets.ConfirmationForm;
import necesse.gfx.forms.presets.ModsForm;

public class ModSaveConfirmationForm
extends ConfirmationForm {
    public ModSaveConfirmationForm(String name) {
        super(name);
    }

    public void setupModSaveConfirmation(Runnable restartLaterAction) {
        Runnable restart = ModsForm.restartGameRunnable();
        this.setupConfirmation(new LocalMessage("ui", "modssavenotice"), (GameMessage)new LocalMessage("ui", restart != null ? "modsrestart" : "modsquit"), (GameMessage)new LocalMessage("ui", "modslater"), () -> {
            if (restart != null) {
                restart.run();
            } else {
                WindowManager.getWindow().requestClose();
            }
        }, restartLaterAction);
    }
}

