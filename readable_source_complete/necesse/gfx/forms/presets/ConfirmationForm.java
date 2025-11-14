/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;

public class ConfirmationForm
extends Form {
    protected FormLocalTextButton confirm;
    protected FormLocalTextButton back;
    protected FormContentBox content;
    public int padding = 10;
    public final int maxHeight;
    protected Runnable confirmEvent;
    protected Runnable backEvent;
    private GameMessage confirmText;
    private long confirmCooldownTime = -1L;
    private int confirmSecondsLeft = -1;

    public ConfirmationForm(String name, int width, int maxHeight) {
        super(name, width, maxHeight);
        this.maxHeight = maxHeight;
        this.confirm = this.addComponent(new FormLocalTextButton("ui", "confirmbutton", 4, maxHeight - 40, width / 2 - 6));
        this.confirm.onClicked(e -> this.submitConfirmEvent());
        this.back = this.addComponent(new FormLocalTextButton("ui", "backbutton", width / 2 + 2, maxHeight - 40, width / 2 - 6));
        this.back.onClicked(e -> this.submitBackEvent());
        this.content = this.addComponent(new FormContentBox(0, 0, width, maxHeight - 40));
    }

    public ConfirmationForm(String name) {
        this(name, 400, 400);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public void setupConfirmation(Consumer<FormContentBox> setup, GameMessage confirmButton, GameMessage backButton, Runnable confirmEvent, Runnable backEvent) {
        this.content.setWidth(this.getWidth());
        this.content.setContentBox(new Rectangle(0, 0, this.getWidth(), this.maxHeight - 40));
        this.confirm.setWidth(this.getWidth() / 2 - 6);
        this.back.setWidth(this.getWidth() / 2 - 6);
        this.back.setX(this.getWidth() / 2);
        this.content.clearComponents();
        setup.accept(this.content);
        this.confirmText = confirmButton;
        this.confirm.setActive(true);
        this.confirm.setLocalization(confirmButton);
        this.back.setLocalization(backButton);
        this.confirmEvent = confirmEvent;
        this.backEvent = backEvent;
        this.updateHeight();
        this.prioritizeConfirm();
    }

    public final void setupConfirmation(Consumer<FormContentBox> setup, Runnable confirmEvent, Runnable backEvent) {
        this.setupConfirmation(setup, (GameMessage)new LocalMessage("ui", "confirmbutton"), (GameMessage)new LocalMessage("ui", "backbutton"), confirmEvent, backEvent);
    }

    public void setupConfirmation(GameMessage message, GameMessage confirmButton, GameMessage backButton, Runnable confirmEvent, Runnable backEvent) {
        this.setupConfirmation((FormContentBox content) -> content.addComponent(new FormLocalLabel(message, new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20)), confirmButton, backButton, confirmEvent, backEvent);
    }

    public final void setupConfirmation(GameMessage message, Runnable confirmEvent, Runnable backEvent) {
        this.setupConfirmation(message, (GameMessage)new LocalMessage("ui", "confirmbutton"), (GameMessage)new LocalMessage("ui", "backbutton"), confirmEvent, backEvent);
    }

    public void submitConfirmEvent() {
        if (this.confirmEvent != null) {
            this.confirmEvent.run();
        }
    }

    public void submitBackEvent() {
        if (this.backEvent != null) {
            this.backEvent.run();
        }
    }

    public void updateHeight() {
        Rectangle box = this.content.getContentBoxToFitComponents();
        box.x = 0;
        box.height += box.y;
        box.y = 0;
        box.height += this.padding;
        box.width = this.getWidth();
        this.content.setContentBox(box);
        int buttonHeight = 40;
        this.setHeight(Math.min(box.height, this.maxHeight - buttonHeight) + buttonHeight);
        this.content.setHeight(this.getHeight() - buttonHeight);
        this.confirm.setY(this.getHeight() - 40);
        this.back.setY(this.getHeight() - 40);
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.tickCooldown();
        super.draw(tickManager, perspective, renderBox);
    }

    private void tickCooldown() {
        if (this.confirmCooldownTime != -1L) {
            long msLeft = this.confirmCooldownTime - System.currentTimeMillis();
            if (msLeft <= 0L) {
                if (this.confirmSecondsLeft > 0) {
                    this.confirm.setLocalization(this.confirmText);
                }
                this.confirm.setActive(true);
                this.confirmCooldownTime = -1L;
                this.confirmSecondsLeft = -1;
            } else {
                int nextSecondsLeft = (int)Math.ceil((float)msLeft / 1000.0f);
                if (nextSecondsLeft != this.confirmSecondsLeft) {
                    this.confirmSecondsLeft = nextSecondsLeft;
                    GameMessageBuilder builder = new GameMessageBuilder().append(this.confirmText).append(" (" + this.confirmSecondsLeft + ")");
                    this.confirm.setLocalization(builder);
                }
            }
        }
    }

    public void startConfirmCooldown(int milliseconds, boolean updateButtonWithSecondsLeft) {
        this.confirmCooldownTime = System.currentTimeMillis() + (long)milliseconds;
        if (updateButtonWithSecondsLeft) {
            this.confirmSecondsLeft = (int)Math.ceil((float)milliseconds / 1000.0f);
            GameMessageBuilder builder = new GameMessageBuilder().append(this.confirmText).append(" (" + this.confirmSecondsLeft + ")");
            this.confirm.setLocalization(builder);
        } else {
            this.confirmSecondsLeft = -1;
            this.confirm.setLocalization(this.confirmText);
        }
        this.confirm.setActive(false);
    }

    public void prioritizeConfirm() {
        this.prioritizeControllerFocus(this.confirm);
    }

    public void prioritizeBack() {
        this.prioritizeControllerFocus(this.back);
    }
}

