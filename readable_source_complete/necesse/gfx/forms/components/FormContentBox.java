/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.util.GameMath;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.forms.ComponentList;
import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormScrollEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.forms.presets.ContentBoxListManager;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.HUD;

public class FormContentBox
extends FormComponent
implements FormPositionContainer,
ComponentListContainer<FormComponent> {
    public boolean drawVerticalOnLeft;
    public boolean drawHorizontalOnTop;
    public boolean alwaysShowVerticalScrollBar;
    public boolean alwaysShowHorizontalScrollBar;
    public boolean drawScrollBarOutsideBox = false;
    public boolean shouldLimitDrawArea = true;
    public boolean shouldLimitScrollBarDrawArea = true;
    public boolean hitboxFullSize = true;
    public boolean allowControllerFocusOnScrollbar = true;
    public int controllerScrollPadding = 30;
    private final ControllerFocusHandler scrollXControllerFocus = new ScrollbarControllerFocusHandler();
    private final ControllerFocusHandler scrollYControllerFocus = new ScrollbarControllerFocusHandler();
    private final GameBackground background;
    public float backgroundDrawBrightness = 1.0f;
    private final ComponentList<FormComponent> components;
    private boolean hidden = false;
    private FormPosition position;
    private int width;
    private int height;
    private Rectangle contentBox;
    private int scrollX;
    private int scrollY;
    private final MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(true);
    private boolean isHoveringScrollX;
    private boolean isHoveringScrollY;
    private int scrollMouseX = Integer.MIN_VALUE;
    private int scrollMouseY = Integer.MIN_VALUE;
    private int scrollStartX;
    private int scrollStartY;
    protected FormEventsHandler<FormScrollEvent<FormContentBox>> scrollXChangedEvents;
    protected FormEventsHandler<FormScrollEvent<FormContentBox>> scrollYChangedEvents;

    public FormContentBox(int x, int y, int width, int height) {
        this(x, y, width, height, null);
    }

    public FormContentBox(int x, int y, int width, int height, GameBackground background) {
        this(x, y, width, height, background, new Rectangle(width - (background == null ? 0 : background.getContentPadding() * 2), height - (background == null ? 0 : background.getContentPadding() * 2)));
    }

    public FormContentBox(int x, int y, int width, int height, GameBackground background, Rectangle contentBox) {
        final FormContentBox self = this;
        this.components = new ComponentList<FormComponent>((FormComponent)this){

            @Override
            public InputEvent offsetEvent(InputEvent event, boolean allowOutside) {
                int y;
                int contentOffset;
                int x;
                if (!(allowOutside || event.pos.hudX >= FormContentBox.this.getX() && event.pos.hudX <= FormContentBox.this.getX() + FormContentBox.this.getContentWidth())) {
                    x = Integer.MIN_VALUE;
                } else {
                    contentOffset = self.background == null ? 0 : self.background.getContentPadding();
                    int xOffsetByOtherBar = !FormContentBox.this.drawScrollBarOutsideBox && FormContentBox.this.hasScrollbarY() ? FormContentBox.this.getScrollBarWidth() : 0;
                    x = event.pos.hudX - FormContentBox.this.getX() + ((FormContentBox)self).contentBox.x + FormContentBox.this.scrollX - (FormContentBox.this.drawVerticalOnLeft ? xOffsetByOtherBar : 0) - contentOffset;
                }
                if (!(allowOutside || event.pos.hudY >= FormContentBox.this.getY() && event.pos.hudY <= FormContentBox.this.getY() + FormContentBox.this.getContentHeight())) {
                    y = Integer.MIN_VALUE;
                } else {
                    contentOffset = self.background == null ? 0 : self.background.getContentPadding();
                    int yOffsetByOtherBar = !FormContentBox.this.drawScrollBarOutsideBox && FormContentBox.this.hasScrollbarX() ? FormContentBox.this.getScrollBarWidth() : 0;
                    y = event.pos.hudY - FormContentBox.this.getY() + ((FormContentBox)self).contentBox.y + FormContentBox.this.scrollY - (FormContentBox.this.drawHorizontalOnTop ? yOffsetByOtherBar : 0) - contentOffset;
                }
                return InputEvent.ReplacePosEvent(event, InputPosition.fromHudPos(WindowManager.getWindow().getInput(), x, y));
            }

            @Override
            public FormManager getManager() {
                return FormContentBox.this.getManager();
            }
        };
        this.setPosition(new FormFixedPosition(x, y));
        this.setWidth(width);
        this.setHeight(height);
        this.setContentBox(contentBox);
        this.background = background;
        this.scrollXChangedEvents = new FormEventsHandler();
        this.scrollYChangedEvents = new FormEventsHandler();
    }

    @Override
    protected void init() {
        super.init();
        this.components.init();
    }

    public FormContentBox onScrollXChanged(FormEventListener<FormScrollEvent<FormContentBox>> listener) {
        this.scrollXChangedEvents.addListener(listener);
        return this;
    }

    public FormContentBox onScrollYChanged(FormEventListener<FormScrollEvent<FormContentBox>> listener) {
        this.scrollYChangedEvents.addListener(listener);
        return this;
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        int scrollbarSize;
        int xOffsetByOtherBar;
        boolean hasScrollbarY;
        if (event.getID() == -100) {
            if (event.state) {
                hasScrollbarY = this.hasScrollbarY();
                if (hasScrollbarY && this.scrollMouseX == Integer.MIN_VALUE) {
                    int scrollbarSize2 = this.getScrollbarSize(this.contentBox.height, this.getHeight());
                    int scrollbarPos = this.getScrollbarPos(this.contentBox.height, this.getHeight(), this.getContentHeight(), this.scrollY, scrollbarSize2);
                    if (this.isMouseOverScrollbarY(scrollbarPos, scrollbarSize2, event)) {
                        this.scrollMouseY = event.pos.hudY;
                        this.scrollStartY = this.scrollY;
                        event.use();
                    }
                }
                if (this.hasScrollbarX() && this.scrollMouseY == Integer.MIN_VALUE) {
                    xOffsetByOtherBar = !this.drawScrollBarOutsideBox && hasScrollbarY ? this.getScrollBarWidth() : 0;
                    scrollbarSize = this.getScrollbarSize(this.contentBox.width, this.getWidth() - xOffsetByOtherBar);
                    int scrollbarPos = this.getScrollbarPos(this.contentBox.width, this.getWidth() - xOffsetByOtherBar, this.getContentWidth() - xOffsetByOtherBar, this.scrollX, scrollbarSize);
                    if (this.isMouseOverScrollbarX(scrollbarPos, scrollbarSize, event)) {
                        this.scrollMouseX = event.pos.hudX;
                        this.scrollStartX = this.scrollX;
                        event.use();
                    }
                }
            } else if (this.scrollMouseX != Integer.MIN_VALUE || this.scrollMouseY != Integer.MIN_VALUE) {
                this.scrollMouseX = Integer.MIN_VALUE;
                this.scrollMouseY = Integer.MIN_VALUE;
                event.use();
            }
        } else if (event.isMouseMoveEvent()) {
            float mouseToScroll;
            int difference;
            hasScrollbarY = this.hasScrollbarY();
            int n = xOffsetByOtherBar = !this.drawScrollBarOutsideBox && hasScrollbarY ? this.getScrollBarWidth() : 0;
            if (this.scrollMouseY != Integer.MIN_VALUE && event.pos.hudY != Integer.MIN_VALUE) {
                difference = event.pos.hudY - this.scrollMouseY;
                mouseToScroll = (float)this.contentBox.height / (float)this.getHeight();
                this.scrollY = this.limitScroll(this.contentBox.height, this.getContentHeight(), this.scrollStartY + (int)((float)difference * mouseToScroll));
                this.scrollYChangedEvents.onEvent(new FormScrollEvent<FormContentBox>(this, event, this.scrollY));
            } else if (this.scrollMouseX != Integer.MIN_VALUE && event.pos.hudX != Integer.MIN_VALUE) {
                difference = event.pos.hudX - this.scrollMouseX;
                mouseToScroll = (float)this.contentBox.width / (float)(this.getWidth() - xOffsetByOtherBar);
                this.scrollX = this.limitScroll(this.contentBox.width, this.getContentWidth() - xOffsetByOtherBar, this.scrollStartX + (int)((float)difference * mouseToScroll));
                this.scrollXChangedEvents.onEvent(new FormScrollEvent<FormContentBox>(this, event, this.scrollX));
            }
            if (hasScrollbarY) {
                scrollbarSize = this.getScrollbarSize(this.contentBox.height, this.getHeight());
                int scrollbarPos = this.getScrollbarPos(this.contentBox.height, this.getHeight(), this.getContentHeight(), this.scrollY, scrollbarSize);
                this.isHoveringScrollY = this.isMouseOverScrollbarY(scrollbarPos, scrollbarSize, event);
                if (this.isHoveringScrollY) {
                    event.useMove();
                }
            } else {
                this.isHoveringScrollY = false;
            }
            if (this.hasScrollbarX()) {
                scrollbarSize = this.getScrollbarSize(this.contentBox.width, this.getWidth() - xOffsetByOtherBar);
                int scrollbarPos = this.getScrollbarPos(this.contentBox.width, this.getWidth() - xOffsetByOtherBar, this.getContentWidth() - xOffsetByOtherBar, this.scrollX, scrollbarSize);
                if (this.drawVerticalOnLeft) {
                    scrollbarPos += xOffsetByOtherBar;
                }
                this.isHoveringScrollX = this.isMouseOverScrollbarX(scrollbarPos, scrollbarSize, event);
                if (this.isHoveringScrollX) {
                    event.useMove();
                }
            } else {
                this.isHoveringScrollX = false;
            }
        }
        this.components.submitInputEvent(event, tickManager, perspective);
        if (event.isUsed()) {
            return;
        }
        if (this.isMouseOver(event) && event.isMouseWheelEvent()) {
            int wheelX;
            this.wheelBuffer.add(event, 20.0);
            int wheelY = this.wheelBuffer.useAllScrollY();
            if (wheelY != 0 && this.hasScrollbarY()) {
                int oldScroll = this.scrollY;
                this.scrollY = this.limitScroll(this.contentBox.height, this.getContentHeight(), this.scrollY - wheelY);
                if (oldScroll != this.scrollY) {
                    this.playTickSound();
                    event.use();
                    this.handleInputEvent(InputEvent.MouseMoveEvent(event.pos, tickManager), tickManager, perspective);
                    this.scrollYChangedEvents.onEvent(new FormScrollEvent<FormContentBox>(this, event, this.scrollY));
                }
            }
            if ((wheelX = this.wheelBuffer.useAllScrollX()) != 0 && this.hasScrollbarX()) {
                int oldScroll = this.scrollX;
                boolean hasScrollbarY2 = this.hasScrollbarY();
                int xOffsetByOtherBar2 = !this.drawScrollBarOutsideBox && hasScrollbarY2 ? this.getScrollBarWidth() : 0;
                this.scrollX = this.limitScroll(this.contentBox.width, this.getContentWidth() - xOffsetByOtherBar2, this.scrollX - wheelX);
                if (oldScroll != this.scrollX) {
                    this.playTickSound();
                    event.use();
                    this.handleInputEvent(InputEvent.MouseMoveEvent(event.pos, tickManager), tickManager, perspective);
                    this.scrollXChangedEvents.onEvent(new FormScrollEvent<FormContentBox>(this, event, this.scrollX));
                }
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!this.isHidden()) {
            this.scrollYControllerFocus.handleControllerEvent(event, tickManager, perspective);
            this.scrollXControllerFocus.handleControllerEvent(event, tickManager, perspective);
            this.components.submitControllerEvent(event, tickManager, perspective);
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (!this.isHidden()) {
            ScrollAndContentOffset offset = new ScrollAndContentOffset();
            if (this.allowControllerFocusOnScrollbar) {
                Rectangle hitbox;
                if (this.hasScrollbarY()) {
                    hitbox = this.getScrollbarYHitbox();
                    list.add(new ControllerFocus(this.scrollYControllerFocus, hitbox, currentXOffset, currentYOffset, 0, null));
                }
                if (this.hasScrollbarX()) {
                    hitbox = this.getScrollbarXHitbox();
                    list.add(new ControllerFocus(this.scrollXControllerFocus, hitbox, currentXOffset, currentYOffset, 0, null));
                }
            }
            area = area.intersection(new Rectangle(currentXOffset + this.getX(), currentYOffset + this.getY(), this.width, this.height));
            if (draw) {
                Renderer.drawShape(area, false, 0.0f, 1.0f, 1.0f, 1.0f);
            }
            for (FormComponent c : this.components) {
                if (!c.shouldDraw()) continue;
                c.addNextControllerFocus(list, currentXOffset + offset.xOffset, currentYOffset + offset.yOffset, this.getControllerNavigationHandler(currentXOffset, currentYOffset), area, draw);
            }
        }
    }

    protected ControllerNavigationHandler getControllerNavigationHandler(int currentXOffset, int currentYOffset) {
        return (dir, event, tickManager, perspective) -> {
            LinkedList<ControllerFocus> componentsList = new LinkedList<ControllerFocus>();
            ScrollAndContentOffset offset = new ScrollAndContentOffset();
            Rectangle area = new Rectangle(currentXOffset + offset.xOffset, currentYOffset + offset.yOffset, this.contentBox.width, this.contentBox.height);
            this.components.addNextControllerComponents(componentsList, currentXOffset + offset.xOffset, currentYOffset + offset.yOffset, this.getControllerNavigationHandler(currentXOffset, currentYOffset), area, false);
            ControllerFocus next = ControllerFocus.getNext(dir, this.getManager(), componentsList);
            if (next != null) {
                int currentScrollX = this.scrollX;
                int currentScrollY = this.scrollY;
                Rectangle box = new Rectangle(next.boundingBox.x - (currentXOffset + offset.xOffset), next.boundingBox.y - (currentYOffset + offset.yOffset), next.boundingBox.width, next.boundingBox.height);
                int scrollPadding = this.controllerScrollPadding;
                Rectangle extraBox = new Rectangle(box.x - scrollPadding, box.y - scrollPadding, box.width + scrollPadding * 2, box.height + scrollPadding * 2);
                this.scrollToFit(extraBox);
                int deltaScrollX = this.scrollX - currentScrollX;
                int deltaScrollY = this.scrollY - currentScrollY;
                this.getManager().setControllerFocus(new ControllerFocus(next, -deltaScrollX, -deltaScrollY));
                return true;
            }
            return false;
        };
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        int scrollbarPos;
        int scrollbarSize;
        GameTexture texture;
        Color drawCol;
        Rectangle newRenderBox;
        Rectangle drawArea;
        if (this.background != null) {
            SharedTextureDrawOptions backgroundDrawOptions = this.background.getDrawOptions(this.getX(), this.getY(), this.width, this.height);
            if (this.backgroundDrawBrightness != 1.0f) {
                backgroundDrawOptions.forEachDraw(wrapper -> wrapper.brightness(this.backgroundDrawBrightness));
            }
            backgroundDrawOptions.draw();
        }
        ScrollAndContentOffset offset = new ScrollAndContentOffset();
        if (this.shouldLimitDrawArea) {
            int contentWidth = this.shouldLimitScrollBarDrawArea ? this.getContentWidth() : this.getWidth();
            int contentHeight = this.shouldLimitScrollBarDrawArea ? this.getContentHeight() : this.getHeight();
            drawArea = new Rectangle(this.scrollX + this.contentBox.x, this.scrollY + this.contentBox.y, contentWidth, contentHeight);
        } else {
            GameWindow window = WindowManager.getWindow();
            drawArea = new Rectangle(-offset.xOffset, -offset.yOffset, window.getHudWidth() + offset.xOffset, window.getHudHeight() + offset.yOffset);
        }
        FormShader.FormShaderState shaderState = GameResources.formShader.startState(new Point(offset.xOffset, offset.yOffset), drawArea);
        Rectangle bounding = new Rectangle(this.getContentWidth(), this.getContentHeight());
        if (renderBox == null) {
            newRenderBox = null;
        } else {
            newRenderBox = bounding.intersection(new Rectangle(renderBox.x - this.getX(), renderBox.y - this.getY(), renderBox.width, renderBox.height));
            newRenderBox.setLocation(newRenderBox.x + this.scrollX + this.contentBox.x, newRenderBox.y + this.scrollY + this.contentBox.y);
        }
        try {
            this.components.drawComponents(tickManager, perspective, newRenderBox);
        }
        finally {
            shaderState.end();
        }
        if (offset.hasScrollbarY) {
            drawCol = this.getInterfaceStyle().activeElementColor;
            texture = this.getInterfaceStyle().scrollbar.active;
            scrollbarSize = this.getScrollbarSize(this.contentBox.height, this.getHeight());
            scrollbarPos = this.getScrollbarPos(this.contentBox.height, this.getHeight(), this.getContentHeight(), this.scrollY, scrollbarSize);
            if (this.isHoveringScrollY) {
                drawCol = this.getInterfaceStyle().highlightElementColor;
                texture = this.getInterfaceStyle().scrollbar.highlighted;
            }
            if (this.drawScrollBarOutsideBox) {
                FormContentBox.drawWidthComponent(new GameSprite(texture, 0, 0, texture.getHeight()), new GameSprite(texture, 1, 0, texture.getHeight()), this.getX() + (this.drawVerticalOnLeft ? -this.getScrollBarWidth() : this.getWidth()), this.getY() + scrollbarPos, scrollbarSize, drawCol, true);
            } else {
                FormContentBox.drawWidthComponent(new GameSprite(texture, 0, 0, texture.getHeight()), new GameSprite(texture, 1, 0, texture.getHeight()), this.getX() + (this.drawVerticalOnLeft ? 0 : this.getWidth() - this.getScrollBarWidth()), this.getY() + scrollbarPos, scrollbarSize, drawCol, true);
            }
        }
        if (offset.hasScrollbarX) {
            drawCol = this.getInterfaceStyle().activeElementColor;
            texture = this.getInterfaceStyle().scrollbar.active;
            scrollbarSize = this.getScrollbarSize(this.contentBox.width, this.getWidth() - offset.xOffsetByOtherBar);
            scrollbarPos = this.getScrollbarPos(this.contentBox.width, this.getWidth() - offset.xOffsetByOtherBar, this.getContentWidth() - offset.xOffsetByOtherBar, this.scrollX, scrollbarSize);
            if (this.drawVerticalOnLeft) {
                scrollbarPos += offset.xOffsetByOtherBar;
            }
            if (this.isHoveringScrollX) {
                drawCol = this.getInterfaceStyle().highlightElementColor;
                texture = this.getInterfaceStyle().scrollbar.highlighted;
            }
            if (this.drawScrollBarOutsideBox) {
                FormContentBox.drawWidthComponent(new GameSprite(texture, 0, 0, texture.getHeight()), new GameSprite(texture, 1, 0, texture.getHeight()), this.getX() + scrollbarPos, this.getY() + (this.drawHorizontalOnTop ? -this.getScrollBarWidth() : this.getHeight()), scrollbarSize, drawCol);
            } else {
                FormContentBox.drawWidthComponent(new GameSprite(texture, 0, 0, texture.getHeight()), new GameSprite(texture, 1, 0, texture.getHeight()), this.getX() + scrollbarPos, this.getY() + (this.drawHorizontalOnTop ? 0 : this.getHeight() - this.getScrollBarWidth()), scrollbarSize, drawCol);
            }
        }
        if (this.background != null) {
            SharedTextureDrawOptions edgeDrawOptions = this.background.getEdgeDrawOptions(this.getX(), this.getY(), this.width, this.height);
            if (this.backgroundDrawBrightness != 1.0f) {
                edgeDrawOptions.forEachDraw(wrapper -> wrapper.brightness(this.backgroundDrawBrightness));
            }
            edgeDrawOptions.draw();
        }
    }

    @Override
    public List<Rectangle> getHitboxes() {
        if (this.hitboxFullSize) {
            return FormContentBox.singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
        }
        List<Rectangle> hitboxes = this.components.getHitboxes();
        ScrollAndContentOffset offset = new ScrollAndContentOffset();
        ArrayList<Rectangle> movedHitboxes = new ArrayList<Rectangle>(hitboxes.size() + 2);
        for (Rectangle hitbox : hitboxes) {
            Rectangle offsetHitbox = new Rectangle(hitbox.x + offset.xOffset, hitbox.y + offset.yOffset, hitbox.width, hitbox.height);
            if (this.shouldLimitDrawArea) {
                int contentWidth = this.shouldLimitScrollBarDrawArea ? this.getContentWidth() : this.getWidth();
                int contentHeight = this.shouldLimitScrollBarDrawArea ? this.getContentHeight() : this.getHeight();
                Rectangle drawArea = new Rectangle(offset.xOffset + this.scrollX + this.contentBox.x, offset.yOffset + this.scrollY + this.contentBox.y, contentWidth, contentHeight);
                movedHitboxes.add(drawArea.intersection(offsetHitbox));
                continue;
            }
            movedHitboxes.add(offsetHitbox);
        }
        if (this.hasScrollbarX()) {
            movedHitboxes.add(this.getScrollbarXHitbox());
        } else if (this.hasScrollbarY()) {
            movedHitboxes.add(this.getScrollbarYHitbox());
        }
        return movedHitboxes;
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        if (this.hitboxFullSize) {
            return new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()).contains(event.pos.hudX, event.pos.hudY);
        }
        return this.components.isMouseOver(event);
    }

    protected boolean hasScrollbarX() {
        if (this.alwaysShowHorizontalScrollBar) {
            return true;
        }
        int contentWidth = this.contentBox.width;
        if (this.background != null) {
            contentWidth += this.background.getContentPadding() * 2;
        }
        return contentWidth > this.getWidth();
    }

    protected boolean hasScrollbarY() {
        if (this.alwaysShowVerticalScrollBar) {
            return true;
        }
        int contentHeight = this.contentBox.height;
        if (this.background != null) {
            contentHeight += this.background.getContentPadding() * 2;
        }
        return contentHeight > this.getHeight();
    }

    protected boolean isMouseOverScrollbarX(InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        return this.getScrollbarXHitbox().contains(event.pos.hudX, event.pos.hudY);
    }

    protected boolean isMouseOverScrollbarX(int scrollbarPos, int scrollbarSize, InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        return this.getScrollbarXHitbox(scrollbarPos, scrollbarSize).contains(event.pos.hudX, event.pos.hudY);
    }

    protected Rectangle getScrollbarXHitbox() {
        int offsetByOtherBar = !this.drawScrollBarOutsideBox && this.hasScrollbarY() ? this.getScrollBarWidth() : 0;
        int scrollbarSize = this.getScrollbarSize(this.contentBox.width, this.getWidth() - offsetByOtherBar);
        int scrollbarPos = this.getScrollbarPos(this.contentBox.width, this.getWidth() - offsetByOtherBar, this.getContentWidth() - offsetByOtherBar, this.scrollX, scrollbarSize);
        if (this.drawVerticalOnLeft) {
            scrollbarPos += offsetByOtherBar;
        }
        return this.getScrollbarXHitbox(scrollbarPos, scrollbarSize);
    }

    protected Rectangle getScrollbarXHitbox(int scrollbarPos, int scrollbarSize) {
        if (this.drawScrollBarOutsideBox) {
            if (this.drawHorizontalOnTop) {
                return new Rectangle(this.getX() + scrollbarPos, this.getY() - this.getScrollBarWidth(), scrollbarSize, this.getScrollBarWidth());
            }
            return new Rectangle(this.getX() + scrollbarPos, this.getY() + this.getHeight(), scrollbarSize, this.getScrollBarWidth());
        }
        if (this.drawHorizontalOnTop) {
            return new Rectangle(this.getX() + scrollbarPos, this.getY(), scrollbarSize, this.getScrollBarWidth());
        }
        return new Rectangle(this.getX() + scrollbarPos, this.getY() + this.getHeight() - this.getScrollBarWidth(), scrollbarSize, this.getScrollBarWidth());
    }

    protected boolean isMouseOverScrollbarY(InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        return this.getScrollbarYHitbox().contains(event.pos.hudX, event.pos.hudY);
    }

    protected boolean isMouseOverScrollbarY(int scrollbarPos, int scrollbarSize, InputEvent event) {
        if (event.isMoveUsed()) {
            return false;
        }
        return this.getScrollbarYHitbox(scrollbarPos, scrollbarSize).contains(event.pos.hudX, event.pos.hudY);
    }

    protected Rectangle getScrollbarYHitbox() {
        int scrollbarSize = this.getScrollbarSize(this.contentBox.height, this.getHeight());
        int scrollbarPos = this.getScrollbarPos(this.contentBox.height, this.getHeight(), this.getContentHeight(), this.scrollY, scrollbarSize);
        return this.getScrollbarYHitbox(scrollbarPos, scrollbarSize);
    }

    protected Rectangle getScrollbarYHitbox(int scrollbarPos, int scrollbarSize) {
        if (this.drawScrollBarOutsideBox) {
            if (this.drawVerticalOnLeft) {
                return new Rectangle(this.getX() - this.getScrollBarWidth(), this.getY() + scrollbarPos, this.getScrollBarWidth(), scrollbarSize);
            }
            return new Rectangle(this.getX() + this.getWidth(), this.getY() + scrollbarPos, this.getScrollBarWidth(), scrollbarSize);
        }
        if (this.drawVerticalOnLeft) {
            return new Rectangle(this.getX(), this.getY() + scrollbarPos, this.getScrollBarWidth(), scrollbarSize);
        }
        return new Rectangle(this.getX() + this.getWidth() - this.getScrollBarWidth(), this.getY() + scrollbarPos, this.getScrollBarWidth(), scrollbarSize);
    }

    protected int getScrollbarSize(int contentSize, int windowSize) {
        if (contentSize <= windowSize) {
            return windowSize;
        }
        return (int)Math.max((float)windowSize / (float)contentSize * (float)windowSize, (float)Math.min(20, windowSize));
    }

    protected int getScrollbarPos(int contentSize, int windowSize, int contentWindowSize, int scroll, int barSize) {
        if (contentSize <= windowSize) {
            return 0;
        }
        float scrollPerc = (float)scroll / (float)(contentSize - contentWindowSize);
        return (int)(scrollPerc * (float)(windowSize - barSize));
    }

    protected int limitScroll(int contentSize, int windowSize, int scroll) {
        return Math.max(0, Math.min(contentSize - windowSize, scroll));
    }

    public void scrollToFitForced(Rectangle content) {
        int minX = content.x;
        int minY = content.y;
        int maxX = content.x + content.width - this.getContentWidth();
        int maxY = content.y + content.height - this.getContentHeight();
        this.setScrollX(GameMath.limit(this.scrollX, maxX, minX));
        this.setScrollY(GameMath.limit(this.scrollY, maxY, minY));
    }

    public void scrollToFit(Rectangle content) {
        int minX = content.x;
        int minY = content.y;
        int maxX = content.x + content.width - this.getContentWidth();
        int maxY = content.y + content.height - this.getContentHeight();
        if (this.hasScrollbarX()) {
            int xOffsetByOtherBar = !this.drawScrollBarOutsideBox && this.hasScrollbarY() ? this.getScrollBarWidth() : 0;
            this.scrollX = this.limitScroll(this.contentBox.width, this.getContentWidth() - xOffsetByOtherBar, GameMath.limit(this.scrollX, maxX, minX));
        }
        if (this.hasScrollbarY()) {
            this.scrollY = this.limitScroll(this.contentBox.height, this.getContentHeight(), GameMath.limit(this.scrollY, maxY, minY));
        }
    }

    public void setScrollX(int x) {
        this.scrollX = this.limitScroll(this.contentBox.width, this.getContentWidth(), x);
    }

    public int getScrollX() {
        return this.scrollX;
    }

    public void scrollX(int deltaX) {
        this.setScrollX(this.getScrollX() + deltaX);
    }

    public void setScrollY(int y) {
        this.scrollY = this.limitScroll(this.contentBox.height, this.getContentHeight(), y);
    }

    public void scrollY(int deltaY) {
        this.setScrollY(this.getScrollY() + deltaY);
    }

    public int getScrollY() {
        return this.scrollY;
    }

    public void setScroll(int x, int y) {
        this.setScrollX(x);
        this.setScrollY(y);
    }

    @Override
    public ComponentList<FormComponent> getComponentList() {
        return this.components;
    }

    public void setContentBox(Rectangle contentBox) {
        this.contentBox = contentBox;
        this.scrollX = this.limitScroll(contentBox.width, this.getContentWidth(), this.scrollX);
        this.scrollY = this.limitScroll(contentBox.height, this.getContentHeight(), this.scrollY);
    }

    public Rectangle getContentBox() {
        return new Rectangle(this.contentBox);
    }

    public void fitContentBoxToComponents(int leftPadding, int rightPadding, int topPadding, int botPadding) {
        Rectangle box = this.getContentBoxToFitComponents();
        box.x -= leftPadding;
        box.y -= topPadding;
        box.width += rightPadding * 2;
        box.height += botPadding * 2;
        this.setContentBox(box);
    }

    public void fitContentBoxToComponents(int padding) {
        this.fitContentBoxToComponents(padding, padding, padding, padding);
    }

    public void fitContentBoxToComponents() {
        this.fitContentBoxToComponents(0);
    }

    public void centerContentHorizontal() {
        Rectangle box = new Rectangle(this.contentBox);
        if (box.width < this.width) {
            int offset = (this.width - box.width) / 2;
            box.x -= offset;
            box.width += offset;
        }
        this.setContentBox(box);
    }

    public void centerContentVertical() {
        Rectangle box = new Rectangle(this.contentBox);
        if (box.height < this.height) {
            int offset = (this.height - box.height) / 2;
            box.y -= offset;
            box.height += offset;
        }
        this.setContentBox(box);
    }

    public Rectangle getContentBoxToFitComponents() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        if (this.components.getComponents().isEmpty()) {
            minX = 0;
            minY = 0;
            maxX = 0;
            maxY = 0;
        } else {
            for (FormComponent comp : this.components.getComponents()) {
                Rectangle bb = comp.getBoundingBox();
                if (bb.x < minX) {
                    minX = bb.x;
                }
                if (bb.y < minY) {
                    minY = bb.y;
                }
                if (bb.x + bb.width > maxX) {
                    maxX = bb.x + bb.width;
                }
                if (bb.y + bb.height <= maxY) continue;
                maxY = bb.y + bb.height;
            }
        }
        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }

    public ContentBoxListManager listManager() {
        return new ContentBoxListManager(this);
    }

    public int getScrollBarWidth() {
        return this.getInterfaceStyle().scrollbar.active.getHeight();
    }

    public int getMinContentWidth() {
        int scrollbarWidth;
        int width = this.getWidth();
        int n = scrollbarWidth = this.drawScrollBarOutsideBox ? 0 : this.getScrollBarWidth();
        width = this.background != null ? (width -= Math.max(this.background.getContentPadding() * 2, scrollbarWidth)) : (width -= scrollbarWidth);
        return width;
    }

    public int getMinContentHeight() {
        int scrollbarHeight;
        int height = this.getHeight();
        int n = scrollbarHeight = this.drawScrollBarOutsideBox ? 0 : this.getScrollBarWidth();
        height = this.background != null ? (height -= Math.max(this.background.getContentPadding() * 2, scrollbarHeight)) : (height -= scrollbarHeight);
        return height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getContentWidth() {
        int width = this.getWidth();
        if (!this.drawScrollBarOutsideBox && this.hasScrollbarY()) {
            width = this.background != null ? (width -= Math.max(this.background.getContentPadding() * 2, this.getScrollBarWidth())) : (width -= this.getScrollBarWidth());
        } else if (this.background != null) {
            width -= this.background.getContentPadding() * 2;
        }
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        if (this.contentBox != null) {
            this.scrollX = this.limitScroll(this.contentBox.width, this.getContentWidth(), this.scrollX);
        }
    }

    public int getHeight() {
        return this.height;
    }

    public int getContentHeight() {
        int height = this.getHeight();
        if (!this.drawScrollBarOutsideBox && this.hasScrollbarX()) {
            height = this.background != null ? (height -= Math.max(this.background.getContentPadding() * 2, this.getScrollBarWidth())) : (height -= this.getScrollBarWidth());
        } else if (this.background != null) {
            height -= this.background.getContentPadding() * 2;
        }
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        if (this.contentBox != null) {
            this.scrollY = this.limitScroll(this.contentBox.height, this.getContentHeight(), this.scrollY);
        }
    }

    @Override
    public FormPosition getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(FormPosition position) {
        this.position = position;
    }

    @Override
    public boolean shouldDraw() {
        return !this.isHidden();
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        if (this.hidden != hidden) {
            this.hidden = hidden;
            WindowManager.getWindow().submitNextMoveEvent();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        this.components.disposeComponents();
    }

    private class ScrollbarControllerFocusHandler
    implements ControllerFocusHandler {
        private boolean isSelected;

        @Override
        public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (event.getState() == ControllerInput.MENU_SELECT && FormContentBox.this.isControllerFocus(this)) {
                this.isSelected = true;
                event.use();
            } else if (this.isSelected && event.getState() == ControllerInput.MENU_BACK && FormContentBox.this.isControllerFocus(this)) {
                this.isSelected = false;
                event.use();
            }
        }

        @Override
        public boolean handleControllerNavigate(int dir, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
            if (this.isSelected) {
                if (FormContentBox.this.hasScrollbarY() && (dir == 0 || dir == 2)) {
                    int scroll = dir == 0 ? -20 : 20;
                    int oldScroll = FormContentBox.this.scrollY;
                    FormContentBox.this.scrollY = FormContentBox.this.limitScroll(((FormContentBox)FormContentBox.this).contentBox.height, FormContentBox.this.getContentHeight(), FormContentBox.this.scrollY + scroll);
                    if (oldScroll != FormContentBox.this.scrollY) {
                        FormContentBox.this.playTickSound();
                        event.use();
                        WindowManager.getWindow().submitNextMoveEvent();
                    }
                } else if (FormContentBox.this.hasScrollbarX() && (dir == 1 || dir == 3)) {
                    int scroll = dir == 1 ? 20 : -20;
                    int oldScroll = FormContentBox.this.scrollX;
                    boolean hasScrollbarY = FormContentBox.this.hasScrollbarY();
                    int xOffsetByOtherBar = !FormContentBox.this.drawScrollBarOutsideBox && hasScrollbarY ? FormContentBox.this.getScrollBarWidth() : 0;
                    FormContentBox.this.scrollX = FormContentBox.this.limitScroll(((FormContentBox)FormContentBox.this).contentBox.width, FormContentBox.this.getContentWidth() - xOffsetByOtherBar, FormContentBox.this.scrollX + scroll);
                    if (oldScroll != FormContentBox.this.scrollX) {
                        FormContentBox.this.playTickSound();
                        event.use();
                        WindowManager.getWindow().submitNextMoveEvent();
                    }
                }
                return true;
            }
            return false;
        }

        @Override
        public void drawControllerFocus(ControllerFocus current) {
            if (this.isSelected) {
                Rectangle box = current.boundingBox;
                int padding = 5;
                box = new Rectangle(box.x - padding, box.y - padding, box.width + padding * 2, box.height + padding * 2);
                HUD.selectBoundOptions(FormContentBox.this.getInterfaceStyle().controllerFocusBoundsHighlightColor, true, box).draw();
            } else {
                ControllerFocusHandler.super.drawControllerFocus(current);
            }
        }
    }

    protected class ScrollAndContentOffset {
        public boolean hasScrollbarY;
        public boolean hasScrollbarX;
        public int xOffsetByOtherBar;
        public int yOffsetByOtherBar;
        public int contentOffset;
        public int xOffset;
        public int yOffset;

        protected ScrollAndContentOffset() {
            this.hasScrollbarY = FormContentBox.this.hasScrollbarY();
            this.hasScrollbarX = FormContentBox.this.hasScrollbarX();
            this.xOffsetByOtherBar = !FormContentBox.this.drawScrollBarOutsideBox && this.hasScrollbarY ? FormContentBox.this.getScrollBarWidth() : 0;
            this.yOffsetByOtherBar = !FormContentBox.this.drawScrollBarOutsideBox && this.hasScrollbarX ? FormContentBox.this.getScrollBarWidth() : 0;
            this.contentOffset = FormContentBox.this.background == null ? 0 : FormContentBox.this.background.getContentPadding();
            this.xOffset = FormContentBox.this.getX() - FormContentBox.this.scrollX - ((FormContentBox)FormContentBox.this).contentBox.x + (FormContentBox.this.drawVerticalOnLeft ? this.xOffsetByOtherBar : 0) + this.contentOffset;
            this.yOffset = FormContentBox.this.getY() - FormContentBox.this.scrollY - ((FormContentBox)FormContentBox.this).contentBox.y + (FormContentBox.this.drawHorizontalOnTop ? this.yOffsetByOtherBar : 0) + this.contentOffset;
        }
    }
}

