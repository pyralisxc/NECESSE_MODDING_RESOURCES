/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.gameTool.GameToolManager;
import necesse.engine.input.controller.ControllerInput;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.client.Client;
import necesse.engine.util.GameMath;
import necesse.engine.window.WindowManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.tools.ModularGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.hudManager.floatText.UniqueFloatText;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetMirrorException;
import necesse.level.maps.presets.PresetRotateException;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public class PresetPasteGameTool
extends ModularGameTool {
    protected Client client;
    private Preset preset;
    private HudDrawElement hudElement;
    private boolean showBounds;

    public PresetPasteGameTool(Client client, Preset preset) {
        this.client = client;
        this.preset = preset;
        this.onLeftClick(e -> {
            Point placeTile = this.getPlaceTile(this.preset);
            PresetUtils.placePresetFromClient(client, this.preset, placeTile.x, placeTile.y);
            return true;
        }, new LocalMessage("ui", "presetplace"));
        this.onRightClick(e -> {
            GameToolManager.clearGameTool(this);
            return true;
        }, null);
        this.onKeyClick(82, ControllerInput.MENU_SELECT, e -> {
            try {
                this.preset = WindowManager.getWindow().isKeyDown(340) ? this.preset.rotate(PresetRotation.ANTI_CLOCKWISE) : this.preset.rotate(PresetRotation.CLOCKWISE);
            }
            catch (PresetRotateException ex) {
                client.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, ex.getGameMessage().translate(), new FontOptions(16).outline()));
            }
            return true;
        }, new LocalMessage("ui", "presetrotate"));
        this.onKeyClick(84, null, e -> {
            try {
                this.preset = this.preset.rotate(PresetRotation.HALF_180);
            }
            catch (PresetRotateException ex) {
                client.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, ex.getGameMessage().translate(), new FontOptions(16).outline()));
            }
            return true;
        }, new LocalMessage("ui", "presetrotate180"));
        this.onKeyClick(86, ControllerInput.MENU_ITEM_ACTIONS_MENU, e -> {
            try {
                this.preset = this.preset.mirrorY();
            }
            catch (PresetMirrorException ex) {
                client.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, ex.getGameMessage().translate(), new FontOptions(16).outline()));
            }
            return true;
        }, new LocalMessage("ui", "presetmirrory"));
        this.onKeyClick(72, ControllerInput.INVENTORY, e -> {
            try {
                this.preset = this.preset.mirrorX();
            }
            catch (PresetMirrorException ex) {
                client.getLevel().hudManager.addElement(new UniqueFloatText(this.getMouseX(), this.getMouseY() - 4, ex.getGameMessage().translate(), new FontOptions(16).outline()));
            }
            return true;
        }, new LocalMessage("ui", "presetmirrorx"));
        this.onKeyClick(66, null, e -> {
            this.showBounds = !this.showBounds;
            return true;
        }, null);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.client.getPlayer().isInventoryExtended()) {
            GameToolManager.clearGameTool(this);
        }
    }

    @Override
    public void init() {
        this.hudElement = new HudDrawElement(){

            @Override
            public void addDrawables(List<SortedDrawable> list, final GameCamera camera, final PlayerMob perspective) {
                final Point placeTile = PresetPasteGameTool.this.getPlaceTile(PresetPasteGameTool.this.preset);
                final TextureDrawOptionsEnd canApplyOptions = PresetPasteGameTool.this.preset.canApplyToLevel(this.getLevel(), placeTile.x, placeTile.y) ? null : Renderer.initQuadDraw(((PresetPasteGameTool)PresetPasteGameTool.this).preset.width * 32, ((PresetPasteGameTool)PresetPasteGameTool.this).preset.height * 32).color(1.0f, 0.0f, 0.0f, 0.5f).pos(camera.getTileDrawX(placeTile.x), camera.getTileDrawY(placeTile.y));
                final Rectangle boundsRectangle = new Rectangle(camera.getTileDrawX(placeTile.x), camera.getTileDrawY(placeTile.y), ((PresetPasteGameTool)PresetPasteGameTool.this).preset.width * 32, ((PresetPasteGameTool)PresetPasteGameTool.this).preset.height * 32);
                list.add(new SortedDrawable(){

                    @Override
                    public int getPriority() {
                        return Integer.MIN_VALUE;
                    }

                    @Override
                    public void draw(TickManager tickManager) {
                        PresetPasteGameTool.this.preset.drawPlacePreview(this.getLevel(), placeTile.x, placeTile.y, perspective, camera);
                        if (canApplyOptions != null) {
                            canApplyOptions.draw();
                        }
                        if (PresetPasteGameTool.this.showBounds) {
                            Renderer.drawRectangleLines(boundsRectangle, 1.0f, 0.0f, 0.0f, 1.0f);
                        }
                    }
                });
            }
        };
        this.client.getLevel().hudManager.addElement(this.hudElement);
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

    public Point getPlaceTile(Preset preset) {
        return new Point(GameMath.getTileCoordinate(this.getMouseX() - preset.width * 32 / 2 + 16), GameMath.getTileCoordinate(this.getMouseY() - preset.height * 32 / 2 + 16));
    }

    @Override
    public boolean shouldShowWires() {
        return true;
    }
}

