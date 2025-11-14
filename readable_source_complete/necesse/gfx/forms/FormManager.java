/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.input.Input;
import necesse.engine.input.InputEvent;
import necesse.engine.input.InputPosition;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.util.ObjectValue;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.Renderer;
import necesse.gfx.forms.ComponentList;
import necesse.gfx.forms.ComponentListContainer;
import necesse.gfx.forms.ControllerKeyboardForm;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormTypingComponent;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerFocusHandler;
import necesse.gfx.forms.floatMenu.ComponentFloatMenu;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.gameTooltips.GameTooltipManager;
import necesse.inventory.InventoryItem;

public class FormManager
implements ComponentListContainer<FormComponent> {
    public static boolean drawControllerFocusBoxes;
    public static boolean drawControllerAreaBoxes;
    private boolean isDisposed;
    private final ComponentList<FormComponent> components;
    private boolean isMouseOver;
    private CurrentFloatMenu floatMenu = null;
    private CurrentFloatMenu visibleKeyboard = null;
    private final LinkedList<Timeout> timeouts = new LinkedList();
    private ControllerFocus currentControllerFocus;
    private ArrayList<ControllerFocusHandler> nextControllerFocuses;
    private HashSet<ControllerFocusHandler> nextControllerFocusesSet;
    private static final HashMap<Integer, Long> lastControllerFocuses;

    public static void cleanUpLastControllerFocuses() {
        HashSet<Integer> removes = new HashSet<Integer>();
        for (Map.Entry<Integer, Long> e : lastControllerFocuses.entrySet()) {
            long timeSince = System.currentTimeMillis() - e.getValue();
            if (timeSince <= 60000L) continue;
            removes.add(e.getKey());
        }
        removes.forEach(lastControllerFocuses::remove);
    }

    public FormManager() {
        this.components = new ComponentList<FormComponent>(null){

            @Override
            public InputEvent offsetEvent(InputEvent event, boolean allowOutside) {
                return event;
            }

            @Override
            public FormManager getManager() {
                return FormManager.this;
            }

            @Override
            public void onChange() {
                FormManager.this.updateMouseOver();
            }
        };
    }

    @Override
    public ComponentList<FormComponent> getComponentList() {
        return this.components;
    }

    public void openFloatMenu(FloatMenu menu) {
        this.openFloatMenu(menu, 0, 0);
    }

    public void openFloatMenu(FloatMenu menu, FormComponent relativeTo, InputEvent event) {
        this.openFloatMenu(menu, relativeTo, event, 0, 0);
    }

    public void openFloatMenu(FloatMenu menu, FormComponent relativeTo, InputEvent event, int xOffset, int yOffset) {
        ControllerFocus currentFocus;
        if (event.isControllerEvent() && (currentFocus = this.getCurrentFocus()) != null) {
            Point point = currentFocus.getTooltipAndFloatMenuPoint();
            this.openFloatMenuAt(menu, point.x + xOffset, point.y + yOffset);
            return;
        }
        Rectangle boundingBox = relativeTo.getBoundingBox();
        this.openFloatMenu(menu, boundingBox.x - event.pos.hudX + xOffset, boundingBox.y - event.pos.hudY + yOffset);
    }

    public void openFloatMenu(FloatMenu menu, int xOffset, int yOffset) {
        ControllerFocus currentFocus = this.getCurrentFocus();
        if (currentFocus != null) {
            Point point = currentFocus.getTooltipAndFloatMenuPoint();
            this.openFloatMenuAt(menu, point.x + xOffset, point.y + yOffset);
        } else {
            InputPosition mousePos = WindowManager.getWindow().mousePos();
            this.openFloatMenuAt(menu, mousePos.hudX + xOffset, mousePos.hudY + yOffset);
        }
    }

    public void openFloatMenuAt(FloatMenu menu, int drawX, int drawY) {
        Objects.requireNonNull(menu);
        if (this.floatMenu != null) {
            this.floatMenu.menu.dispose();
        }
        this.floatMenu = new CurrentFloatMenu(menu, drawX, drawY, GlobalData.getCurrentGameLoop());
        menu.init(this.floatMenu.drawX, this.floatMenu.drawY, () -> {
            if (this.floatMenu != null && this.floatMenu.menu == menu) {
                this.floatMenu.menu.dispose();
                if (this.floatMenu.menu.disposeFocus != null) {
                    this.setNextControllerFocus(this.floatMenu.menu.disposeFocus);
                }
                this.floatMenu = null;
                ControllerInput.submitNextRefreshFocusEvent();
            }
        });
        this.updateMouseOver();
        ControllerInput.submitNextRefreshFocusEvent();
    }

    public boolean hasFloatMenu() {
        return this.floatMenu != null;
    }

    public void openControllerKeyboard(final FormTypingComponent typingComponent) {
        if (this.visibleKeyboard != null) {
            this.visibleKeyboard.menu.dispose();
        }
        final AtomicReference<ComponentFloatMenu> menuRef = new AtomicReference<ComponentFloatMenu>();
        ComponentFloatMenu menu = new ComponentFloatMenu(typingComponent, new ControllerKeyboardForm(typingComponent){

            @Override
            public void submitEnter() {
                typingComponent.submitControllerEnter();
                ((FloatMenu)menuRef.get()).remove();
            }
        });
        menuRef.set(menu);
        this.visibleKeyboard = new CurrentFloatMenu(menu, 0, 0, GlobalData.getCurrentGameLoop());
        menu.init(this.visibleKeyboard.drawX, this.visibleKeyboard.drawY, () -> {
            if (this.visibleKeyboard != null && this.visibleKeyboard.menu == menu) {
                this.visibleKeyboard.menu.dispose();
                if (this.visibleKeyboard.menu.disposeFocus != null) {
                    this.setNextControllerFocus(this.visibleKeyboard.menu.disposeFocus);
                }
                this.visibleKeyboard = null;
                ControllerInput.submitNextRefreshFocusEvent();
            }
            typingComponent.setTyping(false);
        });
        this.updateMouseOver();
        ControllerInput.submitNextRefreshFocusEvent();
    }

    public boolean isControllerKeyboardOpen() {
        return this.visibleKeyboard != null;
    }

    public void updateMouseOver(InputEvent event) {
        this.isMouseOver = this.visibleKeyboard != null && this.visibleKeyboard.menu.isMouseOver(event);
        this.isMouseOver = this.floatMenu != null && this.floatMenu.menu.isMouseOver(event);
        for (FormComponent component : this.components) {
            if (!component.shouldDraw() || !component.isMouseOver(event)) continue;
            this.isMouseOver = true;
            break;
        }
    }

    public final void updateMouseOver() {
        this.updateMouseOver(InputEvent.MouseMoveEvent(WindowManager.getWindow().mousePos(), GlobalData.getCurrentGameLoop()));
    }

    public void frameTick(TickManager tickManager) {
        long currentTime = System.currentTimeMillis();
        while (!this.timeouts.isEmpty()) {
            Timeout first = this.timeouts.getFirst();
            if (first.time >= currentTime) break;
            this.timeouts.removeFirst();
            first.runnable.run();
        }
        if (this.currentControllerFocus != null) {
            this.currentControllerFocus.handler.frameTickControllerFocus(tickManager, this.currentControllerFocus);
        }
    }

    public void submitInputEvent(InputEvent event, TickManager tickManager, PlayerMob perspective) {
        if (event.isMouseMoveEvent()) {
            this.updateMouseOver(event);
        }
        if (this.visibleKeyboard != null && !this.visibleKeyboard.isSameStartTime(event)) {
            this.visibleKeyboard.menu.handleInputEvent(event, tickManager, perspective);
            if (event.isMouseMoveEvent() && this.visibleKeyboard.menu.isMouseOver(event)) {
                event.useMove();
            }
            if (event.isUsed()) {
                return;
            }
            if (event.state && event.getID() == 256) {
                this.visibleKeyboard.menu.remove();
                event.use();
            }
        }
        if (this.floatMenu != null && !this.floatMenu.isSameStartTime(event)) {
            this.floatMenu.menu.handleInputEvent(event, tickManager, perspective);
            if (event.isMouseMoveEvent() && this.floatMenu.menu.isMouseOver(event)) {
                event.useMove();
            }
            if (event.isUsed()) {
                return;
            }
            if (event.state && event.getID() == 256) {
                this.floatMenu.menu.remove();
                event.use();
            }
        }
        this.components.submitInputEvent(event, tickManager, perspective);
        if ((event.isMouseWheelEvent() || event.isMouseClickEvent()) && this.isMouseOver(event)) {
            event.use();
        }
        if (event.wasMouseClickEvent() && FormTypingComponent.isCurrentlyTyping()) {
            FormTypingComponent.getCurrentTypingComponent().submitUsedInputEvent(event);
        }
    }

    public void submitControllerEvent(ControllerEvent event, TickManager tickManager, PlayerMob perspective) {
        int dir;
        InputEvent mouseEvent;
        InputPosition mousePos;
        GameWindow window = WindowManager.getWindow();
        if (!(event.getState() != ControllerInput.REFRESH_FOCUS || ControllerInput.isCursorVisible() && this.currentControllerFocus == null)) {
            if (ControllerInput.isLayerActive(ControllerInput.MENU_SET_LAYER) && Input.lastInputIsController) {
                LinkedList<ControllerFocus> list = new LinkedList<ControllerFocus>();
                if (this.visibleKeyboard != null) {
                    this.visibleKeyboard.menu.addNextControllerFocus(list, this.visibleKeyboard.drawX, this.visibleKeyboard.drawY, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), false);
                } else if (this.floatMenu != null) {
                    this.floatMenu.menu.addNextControllerFocus(list, this.floatMenu.drawX, this.floatMenu.drawY, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), false);
                } else {
                    this.components.addNextControllerComponents(list, 0, 0, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), false);
                }
                this.refreshFocus(list);
            }
            return;
        }
        if ((event.getState() == ControllerInput.AIM || event.getState() == ControllerInput.CURSOR) && ControllerInput.isCursorVisible()) {
            InputPosition mousePos2 = window.mousePos();
            this.submitInputEvent(InputEvent.MouseMoveEvent(mousePos2, tickManager), tickManager, perspective);
            this.setControllerFocus(null);
            return;
        }
        if (event.isUsed()) {
            return;
        }
        if ((event.getState() == ControllerInput.ATTACK || event.getState() == ControllerInput.MENU_SELECT) && ControllerInput.isCursorVisible()) {
            mousePos = window.mousePos();
            mouseEvent = InputEvent.MouseButtonEvent(0, event.buttonState, mousePos, tickManager);
            if (!event.buttonState) {
                window.getInput().stopRepeatEvent(mouseEvent);
            }
            this.submitInputEvent(mouseEvent, tickManager, perspective);
            if (mouseEvent.isUsed()) {
                event.use();
            }
        }
        if (event.isUsed()) {
            return;
        }
        if (event.getState() == ControllerInput.INTERACT && ControllerInput.isCursorVisible()) {
            mousePos = window.mousePos();
            mouseEvent = InputEvent.MouseButtonEvent(1, event.buttonState, mousePos, tickManager);
            if (!event.buttonState) {
                window.getInput().stopRepeatEvent(mouseEvent);
            }
            this.submitInputEvent(mouseEvent, tickManager, perspective);
            if (mouseEvent.isUsed()) {
                event.use();
            }
        }
        if (event.isUsed()) {
            return;
        }
        if (this.visibleKeyboard != null) {
            this.visibleKeyboard.menu.handleControllerEvent(event, tickManager, perspective);
            if ((event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU) && event.buttonState) {
                this.visibleKeyboard.menu.remove();
                event.use();
            }
        } else if (this.floatMenu != null) {
            this.floatMenu.menu.handleControllerEvent(event, tickManager, perspective);
            if ((event.getState() == ControllerInput.MENU_BACK || event.getState() == ControllerInput.MAIN_MENU) && event.buttonState) {
                this.floatMenu.menu.remove();
                event.use();
            }
        } else {
            this.components.submitControllerEvent(event, tickManager, perspective);
        }
        if (event.isUsed()) {
            return;
        }
        if (event.getState() == ControllerInput.MENU_UP && event.buttonState || event.isRepeatEvent(ControllerInput.MENU_UP)) {
            event.startRepeatEvents(ControllerInput.MENU_UP);
            dir = 0;
        } else if (event.getState() == ControllerInput.MENU_RIGHT && event.buttonState || event.isRepeatEvent(ControllerInput.MENU_RIGHT)) {
            event.startRepeatEvents(ControllerInput.MENU_RIGHT);
            dir = 1;
        } else if (event.getState() == ControllerInput.MENU_DOWN && event.buttonState || event.isRepeatEvent(ControllerInput.MENU_DOWN)) {
            event.startRepeatEvents(ControllerInput.MENU_DOWN);
            dir = 2;
        } else if (event.getState() == ControllerInput.MENU_LEFT && event.buttonState || event.isRepeatEvent(ControllerInput.MENU_LEFT)) {
            event.startRepeatEvents(ControllerInput.MENU_LEFT);
            dir = 3;
        } else {
            dir = -1;
        }
        if (dir != -1) {
            ControllerFocus next;
            event.use();
            LinkedList<ControllerFocus> list = null;
            if (this.currentControllerFocus != null) {
                list = new LinkedList<ControllerFocus>();
                if (this.visibleKeyboard != null) {
                    this.visibleKeyboard.menu.addNextControllerFocus(list, this.visibleKeyboard.drawX, this.visibleKeyboard.drawY, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), false);
                } else if (this.floatMenu != null) {
                    this.floatMenu.menu.addNextControllerFocus(list, this.floatMenu.drawX, this.floatMenu.drawY, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), false);
                } else {
                    this.components.addNextControllerComponents(list, 0, 0, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), false);
                }
                if (list.stream().anyMatch(c -> c.handler == this.currentControllerFocus.handler)) {
                    if (this.currentControllerFocus.handler.handleControllerNavigate(dir, event, tickManager, perspective)) {
                        return;
                    }
                    if (this.currentControllerFocus.customNavigationHandler != null && this.currentControllerFocus.customNavigationHandler.handleNavigate(dir, event, tickManager, perspective)) {
                        return;
                    }
                }
            }
            if (list == null) {
                list = new LinkedList();
                if (this.visibleKeyboard != null) {
                    this.visibleKeyboard.menu.addNextControllerFocus(list, this.visibleKeyboard.drawX, this.visibleKeyboard.drawY, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), false);
                } else if (this.floatMenu != null) {
                    this.floatMenu.menu.addNextControllerFocus(list, this.floatMenu.drawX, this.floatMenu.drawY, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), false);
                } else {
                    this.components.addNextControllerComponents(list, 0, 0, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), false);
                }
            }
            if (this.currentControllerFocus == null) {
                InputPosition mousePos3 = window.mousePos();
                next = list.stream().min(Comparator.comparingDouble(c -> c.getBoundingBoxCenter().distance(mousePos.hudX, mousePos.hudY))).orElse(null);
            } else {
                next = ControllerFocus.getNext(dir, this, list);
            }
            if (next != null) {
                this.setControllerFocus(next);
            }
        }
    }

    public void refreshFocus(List<ControllerFocus> list) {
        ControllerFocus next;
        boolean reset = true;
        if (this.nextControllerFocuses != null) {
            ControllerFocus best = null;
            for (ControllerFocusHandler nextHandler : this.nextControllerFocuses) {
                ControllerFocus next2 = list.stream().filter(c -> c.handler == nextHandler).findFirst().orElse(null);
                long nextLastTime = lastControllerFocuses.getOrDefault(nextHandler.getControllerFocusHashcode(), -1L);
                if (best == null) {
                    best = next2;
                    continue;
                }
                long bestLastTime = lastControllerFocuses.getOrDefault(best.handler.getControllerFocusHashcode(), -1L);
                if (bestLastTime >= nextLastTime) continue;
                best = next2;
            }
            this.nextControllerFocuses = null;
            this.nextControllerFocusesSet = null;
            if (best != null) {
                this.setControllerFocus(best);
                return;
            }
        }
        if (this.currentControllerFocus != null && (next = (ControllerFocus)list.stream().filter(c -> c.handler == this.currentControllerFocus.handler).findFirst().orElse(null)) != null) {
            this.currentControllerFocus = next;
            reset = false;
        }
        if (reset) {
            next = list.stream().map(c -> {
                long time = lastControllerFocuses.getOrDefault(c.handler.getControllerFocusHashcode(), -1L);
                return new ObjectValue<ControllerFocus, Long>((ControllerFocus)c, time);
            }).filter(c -> (Long)c.value != -1L).max(Comparator.comparingLong(c -> (Long)c.value)).map(c -> (ControllerFocus)c.object).orElse(null);
            if (this.currentControllerFocus != null && next == null) {
                lastControllerFocuses.put(this.currentControllerFocus.handler.getControllerFocusHashcode(), System.currentTimeMillis());
                Comparator<ControllerFocus> comparator = Comparator.comparingDouble(c -> {
                    Rectangle intersection = c.boundingBox.intersection(this.currentControllerFocus.boundingBox);
                    if (intersection.isEmpty()) {
                        return 0.0;
                    }
                    double cArea = (double)c.boundingBox.width * (double)c.boundingBox.height;
                    double iArea = (double)intersection.width * (double)intersection.height;
                    return iArea / cArea;
                });
                comparator = comparator.thenComparingDouble(c -> -new Point2D.Double(c.boundingBox.getCenterX(), c.boundingBox.getCenterY()).distance(this.currentControllerFocus.boundingBox.getCenterX(), this.currentControllerFocus.boundingBox.getCenterY()));
                next = list.stream().max(comparator).orElse(null);
            }
            if (next == null) {
                next = list.stream().max(Comparator.comparingInt(f -> f.initialFocusPriority)).orElse(null);
            }
            this.setControllerFocus(next);
        }
    }

    public void prioritizeControllerFocus(ControllerFocusHandler ... handlers) {
        if (handlers == null) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        for (ControllerFocusHandler handler : handlers) {
            if (handler == null) continue;
            lastControllerFocuses.put(handler.getControllerFocusHashcode(), currentTime--);
        }
    }

    public void tryPrioritizeControllerFocus(ControllerFocusHandler ... handlers) {
        if (handlers == null) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        for (ControllerFocusHandler handler : handlers) {
            if (handler == null || lastControllerFocuses.containsKey(handler.getControllerFocusHashcode())) continue;
            lastControllerFocuses.put(handler.getControllerFocusHashcode(), currentTime--);
        }
    }

    public void setControllerFocus(ControllerFocus focus) {
        if (this.currentControllerFocus != null) {
            this.currentControllerFocus.handler.onControllerUnfocused(this.currentControllerFocus);
        }
        this.currentControllerFocus = focus;
        if (focus != null && !focus.boundingBox.contains(Input.mousePos.hudX, Input.mousePos.hudY)) {
            Point center = focus.getBoundingBoxCenter();
            InputPosition position = InputPosition.fromHudPos(WindowManager.getWindow(), center.x, center.y);
            WindowManager.getWindow().getInput().setCursorPosition(position.windowX, position.windowY, null);
        }
        if (this.currentControllerFocus != null) {
            lastControllerFocuses.put(this.currentControllerFocus.handler.getControllerFocusHashcode(), System.currentTimeMillis());
            this.currentControllerFocus.handler.onControllerFocused(this.currentControllerFocus);
        }
    }

    public void setNextControllerFocus(ControllerFocusHandler ... handlers) {
        boolean update = false;
        if (this.nextControllerFocuses == null) {
            this.nextControllerFocuses = new ArrayList(handlers.length);
            this.nextControllerFocusesSet = new HashSet();
        } else {
            this.nextControllerFocuses.ensureCapacity(this.nextControllerFocuses.size() + handlers.length);
        }
        for (ControllerFocusHandler handler : handlers) {
            if (!this.nextControllerFocusesSet.add(handler)) continue;
            this.nextControllerFocuses.add(handler);
            update = true;
        }
        if (update) {
            ControllerInput.submitNextRefreshFocusEvent();
        }
    }

    public boolean isControllerFocus(ControllerFocusHandler handler) {
        if (!Input.lastInputIsController || !ControllerInput.isLayerActive(ControllerInput.MENU_SET_LAYER)) {
            return false;
        }
        if (handler == null) {
            return this.currentControllerFocus == null;
        }
        return this.currentControllerFocus != null && this.currentControllerFocus.handler == handler;
    }

    public ControllerFocus getCurrentFocus() {
        if (!Input.lastInputIsController || !ControllerInput.isLayerActive(ControllerInput.MENU_SET_LAYER)) {
            return null;
        }
        return this.currentControllerFocus;
    }

    public boolean isControllerTyping() {
        return this.visibleKeyboard != null;
    }

    public boolean isControllerTyping(ControllerFocusHandler handler) {
        if (this.visibleKeyboard != null) {
            return this.visibleKeyboard.menu.disposeFocus == handler;
        }
        return false;
    }

    public boolean isMouseOver() {
        return this.isMouseOver;
    }

    public boolean isMouseOver(InputEvent event) {
        if (this.visibleKeyboard != null && this.visibleKeyboard.menu.isMouseOver(event)) {
            return true;
        }
        if (this.floatMenu != null && this.floatMenu.menu.isMouseOver(event)) {
            return true;
        }
        for (FormComponent form : this.components) {
            if (!form.shouldDraw() || !form.isMouseOver(event)) continue;
            return true;
        }
        return false;
    }

    public boolean isMouseOver(InputPosition pos) {
        return this.isMouseOver(InputEvent.MouseMoveEvent(pos, null));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void draw(TickManager tickManager, PlayerMob perspective) {
        InventoryItem draggingItem;
        GameResources.formShader.use();
        GameWindow window = WindowManager.getWindow();
        try {
            this.components.drawComponents(tickManager, perspective, new Rectangle(window.getHudWidth(), window.getHudHeight()));
            if (this.floatMenu != null) {
                this.floatMenu.menu.draw(tickManager, perspective);
            }
            if (this.visibleKeyboard != null) {
                Renderer.initQuadDraw(window.getHudWidth(), window.getHudHeight()).color(new Color(0, 0, 0, 70)).draw(0, 0);
                this.visibleKeyboard.menu.draw(tickManager, perspective);
            }
        }
        finally {
            GameResources.formShader.stop();
        }
        if (drawControllerFocusBoxes || drawControllerAreaBoxes) {
            LinkedList<ControllerFocus> list = new LinkedList<ControllerFocus>();
            if (this.floatMenu != null) {
                this.floatMenu.menu.addNextControllerFocus(list, this.floatMenu.drawX, this.floatMenu.drawY, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), drawControllerAreaBoxes);
            } else {
                this.components.addNextControllerComponents(list, 0, 0, null, new Rectangle(window.getHudWidth(), window.getHudHeight()), drawControllerAreaBoxes);
            }
            if (drawControllerFocusBoxes) {
                for (ControllerFocus c : list) {
                    if (this.currentControllerFocus != null && c.handler == this.currentControllerFocus.handler) continue;
                    Rectangle box = c.boundingBox;
                    Renderer.drawShape(box, false, 1.0f, 1.0f, 0.0f, 1.0f);
                }
            }
        }
        Runnable drawDraggingItem = null;
        if (GlobalData.getCurrentState().isRunning() && perspective != null && perspective.isInventoryExtended() && (draggingItem = perspective.getDraggingItem()) != null) {
            ControllerFocus currentFocus = this.getCurrentFocus();
            if (currentFocus != null && !ControllerInput.isCursorVisible()) {
                Point boundingBoxCenter = currentFocus.getBoundingBoxCenter();
                drawDraggingItem = () -> draggingItem.draw(perspective, boundingBoxCenter.x - 16, boundingBoxCenter.y - 28, false, true);
            } else {
                InputPosition mousePos = window.mousePos();
                drawDraggingItem = () -> draggingItem.draw(perspective, mousePos.hudX - 16, mousePos.hudY - 16, false, true);
            }
        }
        if (Input.lastInputIsController && ControllerInput.isLayerActive(ControllerInput.MENU_SET_LAYER) && this.currentControllerFocus != null) {
            Point controllerTooltipPoint = this.currentControllerFocus.getTooltipAndFloatMenuPoint();
            if (controllerTooltipPoint == null) {
                controllerTooltipPoint = this.currentControllerFocus.boundingBox.getLocation();
            }
            GameTooltipManager.setTooltipsFormFocus(controllerTooltipPoint.x, controllerTooltipPoint.y - (drawDraggingItem != null ? 4 : 0));
            if (drawDraggingItem != null) {
                GameTooltipManager.setTooltipFocusOffset(0, 4);
            }
            this.currentControllerFocus.handler.drawControllerFocus(this.currentControllerFocus);
        } else if (drawDraggingItem != null) {
            GameTooltipManager.setTooltipFocusOffset(0, 12);
        }
        if (drawDraggingItem != null) {
            drawDraggingItem.run();
        }
    }

    public Timeout setTimeout(Runnable runnable, long time) {
        Timeout timeout = new Timeout(runnable, time);
        this.timeouts.add(timeout);
        this.timeouts.sort(Comparator.comparingLong(t -> t.time));
        return timeout;
    }

    public boolean clearTimeout(Timeout timeout) {
        return this.timeouts.remove(timeout);
    }

    public void onWindowResized(GameWindow window) {
        this.components.onWindowResized(window);
    }

    public void dispose() {
        this.isDisposed = true;
        if (this.floatMenu != null) {
            this.floatMenu.menu.dispose();
        }
        if (this.visibleKeyboard != null) {
            this.visibleKeyboard.menu.dispose();
        }
        this.components.disposeComponents();
    }

    public boolean isDisposed() {
        return this.isDisposed;
    }

    public void onComponentDispose(FormComponent component) {
        if (this.floatMenu != null && this.floatMenu.menu.parent == component) {
            this.floatMenu.menu.remove();
        }
        if (this.visibleKeyboard != null && this.visibleKeyboard.menu.parent == component) {
            this.visibleKeyboard.menu.remove();
        }
    }

    static {
        lastControllerFocuses = new HashMap();
    }

    private static class CurrentFloatMenu {
        public final long tick;
        public final long frame;
        public final FloatMenu menu;
        public final int drawX;
        public final int drawY;

        public CurrentFloatMenu(FloatMenu menu, int drawX, int drawY, TickManager tickManager) {
            this.menu = menu;
            this.drawX = drawX;
            this.drawY = drawY;
            this.tick = tickManager.getTotalTicks();
            this.frame = tickManager.getTotalFrames();
        }

        public boolean isSameStartTime(InputEvent event) {
            return this.frame == event.frame;
        }
    }

    public static class Timeout {
        public final Runnable runnable;
        public final long time;

        public Timeout(Runnable runnable, long time) {
            this.runnable = runnable;
            this.time = System.currentTimeMillis() + time;
        }
    }
}

