/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import java.awt.Color;
import java.awt.Rectangle;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.PointHashSet;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormComponentList;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormContentIconToggleButton;
import necesse.gfx.forms.components.FormFairTypeDraw;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonIcon;
import necesse.gfx.ui.ButtonTexture;

public abstract class ControllerKeyboardForm
extends FormComponentList {
    public static ArrayList<String[]> keyboards = new ArrayList(Arrays.asList({"1234567890", "1234567890", "qwertyuiop", "QWERTYUIOP", "asdfghjkl'", "ASDFGHJKL\"", "zxcvbnm,.?", "ZXCVBNM;:!"}, {"!#%&/\\`\u00b4~^", "!#%&/\\`\u00b4~^", "@_-+=;:<>|", "@_-+=;:<>|", "()[]{}*\u20ac$\u00a3", "()[]{}*\u00e2\u00ea\u00e3", "\u00e1\u00e9\u00ed\u00fa\u00fc\u00a1\u00bf\u00e6\u00f8\u00e5", "\u00e3\u00ea\u0457\u00f1\u00f6\u0161\u00e7\u00c6\u00d8\u00c5"}));
    private final FormTypingComponent typingComponent;
    private FormLocalLabel header;
    private final FormFairTypeDraw label;
    private final FormSwitcher keyboardSwitcher;
    private final ArrayList<Form> normalForms = new ArrayList(keyboards.size());
    private final ArrayList<Form> capitalizedForms = new ArrayList(keyboards.size());
    private final Object repeatSpacebar = new Object();
    private final Object repeatBackspace = new Object();
    private int currentForm;
    private boolean capitalized;

    public ControllerKeyboardForm(FormTypingComponent typingComponent) {
        this.typingComponent = typingComponent;
        this.keyboardSwitcher = this.addComponent(new FormSwitcher());
        GameMessage typingHeader = typingComponent.getControllerTypingHeader();
        if (typingHeader != null) {
            this.header = this.addComponent(new FormLocalLabel(typingHeader, new FontOptions(20).color(Color.WHITE).outline(), 0, 0, 0, 400));
        }
        this.label = this.addComponent(new FormFairTypeDraw(0, 0));
        this.label.drawOptions = typingComponent.getTextBoxDrawOptions();
        for (int i = 0; i < keyboards.size(); ++i) {
            String[] keyboard = keyboards.get(i);
            PointHashSet tiles = new PointHashSet();
            int maxLength = 8;
            for (int j = 0; j < keyboard.length; j += 2) {
                String normal = keyboard[j];
                String cap = keyboard[j + 1];
                int length = Math.max(normal.length(), cap.length());
                maxLength = Math.max(length, maxLength);
                for (int k = 0; k < length; ++k) {
                    tiles.add(k, j / 2);
                }
            }
            int height = keyboard.length / 2;
            for (int j = 0; j < maxLength; ++j) {
                tiles.add(j, height);
            }
            int padding = 1;
            FormInputSize inputSize = FormInputSize.SIZE_32;
            int componentSize = inputSize.height + padding * 2;
            int buttonSize = inputSize.height;
            Form normalForm = this.keyboardSwitcher.addComponent(new Form("keyboardNormal" + i, tiles, buttonSize + padding * 2, 4));
            this.normalForms.add(normalForm);
            Form capitalizedForm = this.keyboardSwitcher.addComponent(new Form("keyboardCap" + i, tiles, buttonSize + padding * 2, 4));
            this.capitalizedForms.add(capitalizedForm);
            for (int j = 0; j < keyboard.length; j += 2) {
                String normal = keyboard[j];
                String cap = keyboard[j + 1];
                int length = Math.max(normal.length(), cap.length());
                for (int k = 0; k < length; ++k) {
                    char normalChar = k < normal.length() ? (char)normal.charAt(k) : (char)' ';
                    char capChar = k < cap.length() ? (char)cap.charAt(k) : (char)' ';
                    FormTextButton normalCharButton = normalForm.addComponent(new FormTextButton(Character.toString(normalChar), k * componentSize + padding, j / 2 * componentSize + padding, buttonSize, inputSize, ButtonColor.BASE));
                    normalCharButton.onClicked(e -> this.submitText(String.valueOf(normalChar)));
                    normalCharButton.controllerFocusHashcode = "keyboardKey" + j + "x" + k;
                    normalCharButton.acceptMouseRepeatEvents = true;
                    normalCharButton.submitControllerPressEvent = true;
                    normalCharButton.setActive(typingComponent.isValidAppendText(String.valueOf(normalChar)));
                    FormTextButton capCharButton = capitalizedForm.addComponent(new FormTextButton(Character.toString(capChar), k * componentSize + padding, j / 2 * componentSize + padding, buttonSize, inputSize, ButtonColor.BASE));
                    capCharButton.onClicked(e -> this.submitText(String.valueOf(capChar)));
                    capCharButton.controllerFocusHashcode = "keyboardKey" + j + "x" + k;
                    capCharButton.acceptMouseRepeatEvents = true;
                    normalCharButton.submitControllerPressEvent = true;
                    capCharButton.setActive(typingComponent.isValidAppendText(String.valueOf(capChar)));
                }
            }
            int spacebarWidth = maxLength - 5;
            normalForm.addComponent(new FormContentIconToggleButton((int)padding, (int)(height * componentSize + padding), (int)buttonSize, (FormInputSize)inputSize, (ButtonColor)ButtonColor.YELLOW, (ButtonIcon)this.getInterfaceStyle().keyboard_shift, (GameMessage[])new GameMessage[0]){

                @Override
                public boolean isToggled() {
                    return ControllerKeyboardForm.this.capitalized;
                }
            }).onClicked((FormEventListener<FormInputEvent<FormButton>>)LambdaMetafactory.metafactory(null, null, null, (Lnecesse/gfx/forms/events/FormEvent;)V, lambda$new$2(necesse.gfx.forms.events.FormInputEvent ), (Lnecesse/gfx/forms/events/FormInputEvent;)V)((ControllerKeyboardForm)this)).controllerFocusHashcode = "keyboardCap";
            normalForm.addComponent(new FormContentIconButton((int)(componentSize + padding), (int)(height * componentSize + padding), (int)buttonSize, (FormInputSize)inputSize, (ButtonColor)ButtonColor.YELLOW, (ButtonTexture)this.getInterfaceStyle().keyboard_next, (GameMessage[])new GameMessage[0])).onClicked((FormEventListener<FormInputEvent<FormButton>>)LambdaMetafactory.metafactory(null, null, null, (Lnecesse/gfx/forms/events/FormEvent;)V, lambda$new$3(necesse.gfx.forms.events.FormInputEvent ), (Lnecesse/gfx/forms/events/FormInputEvent;)V)((ControllerKeyboardForm)this)).controllerFocusHashcode = "keyboardChange";
            FormContentIconButton normalSpaceButton = normalForm.addComponent(new FormContentIconButton(componentSize * 2 + padding, height * componentSize + padding, spacebarWidth * buttonSize + padding * (spacebarWidth - 1) * 2, inputSize, ButtonColor.YELLOW, this.getInterfaceStyle().keyboard_spacebar, new GameMessage[0]));
            normalSpaceButton.onClicked(e -> this.submitText(" "));
            normalSpaceButton.controllerFocusHashcode = "keyboardSpace";
            normalSpaceButton.acceptMouseRepeatEvents = true;
            normalSpaceButton.setActive(typingComponent.isValidAppendText(" "));
            FormContentIconButton normalBackspaceButton = normalForm.addComponent(new FormContentIconButton(componentSize * 2 + spacebarWidth * componentSize + padding, height * componentSize + padding, buttonSize * 2 + padding * 2, inputSize, ButtonColor.YELLOW, this.getInterfaceStyle().keyboard_backspace, new GameMessage[0]));
            normalBackspaceButton.onClicked(e -> this.submitBackspace());
            normalBackspaceButton.controllerFocusHashcode = "keyboardBack";
            normalBackspaceButton.acceptMouseRepeatEvents = true;
            normalForm.addComponent(new FormContentIconButton((int)(componentSize * 4 + spacebarWidth * componentSize + padding), (int)(height * componentSize + padding), (int)buttonSize, (FormInputSize)inputSize, (ButtonColor)ButtonColor.GREEN, (ButtonTexture)this.getInterfaceStyle().keyboard_return, (GameMessage[])new GameMessage[0])).onClicked((FormEventListener<FormInputEvent<FormButton>>)LambdaMetafactory.metafactory(null, null, null, (Lnecesse/gfx/forms/events/FormEvent;)V, lambda$new$6(necesse.gfx.forms.events.FormInputEvent ), (Lnecesse/gfx/forms/events/FormInputEvent;)V)((ControllerKeyboardForm)this)).controllerFocusHashcode = "keyboardEnter";
            capitalizedForm.addComponent(new FormContentIconToggleButton((int)padding, (int)(height * componentSize + padding), (int)buttonSize, (FormInputSize)inputSize, (ButtonColor)ButtonColor.YELLOW, (ButtonIcon)this.getInterfaceStyle().keyboard_shift, (GameMessage[])new GameMessage[0]){

                @Override
                public boolean isToggled() {
                    return ControllerKeyboardForm.this.capitalized;
                }
            }).onClicked((FormEventListener<FormInputEvent<FormButton>>)LambdaMetafactory.metafactory(null, null, null, (Lnecesse/gfx/forms/events/FormEvent;)V, lambda$new$7(necesse.gfx.forms.events.FormInputEvent ), (Lnecesse/gfx/forms/events/FormInputEvent;)V)((ControllerKeyboardForm)this)).controllerFocusHashcode = "keyboardCap";
            capitalizedForm.addComponent(new FormContentIconButton((int)(componentSize + padding), (int)(height * componentSize + padding), (int)buttonSize, (FormInputSize)inputSize, (ButtonColor)ButtonColor.YELLOW, (ButtonTexture)this.getInterfaceStyle().keyboard_next, (GameMessage[])new GameMessage[0])).onClicked((FormEventListener<FormInputEvent<FormButton>>)LambdaMetafactory.metafactory(null, null, null, (Lnecesse/gfx/forms/events/FormEvent;)V, lambda$new$8(necesse.gfx.forms.events.FormInputEvent ), (Lnecesse/gfx/forms/events/FormInputEvent;)V)((ControllerKeyboardForm)this)).controllerFocusHashcode = "keyboardChange";
            FormContentIconButton capSpacebarButton = capitalizedForm.addComponent(new FormContentIconButton(componentSize * 2 + padding, height * componentSize + padding, spacebarWidth * buttonSize + padding * (spacebarWidth - 1) * 2, inputSize, ButtonColor.YELLOW, this.getInterfaceStyle().keyboard_spacebar, new GameMessage[0]));
            capSpacebarButton.onClicked(e -> this.submitText(" "));
            capSpacebarButton.controllerFocusHashcode = "keyboardSpace";
            capSpacebarButton.acceptMouseRepeatEvents = true;
            capSpacebarButton.setActive(typingComponent.isValidAppendText(" "));
            FormContentIconButton capBackspaceButton = capitalizedForm.addComponent(new FormContentIconButton(componentSize * 2 + spacebarWidth * componentSize + padding, height * componentSize + padding, buttonSize * 2 + padding * 2, inputSize, ButtonColor.YELLOW, this.getInterfaceStyle().keyboard_backspace, new GameMessage[0]));
            capBackspaceButton.onClicked(e -> this.submitBackspace());
            capBackspaceButton.controllerFocusHashcode = "keyboardBack";
            capBackspaceButton.acceptMouseRepeatEvents = true;
            capitalizedForm.addComponent(new FormContentIconButton((int)(componentSize * 4 + spacebarWidth * componentSize + padding), (int)(height * componentSize + padding), (int)buttonSize, (FormInputSize)inputSize, (ButtonColor)ButtonColor.GREEN, (ButtonTexture)this.getInterfaceStyle().keyboard_return, (GameMessage[])new GameMessage[0])).onClicked((FormEventListener<FormInputEvent<FormButton>>)LambdaMetafactory.metafactory(null, null, null, (Lnecesse/gfx/forms/events/FormEvent;)V, lambda$new$11(necesse.gfx.forms.events.FormInputEvent ), (Lnecesse/gfx/forms/events/FormInputEvent;)V)((ControllerKeyboardForm)this)).controllerFocusHashcode = "keyboardEnter";
        }
        this.refreshCurrentKeyboard();
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleInputEvent(event, tickManager, perspective);
        if (event.isUsed()) {
            return;
        }
        if (this.typingComponent.submitTypingEvent(event, false)) {
            this.label.drawOptions = this.typingComponent.getTextBoxDrawOptions();
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        super.handleControllerEvent(event, tickManager, perspective);
        if (!event.isUsed()) {
            if (event.buttonState && event.getState() == ControllerInput.MENU_ITEM_ACTIONS_MENU || event.isRepeatEvent(this.repeatSpacebar)) {
                if (this.typingComponent.isValidAppendText(" ")) {
                    event.startRepeatEvents(this.repeatSpacebar);
                    this.submitText(" ");
                    if (event.shouldSubmitSound()) {
                        this.playTickSound();
                    }
                }
                event.use();
            } else if (event.buttonState && event.getState() == ControllerInput.MENU_NEXT || event.isRepeatEvent(this.repeatBackspace)) {
                event.startRepeatEvents(this.repeatBackspace);
                this.submitBackspace();
                if (event.shouldSubmitSound()) {
                    this.playTickSound();
                }
                event.use();
            } else if (event.buttonState && event.getState() == ControllerInput.MENU_PREV) {
                this.capitalized = !this.capitalized;
                this.refreshCurrentKeyboard();
                if (event.shouldSubmitSound()) {
                    this.playTickSound();
                }
                event.use();
            }
        }
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Form form = this.capitalized ? this.capitalizedForms.get(this.currentForm) : this.normalForms.get(this.currentForm);
        Rectangle boundingBox = this.label.getBoundingBox();
        GameWindow window = WindowManager.getWindow();
        if (this.label.drawOptions != null) {
            switch (this.label.drawOptions.align) {
                case LEFT: {
                    this.label.setPosition(window.getHudWidth() / 2 - boundingBox.width / 2, form.getY() - boundingBox.height - 30);
                    break;
                }
                case CENTER: {
                    this.label.setPosition(window.getHudWidth() / 2, form.getY() - boundingBox.height - 30);
                    break;
                }
                case RIGHT: {
                    this.label.setPosition(window.getHudWidth() / 2 + boundingBox.width / 2, form.getY() - boundingBox.height - 30);
                }
            }
        } else {
            this.label.setPosition(window.getHudWidth() / 2, form.getY() - boundingBox.height - 30);
        }
        if (this.header != null) {
            this.header.setPosition(window.getHudWidth() / 2, this.label.getY() - this.header.getHeight() - 15);
        }
        GameBackground.textBox.getDrawOptions(boundingBox.x - 8, boundingBox.y - 8, boundingBox.width + 16, boundingBox.height + 16).draw();
        super.draw(tickManager, perspective, renderBox);
        GameBackground.textBox.getEdgeDrawOptions(boundingBox.x - 8, boundingBox.y - 8, boundingBox.width + 16, boundingBox.height + 16).draw();
        GameTooltipManager.addControllerGlyph(Localization.translate("controls", "spacebartip"), ControllerInput.MENU_ITEM_ACTIONS_MENU);
        GameTooltipManager.addControllerGlyph(Localization.translate("controls", "backspacetip"), ControllerInput.MENU_NEXT);
        GameTooltipManager.addControllerGlyph(Localization.translate("controls", "capitalizetip"), ControllerInput.MENU_PREV);
    }

    public void refreshCurrentKeyboard() {
        Form form = this.capitalized ? this.capitalizedForms.get(this.currentForm) : this.normalForms.get(this.currentForm);
        GameWindow window = WindowManager.getWindow();
        form.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
        this.keyboardSwitcher.makeCurrent(form);
    }

    public void submitText(String text) {
        this.typingComponent.appendText(text);
        this.label.drawOptions = this.typingComponent.getTextBoxDrawOptions();
    }

    public void submitBackspace() {
        this.typingComponent.submitBackspace();
        this.label.drawOptions = this.typingComponent.getTextBoxDrawOptions();
    }

    public abstract void submitEnter();

    private /* synthetic */ void lambda$new$11(FormInputEvent e) {
        this.submitEnter();
    }

    private /* synthetic */ void lambda$new$8(FormInputEvent e) {
        this.currentForm = (this.currentForm + 1) % this.normalForms.size();
        this.refreshCurrentKeyboard();
    }

    private /* synthetic */ void lambda$new$7(FormInputEvent e) {
        this.capitalized = !this.capitalized;
        this.refreshCurrentKeyboard();
    }

    private /* synthetic */ void lambda$new$6(FormInputEvent e) {
        this.submitEnter();
    }

    private /* synthetic */ void lambda$new$3(FormInputEvent e) {
        this.currentForm = (this.currentForm + 1) % this.normalForms.size();
        this.refreshCurrentKeyboard();
    }

    private /* synthetic */ void lambda$new$2(FormInputEvent e) {
        this.capitalized = !this.capitalized;
        this.refreshCurrentKeyboard();
    }
}

