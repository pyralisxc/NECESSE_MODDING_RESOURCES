/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.platforms.sharedOnPC.forms;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import necesse.engine.input.Control;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.LocalMessage;
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
import necesse.gfx.ui.ButtonColor;

public class FormKeyboardAndMouseControlListPopulator
implements FormControlListPopulator {
    private final ArrayList<Control> controls = new ArrayList();
    private final HashSet<Control> overlappingControls = new HashSet();
    private Runnable onBindChanged;
    private int lastScrollY;
    private Control selectedControl = null;
    private FormContentBox contentBox;

    @Override
    public void populateForm(Form form, int x, int y, int width, int height, Runnable repopulate) {
        FormFlow flow = new FormFlow();
        this.contentBox = form.addComponent(new FormContentBox(x, y, width, height - 40));
        this.controls.clear();
        Control.getControls().forEach(this.controls::add);
        this.overlappingControls.clear();
        for (Control control : this.controls) {
            for (Control otherControl : this.controls) {
                if (!control.overlaps(otherControl)) continue;
                this.overlappingControls.add(control);
                this.overlappingControls.add(otherControl);
            }
        }
        Comparator<Control.ControlGroup> comparator = Comparator.comparingInt(g -> g.sort);
        comparator = comparator.thenComparing(c -> c.displayName.translate());
        Control.streamGroups().sorted(comparator).forEach(g -> {
            flow.next(10);
            this.contentBox.addComponent(new FormLocalLabel(g.displayName, new FontOptions(20), 0, width / 2, flow.next(28), width));
            for (Control control : g.getControls()) {
                int currentY = flow.next(25);
                this.contentBox.addComponent(new FormLocalLabel(control.text, new FontOptions(16), -1, 0, currentY, width));
                int restoreButtonWidth = 20;
                int spaceAfterBind = 2;
                int rebindButtonWidth = width / 3 + 10;
                int rightSide = width - this.contentBox.getScrollBarWidth() - 1;
                this.contentBox.addComponent(new FormTextButton(this.selectedControl == control ? "???" : control.getKeyName(), control.tooltip != null ? control.tooltip.translate() : null, rightSide - rebindButtonWidth - restoreButtonWidth - spaceAfterBind, currentY, rebindButtonWidth, FormInputSize.SIZE_20, this.selectedControl == control ? ButtonColor.GREEN : (this.overlappingControls.contains(control) ? ButtonColor.RED : ButtonColor.BASE)).onClicked(e -> {
                    if (this.selectedControl == null) {
                        this.selectedControl = control;
                        form.playTickSound();
                        this.lastScrollY = this.contentBox.getScrollY();
                        repopulate.run();
                    }
                }));
                if (control.isDefaultBind()) continue;
                this.contentBox.addComponent(new FormContentIconButton(rightSide - restoreButtonWidth, currentY, restoreButtonWidth, FormInputSize.SIZE_20, ButtonColor.BASE, this.contentBox.getInterfaceStyle().button_reset_16, new LocalMessage("settingsui", "restoredefaultbind").addReplacement("bind", control.getDefaultKeyName()))).onClicked(e -> {
                    this.lastScrollY = this.contentBox.getScrollY();
                    control.restoreDefaultKey();
                    form.playTickSound();
                    this.onBindChanged.run();
                    repopulate.run();
                });
            }
        });
        this.contentBox.setContentBox(new Rectangle(width, flow.next()));
        form.addComponent(new FormLocalTextButton("settingsui", "restoredefaultbindall", "settingsui", "restoredefaultbindalltooltip", 4, height - 5, width)).onClicked(e -> {
            this.lastScrollY = this.contentBox.getScrollY();
            for (Control control : this.controls) {
                control.restoreDefaultKey();
            }
            form.playTickSound();
            this.onBindChanged.run();
            repopulate.run();
        });
        this.contentBox.setScroll(0, this.lastScrollY);
    }

    @Override
    public void handleInputEvent(InputEvent event, Form form, Runnable repopulate) {
        if (this.selectedControl != null) {
            if (event.getID() == 256 && !event.state) {
                this.selectedControl.changeKey(-1);
                this.selectedControl = null;
                form.playTickSound();
                this.lastScrollY = this.contentBox.getScrollY();
                this.onBindChanged.run();
                repopulate.run();
            } else if (!event.state && (event.isMouseClickEvent() || event.isKeyboardEvent()) || event.isMouseWheelEvent()) {
                this.selectedControl.changeKey(event.getID());
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
        if (this.selectedControl != null) {
            if (event.getState() == ControllerInput.MENU_BACK && !event.buttonState) {
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

