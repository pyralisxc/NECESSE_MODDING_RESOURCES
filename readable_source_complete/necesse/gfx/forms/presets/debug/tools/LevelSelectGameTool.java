/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import necesse.engine.GlobalData;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameTool;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerButtonState;
import necesse.engine.input.controller.ControllerEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public abstract class LevelSelectGameTool
implements GameTool {
    private boolean isSelecting;
    private int startX;
    private int startY;
    private HudDrawElement hudElement;
    private final int selectEventID;
    private final HashSet<ControllerButtonState> selectControllerState;

    public LevelSelectGameTool(int selectEventID, ControllerButtonState ... selectControllerStates) {
        this.selectEventID = selectEventID;
        this.selectControllerState = selectControllerStates == null || selectControllerStates.length == 0 ? new HashSet() : new HashSet<ControllerButtonState>(Arrays.asList(selectControllerStates));
    }

    public LevelSelectGameTool() {
        this(-100, ControllerInput.MENU_NEXT);
    }

    @Override
    public boolean inputEvent(InputEvent event) {
        if (event.getID() == this.selectEventID) {
            if (event.state && !GlobalData.getCurrentState().getFormManager().isMouseOver(event)) {
                this.startX = this.getMouseX();
                this.startY = this.getMouseY();
                this.isSelecting = true;
                return true;
            }
            if (this.isSelecting) {
                int startX = Math.min(this.startX, this.getMouseX());
                int startY = Math.min(this.startY, this.getMouseY());
                int endX = Math.max(this.startX, this.getMouseX());
                int endY = Math.max(this.startY, this.getMouseY());
                this.onSelection(startX, startY, endX, endY);
                this.isSelecting = false;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean controllerEvent(ControllerEvent event) {
        if (this.selectControllerState.isEmpty()) {
            return false;
        }
        if (event.isButton && this.selectControllerState.contains(event.getState())) {
            if (event.buttonState && !GlobalData.getCurrentState().getFormManager().isMouseOver(WindowManager.getWindow().mousePos())) {
                this.startX = this.getMouseX();
                this.startY = this.getMouseY();
                this.isSelecting = true;
                return true;
            }
            if (this.isSelecting) {
                int startX = Math.min(this.startX, this.getMouseX());
                int startY = Math.min(this.startY, this.getMouseY());
                int endX = Math.max(this.startX, this.getMouseX());
                int endY = Math.max(this.startY, this.getMouseY());
                this.onSelection(startX, startY, endX, endY);
                this.isSelecting = false;
                return true;
            }
        }
        return false;
    }

    public boolean isSelecting() {
        return this.isSelecting;
    }

    public void clearSelecting() {
        this.isSelecting = false;
    }

    @Override
    public void init() {
        this.startX = Integer.MIN_VALUE;
        this.startY = Integer.MIN_VALUE;
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, final PlayerMob perspective) {
                if (LevelSelectGameTool.this.isSelecting) {
                    Dimension maxSize = LevelSelectGameTool.this.getMaxSize();
                    int mouseX = LevelSelectGameTool.this.getMouseX();
                    int mouseY = LevelSelectGameTool.this.getMouseY();
                    if (maxSize != null) {
                        int deltaY;
                        int deltaX = mouseX - LevelSelectGameTool.this.startX;
                        if (Math.abs(deltaX) > maxSize.width) {
                            mouseX = deltaX > 0 ? (mouseX -= deltaX - maxSize.width) : (mouseX += -deltaX - maxSize.width);
                        }
                        if (Math.abs(deltaY = mouseY - LevelSelectGameTool.this.startY) > maxSize.height) {
                            mouseY = deltaY > 0 ? (mouseY -= deltaY - maxSize.height) : (mouseY += -deltaY - maxSize.height);
                        }
                    }
                    final int startX = Math.min(LevelSelectGameTool.this.startX, mouseX);
                    final int startY = Math.min(LevelSelectGameTool.this.startY, mouseY);
                    final int endX = Math.max(LevelSelectGameTool.this.startX, mouseX);
                    final int endY = Math.max(LevelSelectGameTool.this.startY, mouseY);
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return -10000;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            LevelSelectGameTool.this.drawSelection(camera, perspective, startX, startY, endX, endY);
                        }
                    });
                }
            }
        };
        this.getLevel().hudManager.addElement(this.hudElement);
    }

    public abstract Level getLevel();

    public abstract void onSelection(int var1, int var2, int var3, int var4);

    public abstract void drawSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6);

    public Dimension getMaxSize() {
        return null;
    }

    @Override
    public void isCancelled() {
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }

    @Override
    public void isCleared() {
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }
}

