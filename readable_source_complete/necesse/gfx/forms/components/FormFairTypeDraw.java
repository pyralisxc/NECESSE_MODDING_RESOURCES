/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Supplier;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;

public class FormFairTypeDraw
extends FormComponent
implements FormPositionContainer {
    protected FormPosition position;
    public FairTypeDrawOptions drawOptions;
    public Supplier<Color> colorSupplier;

    public FormFairTypeDraw(int x, int y) {
        this.setPosition(new FormFixedPosition(x, y));
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.drawOptions != null) {
            this.drawOptions.handleInputEvent(this.getX(), this.getY(), event);
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (this.drawOptions != null) {
            Color color = this.colorSupplier == null ? this.getInterfaceStyle().textBoxTextColor : this.colorSupplier.get();
            this.drawOptions.draw(this.getX(), this.getY(), color);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        if (this.drawOptions != null) {
            return FormFairTypeDraw.singleBox(this.drawOptions.getBoundingBox(this.getX(), this.getY()));
        }
        return FormFairTypeDraw.singleBox(new Rectangle(this.getX(), this.getY()));
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

