/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import necesse.engine.input.InputEvent;
import necesse.engine.util.FloatDimension;
import necesse.engine.window.GameWindow;
import necesse.gfx.Renderer;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;

public class FairGameSpriteGlyph
implements FairGlyph {
    protected GameSprite sprite;
    protected boolean isHovering;
    protected int xOffset;
    protected int yOffset;

    public FairGameSpriteGlyph(GameSprite sprite) {
        this.sprite = sprite;
    }

    public FairGameSpriteGlyph(GameSprite sprite, int xOffset, int yOffset) {
        this.sprite = sprite;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public FairGameSpriteGlyph(GameTexture texture) {
        this(new GameSprite(texture));
    }

    public FairGameSpriteGlyph(GameTexture texture, int xOffset, int yOffset) {
        this(new GameSprite(texture), xOffset, yOffset);
    }

    @Override
    public FloatDimension getDimensions() {
        return new FloatDimension(this.sprite.width + this.xOffset, this.sprite.height);
    }

    @Override
    public void updateDimensions() {
    }

    @Override
    public void draw(float x, float y, Color defaultColor) {
        this.sprite.initDraw().alpha((float)defaultColor.getAlpha() / 255.0f).draw((int)x + this.xOffset, (int)y - this.sprite.height + this.yOffset);
        if (this.isHovering()) {
            Renderer.setCursor(GameWindow.CURSOR.INTERACT);
        }
    }

    @Override
    public FairGlyph getTextBoxCharacter() {
        return this;
    }

    @Override
    public void handleInputEvent(float drawX, float drawY, InputEvent event) {
        if (event.isMouseMoveEvent()) {
            Dimension dim = this.getDimensions().toInt();
            this.isHovering = new Rectangle((int)drawX + 2 + this.xOffset, (int)drawY - dim.height - 2 + this.yOffset, dim.width, dim.height).contains(event.pos.hudX, event.pos.hudY);
        }
    }

    public boolean isHovering() {
        return this.isHovering;
    }

    @Override
    public String getParseString() {
        return "";
    }
}

