/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.platforms.sharedOnPC.forms;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import necesse.engine.Settings;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputSource;
import necesse.engine.input.controller.ControllerAnalogState;
import necesse.engine.input.controller.ControllerBind;
import necesse.engine.input.controller.ControllerButtonState;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerGlyphCollections;
import necesse.engine.input.controller.ControllerHandle;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.input.controller.ControllerState;
import necesse.engine.localization.message.GameMessage;
import necesse.gfx.GameResources;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.lists.FormControlListPopulator;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonTexture;

public class FormControllerControlListPopulator
implements FormControlListPopulator {
    private final HashSet<ControllerInputState> overlappingControls = new HashSet();
    private final InputSource inputSource;
    private Runnable onBindChanged;
    private int lastScrollY;
    private ControllerInputState selectedControl = null;
    private FormContentBox contentBox;
    private ControllerHandle lastControllerHandle;
    private Form form;
    private Runnable repopulate;

    public FormControllerControlListPopulator(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    @Override
    public void populateForm(Form form, int x, int y, int width, int height, Runnable repopulate) {
        this.form = form;
        this.repopulate = repopulate;
        this.lastControllerHandle = ControllerInput.getLatestControllerHandle();
        this.findOverlappingControls();
        FormFlow flow = new FormFlow();
        this.contentBox = form.addComponent(new FormContentBox(x, y, width, height - 40));
        flow.next(10);
        this.contentBox.addComponent(new FormLocalLabel(ControllerInput.GAME_CONTROLS.displayName, new FontOptions(20), 0, width / 2, flow.next(28), width));
        this.populateWithStates(ControllerInput.GAME_CONTROLS.states, flow, width);
        flow.next(10);
        this.contentBox.addComponent(new FormLocalLabel(ControllerInput.MENU_SET_LAYER.displayName, new FontOptions(20), 0, width / 2, flow.next(28), width));
        this.populateWithStates(ControllerInput.MENU_SET_LAYER.states, flow, width);
        this.contentBox.setContentBox(new Rectangle(width, flow.next()));
        form.addComponent(new FormLocalTextButton("settingsui", "restoredefaultbindall", "settingsui", "restoredefaultbindalltooltip", 4, height - 5, width)).onClicked(e -> {
            this.lastScrollY = this.contentBox.getScrollY();
            this.inputSource.restoreAllControllerBinds(this.lastControllerHandle);
            form.playTickSound();
            this.onBindChanged.run();
            repopulate.run();
        });
        this.contentBox.setScroll(0, this.lastScrollY);
    }

    private void findOverlappingControls() {
        ControllerBind otherBind;
        ControllerBind bind;
        this.overlappingControls.clear();
        for (ControllerInputState control : ControllerInput.GAME_CONTROLS.states) {
            for (ControllerInputState otherControl : ControllerInput.GAME_CONTROLS.states) {
                bind = this.inputSource.getControllerBindForState(control, this.lastControllerHandle);
                otherBind = this.inputSource.getControllerBindForState(otherControl, this.lastControllerHandle);
                if (control == otherControl || !bind.isBound() || !otherBind.isBound() || !bind.equals(otherBind)) continue;
                this.overlappingControls.add(control);
                this.overlappingControls.add(otherControl);
            }
        }
        for (ControllerInputState control : ControllerInput.MENU_SET_LAYER.states) {
            for (ControllerInputState otherControl : ControllerInput.MENU_SET_LAYER.states) {
                bind = this.inputSource.getControllerBindForState(control, this.lastControllerHandle);
                otherBind = this.inputSource.getControllerBindForState(otherControl, this.lastControllerHandle);
                if (control == otherControl || !bind.isBound() || !otherBind.isBound() || !bind.equals(otherBind)) continue;
                this.overlappingControls.add(control);
                this.overlappingControls.add(otherControl);
            }
        }
    }

    private void populateWithStates(ArrayList<ControllerInputState> states, FormFlow flow, int width) {
        states.stream().sorted(Comparator.comparing(c -> c.getDisplayName().translate())).forEach(control -> {
            int currentY = flow.next(35);
            this.contentBox.addComponent(new FormLocalLabel(control.getDisplayName(), new FontOptions(16), -1, 0, currentY + 8, width));
            int restoreButtonWidth = 32;
            int spaceAfterBind = 2;
            int rebindButtonWidth = 96;
            int rightSide = width - this.contentBox.getScrollBarWidth() - 1;
            if (this.lastControllerHandle == null) {
                this.contentBox.addComponent(new FormContentIconButton(rightSide - rebindButtonWidth - restoreButtonWidth - spaceAfterBind, currentY, rebindButtonWidth, FormInputSize.SIZE_32, ButtonColor.BASE, new ButtonTexture(Settings.UI, ControllerGlyphCollections.FLAIR_CONTROLLER_DISCONNECTED.getTexture().resize(27, 22), false), new GameMessage[0]));
            } else {
                ControllerBind bind = this.inputSource.getControllerBindForState((ControllerInputState)control, this.lastControllerHandle);
                if (this.selectedControl != control && bind.isBound()) {
                    GameTexture glyph = bind.getGlyph(ControllerInput.getLatestControllerHandle());
                    if (glyph == null) {
                        glyph = GameResources.error;
                    }
                    glyph = glyph.resize(22, 22);
                    ButtonTexture icon = new ButtonTexture(Settings.UI, glyph, false);
                    this.contentBox.addComponent(new FormContentIconButton(rightSide - rebindButtonWidth - restoreButtonWidth - spaceAfterBind, currentY, rebindButtonWidth, FormInputSize.SIZE_32, this.overlappingControls.contains(control) ? ButtonColor.RED : ButtonColor.BASE, icon, new GameMessage[0])).onClicked(e -> this.startBindDetection((ControllerInputState)control));
                } else {
                    this.contentBox.addComponent(new FormTextButton(this.selectedControl == control ? "???" : "Not set", null, rightSide - rebindButtonWidth - restoreButtonWidth - spaceAfterBind, currentY, rebindButtonWidth, FormInputSize.SIZE_32, this.selectedControl == control ? ButtonColor.GREEN : ButtonColor.BASE).onClicked(e -> this.startBindDetection((ControllerInputState)control)));
                }
                if (!this.inputSource.getDefaultControllerBind((ControllerInputState)control, this.lastControllerHandle).equals(bind)) {
                    this.contentBox.addComponent(new FormContentIconButton(rightSide - restoreButtonWidth, currentY, restoreButtonWidth, FormInputSize.SIZE_32, ButtonColor.BASE, Settings.UI.button_reset_20, new GameMessage[0]).onClicked(e -> {
                        this.lastScrollY = this.contentBox.getScrollY();
                        this.inputSource.restoreControllerBind((ControllerState)control, this.lastControllerHandle);
                        this.form.playTickSound();
                        this.onBindChanged.run();
                        this.repopulate.run();
                    }));
                }
            }
        });
    }

    private void startBindDetection(ControllerInputState control) {
        if (control instanceof ControllerAnalogState) {
            this.inputSource.onNextAnalogInput(this::handleAnalogInput);
        } else {
            this.inputSource.onNextButtonInput(this::handleButtonInput);
        }
        this.selectedControl = control;
        this.form.playTickSound();
        this.lastScrollY = this.contentBox.getScrollY();
        this.repopulate.run();
    }

    private void handleAnalogInput(ControllerBind bind) {
        if (this.selectedControl != null && this.selectedControl instanceof ControllerAnalogState) {
            this.lastControllerHandle = ControllerInput.getLatestControllerHandle();
            this.inputSource.setStateBind(this.selectedControl, bind, this.lastControllerHandle);
            this.selectedControl = null;
            this.form.playTickSound();
            this.lastScrollY = this.contentBox.getScrollY();
            this.onBindChanged.run();
            this.repopulate.run();
            for (ControllerEvent event : ControllerInput.getEvents()) {
                event.use();
            }
        }
    }

    private void handleButtonInput(ControllerBind bind) {
        if (this.selectedControl != null && this.selectedControl instanceof ControllerButtonState) {
            this.lastControllerHandle = ControllerInput.getLatestControllerHandle();
            this.inputSource.setStateBind(this.selectedControl, bind, this.lastControllerHandle);
            this.form.playTickSound();
            this.lastScrollY = this.contentBox.getScrollY();
            this.selectedControl = null;
            this.onBindChanged.run();
            this.repopulate.run();
        }
    }

    @Override
    public void handleInputEvent(InputEvent event, Form form, Runnable repopulate) {
        if (this.selectedControl != null) {
            if (event.getID() == 256 && !event.state) {
                this.inputSource.clearControllerBind(this.selectedControl, this.lastControllerHandle);
                this.selectedControl = null;
                form.playTickSound();
                this.lastScrollY = this.contentBox.getScrollY();
                this.onBindChanged.run();
                repopulate.run();
            }
            event.use();
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, Form form, Runnable repopulate) {
        if (event.controllerHandle != this.lastControllerHandle) {
            this.lastScrollY = this.contentBox.getScrollY();
            repopulate.run();
        }
        if (this.selectedControl != null) {
            if ((event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU) && event.buttonState) {
                this.inputSource.clearControllerBind(this.selectedControl, this.lastControllerHandle);
                this.selectedControl = null;
                form.playTickSound();
                this.lastScrollY = this.contentBox.getScrollY();
                repopulate.run();
            }
            event.use();
        }
    }

    @Override
    public void runOnBindChanged(Runnable onBindChanged) {
        this.onBindChanged = onBindChanged;
    }
}

