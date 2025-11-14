/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import necesse.engine.GameLog;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.util.PointTreeSet;
import necesse.engine.util.Zoning;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameBackground;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.forms.ComponentList;
import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.events.FormEventListener;
import necesse.gfx.forms.events.FormEventsHandler;
import necesse.gfx.forms.events.FormMoveEvent;
import necesse.gfx.forms.events.FormResizeEvent;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.shader.FormShader;

public class Form
extends FormComponent
implements FormPositionContainer,
ComponentListContainer<FormComponent> {
    public final String name;
    private final ComponentList<FormComponent> components;
    private boolean hidden;
    private FormPosition position;
    private int width;
    private int height;
    private PointSetAbstract<?> tiles;
    private List<Rectangle> tileBounds;
    private int tileWidth;
    private int tileHeight;
    private int tileXPadding;
    private int tileYPadding;
    private Rectangle draggingBox;
    private boolean showDraggingCursor;
    private boolean isHoveringDraggingBox;
    private InputEvent draggingMouseDown;
    private Point draggingStartPos;
    private boolean resizeUp;
    private boolean resizeDown;
    private boolean resizeLeft;
    private boolean resizeRight;
    protected Dimension resizeMinimum = new Dimension(0, 0);
    protected Dimension resizeMaximum = new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    private ResizeDir resizeDir;
    private InputEvent resizeMouseDown;
    private Point resizeStartPos;
    private Dimension resizeStartDim;
    private GameBackground background = GameBackground.form;
    public boolean shouldLimitDrawArea = true;
    public boolean drawBase;
    public boolean overrideUseBaseAsSize;
    protected FormEventsHandler<FormMoveEvent<Form>> dragEvents = new FormEventsHandler();
    protected FormEventsHandler<FormResizeEvent<Form>> resizeEvents = new FormEventsHandler();

    protected Form(String name, PointSetAbstract<?> tiles, int tileWidth, int tileHeight, int width, int height, int xPadding, int yPadding) {
        this.name = name;
        this.width = width;
        this.height = height;
        if (tiles != null) {
            this.setTiled(tiles, tileWidth, tileHeight, xPadding, yPadding);
        }
        this.position = new FormFixedPosition(0, 0);
        this.components = new ComponentList<FormComponent>((FormComponent)this){

            @Override
            public InputEvent offsetEvent(InputEvent event, boolean allowOutside) {
                int y;
                int x;
                if (!(allowOutside || event.pos.hudX >= Form.this.getX() && event.pos.hudX <= Form.this.getX() + Form.this.getWidth())) {
                    x = Integer.MIN_VALUE;
                } else {
                    x = event.pos.hudX - Form.this.getX();
                    if (Form.this.tiles != null) {
                        x -= Form.this.tileXPadding;
                    }
                }
                if (!(allowOutside || event.pos.hudY >= Form.this.getY() && event.pos.hudY <= Form.this.getY() + Form.this.getHeight())) {
                    y = Integer.MIN_VALUE;
                } else {
                    y = event.pos.hudY - Form.this.getY();
                    if (Form.this.tiles != null) {
                        y -= Form.this.tileYPadding;
                    }
                }
                return InputEvent.ReplacePosEvent(event, InputPosition.fromHudPos(WindowManager.getWindow().getInput(), x, y));
            }

            @Override
            public FormManager getManager() {
                return Form.this.getManager();
            }
        };
        this.drawBase = true;
        this.zIndex = 0;
    }

    public Form(String name, int width, int height) {
        this(name, null, width, height, width, height);
    }

    public Form(int width, int height) {
        this((String)null, width, height);
    }

    public Form(String name, PointSetAbstract<?> tiles, int tileWidth, int tileHeight, int xPadding, int yPadding) {
        this(name, tiles, tileWidth, tileHeight, tileWidth, tileHeight, xPadding, yPadding);
    }

    public Form(PointSetAbstract<?> tiles, int tileWidth, int tileHeight) {
        this(null, tiles, tileWidth, tileHeight);
    }

    public Form(String name, PointSetAbstract<?> tiles, int tileResolution, int padding) {
        this(name, tiles, tileResolution, tileResolution, padding, padding);
    }

    public Form(PointSetAbstract<?> tiles, int tileResolution) {
        this(null, tiles, tileResolution, 0);
    }

    @Override
    protected void init() {
        super.init();
        this.components.init();
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isHidden()) {
            return;
        }
        GameWindow window = WindowManager.getWindow();
        if (event.isMouseMoveEvent()) {
            int deltaY;
            int deltaX;
            this.isHoveringDraggingBox = false;
            if (this.resizeMouseDown != null) {
                deltaX = GameMath.limit(window.mousePos().hudX, 0, window.getHudWidth()) - this.resizeMouseDown.pos.hudX;
                deltaY = GameMath.limit(window.mousePos().hudY, 0, window.getHudHeight()) - this.resizeMouseDown.pos.hudY;
                int newX = this.getX();
                int newY = this.getY();
                int newWidth = this.width;
                int newHeight = this.height;
                if (this.resizeDir == ResizeDir.LEFT || this.resizeDir == ResizeDir.UP_LEFT || this.resizeDir == ResizeDir.DOWN_LEFT) {
                    newX = this.resizeStartPos.x + GameMath.limit(deltaX, this.resizeStartDim.width - this.resizeMaximum.width, this.resizeStartDim.width - this.resizeMinimum.width);
                    newWidth = GameMath.limit(this.resizeStartDim.width - deltaX, this.resizeMinimum.width, this.resizeMaximum.width);
                } else if (this.resizeDir == ResizeDir.RIGHT || this.resizeDir == ResizeDir.UP_RIGHT || this.resizeDir == ResizeDir.DOWN_RIGHT) {
                    newWidth = GameMath.limit(this.resizeStartDim.width + deltaX, this.resizeMinimum.width, this.resizeMaximum.width);
                }
                if (this.resizeDir == ResizeDir.UP || this.resizeDir == ResizeDir.UP_LEFT || this.resizeDir == ResizeDir.UP_RIGHT) {
                    newY = this.resizeStartPos.y + GameMath.limit(deltaY, this.resizeStartDim.height - this.resizeMaximum.height, this.resizeStartDim.height - this.resizeMinimum.height);
                    newHeight = GameMath.limit(this.resizeStartDim.height - deltaY, this.resizeMinimum.height, this.resizeMaximum.height);
                } else if (this.resizeDir == ResizeDir.DOWN || this.resizeDir == ResizeDir.DOWN_LEFT || this.resizeDir == ResizeDir.DOWN_RIGHT) {
                    newHeight = GameMath.limit(this.resizeStartDim.height + deltaY, this.resizeMinimum.height, this.resizeMaximum.height);
                }
                FormResizeEvent<Form> e = new FormResizeEvent<Form>(this, event, newX, newY, newWidth, newHeight);
                this.resizeEvents.onEvent(e);
                if (!e.hasPreventedDefault()) {
                    this.setPosition(e.x, e.y);
                    this.setWidth(e.width);
                    this.setHeight(e.height);
                    event.useMove();
                }
            } else if (this.draggingMouseDown != null) {
                this.isHoveringDraggingBox = false;
                deltaX = window.mousePos().hudX - this.draggingMouseDown.pos.hudX;
                deltaY = window.mousePos().hudY - this.draggingMouseDown.pos.hudY;
                FormMoveEvent<Form> e = new FormMoveEvent<Form>(this, event, this.draggingStartPos.x + deltaX, this.draggingStartPos.y + deltaY);
                this.dragEvents.onEvent(e);
                if (!e.hasPreventedDefault()) {
                    this.setPosition(e.x, e.y);
                    event.useMove();
                }
            }
        }
        if (this.resizeUp || this.resizeDown || this.resizeLeft || this.resizeRight) {
            InputEvent offsetEvent = this.components.offsetEvent(event, true);
            int edgeIn = 2;
            int edgeOut = 6;
            if (!offsetEvent.isMoveUsed() && this.resizeMouseDown == null) {
                this.resizeDir = null;
                if (offsetEvent.pos.hudX >= -edgeOut && offsetEvent.pos.hudX <= this.width + edgeOut && offsetEvent.pos.hudY >= -edgeOut && offsetEvent.pos.hudY <= this.height + edgeOut) {
                    if (this.resizeLeft && offsetEvent.pos.hudX < edgeIn) {
                        this.resizeDir = ResizeDir.LEFT;
                    }
                    if (this.resizeRight && offsetEvent.pos.hudX > this.width - edgeIn - 1) {
                        this.resizeDir = ResizeDir.RIGHT;
                    }
                    if (this.resizeUp && offsetEvent.pos.hudY < edgeIn) {
                        this.resizeDir = this.resizeLeft && offsetEvent.pos.hudX < edgeIn ? ResizeDir.UP_LEFT : (this.resizeRight && offsetEvent.pos.hudX > this.width - edgeIn - 1 ? ResizeDir.UP_RIGHT : ResizeDir.UP);
                    }
                    if (this.resizeDown && offsetEvent.pos.hudY > this.height - edgeIn - 1) {
                        this.resizeDir = this.resizeLeft && offsetEvent.pos.hudX < edgeIn ? ResizeDir.DOWN_LEFT : (this.resizeRight && offsetEvent.pos.hudX > this.width - edgeIn - 1 ? ResizeDir.DOWN_RIGHT : ResizeDir.DOWN);
                    }
                }
                if (this.resizeDir != null) {
                    offsetEvent.useMove();
                }
            }
            if (offsetEvent.getID() == -100) {
                if (!offsetEvent.state) {
                    if (this.resizeMouseDown != null) {
                        this.resizeMouseDown = null;
                        offsetEvent.use();
                    }
                } else if (this.resizeDir != null) {
                    this.resizeMouseDown = InputEvent.MouseMoveEvent(window.mousePos(), tickManager);
                    this.resizeStartPos = new Point(this.getX(), this.getY());
                    this.resizeStartDim = new Dimension(this.getWidth(), this.getHeight());
                    offsetEvent.use();
                }
            }
        } else {
            this.resizeDir = null;
        }
        if (event.isUsed()) {
            return;
        }
        this.components.submitInputEvent(event, tickManager, perspective);
        if (event.isUsed()) {
            return;
        }
        if (this.draggingBox != null) {
            if (event.isMouseMoveEvent()) {
                InputEvent offsetEvent = this.components.offsetEvent(event, false);
                this.isHoveringDraggingBox = !event.isMoveUsed() && this.draggingBox.contains(offsetEvent.pos.hudX, offsetEvent.pos.hudY);
            } else if (event.getID() == -100) {
                InputEvent offsetEvent = this.components.offsetEvent(event, false);
                if (!offsetEvent.state) {
                    if (this.draggingMouseDown != null) {
                        this.draggingMouseDown = null;
                        offsetEvent.use();
                    }
                } else if (this.draggingBox.contains(offsetEvent.pos.hudX, offsetEvent.pos.hudY)) {
                    this.draggingMouseDown = InputEvent.MouseMoveEvent(window.mousePos(), tickManager);
                    this.draggingStartPos = new Point(this.getX(), this.getY());
                    offsetEvent.use();
                }
            }
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (!this.isHidden()) {
            this.components.submitControllerEvent(event, tickManager, perspective);
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (!this.isHidden()) {
            if (this.shouldLimitDrawArea) {
                area = area.intersection(new Rectangle(currentXOffset + this.getX() + this.tileXPadding, currentYOffset + this.getY() + this.tileYPadding, this.width, this.height));
            }
            if (draw) {
                Renderer.drawShape(area, false, 0.0f, 1.0f, 1.0f, 1.0f);
            }
            this.components.addNextControllerComponents(list, currentXOffset + this.getX() + this.tileXPadding, currentYOffset + this.getY() + this.tileYPadding, customNavigationHandler, area, draw);
        }
    }

    public Form onDragged(FormEventListener<FormMoveEvent<Form>> listener) {
        this.dragEvents.addListener(listener);
        return this;
    }

    public Form onResize(FormEventListener<FormResizeEvent<Form>> listener) {
        this.resizeEvents.addListener(listener);
        return this;
    }

    public Form setMinimumResize(int minWidth, int minHeight) {
        if (minWidth > this.resizeMaximum.width || minHeight > this.resizeMaximum.height) {
            GameLog.err.println("Minimum resize size is greater than maximum resize size!");
            return this;
        }
        this.resizeMinimum = new Dimension(minWidth, minHeight);
        return this;
    }

    public Form setMaximumResize(int maxWidth, int maxHeight) {
        if (maxWidth < this.resizeMinimum.width || maxHeight < this.resizeMinimum.height) {
            GameLog.err.println("Maximum resize size is smaller than minimum resize size!");
            return this;
        }
        this.resizeMaximum = new Dimension(maxWidth, maxHeight);
        return this;
    }

    public Form allowResize(FormEventListener<FormResizeEvent<Form>> listener) {
        return this.allowResize(true, true, true, true, listener);
    }

    public Form allowResize(boolean up, boolean down, boolean left, boolean right, FormEventListener<FormResizeEvent<Form>> listener) {
        this.resizeUp = up;
        this.resizeDown = down;
        this.resizeLeft = left;
        this.resizeRight = right;
        this.onResize(listener);
        return this;
    }

    @Override
    public ComponentList<FormComponent> getComponentList() {
        return this.components;
    }

    public void setPosMiddle(int x, int y) {
        this.setPosition(x - this.width / 2, y - this.height / 2);
    }

    public void drawBase(TickManager tickManager) {
        Performance.record((PerformanceTimerManager)tickManager, "base", () -> {
            if (this.background == null) {
                return;
            }
            if (this.tiles != null) {
                this.background.getTiledDrawOptions(this.getX(), this.getY(), this.tileXPadding, this.tileYPadding, this.tiles, this.tileWidth, this.tileHeight).draw();
            } else {
                this.background.getDrawOptions(this.getX(), this.getY(), this.width, this.height).draw();
            }
        });
    }

    public void drawEdge(TickManager tickManager) {
        Performance.record((PerformanceTimerManager)tickManager, "base", () -> {
            if (this.background == null) {
                return;
            }
            if (this.tiles != null) {
                this.background.getTiledEdgeDrawOptions(this.getX(), this.getY(), this.tileXPadding, this.tileYPadding, this.tiles, this.tileWidth, this.tileHeight).draw();
            } else {
                this.background.getEdgeDrawOptions(this.getX(), this.getY(), this.width, this.height).draw();
            }
        });
    }

    public void drawComponents(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        this.components.drawComponents(tickManager, perspective, renderBox);
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Performance.record((PerformanceTimerManager)tickManager, this.name == null ? "form" : this.name, () -> {
            if (this.resizeDir != null) {
                switch (this.resizeDir) {
                    case UP_LEFT: 
                    case DOWN_RIGHT: {
                        Renderer.setCursor(GameWindow.CURSOR.ARROWS_DIAGONAL1);
                        break;
                    }
                    case UP_RIGHT: 
                    case DOWN_LEFT: {
                        Renderer.setCursor(GameWindow.CURSOR.ARROWS_DIAGONAL2);
                        break;
                    }
                    case UP: 
                    case DOWN: {
                        Renderer.setCursor(GameWindow.CURSOR.ARROWS_VERTICAL);
                        break;
                    }
                    case LEFT: 
                    case RIGHT: {
                        Renderer.setCursor(GameWindow.CURSOR.ARROWS_HORIZONTAL);
                    }
                }
            }
            if (this.drawBase) {
                this.drawBase(tickManager);
            }
            Rectangle drawArea = null;
            if (this.shouldLimitDrawArea) {
                drawArea = new Rectangle(this.width, this.height);
            }
            Rectangle bounding = new Rectangle(this.width, this.height);
            Rectangle newRenderBox = renderBox == null ? null : bounding.intersection(new Rectangle(renderBox.x - this.getX(), renderBox.y - this.getY(), renderBox.width, renderBox.height));
            if (this.showDraggingCursor) {
                if (this.draggingMouseDown != null) {
                    Renderer.setCursor(GameWindow.CURSOR.GRAB_ON);
                } else if (this.isHoveringDraggingBox) {
                    Renderer.setCursor(GameWindow.CURSOR.GRAB_OFF);
                }
            }
            int xOffset = this.getX();
            int yOffset = this.getY();
            if (this.tiles != null) {
                xOffset += this.tileXPadding;
                yOffset += this.tileYPadding;
            }
            FormShader.FormShaderState state = GameResources.formShader.startState(new Point(xOffset, yOffset), drawArea);
            try {
                this.drawComponents(tickManager, perspective, newRenderBox);
            }
            finally {
                state.end();
            }
            if (this.drawBase) {
                this.drawEdge(tickManager);
            }
        });
    }

    @Override
    public List<Rectangle> getHitboxes() {
        if (this.isHidden()) {
            return Form.singleBox(new Rectangle(this.getX(), this.getY(), 0, 0));
        }
        if (this.drawBase) {
            if (this.tiles != null) {
                LinkedList<Rectangle> boxes = new LinkedList<Rectangle>();
                for (Rectangle r : this.tileBounds) {
                    boxes.add(new Rectangle(this.getX() + r.x * this.tileWidth - this.tileXPadding - 2, this.getY() + r.y * this.tileHeight - this.tileYPadding - 2, r.width * this.tileWidth + this.tileXPadding * 2 + 4, r.height * this.tileHeight + this.tileYPadding * 2 + 4));
                }
                return boxes;
            }
            return Form.singleBox(new Rectangle(this.getX() - 2, this.getY() - 2, this.width + 4, this.height + 4));
        }
        if (this.overrideUseBaseAsSize) {
            return Form.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
        }
        List<Rectangle> hitboxes = this.components.getHitboxes();
        ArrayList<Rectangle> movedHitboxes = new ArrayList<Rectangle>(hitboxes.size());
        for (Rectangle hitbox : hitboxes) {
            movedHitboxes.add(new Rectangle(this.getX() + hitbox.x, this.getY() + hitbox.y, hitbox.width, hitbox.height));
        }
        return movedHitboxes;
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        if (this.isHidden()) {
            return false;
        }
        if (this.drawBase) {
            if (this.tiles != null) {
                for (Rectangle r : this.tileBounds) {
                    Rectangle rectangle = new Rectangle(this.getX() + r.x * this.tileWidth - this.tileXPadding - 2, this.getY() + r.y * this.tileHeight - this.tileYPadding - 2, r.width * this.tileWidth + this.tileXPadding * 2 + 4, r.height * this.tileHeight + this.tileYPadding * 2 + 4);
                    if (!rectangle.contains(event.pos.hudX, event.pos.hudY)) continue;
                    return true;
                }
            } else {
                return new Rectangle(this.getX() - 2, this.getY() - 2, this.width + 4, this.height + 4).contains(event.pos.hudX, event.pos.hudY);
            }
        }
        return this.components.isMouseOver(event);
    }

    public int getWidth() {
        if (this.tiles != null) {
            return this.width + this.tileXPadding * 2;
        }
        return this.width;
    }

    public int getHeight() {
        if (this.tiles != null) {
            return this.height + this.tileYPadding * 2;
        }
        return this.height;
    }

    public void setWidth(int width) {
        this.width = width;
        this.tiles = null;
        this.tileBounds = null;
    }

    public void setHeight(int height) {
        this.height = height;
        this.tiles = null;
        this.tileBounds = null;
    }

    public void setBackground(GameBackground background) {
        this.background = background;
    }

    public void setTiled(PointSetAbstract<?> tiles, int tileWidth, int tileHeight, int xPadding, int yPadding) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.tileXPadding = xPadding;
        this.tileYPadding = yPadding;
        if (tiles.isEmpty()) {
            this.tiles = new PointHashSet();
            this.tileBounds = new ArrayList<Rectangle>();
            this.width = 0;
            this.height = 0;
        } else {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (Point tile : tiles) {
                if (tile.x < minX) {
                    minX = tile.x;
                }
                if (tile.y < minY) {
                    minY = tile.y;
                }
                if (tile.x > maxX) {
                    maxX = tile.x;
                }
                if (tile.y <= maxY) continue;
                maxY = tile.y;
            }
            PointTreeSet zoning = Zoning.getNewZoneSet();
            for (Point tile : tiles) {
                zoning.add(tile.x - minX, tile.y - minY);
            }
            this.tiles = zoning;
            this.tileBounds = Zoning.toRectangles(zoning);
            this.width = (maxX - minX + 1) * tileWidth;
            this.height = (maxY - minY + 1) * tileHeight;
        }
    }

    public void setTiled(PointSetAbstract<?> tiles, int tileResolution, int padding) {
        this.setTiled(tiles, tileResolution, tileResolution, padding, padding);
    }

    public void setDraggingBox(Rectangle box, boolean showDraggingCursor) {
        this.draggingBox = box;
        if (this.draggingBox == null) {
            this.draggingMouseDown = null;
            this.draggingStartPos = null;
        }
        this.showDraggingCursor = showDraggingCursor;
    }

    public void setDraggingBox(Rectangle box) {
        this.setDraggingBox(box, true);
    }

    public Rectangle getDraggingBox() {
        return this.draggingBox;
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

    public boolean isEmpty() {
        return this.components.isEmpty();
    }

    @Override
    public void dispose() {
        super.dispose();
        this.components.disposeComponents();
    }

    private static enum ResizeDir {
        UP_LEFT,
        UP,
        UP_RIGHT,
        LEFT,
        RIGHT,
        DOWN_LEFT,
        DOWN,
        DOWN_RIGHT;

    }
}

