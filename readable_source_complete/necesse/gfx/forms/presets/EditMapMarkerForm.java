/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientLevelManager;
import necesse.engine.registries.MapIconRegistry;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.ContinueComponent;
import necesse.gfx.forms.components.FormCustomButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;
import necesse.level.maps.mapData.GameMapIcon;

public class EditMapMarkerForm
extends FormSwitcher
implements ContinueComponent {
    private final ArrayList<Runnable> continueEvents = new ArrayList();
    private boolean isContinued;
    protected ClientLevelManager.MapMarker mapMarker;
    protected GameMessage startingName;
    protected GameMapIcon startingIcon;
    protected Form inputForm;
    protected FormTextInput nameInput;

    public EditMapMarkerForm(Client client, int width, final ClientLevelManager.MapMarker mapMarker, boolean cancelMeansDelete) {
        this.mapMarker = mapMarker;
        this.startingName = mapMarker.name;
        this.startingIcon = mapMarker.icon;
        this.inputForm = this.addComponent(new Form("mapMarkerEdit", width, 100));
        FormFlow inputFlow = new FormFlow(4);
        this.inputForm.addComponent(inputFlow.nextY(new FormLocalLabel("ui", "mapmarkername", new FontOptions(16), 0, this.inputForm.getWidth() / 2, 0), 4));
        this.nameInput = this.inputForm.addComponent(inputFlow.nextY(new FormTextInput(4, 0, FormInputSize.SIZE_24, this.inputForm.getWidth() - 8, -1, 50), 4));
        this.nameInput.setText(mapMarker.name.translate());
        this.nameInput.onChange(e -> {
            mapMarker.name = new StaticMessage(this.nameInput.getText().trim());
        });
        this.inputForm.addComponent(inputFlow.nextY(new FormLocalLabel("ui", "mapmarkericon", new FontOptions(16), 0, this.inputForm.getWidth() / 2, 0), 4));
        ArrayList<GameMapIcon> iconList = new ArrayList<GameMapIcon>();
        for (GameMapIcon icon : MapIconRegistry.getAllIcons()) {
            iconList.add(icon);
        }
        iconList.sort(Comparator.comparingInt(c -> -c.getDrawBoundingBox().height));
        boolean firstIcon = true;
        int currentX = 4;
        int largestCurrentRowHeight = 0;
        for (final GameMapIcon icon : iconList) {
            final Rectangle drawBoundingBox = icon.getDrawBoundingBox();
            if (currentX > 4 && currentX + drawBoundingBox.width > this.inputForm.getWidth() - 4) {
                inputFlow.next(drawBoundingBox.height + 4);
                currentX = 4;
            }
            largestCurrentRowHeight = Math.max(largestCurrentRowHeight, drawBoundingBox.height);
            FormCustomButton button = new FormCustomButton(currentX, inputFlow.next(), drawBoundingBox.width, drawBoundingBox.height, new GameMessage[0]){

                @Override
                public void draw(Color color, int drawX, int drawY, PlayerMob perspective) {
                    if (mapMarker.icon == icon) {
                        color = ButtonState.HIGHLIGHTED.elementColorGetter.apply(this.getInterfaceStyle());
                    }
                    icon.drawIcon(drawX - drawBoundingBox.x, drawY - drawBoundingBox.y, color);
                }
            };
            button.onClicked(e -> {
                mapMarker.icon = icon;
            });
            this.inputForm.addComponent(button);
            currentX += drawBoundingBox.width + 4;
            if (firstIcon) {
                this.nameInput.controllerDownFocus = button;
            }
            firstIcon = false;
        }
        inputFlow.next(largestCurrentRowHeight);
        inputFlow.next(4);
        int endButtonsY = inputFlow.next(36);
        int endButtonsWidth = this.inputForm.getWidth() / 2 - 4 - 2;
        this.inputForm.addComponent(new FormLocalTextButton("ui", "savebutton", 4, endButtonsY, endButtonsWidth, FormInputSize.SIZE_32, ButtonColor.BASE)).onClicked(e -> this.applyContinue());
        this.inputForm.addComponent(new FormLocalTextButton("ui", "cancelbutton", 4 + endButtonsWidth + 4, endButtonsY, endButtonsWidth, FormInputSize.SIZE_32, ButtonColor.BASE)).onClicked(e -> {
            if (cancelMeansDelete) {
                client.levelManager.deleteMapIcon(mapMarker);
            } else {
                mapMarker.name = this.startingName;
                mapMarker.icon = this.startingIcon;
            }
            this.applyContinue();
        });
        this.inputForm.setHeight(inputFlow.next());
        this.makeCurrent(this.inputForm);
        this.onWindowResized(WindowManager.getWindow());
    }

    @Override
    protected void init() {
        super.init();
        if (Input.lastInputIsController) {
            this.nameInput.setNextControllerFocus(this);
        } else {
            this.nameInput.setTyping(true);
        }
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!event.isUsed() && event.state && (event.getID() == 256 || event.getID() == Control.INVENTORY.getKey())) {
            this.applyContinue();
            event.use();
            return;
        }
        super.handleInputEvent(event, tickManager, perspective);
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!event.isUsed() && event.buttonState && (event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU || event.getState() == ControllerInput.INVENTORY)) {
            this.applyContinue();
            event.use();
            return;
        }
        super.handleControllerEvent(event, tickManager, perspective);
    }

    @Override
    public void onContinue(Runnable continueEvent) {
        if (continueEvent != null) {
            this.continueEvents.add(continueEvent);
        }
    }

    @Override
    public void applyContinue() {
        if (this.canContinue()) {
            this.continueEvents.forEach(Runnable::run);
            this.isContinued = true;
        }
    }

    @Override
    public boolean isContinued() {
        return this.isContinued;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (this.nameInput != null) {
            this.nameInput.setTyping(false);
        }
    }

    @Override
    public void onWindowResized(GameWindow window) {
        super.onWindowResized(window);
        this.inputForm.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
    }
}

