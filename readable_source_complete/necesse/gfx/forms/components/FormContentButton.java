/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.forms.components.FormButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.ButtonColor;
import necesse.gfx.ui.ButtonState;

public abstract class FormContentButton
extends FormButton
implements FormPositionContainer {
    private FormPosition position;
    private int width;
    public FormInputSize size;
    public ButtonColor color;

    public FormContentButton(int x, int y, int width, FormInputSize size, ButtonColor color) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.size = size;
        this.color = color;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Color drawCol = this.getDrawColor();
        ButtonState state = this.getButtonState();
        int contentOffset = 0;
        boolean useDownTexture = this.isButtonDown();
        if (useDownTexture) {
            this.size.getButtonDownDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
            contentOffset = this.size.buttonDownContentDrawOffset;
        } else {
            this.size.getButtonDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        }
        Rectangle contentRect = this.size.getContentRectangle(this.width);
        FormShader.FormShaderState contentState = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(contentRect.x, contentRect.y, contentRect.width, contentRect.height));
        try {
            this.drawContent(contentRect.x, contentRect.y + contentOffset, contentRect.width, contentRect.height);
        }
        finally {
            contentState.end();
        }
        if (useDownTexture) {
            this.size.getButtonDownEdgeDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        } else {
            this.size.getButtonEdgeDrawOptions(this.getInterfaceStyle(), this.color, state, this.getX(), this.getY(), this.width, drawCol).draw();
        }
        if (this.isHovering()) {
            this.addTooltips(perspective);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormContentButton.singleBox(new Rectangle(this.getX(), this.getY() + this.size.textureDrawOffset, this.width, this.size.height));
    }

    protected abstract void drawContent(int var1, int var2, int var3, int var4);

    protected void addTooltips(PlayerMob perspective) {
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public boolean isButtonDown() {
        return this.isDown() && this.isHovering();
    }
}

