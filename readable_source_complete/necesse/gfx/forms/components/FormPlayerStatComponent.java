/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class FormPlayerStatComponent<T>
extends FormComponent
implements FormPositionContainer {
    protected FormPosition position;
    protected int width;
    protected GameMessage displayName;
    protected Supplier<T> statSupplier;
    protected Function<T, String> formatter;
    protected boolean isHovering;
    public FontOptions fontOptions;

    public FormPlayerStatComponent(int x, int y, int width, GameMessage displayName, Supplier<T> statSupplier, Function<T, String> formatter) {
        this.fontOptions = new FontOptions(16).color(this.getInterfaceStyle().activeTextColor);
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.displayName = displayName;
        this.statSupplier = statSupplier;
        if (formatter == null) {
            formatter = Objects::toString;
        }
        this.formatter = formatter;
    }

    public FormPlayerStatComponent(int x, int y, int width, GameMessage displayName, Supplier<T> statSupplier) {
        this(x, y, width, displayName, statSupplier, null);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
    }

    public String getTooltip(boolean couldFitData, T value, String formattedValue) {
        if (!couldFitData) {
            return this.displayName.translate() + ": " + formattedValue;
        }
        return null;
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        String tooltip;
        String name;
        String valueFormat;
        boolean couldFitData = true;
        T value = this.statSupplier.get();
        String cutValueFormat = valueFormat = this.formatter.apply(value);
        int valueWidth = FontManager.bit.getWidthCeil(valueFormat, this.fontOptions);
        if (valueWidth > this.width / 2) {
            cutValueFormat = GameUtils.maxString(valueFormat, this.fontOptions, this.width / 2);
            valueWidth = FontManager.bit.getWidthCeil(valueFormat, this.fontOptions);
            couldFitData = false;
        }
        FontManager.bit.drawString(this.getX() + this.width - valueWidth, this.getY() + 2, cutValueFormat, this.fontOptions);
        String cutName = name = this.displayName.translate();
        int nameWidth = FontManager.bit.getWidthCeil(name, this.fontOptions);
        if (nameWidth > this.width - valueWidth - 10) {
            cutName = GameUtils.maxString(name, this.fontOptions, this.width - valueWidth - 10);
            couldFitData = false;
        }
        FontManager.bit.drawString(this.getX(), this.getY() + 2, cutName, this.fontOptions);
        if (this.isHovering && (tooltip = this.getTooltip(couldFitData, value, valueFormat)) != null) {
            GameTooltipManager.addTooltip(new StringTooltips(tooltip), TooltipLocation.FORM_FOCUS);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormPlayerStatComponent.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.fontOptions.getSize() + 4));
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public GameMessage getDisplayName() {
        return this.displayName;
    }
}

