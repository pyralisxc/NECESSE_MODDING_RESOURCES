/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.gameTool;

import java.util.Objects;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameTool;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.platforms.Platform;
import necesse.engine.util.GameLinkedList;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.gfx.Renderer;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class GameToolManager {
    private static final GameLinkedList<CalledGameTool> gameTools = new GameLinkedList();
    private static boolean firstGameToolControllerIsAimBefore;
    private static WindowManager windowManager;

    public static void initialize() {
        windowManager = Platform.getWindowManager();
    }

    public static void preGameTick(TickManager tickManager) {
        if (!gameTools.isEmpty()) {
            GameLinkedList.Element gameToolElement = gameTools.getFirstElement();
            GameTool gameTool = ((CalledGameTool)gameToolElement.object).tool;
            if (tickManager.isGameTick()) {
                gameTool.tick();
            }
            if (!gameToolElement.isRemoved()) {
                GameTooltips tooltips = gameTool.getTooltips();
                if (tooltips != null) {
                    GameTooltipManager.addTooltip(tooltips, TooltipLocation.PLAYER);
                }
            } else {
                if (!gameTools.isEmpty()) {
                    if (GameToolManager.gameTools.getFirst().hasBeenPushedBack) {
                        GameToolManager.gameTools.getFirst().tool.onRenewed();
                        GameToolManager.gameTools.getFirst().hasBeenPushedBack = false;
                        ControllerInput.setAimIsCursor(GameToolManager.gameTools.getFirst().controllerIsAimBefore);
                    }
                } else {
                    ControllerInput.setAimIsCursor(firstGameToolControllerIsAimBefore);
                }
                gameTool = null;
            }
            if (gameTool != null && Input.lastInputIsController) {
                if (gameTool.forceControllerCursor()) {
                    if (!ControllerInput.isCursorVisible()) {
                        if (gameTool.canCancel()) {
                            gameTool.isCancelled();
                            gameToolElement.remove();
                            if (!gameTools.isEmpty()) {
                                if (GameToolManager.gameTools.getFirst().hasBeenPushedBack) {
                                    GameToolManager.gameTools.getFirst().tool.onRenewed();
                                    GameToolManager.gameTools.getFirst().hasBeenPushedBack = false;
                                    ControllerInput.setAimIsCursor(GameToolManager.gameTools.getFirst().controllerIsAimBefore);
                                }
                            } else {
                                ControllerInput.setAimIsCursor(firstGameToolControllerIsAimBefore);
                            }
                            gameTool = null;
                        } else {
                            ControllerInput.setAimIsCursor(true);
                        }
                    }
                } else if (!gameToolElement.hasNext()) {
                    firstGameToolControllerIsAimBefore = ControllerInput.isCursorVisible();
                }
            }
            if (gameTool != null) {
                for (InputEvent inputEvent : WindowManager.getWindow().getInput().getEvents()) {
                    if (inputEvent.state && inputEvent.getID() == 256 && gameTool.canCancel()) {
                        gameTool.isCancelled();
                        gameToolElement.remove();
                        if (!gameTools.isEmpty()) {
                            if (GameToolManager.gameTools.getFirst().hasBeenPushedBack) {
                                GameToolManager.gameTools.getFirst().tool.onRenewed();
                                GameToolManager.gameTools.getFirst().hasBeenPushedBack = false;
                                ControllerInput.setAimIsCursor(GameToolManager.gameTools.getFirst().controllerIsAimBefore);
                            }
                        } else {
                            ControllerInput.setAimIsCursor(firstGameToolControllerIsAimBefore);
                        }
                        inputEvent.use();
                        gameTool = null;
                        break;
                    }
                    if (gameTool.inputEvent(inputEvent)) {
                        inputEvent.use();
                    }
                    if (!gameToolElement.isRemoved()) continue;
                    if (!gameTools.isEmpty()) {
                        if (GameToolManager.gameTools.getFirst().hasBeenPushedBack) {
                            GameToolManager.gameTools.getFirst().tool.onRenewed();
                            GameToolManager.gameTools.getFirst().hasBeenPushedBack = false;
                            ControllerInput.setAimIsCursor(GameToolManager.gameTools.getFirst().controllerIsAimBefore);
                        }
                    } else {
                        ControllerInput.setAimIsCursor(firstGameToolControllerIsAimBefore);
                    }
                    gameTool = null;
                    break;
                }
            }
            if (gameTool != null) {
                for (ControllerEvent controllerEvent : ControllerInput.getEvents()) {
                    if (controllerEvent.buttonState && (controllerEvent.getState() == ControllerInput.MENU_BACK || controllerEvent.getState() == ControllerInput.MAIN_MENU) && gameTool.canCancel()) {
                        gameTool.isCancelled();
                        gameToolElement.remove();
                        if (!gameTools.isEmpty()) {
                            if (GameToolManager.gameTools.getFirst().hasBeenPushedBack) {
                                GameToolManager.gameTools.getFirst().tool.onRenewed();
                                GameToolManager.gameTools.getFirst().hasBeenPushedBack = false;
                                ControllerInput.setAimIsCursor(GameToolManager.gameTools.getFirst().controllerIsAimBefore);
                            }
                        } else {
                            ControllerInput.setAimIsCursor(firstGameToolControllerIsAimBefore);
                        }
                        controllerEvent.use();
                        gameTool = null;
                        break;
                    }
                    if (gameTool.controllerEvent(controllerEvent)) {
                        controllerEvent.use();
                    }
                    if (!gameToolElement.isRemoved()) continue;
                    if (!gameTools.isEmpty()) {
                        if (GameToolManager.gameTools.getFirst().hasBeenPushedBack) {
                            GameToolManager.gameTools.getFirst().tool.onRenewed();
                            GameToolManager.gameTools.getFirst().hasBeenPushedBack = false;
                            ControllerInput.setAimIsCursor(GameToolManager.gameTools.getFirst().controllerIsAimBefore);
                        }
                    } else {
                        ControllerInput.setAimIsCursor(firstGameToolControllerIsAimBefore);
                    }
                    gameTool = null;
                    break;
                }
            }
        }
    }

    public static void lateTick(TickManager tickManager) {
        GameWindow.CURSOR cursor;
        if (!gameTools.isEmpty() && (cursor = GameToolManager.gameTools.getFirst().tool.getCursor()) != null) {
            Renderer.setCursor(cursor);
        }
    }

    public static void setGameTool(GameTool tool, Object caller) {
        Objects.requireNonNull(tool);
        if (gameTools.stream().noneMatch(e -> e.tool == tool)) {
            if (gameTools.isEmpty()) {
                firstGameToolControllerIsAimBefore = ControllerInput.isCursorVisible();
            } else {
                GameToolManager.gameTools.getFirst().tool.onPaused();
                GameToolManager.gameTools.getFirst().controllerIsAimBefore = ControllerInput.isCursorVisible();
                GameToolManager.gameTools.getFirst().hasBeenPushedBack = true;
            }
            gameTools.addFirst(new CalledGameTool(caller, tool));
            tool.init();
            if (tool.startControllerCursor()) {
                ControllerInput.setAimIsCursor(true);
            }
        }
    }

    public static void setGameTool(GameTool tool) {
        GameToolManager.setGameTool(tool, tool);
    }

    public static boolean clearGameTools(Object caller) {
        CalledGameTool prevTool = null;
        if (!gameTools.isEmpty()) {
            prevTool = gameTools.getFirst();
        }
        boolean out = false;
        for (GameLinkedList.Element e : gameTools.elements()) {
            if (((CalledGameTool)e.object).caller != caller) continue;
            ((CalledGameTool)e.object).tool.isCleared();
            e.remove();
            out = true;
        }
        if (!gameTools.isEmpty() && gameTools.getFirst() != prevTool) {
            if (GameToolManager.gameTools.getFirst().hasBeenPushedBack) {
                GameToolManager.gameTools.getFirst().tool.onRenewed();
                GameToolManager.gameTools.getFirst().hasBeenPushedBack = false;
                ControllerInput.setAimIsCursor(GameToolManager.gameTools.getFirst().controllerIsAimBefore);
            }
        } else if (prevTool != null && gameTools.isEmpty()) {
            ControllerInput.setAimIsCursor(firstGameToolControllerIsAimBefore);
        }
        return out;
    }

    public static boolean clearGameTool(GameTool tool) {
        CalledGameTool prevTool = null;
        if (!gameTools.isEmpty()) {
            prevTool = gameTools.getFirst();
        }
        boolean out = false;
        for (GameLinkedList.Element e : gameTools.elements()) {
            if (((CalledGameTool)e.object).tool != tool) continue;
            ((CalledGameTool)e.object).tool.isCleared();
            e.remove();
            out = true;
        }
        if (!gameTools.isEmpty() && gameTools.getFirst() != prevTool) {
            if (GameToolManager.gameTools.getFirst().hasBeenPushedBack) {
                GameToolManager.gameTools.getFirst().tool.onRenewed();
                GameToolManager.gameTools.getFirst().hasBeenPushedBack = false;
                ControllerInput.setAimIsCursor(GameToolManager.gameTools.getFirst().controllerIsAimBefore);
            }
        } else if (prevTool != null && gameTools.isEmpty()) {
            ControllerInput.setAimIsCursor(firstGameToolControllerIsAimBefore);
        }
        return out;
    }

    public static boolean doesToolForceMenuLayer() {
        return !gameTools.isEmpty() && GameToolManager.gameTools.getFirst().tool.shouldForceControllerMenuLayer();
    }

    public static boolean doesToolShowWires() {
        return !gameTools.isEmpty() && GameToolManager.gameTools.getFirst().tool.shouldShowWires();
    }

    private static class CalledGameTool {
        public final Object caller;
        public final GameTool tool;
        public boolean controllerIsAimBefore;
        public boolean hasBeenPushedBack;

        public CalledGameTool(Object caller, GameTool tool) {
            this.caller = caller;
            this.tool = tool;
        }
    }
}

