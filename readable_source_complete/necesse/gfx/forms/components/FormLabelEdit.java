/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.forms.components.FormFairTypeEdit;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormInputEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.shader.FormShader;

public class FormLabelEdit
extends FormFairTypeEdit
implements FormPositionContainer {
    private FormPosition position;
    private int width;
    private int scrollX;
    private final FormEventsHandler<FormInputEvent<FormLabelEdit>> submitEvents = new FormEventsHandler();
    public Color color;
    public boolean addControllerHitbox;

    public FormLabelEdit(String text, FontOptions fontOptions, Color textColor, FairType.TextAlign align, int x, int y, int width, int maxLength) {
        super(fontOptions, align, textColor, -1, 1, maxLength);
        this.color = this.getInterfaceStyle().activeTextColor;
        this.addControllerHitbox = false;
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.setText(text);
        this.onCaretMove(e -> {
            if (!e.causedByMouse) {
                this.fitScroll();
            }
        });
        this.onMouseChangedTyping(e -> {
            if (!this.isTyping()) {
                this.submitEvents.onEvent(new FormInputEvent<FormLabelEdit>(this, e.event));
            }
        });
        this.onChange(e -> this.fitScroll());
    }

    public FormLabelEdit(String text, FontOptions fontOptions, Color textColor, int x, int y, int width, int maxLength) {
        this(text, fontOptions, textColor, FairType.TextAlign.LEFT, x, y, width, maxLength);
    }

    public FormLabelEdit onSubmit(FormEventListener<FormInputEvent<FormLabelEdit>> listener) {
        this.submitEvents.addListener(listener);
        return this;
    }

    public void setWidth(int width) {
        this.width = width;
        this.fitScroll();
    }

    public void fitScroll() {
        Rectangle caretBox = this.getCaretBoundingBox();
        caretBox.x -= this.getTextX();
        Rectangle boundingBox = this.getDrawOptions().getBoundingBox();
        if (boundingBox.width < this.width) {
            this.scrollX = 0;
        } else {
            int caretMax = caretBox.x + caretBox.width;
            switch (this.align) {
                case LEFT: {
                    this.scrollX = 0;
                    if (caretMax <= this.width - 16) break;
                    this.scrollX = caretMax + 16 - this.width;
                    break;
                }
                case CENTER: {
                    int centerOffset = -this.width / 2;
                    this.scrollX = (this.width - boundingBox.width) / 2;
                    if (caretMax <= this.width / 2 - 16 + this.scrollX) break;
                    this.scrollX = caretMax + centerOffset + 16;
                    break;
                }
                case RIGHT: {
                    this.scrollX = this.width - boundingBox.width;
                    if (caretMax <= this.scrollX - 16) break;
                    this.scrollX = Math.min(caretMax + 16, 4);
                }
            }
        }
    }

    @Override
    protected void setFairType(FairType type) {
        super.setFairType(type);
        this.fitScroll();
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isTyping() && event.state && (event.getID() == 257 || event.getID() == 256)) {
            FormInputEvent<FormLabelEdit> ent = new FormInputEvent<FormLabelEdit>(this, event);
            this.setTyping(false);
            this.submitEvents.onEvent(ent);
            if (ent.hasPreventedDefault()) {
                this.setTyping(true);
            } else {
                event.use();
            }
        }
        super.handleInputEvent(InputEvent.OffsetHudEvent(WindowManager.getWindow().getInput(), event, -this.getX(), -this.getY()), tickManager, perspective);
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (this.addControllerHitbox) {
            ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        int offset = 0;
        switch (this.align) {
            case LEFT: {
                break;
            }
            case CENTER: {
                offset = -this.width / 2;
                break;
            }
            case RIGHT: {
                offset = -this.width;
            }
        }
        return FormLabelEdit.singleBox(new Rectangle(this.getX() + offset, this.getY(), this.width, this.fontOptions.getSize()));
    }

    @Override
    protected Rectangle getTextBoundingBox() {
        int offset = 0;
        switch (this.align) {
            case LEFT: {
                break;
            }
            case CENTER: {
                offset = -this.width / 2;
                break;
            }
            case RIGHT: {
                offset = -this.width;
            }
        }
        return new Rectangle(offset, 0, this.width, this.fontOptions.getSize());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        int limitXOffset = 0;
        switch (this.align) {
            case LEFT: {
                break;
            }
            case CENTER: {
                limitXOffset = -this.width / 2;
                break;
            }
            case RIGHT: {
                limitXOffset = -this.width;
            }
        }
        FairTypeDrawOptions drawOptions = this.getDrawOptions();
        Rectangle boundingBox = drawOptions.getBoundingBox();
        int height = boundingBox.height + 4;
        if (this.fontOptions.hasShadow()) {
            int[] offset = this.fontOptions.getShadowOffset();
            int xOffset = offset[0];
            int widthOffset = xOffset < 0 ? -xOffset : xOffset;
            int yOffset = offset[1];
            int heightOffset = yOffset < 0 ? -yOffset : yOffset;
            FormShader.FormShaderState shaderState = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(xOffset + limitXOffset, yOffset, this.width + widthOffset, height + heightOffset));
            try {
                drawOptions.drawShadows(this.getTextX(), this.getTextY());
            }
            finally {
                shaderState.end();
            }
        }
        FormShader.FormShaderState shaderState = GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(limitXOffset, 0, this.width, height));
        try {
            super.draw(tickManager, perspective, renderBox);
        }
        finally {
            shaderState.end();
        }
    }

    @Override
    public void drawGlyphs() {
        this.getDrawOptions().drawCharacters(this.getTextX(), this.getTextY(), this.textColor);
    }

    @Override
    protected int getTextX() {
        return -this.scrollX;
    }

    @Override
    protected int getTextY() {
        return 0;
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

