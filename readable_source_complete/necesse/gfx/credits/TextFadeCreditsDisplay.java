/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.credits;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameMath;
import necesse.gfx.credits.GameCreditsDisplay;
import necesse.gfx.fairType.FairGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontOptions;

public class TextFadeCreditsDisplay
extends GameCreditsDisplay {
    public int fadeInTime;
    public int showTime;
    public int fadeOutTime;
    public int maxWidth;
    public FontOptions fontOptions;
    public GameMessage text;
    public boolean centerText;
    public ArrayList<FairGlyph> startGlyphs = new ArrayList();
    public ArrayList<FairGlyph> endGlyphs = new ArrayList();
    protected FairTypeDrawOptions drawOptions;

    public TextFadeCreditsDisplay(int fadeInTime, int showTime, int fadeOutTime, GameMessage text, int size, int maxWidth, boolean centeredText) {
        this.fadeInTime = fadeInTime;
        this.showTime = showTime;
        this.fadeOutTime = fadeOutTime;
        this.text = text;
        this.fontOptions = new FontOptions(size);
        this.maxWidth = maxWidth;
        this.centerText = centeredText;
    }

    public TextFadeCreditsDisplay(int fadeInTime, int showTime, int fadeOutTime, GameMessage text, int size, boolean centeredText) {
        this(fadeInTime, showTime, fadeOutTime, text, size, 0, centeredText);
    }

    @Override
    public int initDrawAndGetTotalTimeShown() {
        String string = this.text.translate();
        FairType fairType = new FairType();
        for (FairGlyph glyph : this.startGlyphs) {
            fairType.append(glyph);
        }
        fairType.append(this.fontOptions, string);
        for (FairGlyph glyph : this.endGlyphs) {
            fairType.append(glyph);
        }
        fairType.applyParsers(TypeParsers.ItemIcon(this.fontOptions.getSize()), TypeParsers.MobIcon(this.fontOptions.getSize()));
        this.drawOptions = fairType.getDrawOptions(this.centerText ? FairType.TextAlign.CENTER : FairType.TextAlign.LEFT, this.maxWidth, false);
        return this.fadeInTime + this.showTime + this.fadeOutTime;
    }

    @Override
    public Dimension getDrawBounds() {
        Rectangle boundingBox = this.drawOptions.getBoundingBox();
        int xOffset = this.centerText ? this.drawOptions.getBoundingBox().width / 2 : 0;
        return new Dimension(boundingBox.x + xOffset + boundingBox.width, boundingBox.y + boundingBox.height);
    }

    @Override
    public void draw(int currentTime, int x, int y, float alpha) {
        if (this.fadeInTime > 0 && currentTime < this.fadeInTime) {
            float fadeInProgress = (float)currentTime / (float)this.fadeInTime;
            alpha *= fadeInProgress;
        } else if (this.fadeOutTime > 0 && currentTime > this.fadeInTime + this.showTime) {
            float fadeOutProgress = (float)(currentTime - (this.fadeInTime + this.showTime)) / (float)this.fadeOutTime;
            alpha *= 1.0f - fadeOutProgress;
        }
        int xOffset = this.centerText ? this.drawOptions.getBoundingBox().width / 2 : 0;
        this.drawOptions.draw(x + xOffset, y, new Color(1.0f, 1.0f, 1.0f, GameMath.limit(alpha, 0.0f, 1.0f)));
    }

    @Override
    public boolean isDone(int currentTime) {
        return currentTime >= this.fadeInTime + this.showTime + this.fadeOutTime;
    }
}

