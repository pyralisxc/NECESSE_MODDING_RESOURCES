/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameFont.GameFontHandler;

public class FormLabel
extends FormComponent
implements FormPositionContainer {
    public static final int ALIGN_LEFT = -1;
    public static final int ALIGN_MID = 0;
    public static final int ALIGN_RIGHT = 1;
    private FormPosition position;
    private ArrayList<GameMessage> lines;
    private int maxWidth;
    private int align;
    private FontOptions fontOptions;

    public FormLabel(String text, FontOptions fontOptions, int align, int x, int y, int maxWidth) {
        this.fontOptions = fontOptions.defaultColor(this.getInterfaceStyle().activeTextColor);
        this.align = align;
        this.position = new FormFixedPosition(x, y);
        this.setText(text, maxWidth);
    }

    public FormLabel(String text, FontOptions fontOptions, int align, int x, int y) {
        this(text, fontOptions, align, x, y, -1);
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        GameFontHandler font = FontManager.bit;
        for (int i = 0; i < this.lines.size(); ++i) {
            int drawX = this.getX();
            if (this.align == 0) {
                drawX = this.getX() - font.getWidthCeil(this.lines.get(i).translate(), this.fontOptions) / 2;
            } else if (this.align == 1) {
                drawX = this.getX() - font.getWidthCeil(this.lines.get(i).translate(), this.fontOptions);
            }
            font.drawString(drawX, this.getY() + i * this.fontOptions.getSize(), this.lines.get(i).translate(), this.fontOptions);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        int startX = this.getX();
        if (this.align == 0) {
            startX = this.getX() - this.maxWidth / 2;
        } else if (this.align == 1) {
            startX = this.getX() - this.maxWidth;
        }
        return FormLabel.singleBox(new Rectangle(startX, this.getY(), this.maxWidth, this.getLinesCount() * this.fontOptions.getSize()));
    }

    public int getHeight() {
        return this.lines.size() * this.fontOptions.getSize();
    }

    public void setText(GameMessage text, int maxWidth) {
        this.lines = new ArrayList();
        this.maxWidth = 0;
        for (GameMessage line : text.breakMessage(this.fontOptions, maxWidth > 0 ? maxWidth : Integer.MAX_VALUE)) {
            this.addBrokenLine(line);
        }
    }

    public void setColor(Color color) {
        this.fontOptions.defaultColor(color);
    }

    public void setText(GameMessage text) {
        this.setText(text, -1);
    }

    public void setText(String text) {
        this.setText(new StaticMessage(text));
    }

    public void setText(String text, int maxWidth) {
        this.setText(new StaticMessage(text), maxWidth);
    }

    public void addLine(GameMessage text, int maxWidth) {
        for (GameMessage line : text.breakMessage(this.fontOptions, maxWidth > 0 ? maxWidth : Integer.MAX_VALUE)) {
            this.addBrokenLine(line);
        }
    }

    public void addLine(String text, int maxWidth) {
        this.addLine(new StaticMessage(text), maxWidth);
    }

    public void addLine(GameMessage text) {
        this.addLine(text, -1);
    }

    public void addLine(String text) {
        this.addLine(new StaticMessage(text));
    }

    private void addBrokenLine(GameMessage line) {
        this.lines.add(line);
        int lineWidth = FontManager.bit.getWidthCeil(line.translate(), this.fontOptions);
        if (lineWidth > this.maxWidth) {
            this.maxWidth = lineWidth;
        }
    }

    public int getLinesCount() {
        return this.lines.size();
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

