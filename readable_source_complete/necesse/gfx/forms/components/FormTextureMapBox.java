/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL14
 */
package necesse.gfx.forms.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameMath;
import necesse.engine.util.Zoning;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormFixedPosition;
import necesse.gfx.forms.position.FormPosition;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.gameFont.FontManager;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.shader.FormShader;
import necesse.gfx.ui.HUD;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public abstract class FormTextureMapBox
extends FormComponent
implements FormPositionContainer {
    private static final int zoomFadeTime = 1000;
    public Color backgroundColor = new Color(50, 50, 50);
    public Color notDiscoveredColor = new Color(0, 0, 0);
    private FormPosition position;
    private int width;
    private int height;
    private final int textureTileSize;
    private boolean hidden;
    protected final int[] zoomLevels;
    protected int zoomLevel;
    protected long zoomPressTime;
    protected boolean centered;
    protected boolean mouseDown;
    protected int centerX;
    protected int centerY;
    protected int startX;
    protected int startY;
    protected int mouseStartX;
    protected int mouseStartY;
    protected final MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);
    protected boolean allowControllerFocus;
    protected boolean isControllerSelected;
    protected double controllerScrollXBuffer;
    protected double controllerScrollYBuffer;
    protected boolean isHovering;
    protected InputEvent lastMoveEvent;
    protected Rectangle screenshotBounds;
    public Dimension minScreenshotSize = new Dimension(640, 640);
    public Dimension maxScreenshotSize = new Dimension(6400, 6400);
    protected BoundsDir screenshotResizeDir;
    protected Point screenshotResizeStartPos;
    protected Point screenshotDragStartPos;
    protected Rectangle screenshotChangeStartBounds;
    protected boolean isControllerScreenshotResizing;

    public FormTextureMapBox(int x, int y, int width, int height, int textureTileSize, int[] zoomLevels, int zoomLevel, boolean allowControllerFocus) {
        this.position = new FormFixedPosition(x, y);
        this.width = width;
        this.height = height;
        this.textureTileSize = textureTileSize;
        this.zoomLevels = zoomLevels;
        this.zoomLevel = Math.abs(zoomLevel) % this.zoomLevels.length;
        this.allowControllerFocus = allowControllerFocus;
    }

    public abstract int getTileScale();

    public abstract Rectangle getTileBounds();

    public abstract void drawMapTexture(int var1, int var2, double var3, int var5, int var6);

    public void setupMapDraw(int startTileX, int startTileY, int endTileX, int endTileY) {
    }

    public void drawMapOverlays(TickManager tickManager, PlayerMob perspective, int scale, double tileScale, double resHalfX, double resHalfY, int mouseHudX, int mouseHudY) {
    }

    public void onZoomLevelChanged(int zoomLevel) {
    }

    @Override
    public void handleInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        int mouseHudX;
        if (event.isMouseMoveEvent()) {
            int deltaY;
            int mouseHudY;
            int deltaX;
            int tileScale;
            Rectangle tileBounds;
            this.lastMoveEvent = null;
            this.isHovering = this.isMouseOver(event);
            if (this.isHovering) {
                event.useMove();
                this.lastMoveEvent = event;
            }
            if (this.isHovering && this.mouseDown) {
                tileBounds = this.getTileBounds();
                tileScale = this.getTileScale();
                if (event.pos.hudX != Integer.MIN_VALUE) {
                    this.centerX = this.startX + (this.mouseStartX - event.pos.hudX) * this.zoomLevels[this.zoomLevel];
                    if (tileBounds != null && tileBounds.width > 0) {
                        this.centerX = Math.max(tileBounds.x * tileScale, Math.min(tileBounds.width * tileScale, this.centerX));
                    }
                }
                if (event.pos.hudY != Integer.MIN_VALUE) {
                    this.centerY = this.startY + (this.mouseStartY - event.pos.hudY) * this.zoomLevels[this.zoomLevel];
                    if (tileBounds != null && tileBounds.height > 0) {
                        this.centerY = Math.max(tileBounds.y * tileScale, Math.min(tileBounds.height * tileScale, this.centerY));
                    }
                }
            }
            if (this.screenshotResizeStartPos != null) {
                if (this.screenshotBounds != null) {
                    tileBounds = this.getTileBounds();
                    tileScale = this.getTileScale();
                    if (event.pos.hudX != Integer.MIN_VALUE) {
                        mouseHudX = event.pos.hudX - this.getX();
                        deltaX = this.getLevelSize(mouseHudX - this.screenshotResizeStartPos.x);
                        int newX = this.screenshotBounds.x;
                        int newWidth = this.screenshotBounds.width;
                        if (this.screenshotResizeDir == BoundsDir.LEFT || this.screenshotResizeDir == BoundsDir.UP_LEFT || this.screenshotResizeDir == BoundsDir.DOWN_LEFT) {
                            newX = this.screenshotChangeStartBounds.x + GameMath.limit(deltaX, this.screenshotChangeStartBounds.width - this.maxScreenshotSize.width, this.screenshotChangeStartBounds.width - this.minScreenshotSize.width);
                            newWidth = GameMath.limit(this.screenshotChangeStartBounds.width - deltaX, this.minScreenshotSize.width, this.maxScreenshotSize.width);
                        } else if (this.screenshotResizeDir == BoundsDir.RIGHT || this.screenshotResizeDir == BoundsDir.UP_RIGHT || this.screenshotResizeDir == BoundsDir.DOWN_RIGHT) {
                            newWidth = GameMath.limit(this.screenshotChangeStartBounds.width + deltaX, this.minScreenshotSize.width, this.maxScreenshotSize.width);
                        }
                        if (tileBounds != null && tileBounds.width > 0) {
                            if (newX < tileBounds.x * tileScale) {
                                newWidth -= tileBounds.x * tileScale - newX;
                                newX = tileBounds.x * tileScale;
                            }
                            if (newX + newWidth > (tileBounds.x + tileBounds.width) * tileScale) {
                                newWidth = (tileBounds.x + tileBounds.width) * tileScale - newX;
                            }
                        }
                        this.screenshotBounds.x = newX;
                        this.screenshotBounds.width = newWidth;
                    }
                    if (event.pos.hudY != Integer.MIN_VALUE) {
                        mouseHudY = event.pos.hudY - this.getY();
                        deltaY = this.getLevelSize(mouseHudY - this.screenshotResizeStartPos.y);
                        int newY = this.screenshotBounds.y;
                        int newHeight = this.screenshotBounds.height;
                        if (this.screenshotResizeDir == BoundsDir.UP || this.screenshotResizeDir == BoundsDir.UP_LEFT || this.screenshotResizeDir == BoundsDir.UP_RIGHT) {
                            newY = this.screenshotChangeStartBounds.y + GameMath.limit(deltaY, this.screenshotChangeStartBounds.height - this.maxScreenshotSize.height, this.screenshotChangeStartBounds.height - this.minScreenshotSize.height);
                            newHeight = GameMath.limit(this.screenshotChangeStartBounds.height - deltaY, this.minScreenshotSize.height, this.maxScreenshotSize.height);
                        } else if (this.screenshotResizeDir == BoundsDir.DOWN || this.screenshotResizeDir == BoundsDir.DOWN_LEFT || this.screenshotResizeDir == BoundsDir.DOWN_RIGHT) {
                            newHeight = GameMath.limit(this.screenshotChangeStartBounds.height + deltaY, this.minScreenshotSize.height, this.maxScreenshotSize.height);
                        }
                        if (tileBounds != null && tileBounds.height > 0) {
                            if (newY < tileBounds.y * tileScale) {
                                newHeight -= tileBounds.y * tileScale - newY;
                                newY = tileBounds.y * tileScale;
                            }
                            if (newY + newHeight > (tileBounds.y + tileBounds.height) * tileScale) {
                                newHeight = (tileBounds.y + tileBounds.height) * tileScale - newY;
                            }
                        }
                        this.screenshotBounds.y = newY;
                        this.screenshotBounds.height = newHeight;
                    }
                    event.useMove();
                } else {
                    this.screenshotResizeStartPos = null;
                    this.screenshotChangeStartBounds = null;
                }
            } else if (this.screenshotDragStartPos != null) {
                if (this.screenshotBounds != null) {
                    tileBounds = this.getTileBounds();
                    tileScale = this.getTileScale();
                    if (event.pos.hudX != Integer.MIN_VALUE) {
                        mouseHudX = event.pos.hudX - this.getX();
                        deltaX = this.getLevelSize(mouseHudX - this.screenshotDragStartPos.x);
                        this.screenshotBounds.x = this.screenshotChangeStartBounds.x + deltaX;
                        if (tileBounds != null && tileBounds.width > 0) {
                            this.screenshotBounds.x = GameMath.limit(this.screenshotBounds.x, tileBounds.x * tileScale, tileBounds.width * tileScale - this.screenshotBounds.width);
                        }
                    }
                    if (event.pos.hudY != Integer.MIN_VALUE) {
                        mouseHudY = event.pos.hudY - this.getY();
                        deltaY = this.getLevelSize(mouseHudY - this.screenshotDragStartPos.y);
                        this.screenshotBounds.y = this.screenshotChangeStartBounds.y + deltaY;
                        if (tileBounds != null && tileBounds.height > 0) {
                            this.screenshotBounds.y = GameMath.limit(this.screenshotBounds.y, tileBounds.y * tileScale, tileBounds.height * tileScale - this.screenshotBounds.height);
                        }
                    }
                } else {
                    this.screenshotDragStartPos = null;
                    this.screenshotChangeStartBounds = null;
                }
            }
        }
        if (this.screenshotChangeStartBounds == null && this.screenshotBounds != null) {
            this.screenshotResizeDir = null;
            if (this.isHovering) {
                int edgeIn = 8;
                int edgeOut = 8;
                mouseHudX = event.pos.hudX - this.getX();
                int mouseHudY = event.pos.hudY - this.getY();
                int boundsX = mouseHudX - this.getHudPosX(this.screenshotBounds.x);
                int boundsY = mouseHudY - this.getHudPosY(this.screenshotBounds.y);
                int boundsWidth = this.getHudSize(this.screenshotBounds.width);
                int boundsHeight = this.getHudSize(this.screenshotBounds.height);
                if (boundsX >= -edgeOut && boundsX <= boundsWidth + edgeOut && boundsY >= -edgeOut && boundsY <= boundsHeight + edgeOut) {
                    if (boundsX < edgeIn) {
                        this.screenshotResizeDir = BoundsDir.LEFT;
                    }
                    if (boundsX > boundsWidth - edgeIn - 1) {
                        this.screenshotResizeDir = BoundsDir.RIGHT;
                    }
                    if (boundsY < edgeIn) {
                        this.screenshotResizeDir = boundsX < edgeIn ? BoundsDir.UP_LEFT : (boundsX > boundsWidth - edgeIn - 1 ? BoundsDir.UP_RIGHT : BoundsDir.UP);
                    }
                    if (boundsY > boundsHeight - edgeIn - 1) {
                        this.screenshotResizeDir = boundsX < edgeIn ? BoundsDir.DOWN_LEFT : (boundsX > boundsWidth - edgeIn - 1 ? BoundsDir.DOWN_RIGHT : BoundsDir.DOWN);
                    }
                }
            }
        }
        if (event.isKeyboardEvent()) {
            return;
        }
        if (this.mouseDown && !event.state && event.getID() == -100) {
            this.mouseDown = false;
            event.use();
        }
        if (this.screenshotResizeStartPos != null && !event.state && event.getID() == -100) {
            this.screenshotResizeStartPos = null;
            this.screenshotChangeStartBounds = null;
            event.use();
        }
        if (this.screenshotDragStartPos != null && !event.state && event.getID() == -100) {
            this.screenshotDragStartPos = null;
            this.screenshotChangeStartBounds = null;
            event.use();
        }
        if (this.isMouseOver(event) && !event.isUsed()) {
            if (event.state && event.isMouseWheelEvent()) {
                this.wheelBuffer.add(event);
                this.wheelBuffer.useScrollY(isPositive -> {
                    if (isPositive) {
                        this.zoomIn(event.pos.hudX, event.pos.hudY);
                    } else {
                        this.zoomOut(event.pos.hudX, event.pos.hudY);
                    }
                });
                event.use();
            }
            if (event.state && event.getID() == -100) {
                if (this.screenshotResizeDir != null) {
                    this.screenshotResizeStartPos = new Point(event.pos.hudX - this.getX(), event.pos.hudY - this.getY());
                    this.screenshotChangeStartBounds = new Rectangle(this.screenshotBounds);
                    event.use();
                } else {
                    if (this.screenshotBounds != null) {
                        int mouseLevelY;
                        int mouseHudX2 = event.pos.hudX - this.getX();
                        int mouseHudY = event.pos.hudY - this.getY();
                        int mouseLevelX = this.getLevelPosX(mouseHudX2);
                        if (this.screenshotBounds.contains(mouseLevelX, mouseLevelY = this.getLevelPosY(mouseHudY))) {
                            this.screenshotDragStartPos = new Point(new Point(event.pos.hudX - this.getX(), event.pos.hudY - this.getY()));
                            this.screenshotChangeStartBounds = new Rectangle(this.screenshotBounds);
                        }
                    }
                    if (this.screenshotDragStartPos == null) {
                        this.mouseDown = true;
                        this.setCentered(false);
                        this.mouseStartX = event.pos.hudX;
                        this.mouseStartY = event.pos.hudY;
                        this.startX = this.centerX;
                        this.startY = this.centerY;
                    }
                    event.use();
                }
            }
        }
        if (this.screenshotResizeDir != null) {
            event.useMove();
        }
    }

    @Override
    public void handleControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isUsed()) {
            return;
        }
        if (event.getState() == ControllerInput.MENU_SELECT) {
            if (this.isControllerFocus() && event.buttonState) {
                if (this.isControllerSelected) {
                    if (this.screenshotBounds != null) {
                        this.isControllerScreenshotResizing = !this.isControllerScreenshotResizing;
                    }
                } else {
                    this.isControllerSelected = true;
                    event.use();
                    this.playTickSound();
                }
            }
        } else if (event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU) {
            if (this.isControllerSelected && event.buttonState) {
                this.isControllerSelected = false;
                event.use();
                this.playTickSound();
            }
        } else if (event.getState() == ControllerInput.MENU_NEXT) {
            if (this.isControllerSelected && event.buttonState) {
                this.zoomIn();
                event.use();
                this.playTickSound();
            }
        } else if (event.getState() == ControllerInput.MENU_PREV && this.isControllerSelected && event.buttonState) {
            this.zoomOut();
            event.use();
            this.playTickSound();
        }
    }

    @Override
    public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
        if (this.allowControllerFocus) {
            ControllerFocus.add(list, area, this, this.getBoundingBox(), currentXOffset, currentYOffset, this.controllerInitialFocusPriority, customNavigationHandler);
        }
    }

    @Override
    public void onControllerUnfocused(ControllerFocus current) {
        super.onControllerUnfocused(current);
        this.isControllerSelected = false;
    }

    @Override
    public boolean handleControllerNavigate(int dir, ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        if (this.isControllerSelected) {
            Rectangle tileBounds = this.getTileBounds();
            int tileScale = this.getTileScale();
            if (this.screenshotBounds != null) {
                switch (dir) {
                    case 0: {
                        if (this.isControllerScreenshotResizing) {
                            int change = 10 * this.zoomLevels[this.zoomLevel];
                            change = Math.min(change, Math.max(this.maxScreenshotSize.height - this.screenshotBounds.height, 0));
                            if (tileBounds != null && tileBounds.height > 0) {
                                change = Math.min(change, Math.max(tileBounds.height * tileScale - this.screenshotBounds.height, 0));
                            }
                            this.screenshotBounds.y -= change / 2;
                            this.screenshotBounds.height += change;
                            break;
                        }
                        this.screenshotBounds.y -= 10 * this.zoomLevels[this.zoomLevel];
                        break;
                    }
                    case 1: {
                        if (this.isControllerScreenshotResizing) {
                            int change = 10 * this.zoomLevels[this.zoomLevel];
                            change = Math.min(change, Math.max(this.maxScreenshotSize.width - this.screenshotBounds.width, 0));
                            if (tileBounds != null && tileBounds.width > 0) {
                                change = Math.min(change, Math.max(tileBounds.width * tileScale - this.screenshotBounds.width, 0));
                            }
                            this.screenshotBounds.x -= change / 2;
                            this.screenshotBounds.width += change;
                            break;
                        }
                        this.screenshotBounds.x += 10 * this.zoomLevels[this.zoomLevel];
                        break;
                    }
                    case 2: {
                        if (this.isControllerScreenshotResizing) {
                            int change = 10 * this.zoomLevels[this.zoomLevel];
                            change = Math.min(change, Math.max(this.screenshotBounds.height - this.minScreenshotSize.height, 0));
                            this.screenshotBounds.y += change / 2;
                            this.screenshotBounds.height -= change;
                            break;
                        }
                        this.screenshotBounds.y += 10 * this.zoomLevels[this.zoomLevel];
                        break;
                    }
                    case 3: {
                        if (this.isControllerScreenshotResizing) {
                            int change = 10 * this.zoomLevels[this.zoomLevel];
                            change = Math.min(change, Math.max(this.screenshotBounds.width - this.minScreenshotSize.width, 0));
                            this.screenshotBounds.x += change / 2;
                            this.screenshotBounds.width -= change;
                            break;
                        }
                        this.screenshotBounds.x -= 10 * this.zoomLevels[this.zoomLevel];
                    }
                }
                if (tileBounds != null && tileBounds.width > 0) {
                    this.screenshotBounds.width = GameMath.limit(this.screenshotBounds.width, this.minScreenshotSize.width, Math.min((tileBounds.x + tileBounds.width) * tileScale, this.maxScreenshotSize.width));
                    this.screenshotBounds.x = GameMath.limit(this.screenshotBounds.x, tileBounds.x * tileScale, (tileBounds.x + tileBounds.width) * tileScale - this.screenshotBounds.width);
                }
                if (tileBounds != null && tileBounds.height > 0) {
                    this.screenshotBounds.height = GameMath.limit(this.screenshotBounds.height, this.minScreenshotSize.height, Math.min((tileBounds.y + tileBounds.height) * tileScale, this.maxScreenshotSize.height));
                    this.screenshotBounds.y = GameMath.limit(this.screenshotBounds.y, tileBounds.y * tileScale, (tileBounds.y + tileBounds.height) * tileScale - this.screenshotBounds.height);
                }
                this.centerX = this.screenshotBounds.x + this.screenshotBounds.width / 2;
                this.centerY = this.screenshotBounds.y + this.screenshotBounds.height / 2;
            } else {
                switch (dir) {
                    case 0: {
                        this.centerY -= 10 * this.zoomLevels[this.zoomLevel];
                        if (tileBounds == null || tileBounds.height <= 0) break;
                        this.centerY = Math.max(tileBounds.y * tileScale, Math.min(tileBounds.height * tileScale, this.centerY));
                        break;
                    }
                    case 1: {
                        this.centerX += 10 * this.zoomLevels[this.zoomLevel];
                        if (tileBounds == null || tileBounds.width <= 0) break;
                        this.centerX = Math.max(tileBounds.x * tileScale, Math.min(tileBounds.width * tileScale, this.centerX));
                        break;
                    }
                    case 2: {
                        this.centerY += 10 * this.zoomLevels[this.zoomLevel];
                        if (tileBounds == null || tileBounds.height <= 0) break;
                        this.centerY = Math.max(tileBounds.y * tileScale, Math.min(tileBounds.height * tileScale, this.centerY));
                        break;
                    }
                    case 3: {
                        this.centerX -= 10 * this.zoomLevels[this.zoomLevel];
                        if (tileBounds == null || tileBounds.width <= 0) break;
                        this.centerX = Math.max(tileBounds.x * tileScale, Math.min(tileBounds.width * tileScale, this.centerX));
                    }
                }
                this.setCentered(false);
            }
            return true;
        }
        return super.handleControllerNavigate(dir, event, tickManager, perspective);
    }

    @Override
    public void frameTickControllerFocus(TickManager tickManager, ControllerFocus current) {
        if (this.isControllerSelected && !ControllerInput.isCursorVisible()) {
            int delta;
            Rectangle tileBounds = this.getTileBounds();
            int tileScale = this.getTileScale();
            this.controllerScrollXBuffer += (double)(ControllerInput.getAimX() * (float)this.zoomLevels[this.zoomLevel] * tickManager.getDelta()) / 4.0;
            this.controllerScrollYBuffer += (double)(ControllerInput.getAimY() * (float)this.zoomLevels[this.zoomLevel] * tickManager.getDelta()) / 4.0;
            if (Math.abs(this.controllerScrollXBuffer) >= 1.0) {
                delta = (int)this.controllerScrollXBuffer;
                this.controllerScrollXBuffer -= (double)delta;
                this.centerX += delta;
                if (tileBounds != null && tileBounds.width > 0) {
                    this.centerX = Math.max(tileBounds.x * tileScale, Math.min(tileBounds.width * tileScale, this.centerX));
                }
            }
            if (Math.abs(this.controllerScrollYBuffer) >= 1.0) {
                delta = (int)this.controllerScrollYBuffer;
                this.controllerScrollYBuffer -= (double)delta;
                this.centerY += delta;
                if (tileBounds != null && tileBounds.height > 0) {
                    this.centerY = Math.max(tileBounds.y * tileScale, Math.min(tileBounds.height * tileScale, this.centerY));
                }
            }
            this.setCentered(false);
        }
    }

    public int getMouseMapPosX(int mouseX) {
        int relX = mouseX - this.getX();
        return (relX - this.getWidth() / 2) * this.zoomLevels[this.zoomLevel] + this.centerX;
    }

    public int getMouseMapPosY(int mouseY) {
        int relY = mouseY - this.getY();
        return (relY - this.getHeight() / 2) * this.zoomLevels[this.zoomLevel] + this.centerY;
    }

    public void zoomOut() {
        if (this.zoomLevel < this.zoomLevels.length - 1) {
            ++this.zoomLevel;
            this.refreshZoomTime();
            this.onZoomLevelChanged(this.zoomLevel);
        }
    }

    public void zoomIn() {
        if (this.zoomLevel > 0) {
            --this.zoomLevel;
            this.refreshZoomTime();
            this.onZoomLevelChanged(this.zoomLevel);
        }
    }

    public void zoomOut(int mouseX, int mouseY) {
        if (this.zoomLevel < this.zoomLevels.length - 1) {
            Rectangle tileBounds = this.getTileBounds();
            int tileScale = this.getTileScale();
            float startX = (float)this.getMouseMapPosX(mouseX) / (float)tileScale;
            float startY = (float)this.getMouseMapPosY(mouseY) / (float)tileScale;
            ++this.zoomLevel;
            int newX = this.getMouseMapPosX(mouseX) / tileScale;
            int newY = this.getMouseMapPosY(mouseY) / tileScale;
            this.centerX += (int)((startX - (float)newX) * (float)tileScale);
            this.centerY += (int)((startY - (float)newY) * (float)tileScale);
            if (tileBounds != null && tileBounds.width > 0) {
                this.centerX = Math.max(tileBounds.x * tileScale, Math.min(tileBounds.width * tileScale, this.centerX));
            }
            if (tileBounds != null && tileBounds.height > 0) {
                this.centerY = Math.max(tileBounds.y * tileScale, Math.min(tileBounds.height * tileScale, this.centerY));
            }
            this.refreshZoomTime();
            this.onZoomLevelChanged(this.zoomLevel);
        }
    }

    public void zoomIn(int mouseX, int mouseY) {
        if (this.zoomLevel > 0) {
            Rectangle tileBounds = this.getTileBounds();
            int tileScale = this.getTileScale();
            float startX = (float)this.getMouseMapPosX(mouseX) / (float)tileScale;
            float startY = (float)this.getMouseMapPosY(mouseY) / (float)tileScale;
            --this.zoomLevel;
            int newX = this.getMouseMapPosX(mouseX) / tileScale;
            int newY = this.getMouseMapPosY(mouseY) / tileScale;
            this.centerX += (int)((startX - (float)newX) * (float)tileScale);
            this.centerY += (int)((startY - (float)newY) * (float)tileScale);
            if (tileBounds != null && tileBounds.width > 0) {
                this.centerX = Math.max(tileBounds.x * tileScale, Math.min(tileBounds.width * tileScale, this.centerX));
            }
            if (tileBounds != null && tileBounds.height > 0) {
                this.centerY = Math.max(tileBounds.y * tileScale, Math.min(tileBounds.height * tileScale, this.centerY));
            }
            this.refreshZoomTime();
            this.onZoomLevelChanged(this.zoomLevel);
        }
    }

    public void setCentered(boolean centered) {
        Point centerPos;
        if (this.centered && !centered && (centerPos = this.getCenteredPos()) != null) {
            this.centerX = centerPos.x;
            this.centerY = centerPos.y;
        }
        this.centered = centered;
    }

    public boolean isCentered() {
        return this.centered;
    }

    public Point getCenteredPos() {
        return new Point(0, 0);
    }

    public boolean isControllerSelected() {
        return this.isControllerSelected;
    }

    public Rectangle getCurrentDrawBounds() {
        int scale = this.zoomLevels[this.zoomLevel];
        int tileScale = this.getTileScale();
        double fTileScale = (double)tileScale / (double)scale;
        double fScaleX = (double)this.getWidth() / fTileScale;
        double fScaleHalfX = fScaleX / 2.0;
        double fScaleY = (double)this.getHeight() / fTileScale;
        double fScaleHalfY = fScaleY / 2.0;
        double tileStartX = (double)this.centerX / (double)tileScale - fScaleHalfX;
        double tileEndX = tileStartX + fScaleX;
        double tileStartY = (double)this.centerY / (double)tileScale - fScaleHalfY;
        double tileEndY = tileStartY + fScaleY;
        return new Rectangle((int)Math.floor(tileStartX), (int)Math.floor(tileStartY), (int)Math.ceil(tileEndX - tileStartX), (int)Math.ceil(tileEndY - tileStartY));
    }

    @Override
    public void draw(TickManager tickManager, PlayerMob perspective, Rectangle renderBox) {
        Point centerPos;
        boolean isHovering;
        boolean bl = isHovering = this.isHovering || this.isControllerFocus() && this.isControllerSelected;
        if (isHovering) {
            if (this.mouseDown) {
                Renderer.setCursor(GameWindow.CURSOR.GRAB_ON);
            } else {
                Renderer.setCursor(GameWindow.CURSOR.GRAB_OFF);
            }
        }
        if (this.screenshotDragStartPos != null) {
            Renderer.setCursor(GameWindow.CURSOR.GRAB_ON);
        } else if (this.screenshotResizeDir != null) {
            switch (this.screenshotResizeDir) {
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
        if (this.centered && (centerPos = this.getCenteredPos()) != null) {
            this.centerX = centerPos.x;
            this.centerY = centerPos.y;
        }
        int scale = this.zoomLevels[this.zoomLevel];
        Rectangle tileBounds = this.getTileBounds();
        int tileScale = this.getTileScale();
        double fTileScale = (double)tileScale / (double)scale;
        double resHalfX = (double)this.getWidth() / 2.0;
        double fScaleX = (double)this.getWidth() / fTileScale;
        double fScaleHalfX = fScaleX / 2.0;
        double resHalfY = (double)this.getHeight() / 2.0;
        double fScaleY = (double)this.getHeight() / fTileScale;
        double fScaleHalfY = fScaleY / 2.0;
        double tileStartX = Math.max(tileBounds != null && tileBounds.width > 0 ? (double)tileBounds.x : -2.147483648E9, (double)this.centerX / (double)tileScale - fScaleHalfX);
        double tileEndX = Math.min(tileBounds != null && tileBounds.width > 0 ? (double)(tileBounds.x + tileBounds.width) : 2.147483647E9, tileStartX + fScaleX);
        double tileStartY = Math.max(tileBounds != null && tileBounds.height > 0 ? (double)tileBounds.y : -2.147483648E9, (double)this.centerY / (double)tileScale - fScaleHalfY);
        double tileEndY = Math.min(tileBounds != null && tileBounds.height > 0 ? (double)(tileBounds.y + tileBounds.height) : 2.147483647E9, tileStartY + fScaleY);
        this.setupMapDraw((int)Math.floor(tileStartX), (int)Math.floor(tileStartY), (int)Math.ceil(tileEndX), (int)Math.ceil(tileEndY));
        int startTextureX = (int)Math.floor(tileStartX / (double)this.textureTileSize);
        int startTextureY = (int)Math.floor(tileStartY / (double)this.textureTileSize);
        int endTextureX = (int)Math.floor(tileEndX / (double)this.textureTileSize);
        int endTextureY = (int)Math.floor(tileEndY / (double)this.textureTileSize);
        Point currentOffset = GameResources.formShader.getCurrentOffset();
        InputPosition currentMousePos = GameResources.formShader.getCurrentMousePos();
        AtomicReference limitState = new AtomicReference();
        WindowManager.getWindow().applyDraw(() -> {
            Renderer.useShader(null);
            try {
                int textureX;
                int mouseHudY;
                int mouseHudX;
                ControllerFocus currentFocus = this.getManager().getCurrentFocus();
                if (Input.lastInputIsController && this.isControllerSelected && currentFocus != null) {
                    mouseHudX = (int)currentFocus.boundingBox.getCenterX() - currentOffset.x - this.getX();
                    mouseHudY = (int)currentFocus.boundingBox.getCenterY() - currentOffset.y - this.getY();
                } else {
                    mouseHudX = currentMousePos.hudX - this.getX();
                    mouseHudY = currentMousePos.hudY - this.getY();
                }
                if (this.backgroundColor != null) {
                    Renderer.initQuadDraw(this.getWidth(), this.getHeight()).color(this.backgroundColor).draw(0, 0);
                }
                int textureSize = (int)(fTileScale * (double)this.textureTileSize);
                int textureDrawX = this.getHudPosX(resHalfX, scale, textureX * this.textureTileSize * tileScale);
                for (textureX = startTextureX; textureX <= endTextureX; ++textureX) {
                    int textureY;
                    int textureDrawY = this.getHudPosY(resHalfY, scale, textureY * this.textureTileSize * tileScale);
                    for (textureY = startTextureY; textureY <= endTextureY; ++textureY) {
                        if (this.notDiscoveredColor != null) {
                            int actualWidth = textureX == endTextureX ? (int)(fTileScale * GameMath.floorMod(tileEndX, (double)this.textureTileSize)) : textureSize;
                            int actualHeight = textureY == endTextureY ? (int)(fTileScale * GameMath.floorMod(tileEndY, (double)this.textureTileSize)) : textureSize;
                            Renderer.initQuadDraw(actualWidth, actualHeight).color(this.notDiscoveredColor).draw(textureDrawX, textureDrawY);
                        }
                        this.drawMapTexture(textureX, textureY, fTileScale, textureDrawX, textureDrawY);
                        textureDrawY += textureSize;
                        if (textureY >= endTextureY) break;
                    }
                    textureDrawX += textureSize;
                    if (textureX >= endTextureX) break;
                }
                this.drawMapOverlays(tickManager, perspective, scale, fTileScale, resHalfX, resHalfY, mouseHudX, mouseHudY);
                this.drawZoomText(this.getWidth() - 4, 0);
                if (this.screenshotBounds != null) {
                    int mouseLevelY;
                    int mouseLevelX;
                    int startX = this.getHudPosX(resHalfX, scale, this.screenshotBounds.x);
                    int startY = this.getHudPosY(resHalfY, scale, this.screenshotBounds.y);
                    int endX = this.getHudPosX(resHalfX, scale, this.screenshotBounds.x + this.screenshotBounds.width);
                    int endY = this.getHudPosY(resHalfY, scale, this.screenshotBounds.y + this.screenshotBounds.height);
                    int alpha = 10;
                    if (isHovering && this.screenshotBounds.contains(mouseLevelX = this.getLevelPosX(mouseHudX), mouseLevelY = this.getLevelPosY(mouseHudY))) {
                        alpha += 30;
                    }
                    FontManager.bit.drawString(startX, startY - 16, Localization.translate("ui", "mapshotcamera"), new FontOptions(16).outline());
                    Rectangle selection = new Rectangle(startX, startY, endX - startX, endY - startY);
                    Zoning.getRectangleDrawOptions(selection, new Color(0, 0, 0, 200), new Color(255, 255, 255, alpha), new GameCamera(0, 0)).draw();
                }
            }
            finally {
                Renderer.stopShader(null);
            }
        }, () -> {
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL14.glBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
            GameResources.formShader.usePrevState();
            limitState.set(GameResources.formShader.startState(new Point(this.getX(), this.getY()), new Rectangle(this.getWidth(), this.getHeight())));
        }, () -> {
            ((FormShader.FormShaderState)limitState.get()).end();
            GameResources.formShader.stop();
        });
    }

    public void drawZoomText(int x, int y) {
        if (this.zoomPressTime > System.currentTimeMillis()) {
            long difference = this.zoomPressTime - System.currentTimeMillis();
            float fade = (float)difference / 1000.0f;
            String text = "1:" + this.zoomLevels[this.zoomLevel];
            FontOptions options = new FontOptions(16).colorf(1.0f, 1.0f, 1.0f, fade);
            int textWidth = FontManager.bit.getWidthCeil(text, options);
            FontManager.bit.drawString(x - textWidth, y, text, options);
        }
    }

    @Override
    public void drawControllerFocus(ControllerFocus current) {
        int centerX = (int)current.boundingBox.getCenterX();
        int centerY = (int)current.boundingBox.getCenterY();
        if (this.isControllerSelected) {
            if (this.screenshotBounds != null) {
                if (this.isControllerScreenshotResizing) {
                    GameWindow.CURSOR.ARROWS_DIAGONAL1.getTexture().initDraw().color(this.getInterfaceStyle().controllerFocusBoundsHighlightColor).posMiddle(centerX, centerY).draw();
                    GameWindow.CURSOR.ARROWS_DIAGONAL2.getTexture().initDraw().color(this.getInterfaceStyle().controllerFocusBoundsHighlightColor).posMiddle(centerX, centerY).draw();
                } else {
                    GameTexture texture = GameWindow.CURSOR.GRAB_ON.getTexture();
                    texture.initDraw().color(this.getInterfaceStyle().controllerFocusBoundsHighlightColor).posMiddle(centerX, centerY).draw();
                }
            } else {
                int size = 14;
                Rectangle box = new Rectangle(centerX - size, centerY - size, size * 2, size * 2);
                HUD.selectBoundOptions(this.getInterfaceStyle().controllerFocusBoundsHighlightColor, true, box).draw();
                GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
                GameWindow.CURSOR.GRAB_ON.getTexture().initDraw().color(this.getInterfaceStyle().controllerFocusBoundsHighlightColor).posMiddle(centerX, centerY).draw();
            }
            GameTooltipManager.addControllerGlyph(Localization.translate("controls", "zoomtip"), ControllerInput.MENU_PREV, ControllerInput.MENU_NEXT);
        } else {
            GameWindow.CURSOR.GRAB_OFF.getTexture().initDraw().color(this.getInterfaceStyle().controllerFocusBoundsColor).posMiddle(centerX, centerY).draw();
            int size = 20;
            Rectangle box = new Rectangle(centerX - size, centerY - size, size * 2, size * 2);
            HUD.selectBoundOptions(this.getInterfaceStyle().controllerFocusBoundsColor, true, box).draw();
            GameTooltipManager.addControllerGlyph(Localization.translate("ui", "selectbutton"), ControllerInput.MENU_SELECT);
        }
    }

    @Override
    public Point getControllerTooltipAndFloatMenuPoint(ControllerFocus current) {
        int centerX = (int)current.boundingBox.getCenterX();
        int centerY = (int)current.boundingBox.getCenterY();
        if (this.isControllerSelected) {
            int size = 14;
            return new Point(centerX - size, centerY - size);
        }
        int size = 20;
        return new Point(centerX - size, centerY - size);
    }

    public int getHudPos(int mapCenterPos, double mapResHalf, int scale, int itemPos) {
        return (int)(mapResHalf + (double)(itemPos - mapCenterPos) / (double)scale);
    }

    public int getHudPosX(double mapResHalf, int scale, int itemX) {
        return this.getHudPos(this.centerX, mapResHalf, scale, itemX);
    }

    public int getHudPosY(double mapResHalf, int scale, int itemY) {
        return this.getHudPos(this.centerY, mapResHalf, scale, itemY);
    }

    public int getHudPosX(int itemX) {
        return this.getHudPosX((double)this.getWidth() / 2.0, this.zoomLevels[this.zoomLevel], itemX);
    }

    public int getHudPosY(int itemY) {
        return this.getHudPosY((double)this.getHeight() / 2.0, this.zoomLevels[this.zoomLevel], itemY);
    }

    public int getHudSize(int levelSize) {
        return (int)((double)levelSize / (double)this.zoomLevels[this.zoomLevel]);
    }

    public int getLevelPos(int mapCenterPos, double mapResHalf, int scale, int hudPos) {
        return (int)((double)mapCenterPos + (double)scale * ((double)hudPos - mapResHalf));
    }

    public int getLevelPosX(double mapResHalf, int scale, int hudX) {
        return this.getLevelPos(this.centerX, mapResHalf, scale, hudX);
    }

    public int getLevelPosY(double mapResHalf, int scale, int hudY) {
        return this.getLevelPos(this.centerY, mapResHalf, scale, hudY);
    }

    public int getLevelPosX(int hudX) {
        return this.getLevelPosX((double)this.getWidth() / 2.0, this.zoomLevels[this.zoomLevel], hudX);
    }

    public int getLevelPosY(int hudY) {
        return this.getLevelPosY((double)this.getHeight() / 2.0, this.zoomLevels[this.zoomLevel], hudY);
    }

    public int getLevelSize(int hudSize) {
        return (int)((double)hudSize * (double)this.zoomLevels[this.zoomLevel]);
    }

    public void refreshZoomTime() {
        this.zoomPressTime = System.currentTimeMillis() + 1000L;
    }

    public void startScreenshotMode() {
        int paddingX = this.getWidth() / 10;
        int paddingY = this.getHeight() / 10;
        int levelPosX = this.getLevelPosX(paddingX);
        int levelPosY = this.getLevelPosY(paddingY);
        int width = this.getLevelSize(this.getWidth() - paddingX * 2);
        int height = this.getLevelSize(this.getHeight() - paddingY * 2);
        Rectangle tileBounds = this.getTileBounds();
        if (tileBounds != null) {
            int tileScale = this.getTileScale();
            if (tileBounds.width > 0) {
                int levelWidth;
                if (levelPosX < tileBounds.x * tileScale) {
                    width += levelPosX - tileBounds.x * tileScale;
                    levelPosX = tileBounds.x * tileScale;
                }
                if (width > (levelWidth = tileBounds.width * tileScale) - levelPosX) {
                    width = levelWidth - levelPosX;
                }
            }
            width = GameMath.limit(width, this.minScreenshotSize.width, this.maxScreenshotSize.width);
            if (tileBounds.height > 0) {
                int levelHeight;
                if (levelPosY < tileBounds.y * tileScale) {
                    height += levelPosY - tileBounds.y * tileScale;
                    levelPosY = tileBounds.y * tileScale;
                }
                if (height > (levelHeight = tileBounds.height * tileScale) - levelPosY) {
                    height = levelHeight - levelPosY;
                }
            }
            height = GameMath.limit(height, this.minScreenshotSize.height, this.maxScreenshotSize.height);
            this.screenshotBounds = new Rectangle(levelPosX, levelPosY, width, height);
            if (Input.lastInputIsController) {
                this.prioritizeControllerFocus();
                ControllerInput.submitNextRefreshFocusEvent();
                this.isControllerSelected = true;
            }
        }
    }

    public Rectangle getScreenshotBounds() {
        return this.screenshotBounds;
    }

    public void stopScreenshotMode() {
        this.screenshotBounds = null;
    }

    @Override
    public List<Rectangle> getHitboxes() {
        return FormTextureMapBox.singleBox(new Rectangle(this.getX(), this.getY(), this.width, this.height));
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

    public void setSize(int size) {
        this.setWidth(size);
        this.setHeight(size);
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        if (this.isHidden()) {
            return false;
        }
        return super.isMouseOver(event);
    }

    @Override
    public boolean shouldDraw() {
        return !this.isHidden();
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isMouseDown() {
        return this.mouseDown;
    }

    protected static enum BoundsDir {
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

