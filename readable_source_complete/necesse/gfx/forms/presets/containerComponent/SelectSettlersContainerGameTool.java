/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.containerComponent;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameTool;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.state.State;
import necesse.engine.util.GameMath;
import necesse.engine.util.Zoning;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.Entity;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.containerComponent.SelectedSettlersHandler;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementToolHandler;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.settler.CommandMob;

public abstract class SelectSettlersContainerGameTool
implements GameTool {
    public static int maxPixelDistanceToConsiderClick = 4;
    public final Client client;
    public final Level level;
    public final SelectedSettlersHandler selectedSettlers;
    protected Point mouseDownPos;
    protected boolean mouseDownIsLeftClick;
    protected HudDrawElement hudElement;
    protected GameWindow.CURSOR cursor;
    protected ListGameTooltips tooltips;
    protected InputEvent ignoreNextInputEvent;

    public SelectSettlersContainerGameTool(Client client, SelectedSettlersHandler selectedSettlers) {
        this.client = client;
        this.level = client.getLevel();
        this.selectedSettlers = selectedSettlers;
    }

    public abstract SettlementToolHandler getCurrentToolHandler();

    public abstract Stream<Mob> streamAllSettlers(Rectangle var1);

    public abstract void commandAttack(Mob var1);

    public abstract void commandGuard(int var1, int var2);

    public abstract void commandGuard(ArrayList<Point> var1);

    @Override
    public void init() {
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, GameCamera camera, PlayerMob perspective) {
                Point mouseDownPos = SelectSettlersContainerGameTool.this.mouseDownPos;
                if (mouseDownPos != null) {
                    Point mouseReleasePos = camera.getMouseLevelPos();
                    double dist = mouseDownPos.distance(mouseReleasePos);
                    DrawOptions drawOptions = null;
                    SettlementToolHandler toolHandler = SelectSettlersContainerGameTool.this.getCurrentToolHandler();
                    if (dist <= (double)maxPixelDistanceToConsiderClick) {
                        if (toolHandler != null) {
                            drawOptions = SelectSettlersContainerGameTool.this.mouseDownIsLeftClick ? toolHandler.getLeftClickDraw(mouseDownPos, mouseReleasePos) : toolHandler.getRightClickDraw(mouseDownPos, mouseReleasePos);
                        }
                    } else {
                        Rectangle rectangle = new Rectangle(Math.min(mouseDownPos.x, mouseReleasePos.x), Math.min(mouseDownPos.y, mouseReleasePos.y), Math.max(Math.abs(mouseDownPos.x - mouseReleasePos.x), 1), Math.max(Math.abs(mouseDownPos.y - mouseReleasePos.y), 1));
                        if (toolHandler != null) {
                            drawOptions = SelectSettlersContainerGameTool.this.mouseDownIsLeftClick ? toolHandler.getLeftClickSelectionDraw(mouseDownPos, mouseReleasePos, rectangle) : toolHandler.getRightClickSelectionDraw(mouseDownPos, mouseReleasePos, rectangle);
                        }
                        if (drawOptions == null) {
                            if (SelectSettlersContainerGameTool.this.mouseDownIsLeftClick) {
                                Color edgeColor = new Color(255, 255, 255, 200);
                                Color fillColor = new Color(255, 255, 255, 20);
                                SharedTextureDrawOptions zoningDrawOptions = Zoning.getRectangleDrawOptions(rectangle, edgeColor, fillColor, 8, camera);
                                drawOptions = zoningDrawOptions::draw;
                            } else if (!SelectSettlersContainerGameTool.this.selectedSettlers.isEmpty()) {
                                ArrayList<Point> movePositions = SelectSettlersContainerGameTool.this.getSettlerMovePositions(mouseDownPos, mouseReleasePos);
                                DrawOptionsList movePositionDraws = new DrawOptionsList();
                                for (Point pos : movePositions) {
                                    Color color = new Color(93, 3, 255);
                                    movePositionDraws.add(SelectSettlersContainerGameTool.this.getActionParticleDrawOptions(camera, pos.x, pos.y, 0.0f, color));
                                }
                                drawOptions = movePositionDraws;
                            }
                        }
                    }
                    if (drawOptions != null) {
                        final DrawOptions finalDrawOptions = drawOptions;
                        list.add(new SortedDrawable(){

                            @Override
                            public int getPriority() {
                                return Integer.MAX_VALUE;
                            }

                            @Override
                            public void draw(TickManager tickManager) {
                                finalDrawOptions.draw();
                            }
                        });
                    }
                }
            }
        };
        this.level.hudManager.addElement(this.hudElement);
    }

    @Override
    public boolean inputEvent(InputEvent event) {
        boolean out;
        State currentState = GlobalData.getCurrentState();
        if (event.isMouseMoveEvent() || event.isMouseClickEvent()) {
            this.tooltips = null;
            this.cursor = null;
            if (!(this.mouseDownPos != null || currentState.getFormManager() != null && currentState.getFormManager().isMouseOver(event))) {
                int posX = currentState.getCamera().getMouseLevelPosX(event);
                int posY = currentState.getCamera().getMouseLevelPosY(event);
                boolean toolHandled = false;
                SettlementToolHandler toolHandler = this.getCurrentToolHandler();
                if (toolHandler != null) {
                    toolHandled = toolHandler.onHover(new Point(posX, posY), tooltips -> {
                        this.tooltips = tooltips;
                    }, cursor -> {
                        this.cursor = cursor;
                    });
                }
                if (!toolHandled && (this.tooltips == null || this.tooltips.isEmpty()) && this.cursor == null && Settings.showControlTips) {
                    ListGameTooltips tooltips2 = new ListGameTooltips();
                    if (Input.lastInputIsController) {
                        if (!this.selectedSettlers.isEmpty()) {
                            tooltips2.add(new InputTooltip(ControllerInput.MENU_PREV, Localization.translate("ui", "settlementcommandguard")));
                        } else {
                            tooltips2.add(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "settlementcommandselect")));
                        }
                    } else if (!this.selectedSettlers.isEmpty()) {
                        tooltips2.add(new InputTooltip(-99, Localization.translate("ui", "settlementcommandguard")));
                    } else {
                        tooltips2.add(new InputTooltip(-100, Localization.translate("ui", "settlementcommandselect")));
                    }
                    this.tooltips = tooltips2;
                }
            }
        }
        if (event.getID() == -100) {
            if (this.mouseDownPos != null && !event.state) {
                Point releasePos = currentState.getCamera().getMouseLevelPos(event);
                out = false;
                if (this.mouseDownIsLeftClick) {
                    out = this.leftClick(this.mouseDownPos, releasePos);
                }
                if (out) {
                    event.use();
                } else if (this.mouseDownPos.distance(releasePos) <= (double)maxPixelDistanceToConsiderClick) {
                    this.ignoreNextInputEvent = InputEvent.MouseButtonEvent(0, true, event.pos, GlobalData.getCurrentGameLoop());
                    WindowManager.getWindow().getInput().submitNonButtonInputEvent(this.ignoreNextInputEvent);
                }
                this.mouseDownPos = null;
                return out;
            }
        } else if (event.getID() == -99 && this.mouseDownPos != null && !event.state) {
            Point releasePos = currentState.getCamera().getMouseLevelPos(event);
            out = false;
            if (!this.mouseDownIsLeftClick) {
                out = this.rightClick(this.mouseDownPos, releasePos);
            }
            if (out) {
                event.use();
            } else if (this.mouseDownPos.distance(releasePos) <= (double)maxPixelDistanceToConsiderClick) {
                this.ignoreNextInputEvent = InputEvent.MouseButtonEvent(1, true, event.pos, GlobalData.getCurrentGameLoop());
                WindowManager.getWindow().getInput().submitNonButtonInputEvent(this.ignoreNextInputEvent);
            }
            this.mouseDownPos = null;
            return out;
        }
        if (!(event.isMoveUsed() || event.isUsed() || currentState.getFormManager() != null && currentState.getFormManager().isMouseOver(event) || event.isSameEvent(this.ignoreNextInputEvent) || !event.state)) {
            if (event.getID() == -100) {
                this.mouseDownPos = currentState.getCamera().getMouseLevelPos(event);
                this.mouseDownIsLeftClick = true;
                event.use();
                return true;
            }
            if (event.getID() == -99) {
                this.mouseDownPos = currentState.getCamera().getMouseLevelPos(event);
                this.mouseDownIsLeftClick = false;
                event.use();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean controllerEvent(ControllerEvent event) {
        boolean out;
        State currentState = GlobalData.getCurrentState();
        if (event.getState() == ControllerInput.AIM || event.isButton) {
            this.tooltips = null;
            this.cursor = null;
            if (!(this.mouseDownPos != null || currentState.getFormManager() != null && currentState.getFormManager().isMouseOver(WindowManager.getWindow().mousePos()))) {
                int posX = currentState.getCamera().getMouseLevelPosX();
                int posY = currentState.getCamera().getMouseLevelPosY();
                boolean toolHandled = false;
                SettlementToolHandler toolHandler = this.getCurrentToolHandler();
                if (toolHandler != null) {
                    toolHandled = toolHandler.onHover(new Point(posX, posY), tooltips -> {
                        this.tooltips = tooltips;
                    }, cursor -> {
                        this.cursor = cursor;
                    });
                }
                if (!toolHandled && (this.tooltips == null || this.tooltips.isEmpty()) && this.cursor == null && Settings.showControlTips) {
                    ListGameTooltips tooltips2 = new ListGameTooltips();
                    if (Input.lastInputIsController) {
                        if (!this.selectedSettlers.isEmpty()) {
                            tooltips2.add(new InputTooltip(ControllerInput.MENU_PREV, Localization.translate("ui", "settlementcommandguard")));
                        } else {
                            tooltips2.add(new InputTooltip(ControllerInput.MENU_NEXT, Localization.translate("ui", "settlementcommandselect")));
                        }
                    } else if (!this.selectedSettlers.isEmpty()) {
                        tooltips2.add(new InputTooltip(-99, Localization.translate("ui", "settlementcommandguard")));
                    } else {
                        tooltips2.add(new InputTooltip(-100, Localization.translate("ui", "settlementcommandselect")));
                    }
                    this.tooltips = tooltips2;
                }
            }
        }
        if (event.getState() == ControllerInput.MENU_NEXT) {
            if (this.mouseDownPos != null && !event.buttonState) {
                Point releasePos = currentState.getCamera().getMouseLevelPos();
                out = false;
                if (this.mouseDownIsLeftClick) {
                    out = this.leftClick(this.mouseDownPos, releasePos);
                }
                this.mouseDownPos = null;
                if (out) {
                    event.use();
                }
                return out;
            }
        } else if (event.getState() == ControllerInput.MENU_PREV && this.mouseDownPos != null && !event.buttonState) {
            Point releasePos = currentState.getCamera().getMouseLevelPos();
            out = false;
            if (!this.mouseDownIsLeftClick) {
                out = this.rightClick(this.mouseDownPos, releasePos);
            }
            this.mouseDownPos = null;
            if (out) {
                event.use();
            }
            return out;
        }
        if (!(event.isUsed() || currentState.getFormManager() != null && currentState.getFormManager().isMouseOver() || !event.buttonState)) {
            if (event.getState() == ControllerInput.MENU_NEXT) {
                this.mouseDownPos = currentState.getCamera().getMouseLevelPos();
                this.mouseDownIsLeftClick = true;
                event.use();
                return true;
            }
            if (event.getState() == ControllerInput.MENU_PREV) {
                this.mouseDownPos = currentState.getCamera().getMouseLevelPos();
                this.mouseDownIsLeftClick = false;
                event.use();
                return true;
            }
        }
        return false;
    }

    public boolean leftClick(Point startPos, Point endPos) {
        double dist = startPos.distance(endPos);
        SettlementToolHandler toolHandler = this.getCurrentToolHandler();
        if (dist <= (double)maxPixelDistanceToConsiderClick) {
            if (toolHandler != null && toolHandler.onLeftClick(endPos)) {
                return true;
            }
            Rectangle selectionBox = new Rectangle(endPos.x - 50, endPos.y - 50, 100, 100);
            Mob mob = this.streamAllSettlers(selectionBox).filter(m -> m instanceof CommandMob).filter(m -> m.getSelectBox().contains(endPos)).max(Comparator.comparingInt(Mob::getDrawY)).orElse(null);
            if (mob != null) {
                this.selectedSettlers.selectOrDeselectSettler(mob.getUniqueID());
            } else {
                this.selectedSettlers.clear();
            }
            return true;
        }
        Rectangle selectionBox = new Rectangle(Math.min(startPos.x, endPos.x), Math.min(startPos.y, endPos.y), Math.max(Math.abs(startPos.x - endPos.x), 1), Math.max(Math.abs(startPos.y - endPos.y), 1));
        if (toolHandler != null && toolHandler.onLeftClickSelection(startPos, endPos, selectionBox)) {
            return true;
        }
        List<Integer> newSettlers = this.streamAllSettlers(selectionBox).filter(m -> m instanceof CommandMob).filter(m -> ((CommandMob)((Object)m)).canBeCommanded(this.client)).filter(m -> selectionBox.intersects(m.getSelectBox())).map(Entity::getUniqueID).collect(Collectors.toList());
        this.selectedSettlers.selectSettlers(newSettlers);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean rightClick(Point startPos, Point endPos) {
        double dist = startPos.distance(endPos);
        if (dist <= (double)maxPixelDistanceToConsiderClick) {
            SettlementToolHandler toolHandler = this.getCurrentToolHandler();
            if (toolHandler != null && toolHandler.onRightClick(endPos)) {
                return true;
            }
            if (this.selectedSettlers.isEmpty()) {
                return false;
            }
            SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
            synchronized (selectedSettlersHandler) {
                PlayerMob player = this.client.getPlayer();
                Mob targetMob = this.level.entityManager.streamAreaMobsAndPlayersTileRange(endPos.x, endPos.y, 10).filter(m -> m.getSelectBox().contains(endPos)).filter(m -> !this.selectedSettlers.contains(m.getUniqueID())).filter(m -> m.canBeTargeted(player, player.getNetworkClient())).findBestDistance(1, Comparator.comparingInt(m -> -m.getDrawY())).orElse(null);
                if (targetMob != null) {
                    this.commandAttack(targetMob);
                    Color color = new Color(255, 41, 3);
                    Rectangle selectBox = targetMob.getSelectBox();
                    this.spawnActionParticles((float)selectBox.x + (float)selectBox.width / 2.0f, (float)selectBox.y + (float)selectBox.height / 2.0f, color);
                    return true;
                }
                this.commandGuard(endPos.x, endPos.y);
                this.spawnActionParticles(endPos.x, endPos.y);
                return true;
            }
        }
        Rectangle selectionBox = new Rectangle(Math.min(startPos.x, endPos.x), Math.min(startPos.y, endPos.y), Math.abs(startPos.x - endPos.x), Math.abs(startPos.y - endPos.y));
        SettlementToolHandler toolHandler = this.getCurrentToolHandler();
        if (toolHandler != null && toolHandler.onRightClickSelection(startPos, endPos, selectionBox)) {
            return true;
        }
        if (this.selectedSettlers.isEmpty()) {
            return false;
        }
        if (this.selectedSettlers.getSize() == 1) {
            PlayerMob player = this.client.getPlayer();
            Mob targetMob = this.level.entityManager.streamAreaMobsAndPlayersTileRange(endPos.x, endPos.y, 10).filter(m -> m.getSelectBox().contains(endPos)).filter(m -> !this.selectedSettlers.contains(m.getUniqueID())).filter(m -> m.canBeTargeted(player, player.getNetworkClient())).findBestDistance(1, Comparator.comparingInt(m -> -m.getDrawY())).orElse(null);
            if (targetMob != null) {
                this.commandAttack(targetMob);
                Color color = new Color(255, 41, 3);
                Rectangle selectBox = targetMob.getSelectBox();
                this.spawnActionParticles((float)selectBox.x + (float)selectBox.width / 2.0f, (float)selectBox.y + (float)selectBox.height / 2.0f, color);
                return true;
            }
        }
        ArrayList<Point> movePositions = this.getSettlerMovePositions(startPos, endPos);
        SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
        synchronized (selectedSettlersHandler) {
            if (movePositions.size() == 1) {
                Point pos = movePositions.get(0);
                this.commandGuard(pos.x, pos.y);
            } else {
                this.commandGuard(movePositions);
            }
        }
        for (Point pos : movePositions) {
            this.spawnActionParticles(pos.x, pos.y);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ArrayList<Point> getSettlerMovePositions(Point startPos, Point endPos) {
        double dist = startPos.distance(endPos);
        SelectedSettlersHandler selectedSettlersHandler = this.selectedSettlers;
        synchronized (selectedSettlersHandler) {
            if (this.selectedSettlers.getSize() <= 1) {
                return new ArrayList<Point>(Collections.singletonList(endPos));
            }
            Point2D.Float dir = GameMath.normalize(endPos.x - startPos.x, endPos.y - startPos.y);
            int pointsSize = this.selectedSettlers.getSize();
            ArrayList<Point> points = new ArrayList<Point>(pointsSize);
            double distPerSettler = dist / (double)Math.max(pointsSize - 1, 1);
            for (int i = 0; i < pointsSize; ++i) {
                double distFromStart = distPerSettler * (double)i;
                int x = (int)((double)startPos.x + (double)dir.x * distFromStart);
                int y = (int)((double)startPos.y + (double)dir.y * distFromStart);
                points.add(new Point(x, y));
            }
            return points;
        }
    }

    public void spawnActionParticles(float posX, float posY) {
        this.spawnActionParticles(posX, posY, new Color(93, 3, 255));
    }

    public DrawOptions getActionParticleDrawOptions(GameCamera camera, float x, float y, float lifePercent, Color color) {
        int drawX = camera.getDrawX(x);
        int drawY = camera.getDrawY(y);
        float rotation = (float)((double)this.level.getWorldEntity().getLocalTime() / 2.0 % 360.0);
        float percentSize = 1.0f - lifePercent;
        int pixelSize = (int)((float)Math.max(GameResources.aim.getWidth(), GameResources.aim.getHeight()) * percentSize);
        return GameResources.aim.initDraw().color(color).size(pixelSize).rotate(rotation).posMiddle(drawX, drawY);
    }

    public void spawnActionParticles(float posX, float posY, final Color color) {
        this.level.entityManager.addParticle(new Particle(this.level, posX, posY, 500L){

            @Override
            public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, OrderableDrawables overlayList, Level level, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
                DrawOptions drawOptions = SelectSettlersContainerGameTool.this.getActionParticleDrawOptions(camera, this.x, this.y, this.getLifeCyclePercent(), color);
                topList.add(tm -> drawOptions.draw());
            }
        }, Particle.GType.CRITICAL);
    }

    @Override
    public void isCancelled() {
        this.hudElement.remove();
    }

    @Override
    public void isCleared() {
        this.hudElement.remove();
    }

    @Override
    public boolean canCancel() {
        return false;
    }

    @Override
    public boolean forceControllerCursor() {
        return true;
    }

    @Override
    public GameWindow.CURSOR getCursor() {
        return this.cursor;
    }

    @Override
    public GameTooltips getTooltips() {
        return this.tooltips;
    }
}

