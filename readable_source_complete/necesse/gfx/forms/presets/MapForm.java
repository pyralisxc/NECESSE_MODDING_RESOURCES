/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import necesse.engine.Settings;
import necesse.engine.input.InputEvent;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.client.Client;
import necesse.engine.window.GameWindow;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormMapBox;
import necesse.gfx.forms.components.FormTextureMapBox;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.controller.ControllerFocus;
import necesse.gfx.forms.controller.ControllerNavigationHandler;
import necesse.gfx.forms.position.FormPositionContainer;
import necesse.gfx.ui.ButtonColor;

public class MapForm
extends Form {
    public final boolean isMinimap;
    private final Form mapForm;
    public final FormTextureMapBox map;
    private final FormInputSize buttonSize;
    private final ArrayList<FormPositionContainer> buttons;
    private FormFlow buttonFlow;
    private FormLocalTextButton hide;
    private final FormLocalTextButton center;
    private final FormLocalTextButton zoomOut;
    private final FormLocalTextButton zoomIn;

    public MapForm(String name, Client client, int size, FormInputSize buttonSize, boolean canHide, int[] zoomLevels, int zoomLevel, final Consumer<Integer> saveZoomLevel, boolean allowControllerFocus, boolean isMinimap) {
        super(name, size, size + 10);
        this.drawBase = false;
        this.shouldLimitDrawArea = false;
        this.buttonSize = buttonSize;
        this.isMinimap = isMinimap;
        this.buttons = new ArrayList();
        this.buttonFlow = new FormFlow(0);
        this.mapForm = this.addComponent(new Form(name, size, size));
        this.mapForm.setPosition(0, 10);
        this.map = this.mapForm.addComponent(new FormMapBox(client, 0, 0, this.getWidth(), this.getHeight() - 10, zoomLevels, zoomLevel, allowControllerFocus, isMinimap){

            @Override
            public void setCentered(boolean centered) {
                super.setCentered(centered);
                if (MapForm.this.center != null) {
                    MapForm.this.center.setActive(!centered);
                }
            }

            @Override
            public void onZoomLevelChanged(int zoomLevel) {
                super.onZoomLevelChanged(zoomLevel);
                if (saveZoomLevel != null) {
                    saveZoomLevel.accept(zoomLevel);
                }
            }

            @Override
            public Point getCenteredPos() {
                PlayerMob player = this.client.getPlayer();
                if (player != null) {
                    return new Point(player.getX(), player.getY());
                }
                return super.getCenteredPos();
            }
        }, -1000);
        this.map.setCentered(true);
        if (canHide) {
            this.hide = this.addButton(new LocalMessage("ui", "maphide"), 60);
            this.hide.onClicked(e -> {
                if (this.canClickButtons()) {
                    this.setMapHidden(!this.map.isHidden());
                } else {
                    e.preventDefault();
                }
            });
        }
        this.zoomOut = this.addButton("-", buttonSize.height);
        this.zoomOut.onClicked(e -> {
            if (this.canClickButtons()) {
                this.map.zoomOut();
            } else {
                e.preventDefault();
            }
        });
        this.zoomIn = this.addButton("+", buttonSize.height);
        this.zoomIn.onClicked(e -> {
            if (this.canClickButtons()) {
                this.map.zoomIn();
            } else {
                e.preventDefault();
            }
        });
        this.center = this.addButton("c", buttonSize.height);
        this.center.onClicked(e -> {
            if (this.canClickButtons()) {
                this.map.setCentered(true);
            } else {
                e.preventDefault();
            }
        });
        this.center.setActive(!this.map.isCentered());
        this.onWindowResized(WindowManager.getWindow());
    }

    public static MapForm getMiniMapForm(String name, Client client, int size) {
        return new MapForm(name, client, size, FormInputSize.SIZE_16, true, new int[]{2, 4, 6, 8, 12, 16, 20, 24, 28, 32}, Settings.minimapZoomLevel, level -> {
            Settings.minimapZoomLevel = level;
        }, false, true){

            @Override
            public void onWindowResized(GameWindow window) {
                super.onWindowResized(window);
                this.setPosition(WindowManager.getWindow().getHudWidth() - this.getWidth() - 10, 10);
            }

            @Override
            protected void onMapHidden(boolean hidden) {
                if (Settings.minimapHidden != hidden) {
                    Settings.minimapHidden = hidden;
                    Settings.saveClientSettings();
                }
            }

            @Override
            public void addNextControllerFocus(List<ControllerFocus> list, int currentXOffset, int currentYOffset, ControllerNavigationHandler customNavigationHandler, Rectangle area, boolean draw) {
                if (ControllerInput.isCursorVisible()) {
                    super.addNextControllerFocus(list, currentXOffset, currentYOffset, customNavigationHandler, area, draw);
                }
            }
        };
    }

    public static MapForm getFullMapForm(String name, Client client) {
        GameWindow window = WindowManager.getWindow();
        int screenSize = Math.min(window.getHudWidth(), window.getHudHeight());
        return new MapForm(name, client, screenSize - 50, FormInputSize.SIZE_20, false, new int[]{2, 4, 6, 8, 12, 16, 20, 24, 28, 32, 48, 64, 96, 128, 192, 256}, Settings.islandMapZoomLevel, level -> {
            Settings.islandMapZoomLevel = level;
        }, true, false){

            @Override
            public void onWindowResized(GameWindow window) {
                super.onWindowResized(window);
                int screenSize = Math.min(window.getHudWidth(), window.getHudHeight());
                this.setSize(screenSize - 50);
                this.setPosMiddle(window.getHudWidth() / 2, window.getHudHeight() / 2);
            }
        };
    }

    public FormLocalTextButton addButton(String text, int width) {
        return this.addButton(new StaticMessage(text), width);
    }

    public FormLocalTextButton addButton(GameMessage text, int width) {
        return this.addButton(new FormLocalTextButton(text, 0, 0, width, this.buttonSize, ButtonColor.BASE));
    }

    public <T extends FormPositionContainer> T addButton(T button) {
        this.buttons.add(button);
        this.buttonFlow.nextX(button, 2);
        button.setY(0);
        this.addComponent((FormComponent)((Object)button), 1000);
        return button;
    }

    public void removeButton(FormPositionContainer button) {
        if (this.buttons.remove(button)) {
            this.removeComponent((FormComponent)((Object)button));
            this.updateButtonPositions();
        }
    }

    private void updateButtonPositions() {
        this.buttonFlow = new FormFlow(0);
        for (FormPositionContainer button : this.buttons) {
            this.buttonFlow.nextX(button, 2);
            button.setY(0);
        }
    }

    public void setMapHidden(boolean hidden) {
        this.mapForm.setHidden(hidden);
        this.map.setHidden(hidden);
        this.onMapHidden(hidden);
        this.zoomOut.setActive(!hidden);
        this.zoomIn.setActive(!hidden);
        if (this.hide != null) {
            this.hide.setLocalization("ui", hidden ? "mapshow" : "maphide");
        }
    }

    public boolean isMinimized() {
        return this.mapForm.isHidden();
    }

    protected void onMapHidden(boolean hidden) {
    }

    public boolean canClickButtons() {
        return !this.map.isMouseDown();
    }

    public void setSize(int size) {
        this.setWidth(size);
        this.setHeight(size);
        this.mapForm.setWidth(size);
        this.mapForm.setHeight(size);
        this.map.setWidth(size);
        this.map.setHeight(size + 10);
    }

    @Override
    public boolean isMouseOver(InputEvent event) {
        InputEvent offsetEvent = this.getComponentList().offsetEvent(event, false);
        for (FormComponent component : this.getComponents()) {
            if (!component.isMouseOver(offsetEvent)) continue;
            return true;
        }
        return !this.mapForm.isHidden() && super.isMouseOver(event);
    }
}

