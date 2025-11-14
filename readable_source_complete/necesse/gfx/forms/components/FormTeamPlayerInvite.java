/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.ClientClient;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.ui.HoverStateTextures;

public class FormTeamPlayerInvite
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    public final ClientClient client;
    private int width;
    private boolean isHovering;
    public boolean selected;
    public FontOptions fontOptions;
    public Color backgroundColor;

    public FormTeamPlayerInvite(int x, int y, ClientClient client, int width, Color backgroundColor) {
        this.fontOptions = new FontOptions(16).color(this.getInterfaceStyle().activeTextColor);
        this.position = new FormFixedPosition(x, y);
        this.client = client;
        this.width = width;
        this.selected = false;
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
        } else if (!event.state && event.getID() == -100 && this.isMouseOver(event)) {
            this.playTickSound();
            this.selected = !this.selected;
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.getState() == ControllerInput.MENU_SELECT && this.isControllerFocus() && event.buttonState) {
            this.selected = !this.selected;
            this.playTickSound();
            event.use();
        }
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        super.drawControllerFocus(current);
        GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.backgroundColor != null) {
            int margin = this.getInterfaceStyle().form.edgeMargin;
            int edgeResolution = this.getInterfaceStyle().form.edgeResolution;
            this.getInterfaceStyle().form.getCenterDrawOptions(this.getX() - edgeResolution + margin, this.getY() - edgeResolution + margin, this.width + edgeResolution * 2 - margin * 2, 20 + edgeResolution * 2 - margin * 2).forEachDraw(c -> c.color(this.backgroundColor)).draw();
        }
        Color boxColor = this.getInterfaceStyle().activeElementColor;
        if (this.isHovering) {
            boxColor = this.getInterfaceStyle().highlightElementColor;
        }
        HoverStateTextures textures = this.selected ? this.getInterfaceStyle().checkbox_checked : this.getInterfaceStyle().checkbox;
        GameTexture texture = this.isHovering ? textures.highlighted : textures.active;
        texture.initDraw().color(boxColor).draw(this.getX() + 4, this.getY() + 2);
        FontManager.bit.drawString(this.getX() + 24, this.getY() + 2, this.client.getName(), this.fontOptions);
        if (this.backgroundColor != null) {
            int margin = this.getInterfaceStyle().form.edgeMargin;
            int edgeResolution = this.getInterfaceStyle().form.edgeResolution;
            this.getInterfaceStyle().form.getCenterEdgeDrawOptions(this.getX() - edgeResolution + margin, this.getY() - edgeResolution + margin, this.width + edgeResolution * 2 - margin * 2, 20 + edgeResolution * 2 - margin * 2).forEachDraw(c -> c.color(this.backgroundColor)).draw();
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormTeamPlayerInvite.singleBox(new Rectangle(this.getX(), this.getY(), this.width, 16));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }
}

