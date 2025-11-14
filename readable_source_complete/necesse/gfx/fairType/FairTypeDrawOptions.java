/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.fairType;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.LinkedList;
import necesse.engine.input.InputEvent;
import necesse.gfx.GameColor;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.GlyphContainer;
import necesse.gfx.gameFont.FontOptions;

public abstract class FairTypeDrawOptions {
    public final FairType.TextAlign align;
    public final int maxWidth;
    public final int maxLines;
    public final boolean forceMaxWidth;
    public final boolean cutLastLineWord;
    public final boolean removeWhitespaceLeft;
    public final boolean removeWhiteSpaceRight;
    public final FontOptions ellipsisFO;

    public FairTypeDrawOptions(FairType.TextAlign textAlign, int maxWidth, boolean forceMaxWidth, int maxLines, boolean cutLastLineWord, FontOptions ellipsisFO, boolean removeWhitespaceLeft, boolean removeWhiteSpaceRight) {
        this.align = textAlign;
        this.maxWidth = maxWidth;
        this.forceMaxWidth = forceMaxWidth;
        this.maxLines = maxLines;
        this.cutLastLineWord = cutLastLineWord;
        this.ellipsisFO = ellipsisFO;
        this.removeWhitespaceLeft = removeWhitespaceLeft;
        this.removeWhiteSpaceRight = removeWhiteSpaceRight;
    }

    public abstract void handleInputEvent(int var1, int var2, InputEvent var3);

    public DrawOptions pos(int drawX, int drawY) {
        return () -> this.draw(drawX, drawY);
    }

    public DrawOptions pos(int drawX, int drawY, Color defaultColor) {
        return () -> this.draw(drawX, drawY, defaultColor);
    }

    public void draw(int drawX, int drawY) {
        this.draw(drawX, drawY, GameColor.DEFAULT_COLOR.get());
    }

    public abstract Rectangle getBoundingBox(int var1, int var2);

    public Rectangle getBoundingBox() {
        return this.getBoundingBox(0, 0);
    }

    public abstract void drawCharacters(int var1, int var2, Color var3);

    public abstract void drawShadows(int var1, int var2);

    public void draw(int drawX, int drawY, Color defaultColor) {
        this.drawShadows(drawX, drawY);
        this.drawCharacters(drawX, drawY, defaultColor);
    }

    public abstract LinkedList<GlyphContainer> getDrawList();

    public abstract int getLineCount();

    public abstract boolean displaysEverything();

    public abstract FairType getType();

    public abstract boolean shouldUpdate();
}

