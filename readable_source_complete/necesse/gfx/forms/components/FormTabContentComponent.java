/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;

public abstract class FormTabContentComponent
extends FormButton {
    protected final Form form;
    protected int x;
    protected FormInputSize size;
    protected int currentWidth;
    private int wantedWidth;
    ArrayList<Consumer<Integer>> onWantedWidthChanged;

    public FormTabContentComponent(Form form, int x, FormInputSize size, int currentWidth) {
        this(form, x, size, currentWidth, currentWidth);
    }

    public FormTabContentComponent(Form form, int x, FormInputSize size, int currentWidth, int wantedWidth) {
        this.form = form;
        this.x = x;
        this.size = size;
        this.wantedWidth = wantedWidth;
        this.currentWidth = currentWidth;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Color drawCol = this.getDrawColor();
        int textOffset = this.isDown() ? this.getInterfaceStyle().formTabDownTextOffset : 0;
        int yOffset = this.getInterfaceStyle().formTabOffset;
        int drawX = this.form.getX() + this.x;
        int drawY = this.form.getY() - yOffset;
        DrawOptions baseOptions = this.isDown() ? this.size.getFormTabDownDrawOptions(this.getInterfaceStyle(), ButtonColor.BASE, this.getButtonState(), drawX, drawY, this.currentWidth, drawCol) : this.size.getFormTabDrawOptions(this.getInterfaceStyle(), ButtonColor.BASE, this.getButtonState(), drawX, drawY, this.currentWidth, drawCol);
        DrawOptions edgeOptions = this.isDown() ? this.size.getFormTabDownEdgeDrawOptions(this.getInterfaceStyle(), ButtonColor.BASE, this.getButtonState(), drawX, drawY, this.currentWidth, drawCol) : this.size.getFormTabEdgeDrawOptions(this.getInterfaceStyle(), ButtonColor.BASE, this.getButtonState(), drawX, drawY, this.currentWidth, drawCol);
        baseOptions.draw();
        FormShader.FormShaderState textState = GameResources.formShader.startState(new Point(this.form.getX() + this.x, this.form.getY() - this.size.height - yOffset), new Rectangle(this.currentWidth, this.size.height));
        try {
            this.drawContent(0, textOffset, this.currentWidth, this.size.height, perspective);
        }
        finally {
            textState.end();
        }
        edgeOptions.draw();
        if (this.isHovering()) {
            this.addTooltips(perspective);
        }
    }

    @Override
    public ButtonState getButtonState() {
        if (!this.isActive() || this.isOnCooldown()) {
            return ButtonState.INACTIVE;
        }
        if (this.isSelected() || this.isHovering()) {
            return ButtonState.HIGHLIGHTED;
        }
        return ButtonState.ACTIVE;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        int height = this.size.height + this.getInterfaceStyle().formTabOffset + this.getInterfaceStyle().formTabEdgeSize;
        return FormTabContentComponent.singleBox(new Rectangle(this.form.getX() + this.x - this.getInterfaceStyle().formTabEdgeSize, this.form.getY() - height, this.currentWidth + this.getInterfaceStyle().formTabEdgeSize * 2, height));
    }

    public boolean isSelected() {
        return false;
    }

    protected void addTooltips(PlayerMob perspective) {
    }

    protected abstract void drawContent(int var1, int var2, int var3, int var4, PlayerMob var5);

    public void onWantedWidthChanged(Consumer<Integer> consumer) {
        if (this.onWantedWidthChanged == null) {
            this.onWantedWidthChanged = new ArrayList();
        }
        this.onWantedWidthChanged.add(consumer);
    }

    public int getWantedWidth() {
        return this.wantedWidth;
    }

    protected void setWantedWidth(int wantedWidth) {
        if (wantedWidth == this.wantedWidth) {
            return;
        }
        this.wantedWidth = wantedWidth;
        if (this.onWantedWidthChanged != null) {
            for (Consumer<Integer> c : this.onWantedWidthChanged) {
                c.accept(wantedWidth);
            }
        }
    }

    public int getWidth() {
        return this.currentWidth;
    }

    public void setWidth(int width) {
        this.currentWidth = width;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }
}

