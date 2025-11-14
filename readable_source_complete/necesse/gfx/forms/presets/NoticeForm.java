/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.ContinueForm;
import necesse.gfx.gameFont.FontOptions;

public class NoticeForm
extends ContinueForm {
    public static final int BUTTON_COOLDOWN_NONE = 0;
    public static final int BUTTON_COOLDOWN_NEVER = -1;
    public static final int BUTTON_COOLDOWN_HIDE = -2;
    private final FormLocalTextButton button;
    private final FormContentBox content;
    public final int maxHeight;
    public int padding = 10;
    private long noticeTime;
    private int noticeCooldown;
    public boolean escapeOrBackToContinue = false;

    public NoticeForm(String name, int width, int maxHeight) {
        super(name, width, maxHeight);
        this.maxHeight = maxHeight;
        this.button = this.addComponent(new FormLocalTextButton("ui", "continuebutton", 4, this.getHeight() - 40, this.getWidth() - 8));
        this.button.onClicked(e -> this.applyContinue());
        this.content = this.addComponent(new FormContentBox(0, 0, width, maxHeight - 40));
    }

    public NoticeForm(String name) {
        this(name, 400, 400);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.escapeOrBackToContinue && event.state && !event.isUsed() && event.getID() == 256) {
            this.applyContinue();
            event.use();
        }
        super.handleInputEvent(event, tickManager, perspective);
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.escapeOrBackToContinue && event.buttonState && !event.isUsed() && event.getState() == ControllerInput.MENU_BACK) {
            this.applyContinue();
            event.use();
        }
        super.handleControllerEvent(event, tickManager, perspective);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }

    public void setupNotice(Consumer<FormContentBox> setup, GameMessage confirmButton) {
        this.content.clearComponents();
        setup.accept(this.content);
        this.button.setLocalization(confirmButton);
        this.updateHeight();
    }

    public void setupNotice(Consumer<FormContentBox> setup) {
        this.setupNotice(setup, (GameMessage)new LocalMessage("ui", "continuebutton"));
    }

    public void setupNotice(GameMessage message, GameMessage confirmButton) {
        this.setupNotice((FormContentBox content) -> content.addComponent(new FormLocalLabel(message, new FontOptions(20), 0, this.getWidth() / 2, 10, this.getWidth() - 20)), confirmButton);
    }

    public final void setupNotice(GameMessage message) {
        this.setupNotice(message, (GameMessage)new LocalMessage("ui", "continuebutton"));
    }

    @Override
    public boolean canContinue() {
        return this.button.isActive();
    }

    public void setButtonCooldown(int msCooldown) {
        switch (msCooldown) {
            case 0: {
                if (!this.hasComponent(this.button)) {
                    this.addComponent(this.button);
                }
                this.noticeTime = -1L;
                this.button.setActive(true);
                break;
            }
            case -1: {
                if (!this.hasComponent(this.button)) {
                    this.addComponent(this.button);
                }
                this.noticeTime = -1L;
                this.button.setActive(false);
                break;
            }
            case -2: {
                if (!this.hasComponent(this.button)) break;
                this.removeComponent(this.button);
                break;
            }
            default: {
                if (!this.hasComponent(this.button)) {
                    this.addComponent(this.button);
                }
                if (msCooldown > 0) {
                    this.noticeTime = System.currentTimeMillis();
                    this.noticeCooldown = msCooldown;
                    this.button.setActive(false);
                    break;
                }
                this.noticeTime = -1L;
                this.button.setActive(true);
            }
        }
        this.updateHeight();
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.shouldButtonBeInactive()) {
            this.button.setActive(false);
        } else {
            this.tickCooldown();
        }
        super.draw(tickManager, perspective, renderBox);
    }

    private void tickCooldown() {
        if (this.noticeTime != -1L && System.currentTimeMillis() > this.noticeTime + (long)this.noticeCooldown) {
            this.button.setActive(true);
            this.noticeTime = -1L;
        }
    }

    public boolean shouldButtonBeInactive() {
        return false;
    }

    public void updateHeight() {
        Rectangle box = this.content.getContentBoxToFitComponents();
        box.x = 0;
        box.height += box.y;
        box.y = 0;
        box.height += this.padding;
        box.width = this.getWidth();
        this.content.setContentBox(box);
        int buttonHeight = this.hasComponent(this.button) ? 40 : 0;
        this.setHeight(Math.min(box.height, this.maxHeight - buttonHeight) + buttonHeight);
        this.content.setHeight(this.getHeight() - buttonHeight);
        this.button.setY(this.getHeight() - 40);
        this.onWindowResized(WindowManager.getWindow());
    }
}

