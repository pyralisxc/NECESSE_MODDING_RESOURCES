/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.components.FormFairTypeEdit;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;

public class FormTextBox
extends FormFairTypeEdit
implements FormPositionContainer {
    protected FormPosition position;
    public GameMessage placeHolder;

    public FormTextBox(FontOptions fontOptions, FairType.TextAlign align, Color textColor, int x, int y, int maxWidth, int maxLines, int maxLength) {
        super(fontOptions, align, textColor, maxWidth, maxLines, maxLength);
        this.position = new FormFixedPosition(x, y);
    }

    public FormTextBox(FontOptions fontOptions, FairType.TextAlign align, Color textColor, int x, int y, int maxWidth) {
        this(fontOptions, align, textColor, x, y, maxWidth, -1, -1);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        if (!this.isTyping() && this.getTextLength() == 0 && this.placeHolder != null) {
            FontOptions newOptions = new FontOptions(this.fontOptions).color(this.textColor).alphaf(0.5f);
            FontManager.bit.drawString(this.getTextX(), this.getTextY(), this.placeHolder.translate(), newOptions);
        }
        super.draw(tickManager, perspective, renderBox);
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormTextBox.singleBox(this.getTextBoundingBox());
    }

    public void setEmptyTextSpace(Rectangle rectangle) {
        this.textSelectEmptySpace = rectangle;
    }

    @Override
    protected int getTextX() {
        return this.getX();
    }

    @Override
    protected int getTextY() {
        return this.getY();
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

