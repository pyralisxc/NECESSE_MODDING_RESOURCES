/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModListData;
import necesse.engine.modLoader.ModNextListData;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.Renderer;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormIconButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.HoverStateTextures;

public abstract class FormModListElement
extends Form {
    public final LoadedMod mod;
    public final ModListData listData;
    public final boolean[] dependsMet;
    public final boolean[] optionalDependsMet;
    private int dependError;
    private int dependWarning;
    private int currentIndex;
    public FormIconButton moveUpButton;
    public FormIconButton moveDownButton;
    public FormCheckBox enabledCheckbox;

    public FormModListElement(final ModNextListData data, final int width) {
        super(width, 24);
        this.mod = data.mod;
        this.listData = new ModListData(this.mod);
        this.listData.enabled = data.enabled;
        this.dependsMet = new boolean[this.mod.depends.length];
        this.optionalDependsMet = new boolean[this.mod.optionalDepends.length];
        this.dependError = this.dependsMet.length;
        this.dependWarning = this.optionalDependsMet.length;
        this.drawBase = false;
        this.shouldLimitDrawArea = false;
        this.moveUpButton = this.addComponent(new FormIconButton(4, 0, this.getInterfaceStyle().button_moveup, 16, 13, new LocalMessage("ui", "moveupbutton")));
        this.moveUpButton.onClicked(e -> this.onMovedUp());
        this.moveUpButton.setActive(data.enabled);
        this.moveDownButton = this.addComponent(new FormIconButton(4, 11, this.getInterfaceStyle().button_movedown, 16, 13, new LocalMessage("ui", "movedownbutton")));
        this.moveDownButton.onClicked(e -> this.onMovedDown());
        this.moveDownButton.setActive(data.enabled);
        this.enabledCheckbox = this.addComponent(new FormCheckBox("", 24, 6){

            @Override
            public GameTooltips getTooltip() {
                if (this.checked) {
                    return new StringTooltips(Localization.translate("ui", "moddisable"));
                }
                return new StringTooltips(Localization.translate("ui", "modenable"));
            }
        });
        this.enabledCheckbox.checked = data.enabled;
        this.enabledCheckbox.onClicked(e -> {
            this.listData.enabled = ((FormCheckBox)e.from).checked;
            this.onEnabledChanged(((FormCheckBox)e.from).checked);
        });
        this.enabledCheckbox.setupDragToOtherCheckboxes("toggleModEnabled", true);
        this.addComponent(new FormCustomDraw(44, 0, 20, 20){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                Color color;
                HoverStateTextures icon = data.mod.loadLocation.modProvider.getIcon();
                GameTexture texture = icon.active;
                Color color2 = color = FormModListElement.this.listData.enabled ? this.getInterfaceStyle().activeTextColor : this.getInterfaceStyle().inactiveTextColor;
                if (this.isHovering() && Renderer.getMouseDraggingElement() == null || FormModListElement.this.isCurrentlySelected()) {
                    color = this.getInterfaceStyle().highlightTextColor;
                    texture = icon.highlighted;
                }
                texture.initDraw().size(24).color(color).draw(this.getX(), this.getY());
                if (this.isHovering()) {
                    GameTooltipManager.addTooltip(new StringTooltips(data.mod.loadLocation.modProvider.getGameMessage().translate()), TooltipLocation.FORM_FOCUS);
                }
            }
        });
        FormButton button = this.addComponent(new FormButton(){

            @Override
            public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
                Color color;
                Color warningColor = null;
                ListGameTooltips tooltips = new ListGameTooltips();
                if (FormModListElement.this.dependWarning > 0) {
                    warningColor = this.getInterfaceStyle().warningTextColor;
                }
                if (!"1.0.1".equals(data.mod.gameVersion)) {
                    warningColor = this.getInterfaceStyle().warningTextColor;
                    tooltips.add(new StringTooltips(Localization.translate("ui", "modwronggameversion"), new Color(200, 150, 50), 300));
                }
                if (FormModListElement.this.dependError > 0) {
                    warningColor = this.getInterfaceStyle().errorTextColor;
                    tooltips.add(new StringTooltips(Localization.translate("ui", "modmissingdep"), GameColor.RED, 300));
                }
                if (data.mod.initError || data.mod.runError) {
                    warningColor = this.getInterfaceStyle().errorTextColor;
                    tooltips.add(new StringTooltips(Localization.translate("ui", "moderrorwarning"), GameColor.RED, 300));
                }
                int startX = 70;
                if (warningColor != null) {
                    TextureDrawOptionsEnd warningIcon = this.getInterfaceStyle().warning_icon.initDraw().size(24).color(warningColor).alpha(FormModListElement.this.listData.enabled ? 1.0f : 0.5f);
                    warningIcon.draw(startX, 0);
                    startX += warningIcon.getWidth() + 2;
                }
                Color color2 = color = FormModListElement.this.listData.enabled ? this.getInterfaceStyle().activeTextColor : this.getInterfaceStyle().inactiveTextColor;
                if (this.isHovering() && Renderer.getMouseDraggingElement() == null || FormModListElement.this.isCurrentlySelected()) {
                    color = this.getInterfaceStyle().highlightTextColor;
                }
                FontManager.bit.drawString(startX, 4.0f, data.mod.name, new FontOptions(16).color(color));
                if (this.isHovering() && !tooltips.isEmpty()) {
                    GameTooltipManager.addTooltip(tooltips, TooltipLocation.FORM_FOCUS);
                }
            }

            @Override
            public List<Rectangle> getHitboxes() {
                return Collections.singletonList(new Rectangle(70, 0, width - 70, FormModListElement.this.getHeight()));
            }
        }, 100);
        button.onClicked(e -> this.onSelected());
        button.onDragStarted(e -> this.onStartDragged());
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isCurrentlySelected() && !event.isUsed() && !event.state) {
            if (event.getID() == 265) {
                this.onMovedUp();
                this.playTickSound();
                event.use();
            } else if (event.getID() == 264) {
                this.onMovedDown();
                this.playTickSound();
                event.use();
            }
        }
        super.handleInputEvent(event, tickManager, perspective);
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isControllerFocus()) {
            if (event.getState() == ControllerInput.MENU_SELECT) {
                if (event.buttonState) {
                    this.listData.enabled = this.enabledCheckbox.checked = !this.enabledCheckbox.checked;
                    this.onEnabledChanged(this.enabledCheckbox.checked);
                    this.playTickSound();
                }
                event.use();
            } else if (event.getState() == ControllerInput.MENU_PREV || event.isRepeatEvent(this.moveUpButton)) {
                if (event.buttonState && this.listData.enabled && this.moveUpButton.isActive()) {
                    event.startRepeatEvents(this.moveUpButton);
                    this.onMovedUp();
                    this.playTickSound();
                    ControllerInput.submitNextRefreshFocusEvent();
                }
                event.use();
            } else if (event.getState() == ControllerInput.MENU_NEXT || event.isRepeatEvent(this.moveDownButton)) {
                if (event.buttonState && this.listData.enabled && this.moveDownButton.isActive()) {
                    event.startRepeatEvents(this.moveDownButton);
                    this.onMovedDown();
                    this.playTickSound();
                    ControllerInput.submitNextRefreshFocusEvent();
                }
                event.use();
            }
        }
        super.handleControllerEvent(event, tickManager, perspective);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        super.drawControllerFocus(current);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "moveupbutton"), ControllerInput.MENU_PREV);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "movedownbutton"), ControllerInput.MENU_NEXT);
    }

    public abstract void onEnabledChanged(boolean var1);

    public abstract void onMovedUp();

    public abstract void onMovedDown();

    public abstract void onStartDragged();

    public abstract void onSelected();

    public abstract boolean isCurrentlySelected();

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public void setCurrentIndex(List<FormModListElement> list, int currentIndex) {
        this.currentIndex = currentIndex;
        this.enabledCheckbox.checked = this.listData.enabled;
        this.moveUpButton.setActive(currentIndex > 0);
        this.moveDownButton.setActive(currentIndex < list.size() - 1);
    }

    public void updateDepends(List<FormModListElement> list) {
        Arrays.fill(this.dependsMet, false);
        Arrays.fill(this.optionalDependsMet, false);
        this.dependError = this.dependsMet.length;
        this.dependWarning = this.optionalDependsMet.length;
        for (int i = 0; i < this.currentIndex; ++i) {
            int j;
            FormModListElement other = list.get(i);
            if (!other.listData.enabled && this.listData.enabled) continue;
            for (j = 0; j < this.mod.depends.length; ++j) {
                if (!this.mod.depends[j].equals(other.mod.id)) continue;
                this.dependsMet[j] = true;
                --this.dependError;
            }
            for (j = 0; j < this.mod.optionalDepends.length; ++j) {
                if (!this.mod.optionalDepends[j].equals(other.mod.id)) continue;
                this.optionalDependsMet[j] = true;
                --this.dependWarning;
            }
        }
    }
}

