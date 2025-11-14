/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components.lists;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.lists.FormListElement;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.HoverStateTextures;

public abstract class FormGeneralList<E extends FormListElement>
extends FormComponent
implements FormPositionContainer {
    private FormPosition position;
    protected int width;
    protected int height;
    protected final int ELEMENT_PADDING = 16;
    protected List<E> elements;
    public final int elementHeight;
    protected int scroll;
    private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
    private int mouseDown;
    private long mouseDownTime;
    private float scrollBuffer;
    protected boolean isHoveringBot;
    protected boolean isHoveringTop;
    protected boolean isHoveringSpace;
    protected boolean acceptMouseRepeatEvents = false;
    protected boolean limitListElementDraw = true;

    public FormGeneralList(int x, int y, int width, int height, int elementHeight) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.height = height;
        this.elementHeight = elementHeight;
        this.reset();
    }

    public void reset() {
        this.elements = new ArrayList();
        this.resetScroll();
    }

    public void resetScroll() {
        this.scroll = 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.isHoveringTop = this.isMouseOverTop(event);
            this.isHoveringBot = this.isMouseOverBot(event);
            this.isHoveringSpace = this.isMouseOverElementSpace(event);
            FormGeneralList formGeneralList = this;
            synchronized (formGeneralList) {
                this.elements.forEach(e -> e.setMoveEvent(null));
                MouseOverObject over = this.getMouseOverObj(event);
                if (over != null) {
                    ((FormListElement)this.elements.get(over.elementIndex)).setMoveEvent(InputEvent.OffsetHudEvent(WindowManager.getWindow().getInput(), event, -over.xOffset, -over.yOffset));
                    event.useMove();
                }
            }
            if (this.isHoveringTop || this.isHoveringBot || this.isHoveringSpace) {
                event.useMove();
            }
        } else if (event.isMouseWheelEvent()) {
            if (event.state && this.isMouseOverElementSpace(event)) {
                this.wheelBuffer.add(event, this.getScrollAmount());
                int amount = this.wheelBuffer.useAllScrollY();
                if (this.scroll(-amount)) {
                    this.playTickSound();
                    event.use();
                    this.handleInputEvent(InputEvent.MouseMoveEvent(event.pos, tickManager), tickManager, perspective);
                }
            }
        } else if (event.isMouseClickEvent() || this.acceptMouseRepeatEvents && event.getID() == -105) {
            if (event.isMouseClickEvent()) {
                if (event.state) {
                    this.mouseDownTime = System.currentTimeMillis() + 250L;
                    if (this.isMouseOverTop(event)) {
                        if (this.scrollUp()) {
                            this.playTickSound();
                        }
                        event.use();
                        this.mouseDown = 1;
                    } else if (this.isMouseOverBot(event)) {
                        if (this.scrollDown()) {
                            this.playTickSound();
                        }
                        event.use();
                        this.mouseDown = -1;
                    }
                } else {
                    this.mouseDown = 0;
                }
            }
            if (!event.isUsed()) {
                FormGeneralList formGeneralList = this;
                synchronized (formGeneralList) {
                    MouseOverObject over = this.getMouseOverObj(event);
                    if (over != null) {
                        if (event.state) {
                            ((FormListElement)this.elements.get(over.elementIndex)).onClick(this, over.elementIndex, InputEvent.OffsetHudEvent(WindowManager.getWindow().getInput(), event, -over.xOffset, -over.yOffset), perspective);
                            event.use();
                        } else {
                            event.use();
                        }
                    }
                }
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
            FormGeneralList formGeneralList = this;
            synchronized (formGeneralList) {
                int startIndex = Math.max(0, (int)Math.ceil((float)this.scroll / (float)this.elementHeight));
                int spaceHeight = this.height - 32;
                int endIndex = Math.min(this.elements.size(), (int)Math.floor((float)(this.scroll + spaceHeight) / (float)this.elementHeight));
                for (int i = startIndex; i < endIndex; ++i) {
                    FormListElement element = (FormListElement)this.elements.get(i);
                    if (element != currentFocus.handler) continue;
                    element.onControllerEvent(this, i, event, tickManager, perspective);
                }
            }
        }
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
        FormGeneralList formGeneralList = this;
        synchronized (formGeneralList) {
            int startIndex = Math.max(0, (int)Math.ceil((float)this.scroll / (float)this.elementHeight));
            int spaceHeight = this.height - 32;
            int endIndex = Math.min(this.elements.size(), (int)Math.floor((float)(this.scroll + spaceHeight) / (float)this.elementHeight));
            for (int i = startIndex; i < endIndex; ++i) {
                FormListElement element = (FormListElement)this.elements.get(i);
                int elementY = i * this.elementHeight - this.scroll + 16;
                ControllerFocus.add(list, area, element, new Rectangle(this.width, this.elementHeight), currentXOffset + this.getX(), currentYOffset + this.getY() + elementY, 0, this.getControllerNavigationHandler(currentXOffset, currentYOffset));
            }
        }
    }

    protected ControllerNavigationHandler getControllerNavigationHandler(int currentXOffset, int currentYOffset) {
        return (dir, event, tickManager, perspective) -> {
            LinkedList<ControllerFocus> list = new LinkedList<ControllerFocus>();
            Rectangle area = new Rectangle(currentXOffset + this.getX(), currentYOffset + this.getY() - this.elementHeight, this.width, this.height + this.elementHeight * 2);
            FormGeneralList formGeneralList = this;
            synchronized (formGeneralList) {
                int startIndex = Math.max(0, (int)Math.ceil((float)this.scroll / (float)this.elementHeight) - 1);
                int spaceHeight = this.height - 32;
                int endIndex = Math.min(this.elements.size(), (int)Math.floor((float)(this.scroll + spaceHeight) / (float)this.elementHeight) + 1);
                for (int i = startIndex; i < endIndex; ++i) {
                    FormListElement element = (FormListElement)this.elements.get(i);
                    int elementY = i * this.elementHeight - this.scroll + 16;
                    ControllerFocus.add(list, area, element, new Rectangle(this.width, this.elementHeight), currentXOffset + this.getX(), currentYOffset + this.getY() + elementY, 0, this.getControllerNavigationHandler(currentXOffset, currentYOffset));
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
            FormGeneralList formGeneralList = this;
            synchronized (formGeneralList) {
                int startIndex = Math.max(0, this.scroll / this.elementHeight);
                int spaceHeight = this.height - 32;
                int rowsPerSpace = spaceHeight / this.elementHeight + (spaceHeight % this.elementHeight == 0 ? 0 : 1);
                int endIndex = Math.min(this.elements.size(), startIndex + rowsPerSpace + (this.scroll % this.elementHeight == 0 ? 0 : 1));
                for (int i = startIndex; i < endIndex; ++i) {
                    FormShader.FormShaderState shaderState;
                    int maxDraw;
                    int minDraw;
                    int elementY = i * this.elementHeight - this.scroll + 16;
                    int drawX = this.getX();
                    int drawY = this.getY() + elementY;
                    if (this.limitListElementDraw) {
                        minDraw = Math.max(0, 16 - elementY);
                        maxDraw = Math.min(this.elementHeight, this.height - elementY - 16) - minDraw;
                        shaderState = GameResources.formShader.startState(new Point(drawX, drawY), new Rectangle(0, minDraw, this.width, maxDraw));
                    } else {
                        minDraw = Math.max(0, 16 - elementY);
                        maxDraw = this.height - elementY - 16 - minDraw;
                        shaderState = GameResources.formShader.startState(new Point(drawX, drawY), new Rectangle(0, minDraw, this.width, maxDraw));
                    }
                    try {
                        ((FormListElement)this.elements.get(i)).draw(this, tickManager, perspective, i);
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
    public List<Rectangle> getHitboxes() {
        return FormGeneralList.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
    }

    protected void handleDrawScroll(TickManager tickManager) {
        if (this.mouseDown > 0 && this.mouseDownTime < System.currentTimeMillis() && this.isHoveringTop) {
            this.scrollBuffer -= tickManager.getDelta() * 0.5f;
        } else if (this.mouseDown < 0 && this.mouseDownTime < System.currentTimeMillis() && this.isHoveringBot) {
            this.scrollBuffer += tickManager.getDelta() * 0.5f;
        }
        int scrollBuffer = (int)this.scrollBuffer;
        if (scrollBuffer != 0) {
            this.scroll(scrollBuffer);
            this.scrollBuffer -= (float)scrollBuffer;
        }
        this.limitMaxScroll();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void drawEmptyMessage(TickManager tickManager) {
        GameMessage message = this.getEmptyMessage();
        if (message != null) {
            FormShader.FormShaderState shaderState = GameResources.formShader.startState(null, new Rectangle(0, 16, this.width, this.height - 32));
            try {
                FontOptions options = new FontOptions(this.getEmptyMessageFontOptions()).color(this.getInterfaceStyle().activeTextColor);
                String s = message.translate();
                ArrayList<String> lines = GameUtils.breakString(s, options, this.width - 20);
                for (int i = 0; i < lines.size(); ++i) {
                    String line = lines.get(i);
                    int lineWidth = FontManager.bit.getWidthCeil(line, options);
                    FontManager.bit.drawString(this.getX() + this.width / 2 - lineWidth / 2, this.getY() + 16 + i * options.getSize() + 4, line, options);
                }
            }
            finally {
                shaderState.end();
            }
        }
    }

    protected void drawScrollButtons(TickManager tickManager) {
        HoverStateTextures buttonTextures = this.getInterfaceStyle().button_navigate_vertical;
        GameTexture topTexture = this.isHoveringTop ? buttonTextures.highlighted : buttonTextures.active;
        Color topColor = this.isHoveringTop ? this.getInterfaceStyle().highlightElementColor : this.getInterfaceStyle().activeElementColor;
        GameTexture botTexture = this.isHoveringBot ? buttonTextures.highlighted : buttonTextures.active;
        Color botColor = this.isHoveringBot ? this.getInterfaceStyle().highlightElementColor : this.getInterfaceStyle().activeElementColor;
        topTexture.initDraw().color(topColor).draw(this.getX() + this.width / 2 - topTexture.getWidth() / 2, this.getY() + 3);
        botTexture.initDraw().color(botColor).mirrorY().draw(this.getX() + this.width / 2 - botTexture.getWidth() / 2, this.getY() + this.height - 3 - botTexture.getHeight());
    }

    protected synchronized MouseOverObject getMouseOverObj(InputEvent event) {
        if (!this.isMouseOverElementSpace(event)) {
            return null;
        }
        int startIndex = Math.max(0, this.scroll / this.elementHeight);
        int spaceHeight = this.height - 32;
        int rowsPerSpace = spaceHeight / this.elementHeight + (spaceHeight % this.elementHeight == 0 ? 0 : 1);
        int endIndex = Math.min(this.elements.size(), startIndex + rowsPerSpace + (this.scroll % this.elementHeight == 0 ? 0 : 1));
        for (int i = startIndex; i < endIndex; ++i) {
            MouseOverObject out = this.getMouseOffset(i, event);
            if (out.xOffset == -1 || out.yOffset == -1) continue;
            return out;
        }
        return null;
    }

    protected synchronized E getMouseOverElement(InputEvent event) {
        MouseOverObject obj = this.getMouseOverObj(event);
        return (E)(obj != null ? (FormListElement)this.elements.get(obj.elementIndex) : null);
    }

    protected MouseOverObject getMouseOffset(int index, InputEvent event) {
        int elementY = index * this.elementHeight - this.scroll + 16;
        int drawX = this.getX();
        int drawY = this.getY() + elementY;
        return this.getMouseOffset(index, event, drawX, drawY);
    }

    private MouseOverObject getMouseOffset(int index, InputEvent event, int drawX, int drawY) {
        int offsetX = event.pos.hudX - drawX;
        int offsetY = event.pos.hudY - drawY;
        if (offsetX < 0 || offsetX >= this.width) {
            drawX = -1;
        }
        if (offsetY < 0 || offsetY >= this.elementHeight) {
            drawY = -1;
        }
        return new MouseOverObject(index, drawX, drawY);
    }

    public boolean isMouseOverElementSpace(InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        return new Rectangle(this.getX(), this.getY() + 16, this.width, this.height - 32).contains(event.pos.hudX, event.pos.hudY);
    }

    public boolean isMouseOverTop(InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        return new Rectangle(this.getX() + this.width / 2 - 16, this.getY() + 3, 32, 10).contains(event.pos.hudX, event.pos.hudY);
    }

    public boolean isMouseOverBot(InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        return new Rectangle(this.getX() + this.width / 2 - 16, this.getY() + this.height - 13, 32, 10).contains(event.pos.hudX, event.pos.hudY);
    }

    public int getScrollAmount() {
        return 20;
    }

    public boolean scrollUp() {
        return this.scroll(-this.getScrollAmount());
    }

    public boolean scrollDown() {
        return this.scroll(this.getScrollAmount());
    }

    public boolean scroll(int amount) {
        int oldScroll = this.scroll;
        this.scroll += amount;
        if (this.scroll < 0) {
            this.scroll = 0;
        }
        this.limitMaxScroll();
        if (oldScroll != this.scroll) {
            WindowManager.getWindow().submitNextMoveEvent();
            return true;
        }
        return false;
    }

    public void limitMaxScroll() {
        int maxScroll = Math.max(0, (this.elements.size() - 1) * this.elementHeight - (this.height - 32 - this.elementHeight));
        if (this.scroll > maxScroll) {
            this.scroll = maxScroll;
        }
    }

    public GameMessage getEmptyMessage() {
        return null;
    }

    public FontOptions getEmptyMessageFontOptions() {
        return new FontOptions(12);
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    protected class MouseOverObject {
        public int elementIndex;
        public int xOffset;
        public int yOffset;

        public MouseOverObject(int elementIndex, int xOffset, int yOffset) {
            this.elementIndex = elementIndex;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }
}

