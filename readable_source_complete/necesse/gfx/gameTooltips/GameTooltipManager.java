/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.gameTooltips;

import java.awt.Point;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerGlyphTip;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.input.controller.ControllerInputState;
import necesse.engine.util.GameUtils;
import necesse.engine.window.WindowManager;
import necesse.gfx.GameBackground;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ScreenTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;

public class GameTooltipManager {
    private static final Object tooltipsSynchronized = new Object();
    private static LinkedList<ScreenTooltips> tooltips;
    private static Point tooltipsFormFocus;
    private static Point tooltipsInteractFocus;
    private static Point tooltipsPlayer;
    private static Point tooltipFocusOffset;
    private static HashMap<String, ControllerGlyphTip> controllerGlyphs;

    public static void initialize() {
        tooltips = new LinkedList();
        tooltipsFormFocus = null;
        tooltipsInteractFocus = null;
        tooltipsPlayer = null;
        tooltipFocusOffset = null;
        controllerGlyphs = new HashMap();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void preGameTick(TickManager tickManager) {
        Object object = tooltipsSynchronized;
        synchronized (object) {
            tooltips.clear();
            tooltipsFormFocus = null;
            tooltipsInteractFocus = null;
            tooltipsPlayer = null;
            tooltipFocusOffset = null;
            controllerGlyphs.clear();
        }
    }

    public static void drawControllerInputTooltips() {
        if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
            ScreenTooltips.drawAt(tooltips, TooltipLocation.INTERACT_FOCUS, tooltipsInteractFocus, GameColor.DEFAULT_COLOR);
            ScreenTooltips.drawAt(tooltips, TooltipLocation.PLAYER, tooltipsPlayer, GameColor.DEFAULT_COLOR);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void drawHudTooltips() {
        Object object = tooltipsSynchronized;
        synchronized (object) {
            Point drawPos;
            InputPosition mousePos;
            if (Input.lastInputIsController) {
                if (tooltipsFormFocus != null) {
                    Point drawPos2 = new Point(tooltipsFormFocus);
                    if (tooltipFocusOffset != null) {
                        drawPos2.x += GameTooltipManager.tooltipFocusOffset.x;
                        drawPos2.y -= GameTooltipManager.tooltipFocusOffset.y;
                    }
                    ScreenTooltips.drawAt(tooltips, TooltipLocation.FORM_FOCUS, drawPos2, GameColor.DEFAULT_COLOR);
                } else if (!ControllerInput.isCursorVisible()) {
                    tooltips.removeIf(t -> t.location == TooltipLocation.FORM_FOCUS);
                }
                if (ControllerInput.isCursorVisible()) {
                    mousePos = WindowManager.getWindow().mousePos();
                    drawPos = new Point(mousePos.hudX, mousePos.hudY);
                    if (tooltipFocusOffset != null) {
                        drawPos.x += GameTooltipManager.tooltipFocusOffset.x;
                        drawPos.y -= GameTooltipManager.tooltipFocusOffset.y;
                    }
                    ScreenTooltips.drawAt(tooltips, drawPos.x, drawPos.y, GameColor.DEFAULT_COLOR);
                } else {
                    ScreenTooltips.drawAt(tooltips, 0, WindowManager.getWindow().getHudHeight(), GameColor.DEFAULT_COLOR);
                }
            } else {
                mousePos = WindowManager.getWindow().mousePos();
                drawPos = new Point(mousePos.hudX, mousePos.hudY);
                if (tooltipFocusOffset != null) {
                    drawPos.x += GameTooltipManager.tooltipFocusOffset.x;
                    drawPos.y -= GameTooltipManager.tooltipFocusOffset.y;
                }
                ScreenTooltips.drawAt(tooltips, drawPos.x, drawPos.y, GameColor.DEFAULT_COLOR);
            }
            if (Input.lastInputIsController && Settings.showControlTips) {
                int controllerTipsX = WindowManager.getWindow().getHudWidth();
                int controllerTipsY = WindowManager.getWindow().getHudHeight() - ControllerGlyphTip.getHeight();
                for (ControllerGlyphTip tip : controllerGlyphs.values()) {
                    tip.draw(controllerTipsX -= tip.getWidth(), controllerTipsY);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addTooltip(GameTooltips tooltips, GameBackground background, TooltipLocation location) {
        if (tooltips == null) {
            return;
        }
        Object object = tooltipsSynchronized;
        synchronized (object) {
            GameUtils.insertSortedList(GameTooltipManager.tooltips, new ScreenTooltips(tooltips, background, location), Comparator.comparing(t -> t.tooltips.getDrawOrder(), Comparator.reverseOrder()));
        }
    }

    public static void addTooltip(GameTooltips tooltips, TooltipLocation location) {
        GameTooltipManager.addTooltip(tooltips, null, location);
    }

    public static void setTooltipsFormFocus(int x, int y) {
        tooltipsFormFocus = new Point(x, y);
    }

    public static void setTooltipsFormFocus(InputPosition pos) {
        GameTooltipManager.setTooltipsFormFocus(pos.hudX, pos.hudY);
    }

    public static void setTooltipFocusOffset(int x, int y) {
        tooltipFocusOffset = tooltipFocusOffset == null ? new Point(x, y) : new Point(Math.max(GameTooltipManager.tooltipFocusOffset.x, x), Math.max(GameTooltipManager.tooltipFocusOffset.y, y));
    }

    public static void setTooltipsInteractFocus(int x, int y) {
        tooltipsInteractFocus = new Point(x, y);
    }

    public static void setTooltipsInteractFocus(InputPosition pos) {
        GameTooltipManager.setTooltipsInteractFocus(pos.sceneX, pos.sceneY);
    }

    public static void setTooltipsPlayer(int x, int y) {
        tooltipsPlayer = new Point(x, y);
    }

    public static void setTooltipsPlayer(InputPosition pos) {
        GameTooltipManager.setTooltipsPlayer(pos.sceneX, pos.sceneY);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void addControllerGlyph(String text, ControllerInputState ... states) {
        if (!Input.lastInputIsController) {
            return;
        }
        Object object = tooltipsSynchronized;
        synchronized (object) {
            controllerGlyphs.compute(text, (key, tip) -> {
                if (tip == null) {
                    return new ControllerGlyphTip(text, states);
                }
                tip.addGlyphs(states);
                return tip;
            });
        }
    }
}

