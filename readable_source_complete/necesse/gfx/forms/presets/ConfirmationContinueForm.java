/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.util.function.Consumer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.window.GameWindow;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.presets.ConfirmationForm;

public class ConfirmationContinueForm
extends ConfirmationForm
implements ContinueComponent {
    private boolean isContinued;
    private Runnable continueEvent;

    public ConfirmationContinueForm(String name) {
        super(name);
    }

    public ConfirmationContinueForm(String name, int width, int maxHeight) {
        super(name, width, maxHeight);
    }

    @Override
    public void setupConfirmation(Consumer<FormContentBox> setup, GameMessage confirmButton, GameMessage backButton, Runnable confirmEvent, Runnable backEvent) {
        super.setupConfirmation(setup, confirmButton, backButton, confirmEvent, () -> {
            backEvent.run();
            this.applyContinue();
        });
    }

    @Override
    public void setupConfirmation(GameMessage message, GameMessage confirmButton, GameMessage backButton, Runnable confirmEvent, Runnable backEvent) {
        super.setupConfirmation(message, confirmButton, backButton, confirmEvent, () -> {
            backEvent.run();
            this.applyContinue();
        });
    }

    @Override
    public void onContinue(Runnable continueEvent) {
        this.continueEvent = continueEvent;
    }

    @Override
    public void applyContinue() {
        if (this.canContinue()) {
            if (this.continueEvent != null) {
                this.continueEvent.run();
            }
            this.isContinued = true;
        }
    }

    @Override
    public boolean isContinued() {
        return this.isContinued;
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }
}

