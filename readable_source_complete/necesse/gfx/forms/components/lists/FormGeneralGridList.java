/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.lists.FormGeneralList;
import necesse.gfx.forms.components.lists.FormListElement;
import necesse.gfx.forms.components.lists.FormListGridElement;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.shader.FormShader;

public abstract class FormGeneralGridList<E extends FormListGridElement>
extends FormGeneralList<E> {
    public final int elementWidth;

    public FormGeneralGridList(int x, int y, int width, int height, int elementWidth, int elementHeight) {
        super(x, y, width, height, elementHeight);
        this.elementWidth = elementWidth;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        area = area.intersection(new Rectangle(currentXOffset + this.getX(), currentYOffset + this.getY(), this.width, this.height));
        if (draw) {
            Renderer.drawShape(area, false, 0.0f, 1.0f, 1.0f, 1.0f);
        }
        FormGeneralGridList formGeneralGridList = this;
        synchronized (formGeneralGridList) {
            int elementsPerRow = Math.max(1, this.width / this.elementWidth);
            int startRow = Math.max(0, (int)Math.ceil((float)this.scroll / (float)this.elementHeight));
            int startIndex = startRow * elementsPerRow;
            int spaceHeight = this.height - 32;
            int endRow = (int)Math.floor((float)(this.scroll + spaceHeight) / (float)this.elementHeight);
            int endIndex = Math.min(this.elements.size(), endRow * elementsPerRow);
            int centerRow = this.width % this.elementWidth / 2;
            for (int i = startIndex; i < endIndex; ++i) {
                FormListGridElement element = (FormListGridElement)this.elements.get(i);
                int row = i / elementsPerRow;
                int elementY = row * this.elementHeight - this.scroll + 16;
                int elementX = (i - row * elementsPerRow) * this.elementWidth + centerRow;
                ControllerFocus.add(list, area, element, new Rectangle(this.elementWidth, this.elementHeight), currentXOffset + this.getX() + elementX, currentYOffset + this.getY() + elementY, 0, this.getControllerNavigationHandler(currentXOffset, currentYOffset));
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        ControllerFocus currentFocus;
        if ((event.isButton && event.buttonState || this.acceptMouseRepeatEvents && event.getState() == ControllerInput.REPEAT_EVENT) && (currentFocus = this.getManager().getCurrentFocus()) != null && currentFocus.handler instanceof FormListElement) {
            FormGeneralGridList formGeneralGridList = this;
            synchronized (formGeneralGridList) {
                int elementsPerRow = Math.max(1, this.width / this.elementWidth);
                int startRow = Math.max(0, (int)Math.ceil((float)this.scroll / (float)this.elementHeight) - 1);
                int startIndex = startRow * elementsPerRow;
                int spaceHeight = this.height - 32;
                int endRow = (int)Math.floor((float)(this.scroll + spaceHeight) / (float)this.elementHeight) + 1;
                int endIndex = Math.min(this.elements.size(), endRow * elementsPerRow);
                for (int i = startIndex; i < endIndex; ++i) {
                    FormListGridElement element = (FormListGridElement)this.elements.get(i);
                    if (element != currentFocus.handler) continue;
                    element.onControllerEvent(this, i, event, tickManager, perspective);
                }
            }
        }
    }

    @Override
    protected ControllerNavigationHandler getControllerNavigationHandler(int currentXOffset, int currentYOffset) {
        return (dir, event, tickManager, perspective) -> {
            LinkedList<ControllerFocus> list = new LinkedList<ControllerFocus>();
            Rectangle area = new Rectangle(currentXOffset + this.getX(), currentYOffset + this.getY() - this.elementHeight, this.width, this.height + this.elementHeight * 2);
            FormGeneralGridList formGeneralGridList = this;
            synchronized (formGeneralGridList) {
                int elementsPerRow = Math.max(1, this.width / this.elementWidth);
                int startRow = Math.max(0, (int)Math.ceil((float)this.scroll / (float)this.elementHeight) - 1);
                int startIndex = startRow * elementsPerRow;
                int spaceHeight = this.height - 32;
                int endRow = (int)Math.floor((float)(this.scroll + spaceHeight) / (float)this.elementHeight) + 1;
                int endIndex = Math.min(this.elements.size(), endRow * elementsPerRow);
                int centerRow = this.width % this.elementWidth / 2;
                for (int i = startIndex; i < endIndex; ++i) {
                    FormListGridElement element = (FormListGridElement)this.elements.get(i);
                    int row = i / elementsPerRow;
                    int elementY = row * this.elementHeight - this.scroll + 16;
                    int elementX = (i - row * elementsPerRow) * this.elementWidth + centerRow;
                    ControllerFocus.add(list, area, element, new Rectangle(this.elementWidth, this.elementHeight), currentXOffset + this.getX() + elementX, currentYOffset + this.getY() + elementY, 0, this.getControllerNavigationHandler(currentXOffset, currentYOffset));
                }
                ControllerFocus next = ControllerFocus.getNext(dir, this.getManager(), list);
                if (next != null) {
                    int currentScroll = this.scroll;
                    Rectangle box = new Rectangle(next.boundingBox.x - (currentXOffset + this.getX()), next.boundingBox.y - (currentYOffset + this.getY() - this.scroll), next.boundingBox.width, next.boundingBox.height);
                    int scrollPadding = this.elementHeight;
                    int minY = box.y - scrollPadding;
                    int maxY = box.y + box.height + scrollPadding - this.height;
                    this.scroll = Math.max(0, GameMath.limit(this.scroll, maxY, minY));
                    this.limitMaxScroll();
                    int deltaScroll = this.scroll - currentScroll;
                    this.getManager().setControllerFocus(new ControllerFocus(next, 0, -deltaScroll));
                    return true;
                }
            }
            return false;
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.handleDrawScroll(tickManager);
        if (this.elements.size() == 0) {
            this.drawEmptyMessage(tickManager);
        } else {
            FormGeneralGridList formGeneralGridList = this;
            synchronized (formGeneralGridList) {
                int elementsPerRow = Math.max(1, this.width / this.elementWidth);
                int startRow = Math.max(0, this.scroll / this.elementHeight);
                int startIndex = startRow * elementsPerRow;
                int spaceHeight = this.height - 32;
                int rowsPerSpace = spaceHeight / this.elementHeight + (spaceHeight % this.elementHeight == 0 ? 0 : 1);
                int endIndex = Math.min(this.elements.size(), startIndex + (rowsPerSpace + (this.scroll % this.elementHeight == 0 ? 0 : 1)) * elementsPerRow);
                int centerRow = this.width % this.elementWidth / 2;
                for (int i = startIndex; i < endIndex; ++i) {
                    int row = i / elementsPerRow;
                    int elementY = row * this.elementHeight - this.scroll + 16;
                    int elementX = (i - row * elementsPerRow) * this.elementWidth + centerRow;
                    int drawX = this.getX() + elementX;
                    int drawY = this.getY() + elementY;
                    int minDrawY = Math.max(0, 16 - elementY);
                    int maxDrawY = Math.min(this.elementHeight, this.height - elementY - 16) - minDrawY;
                    int maxDrawX = Math.min(this.elementWidth, this.width - elementX);
                    FormShader.FormShaderState shaderState = GameResources.formShader.startState(new Point(drawX, drawY), new Rectangle(0, minDrawY, maxDrawX, maxDrawY));
                    try {
                        ((FormListGridElement)this.elements.get(i)).draw(this, tickManager, perspective, i);
                        continue;
                    }
                    finally {
                        shaderState.end();
                    }
                }
            }
        }
        this.drawScrollButtons(tickManager);
    }

    @Override
    protected synchronized FormGeneralList.MouseOverObject getMouseOverObj(InputEvent event) {
        if (!this.isMouseOverElementSpace(event)) {
            return null;
        }
        int elementsPerRow = Math.max(1, this.width / this.elementWidth);
        int startRow = Math.max(0, this.scroll / this.elementHeight);
        int startIndex = startRow * elementsPerRow;
        int spaceHeight = this.height - 32;
        int rowsPerSpace = spaceHeight / this.elementHeight + (spaceHeight % this.elementHeight == 0 ? 0 : 1);
        int endIndex = Math.min(this.elements.size(), startIndex + (rowsPerSpace + (this.scroll % this.elementHeight == 0 ? 0 : 1)) * elementsPerRow);
        for (int i = startIndex; i < endIndex; ++i) {
            FormGeneralList.MouseOverObject out = this.getMouseOffset(i, event);
            if (out.xOffset == -1 || out.yOffset == -1) continue;
            return out;
        }
        return null;
    }

    @Override
    protected FormGeneralList.MouseOverObject getMouseOffset(int index, InputEvent event) {
        int elementsPerRow = Math.max(1, this.width / this.elementWidth);
        int centerRow = this.width % this.elementWidth / 2;
        int row = index / elementsPerRow;
        int elementY = row * this.elementHeight - this.scroll + 16;
        int elementX = (index - row * elementsPerRow) * this.elementWidth + centerRow;
        int drawX = this.getX() + elementX;
        int drawY = this.getY() + elementY;
        return this.getMouseOffset(index, event, drawX, drawY);
    }

    private FormGeneralList.MouseOverObject getMouseOffset(int index, InputEvent event, int drawX, int drawY) {
        int offsetX = event.pos.hudX - drawX;
        int offsetY = event.pos.hudY - drawY;
        if (offsetX < 0 || offsetX >= this.elementWidth) {
            drawX = -1;
        }
        if (offsetY < 0 || offsetY >= this.elementHeight) {
            drawY = -1;
        }
        return new FormGeneralList.MouseOverObject(this, index, drawX, drawY);
    }

    @Override
    public void limitMaxScroll() {
        int elementsPerRow = Math.max(1, this.width / this.elementWidth);
        int lastRow = (this.elements.size() - 1) / elementsPerRow;
        int maxScroll = Math.max(0, lastRow * this.elementHeight - (this.height - 32 - this.elementHeight));
        if (this.scroll > maxScroll) {
            this.scroll = maxScroll;
        }
    }
}

