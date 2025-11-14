/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import necesse.engine.input.InputEvent;
import necesse.engine.input.MouseWheelBuffer;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.LevelSelectTilesDebugGameTool;
import necesse.gfx.ui.HUD;
import necesse.level.maps.Level;

public class UpdateLightGameTool
extends LevelSelectTilesDebugGameTool {
    private LevelsApplier levelsApplier = LevelsApplier.ONLY_CLIENT;
    private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

    public UpdateLightGameTool(DebugForm parent) {
        super(parent, "Update light");
    }

    @Override
    public void init() {
        super.init();
        this.onScroll(e -> {
            this.wheelBuffer.add((InputEvent)e);
            LevelsApplier[] values = LevelsApplier.values();
            this.levelsApplier = values[Math.floorMod(this.levelsApplier.ordinal() + this.wheelBuffer.useAllScrollY(), values.length)];
            this.setScrollUsage(this.levelsApplier.displayName);
            return true;
        }, this.levelsApplier.displayName);
    }

    @Override
    public void onTileSelection(int tileStartX, int tileStartY, int tileEndX, int tileEndY) {
        this.levelsApplier.applier.accept(this, level -> level.lightManager.updateStaticLight(tileStartX, tileStartY, tileEndX, tileEndY, true));
        int width = tileEndX - tileStartX + 1;
        int height = tileEndY - tileStartY + 1;
        this.parent.client.setMessage("Updated light in a " + width + "x" + height + " area", Color.WHITE);
    }

    @Override
    public void drawTileSelection(GameCamera camera, PlayerMob perspective, int tileStartX, int tileStartY, int tileEndX, int tileEndY) {
        Renderer.initQuadDraw((tileEndX - tileStartX + 1) * 32, (tileEndY - tileStartY + 1) * 32).color(1.0f, 1.0f, 1.0f, 0.2f).draw(camera.getTileDrawX(tileStartX), camera.getTileDrawY(tileStartY));
        HUD.tileBoundOptions(camera, tileStartX, tileStartY, tileEndX, tileEndY).draw();
    }

    private static enum LevelsApplier {
        ONLY_CLIENT("Only client level", (tool, consumer) -> {
            Level clientLevel = tool.getLevel();
            if (clientLevel != null) {
                consumer.accept(clientLevel);
            }
        }),
        ONLY_SERVER("Only server level", (tool, consumer) -> {
            Level serverLevel = tool.getServerLevel();
            if (serverLevel != null) {
                consumer.accept(serverLevel);
            }
        }),
        BOTH("Both client and server level", (tool, consumer) -> {
            Level serverLevel;
            Level clientLevel = tool.getLevel();
            if (clientLevel != null) {
                consumer.accept(clientLevel);
            }
            if ((serverLevel = tool.getServerLevel()) != null) {
                consumer.accept(serverLevel);
            }
        });

        public final BiConsumer<UpdateLightGameTool, Consumer<Level>> applier;
        public final String displayName;

        private LevelsApplier(String displayName, BiConsumer<UpdateLightGameTool, Consumer<Level>> applier) {
            this.displayName = displayName;
            this.applier = applier;
        }
    }
}

