/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.util.regex.Pattern;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;

public abstract class LabeledTextInputForm
extends Form {
    protected FormTextInput input;
    protected FormLocalTextButton confirmButton;
    public boolean escapeOrBackMeansCancel = true;

    public LabeledTextInputForm(String name, int width, GameMessage label, boolean labelCentered, Pattern regexMatch, GameMessage confirmButton, GameMessage cancelButton) {
        super(name, width, 80);
        FormFlow flow = new FormFlow(5);
        this.addComponent(flow.nextY(new FormLocalLabel(label, new FontOptions(16), labelCentered ? 0 : -1, labelCentered ? this.getWidth() / 2 : 5, 5, this.getWidth() - 10), 5));
        this.input = this.addComponent(new FormTextInput(4, flow.next(40), FormInputSize.SIZE_32_TO_40, this.getWidth() - 8, 50));
        if (regexMatch != null) {
            this.input.setRegexMatchFull(regexMatch);
        }
        this.input.onChange(e -> this.enableBasedOnInputError());
        this.input.onSubmit(e -> {
            String text;
            GameMessage error;
            if ((e.event.getID() == 257 || e.event.getID() == 335) && (error = this.getInputError(text = this.input.getText())) == null) {
                this.onConfirmed(text);
            }
        });
        this.input.setControllerTypingHeader(label);
        int buttonsY = flow.next(40);
        this.confirmButton = this.addComponent(new FormLocalTextButton(confirmButton, 4, buttonsY, this.getWidth() / 2 - 6));
        this.confirmButton.onClicked(e -> {
            String text = this.input.getText();
            GameMessage error = this.getInputError(text);
            if (error == null) {
                this.onConfirmed(text);
            }
        });
        this.addComponent(new FormLocalTextButton(cancelButton, this.getWidth() / 2 + 2, buttonsY, this.getWidth() / 2 - 6)).onClicked(e -> this.onCancelled());
        this.setHeight(flow.next());
        this.onWindowResized(WindowManager.getWindow());
        this.enableBasedOnInputError();
    }

    private void enableBasedOnInputError() {
        GameMessage error = this.getInputError(this.input.getText());
        this.confirmButton.setActive(error == null);
        this.confirmButton.setLocalTooltip(error);
    }

    public LabeledTextInputForm(String name, int width, GameMessage label, boolean labelCentered, String regexMatch, GameMessage confirmButton, GameMessage cancelButton) {
        this(name, width, label, labelCentered, Pattern.compile(regexMatch), confirmButton, cancelButton);
    }

    public LabeledTextInputForm(String name, GameMessage label, boolean centered, Pattern regexMatch, GameMessage confirmButton, GameMessage cancelButton) {
        this(name, 400, label, centered, regexMatch, confirmButton, cancelButton);
    }

    public LabeledTextInputForm(String name, GameMessage label, boolean centered, String regexMatch, GameMessage confirmButton, GameMessage cancelButton) {
        this(name, label, centered, Pattern.compile(regexMatch), confirmButton, cancelButton);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (event.state && event.getID() == 256 && this.escapeOrBackMeansCancel) {
            event.use();
            this.onCancelled();
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleControllerEvent(event, tickManager, perspective);
        if (event.buttonState && event.getState() == ControllerInput.MENU_BACK && this.escapeOrBackMeansCancel) {
            event.use();
            this.onCancelled();
        }
    }

    public void setInput(String text) {
        this.input.setText(text);
        GameMessage error = this.getInputError(this.input.getText());
        this.confirmButton.setActive(error == null);
        this.confirmButton.setLocalTooltip(error);
    }

    public void selectAllAndSetTyping() {
        this.input.selectAll();
        this.input.setTyping(true);
    }

    public void startTyping() {
        this.input.setTyping(true);
    }

    public String getInputText() {
        return this.input.getText();
    }

    public abstract GameMessage getInputError(String var1);

    public abstract void onConfirmed(String var1);

    public abstract void onCancelled();

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }
}

