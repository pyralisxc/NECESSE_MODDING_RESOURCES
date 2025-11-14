/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.util.GameMath;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public class PresetPasteDebugGameTool
extends MouseDebugGameTool {
    private ArrayList<Preset> presets;
    private int selected;
    private HudDrawElement hudElement;
    private boolean showBounds;
    private final MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

    public PresetPasteDebugGameTool(DebugForm parent) {
        super(parent, "Paste preset");
        this.onLeftClick(e -> {
            try {
                Preset preset = this.presets.get(this.selected);
                Point placeTile = this.getPlaceTile(preset);
                PresetUtils.placePresetFromClient(parent.client, preset, placeTile.x, placeTile.y);
            }
            catch (Exception applyEx) {
                parent.client.setMessage("Error pasting preset: " + applyEx.getMessage(), Color.WHITE);
                applyEx.printStackTrace();
            }
            return true;
        }, "Place preset");
        this.onRightClick(e -> {
            GameMessage message = PresetUtils.undoLatestPresetFromClient(parent.client);
            if (message != null) {
                parent.client.setMessage(message.translate(), Color.WHITE);
            }
            return true;
        }, "Undo preset");
        this.onScroll(e -> {
            this.wheelBuffer.add((InputEvent)e);
            this.wheelBuffer.useScrollY(isPositive -> {
                int sign = isPositive ? 1 : -1;
                this.selected = Math.floorMod(this.selected + sign, this.presets.size());
                this.updateScrollUsage();
            });
            return true;
        }, null);
        this.onKeyClick(82, e -> {
            try {
                if (WindowManager.getWindow().isKeyDown(340)) {
                    this.presets.set(this.selected, this.presets.get(this.selected).rotate(PresetRotation.ANTI_CLOCKWISE));
                } else {
                    this.presets.set(this.selected, this.presets.get(this.selected).rotate(PresetRotation.CLOCKWISE));
                }
            }
            catch (PresetRotateException ex) {
                this.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, ex.getMessage(), new FontOptions(16).outline()));
            }
            return true;
        }, "Rotate");
        this.onKeyClick(84, e -> {
            try {
                this.presets.set(this.selected, this.presets.get(this.selected).rotate(PresetRotation.HALF_180));
            }
            catch (PresetRotateException ex) {
                this.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, ex.getMessage(), new FontOptions(16).outline()));
            }
            return true;
        }, "Rotate 180");
        this.onKeyClick(86, e -> {
            try {
                this.presets.set(this.selected, this.presets.get(this.selected).mirrorY());
            }
            catch (PresetMirrorException ex) {
                this.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, ex.getMessage(), new FontOptions(16).outline()));
            }
            return true;
        }, "Change Y Mirror");
        this.onKeyClick(72, e -> {
            try {
                this.presets.set(this.selected, this.presets.get(this.selected).mirrorX());
            }
            catch (PresetMirrorException ex) {
                this.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, ex.getMessage(), new FontOptions(16).outline()));
            }
            return true;
        }, "Change X Mirror");
        this.onKeyClick(66, e -> {
            this.showBounds = !this.showBounds;
            return true;
        }, "Toggle preset bounds");
        this.onKeyClick(81, e -> {
            if (this.hudElement != null) {
                this.hudElement.remove();
            }
            this.init();
            return true;
        }, "Reload presets");
    }

    @Override
    public void init() {
        String clipboard = WindowManager.getWindow().getClipboard();
        if (clipboard == null) {
            clipboard = "";
        }
        this.presets = new ArrayList();
        try {
            Preset e = new Preset(clipboard);
            this.presets.add(e);
        }
        catch (Exception presetEx) {
            this.parent.client.setMessage("Clipboard does not contain a preset", Color.WHITE);
        }
        if (this.presets.isEmpty()) {
            GameToolManager.clearGameTool(this);
        } else {
            this.selected = Math.min(this.selected, this.presets.size() - 1);
            this.updateScrollUsage();
            this.parent.client.setMessage("Presets with mobs and other non-parseable settings will not be pasted correctly from non-hosting clients", Color.WHITE);
            this.hudElement = new HudDrawElement(){

                @Override
                public void addDrawables(List<SortedDrawable> list, final GameCamera camera, final PlayerMob perspective) {
                    final Preset preset = (Preset)PresetPasteDebugGameTool.this.presets.get(PresetPasteDebugGameTool.this.selected);
                    final Point placeTile = PresetPasteDebugGameTool.this.getPlaceTile(preset);
                    final TextureDrawOptionsEnd canApplyOptions = preset.canApplyToLevel(this.getLevel(), placeTile.x, placeTile.y) ? null : Renderer.initQuadDraw(preset.width * 32, preset.height * 32).color(1.0f, 0.0f, 0.0f, 0.5f).pos(camera.getTileDrawX(placeTile.x), camera.getTileDrawY(placeTile.y));
                    final Rectangle boundsRectangle = new Rectangle(camera.getTileDrawX(placeTile.x), camera.getTileDrawY(placeTile.y), preset.width * 32, preset.height * 32);
                    list.add(new SortedDrawable(){

                        @Override
                        public int getPriority() {
                            return Integer.MIN_VALUE;
                        }

                        @Override
                        public void draw(TickManager tickManager) {
                            preset.drawPlacePreview(this.getLevel(), placeTile.x, placeTile.y, perspective, camera);
                            if (canApplyOptions != null) {
                                canApplyOptions.draw();
                            }
                            if (PresetPasteDebugGameTool.this.showBounds) {
                                Renderer.drawRectangleLines(boundsRectangle, 1.0f, 0.0f, 0.0f, 1.0f);
                            }
                        }
                    });
                }
            };
            this.getLevel().hudManager.addElement(this.hudElement);
        }
    }

    protected void updateScrollUsage() {
        this.scrollUsage = "Select preset - " + this.presets.get(this.selected).getClass().getSimpleName();
    }

    @Override
    public void isCancelled() {
        super.isCancelled();
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }

    @Override
    public void isCleared() {
        super.isCleared();
        if (this.hudElement != null) {
            this.hudElement.remove();
        }
    }

    public Point getPlaceTile(Preset preset) {
        return new Point(GameMath.getTileCoordinate(this.getMouseX() - preset.width * 32 / 2 + 16), GameMath.getTileCoordinate(this.getMouseY() - preset.height * 32 / 2 + 16));
    }
}

