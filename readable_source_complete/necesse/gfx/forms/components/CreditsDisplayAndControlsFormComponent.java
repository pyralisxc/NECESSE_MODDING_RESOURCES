/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.credits.GameCreditsDisplay;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.CreditsDisplayFormComponent;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormButtonToggle;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormSlider;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;
import necesse.gfx.ui.GameInterfaceStyle;
import org.lwjgl.opengl.GL11;

public abstract class CreditsDisplayAndControlsFormComponent
extends FormComponentList {
    protected CreditsDisplayFormComponent displayComponent;
    protected Form controlForm;
    protected FormSlider timeSlider;
    protected FormContentIconButton pauseButton;
    protected FormContentIconToggleButton speedUpButton;
    protected long lastMouseActivityTime;

    public CreditsDisplayAndControlsFormComponent(final GameWindow window, GameCreditsDisplay credits) {
        this.displayComponent = this.addComponent(new CreditsDisplayFormComponent(0, 0, window, credits));
        this.controlForm = this.addComponent(new Form(400, 100){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                window.applyDraw(() -> super.draw(tickManager, perspective, renderBox), () -> {
                    long timeSinceLastMouseActivity;
                    float alpha = !Input.lastInputIsController && CreditsDisplayAndControlsFormComponent.this.controlForm.isMouseOver(InputEvent.MouseMoveEvent(window.getInput().mousePos(), tickManager)) ? 1.0f : ((timeSinceLastMouseActivity = System.currentTimeMillis() - CreditsDisplayAndControlsFormComponent.this.lastMouseActivityTime) <= 2000L ? 1.0f - GameMath.limit(GameMath.clamp(timeSinceLastMouseActivity - 1000L, 0.0f, 1000.0f), 0.0f, 1.0f) : 0.0f);
                    GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
                }, null);
            }
        });
        this.controlForm.overrideStyle(GameInterfaceStyle.ghost);
        this.controlForm.drawBase = false;
        FormFlow flow = new FormFlow(10);
        this.timeSlider = this.controlForm.addComponent(new FormSlider("", 4, 0, 0, 0, 300, this.controlForm.getWidth() - 8));
        this.timeSlider.drawValue = false;
        this.timeSlider.onChanged(e -> this.updateProgressFromSlider());
        flow.nextY(this.timeSlider, 10);
        int buttonY = flow.next(40);
        ArrayList<FormButton> buttons = new ArrayList<FormButton>();
        this.pauseButton = this.controlForm.addComponent(new FormContentIconButton(0, buttonY, FormInputSize.SIZE_32, ButtonColor.BASE, this.controlForm.getInterfaceStyle().play_song, new GameMessage[0]));
        this.pauseButton.onClicked(e -> this.displayComponent.drawManager.setPaused(!this.displayComponent.drawManager.isPaused()));
        buttons.add(this.pauseButton);
        FormLocalTextButton backButton = this.controlForm.addComponent(new FormLocalTextButton("ui", "backbutton", 0, buttonY, 150, FormInputSize.SIZE_32, ButtonColor.BASE));
        backButton.onClicked(e -> this.onBackPressed());
        buttons.add(backButton);
        this.speedUpButton = this.controlForm.addComponent(new FormContentIconToggleButton(0, buttonY, FormInputSize.SIZE_32, ButtonColor.BASE, this.controlForm.getInterfaceStyle().priority_higher, new GameMessage[0]){

            @Override
            public ButtonState getButtonState() {
                if (this.isToggled()) {
                    return ButtonState.HIGHLIGHTED;
                }
                return super.getButtonState();
            }
        });
        this.speedUpButton.rightAngles(1);
        this.speedUpButton.onToggled(e -> this.displayComponent.drawManager.setSpeed(((FormButtonToggle)e.from).isToggled() ? 4.0f : 1.0f));
        buttons.add(this.speedUpButton);
        int padding = 4;
        int totalWidth = buttons.stream().mapToInt(b -> ((FormComponent)((Object)b)).getBoundingBox().width + 4).sum() - 4;
        int startX = this.controlForm.getWidth() / 2 - totalWidth / 2;
        for (FormPositionContainer formPositionContainer : buttons) {
            Rectangle box = ((FormComponent)((Object)formPositionContainer)).getBoundingBox();
            formPositionContainer.setPosition((startX += box.width + padding) - box.width, buttonY);
        }
        this.controlForm.setHeight(flow.next());
        this.onWindowResized(window);
    }

    public abstract void onBackPressed();

    public void restart() {
        this.displayComponent.restart();
        this.displayComponent.drawManager.setSpeed(1.0f);
        this.speedUpButton.setToggled(false);
        this.timeSlider.setRange(0, this.controlForm.getWidth());
    }

    public boolean isDone() {
        return this.displayComponent.isDone() && !this.timeSlider.isGrabbed();
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.lastMouseActivityTime = System.currentTimeMillis();
        }
        super.handleInputEvent(event, tickManager, perspective);
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        this.lastMouseActivityTime = System.currentTimeMillis();
        super.handleControllerEvent(event, tickManager, perspective);
    }

    protected void updateProgressFromSlider() {
        double progress = (double)this.timeSlider.getValue() / (double)this.timeSlider.getMaxValue();
        int timeProgress = (int)(progress * (double)this.displayComponent.drawManager.getTotalTime());
        this.displayComponent.drawManager.setProgress(timeProgress);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (!this.timeSlider.isGrabbed()) {
            double progress = (double)this.displayComponent.drawManager.getCurrentTime() / (double)this.displayComponent.drawManager.getTotalTime();
            int sliderValue = (int)(progress * (double)this.timeSlider.getMaxValue());
            this.timeSlider.setValue(sliderValue);
        } else {
            this.updateProgressFromSlider();
        }
        this.pauseButton.setIcon(this.displayComponent.drawManager.isPaused() ? this.pauseButton.getInterfaceStyle().play_song : this.pauseButton.getInterfaceStyle().pause_song);
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        Rectangle boundingBox = this.displayComponent.getBoundingBox();
        this.displayComponent.setPosition(window.getHudWidth() / 2 - boundingBox.width / 2, window.getHudHeight() / 2 - boundingBox.height / 2);
        this.controlForm.setPosition(window.getHudWidth() / 2 - this.controlForm.getWidth() / 2, Math.min(boundingBox.y + boundingBox.height + 10, window.getHudHeight() - this.controlForm.getHeight() - 10));
    }
}

