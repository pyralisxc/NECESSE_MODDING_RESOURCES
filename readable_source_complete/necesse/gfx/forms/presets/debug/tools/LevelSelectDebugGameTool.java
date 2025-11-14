/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import necesse.engine.input.InputEvent;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.LevelSelectGameTool;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.level.maps.Level;

public abstract class LevelSelectDebugGameTool
extends MouseDebugGameTool {
    private final LevelSelectGameTool levelSelectGameTool;

    public LevelSelectDebugGameTool(DebugForm parent, String name) {
        super(parent, name);
        this.onLeftEvent(e -> true, "Select area");
        this.levelSelectGameTool = new LevelSelectGameTool(-100, null){

            @Override
            public Level getLevel() {
                return LevelSelectDebugGameTool.this.getLevel();
            }

            @Override
            public void onSelection(int startX, int startY, int endX, int endY) {
                LevelSelectDebugGameTool.this.onSelection(startX, startY, endX, endY);
            }

            @Override
            public void drawSelection(GameCamera camera, PlayerMob perspective, int startX, int startY, int endX, int endY) {
                LevelSelectDebugGameTool.this.drawSelection(camera, perspective, startX, startY, endX, endY);
            }

            @Override
            public GameTooltips getTooltips() {
                return LevelSelectDebugGameTool.this.getTooltips();
            }
        };
    }

    @Override
    public void init() {
        this.levelSelectGameTool.init();
    }

    @Override
    public boolean inputEvent(InputEvent event) {
        if (this.levelSelectGameTool.inputEvent(event)) {
            return true;
        }
        return super.inputEvent(event);
    }

    public abstract void onSelection(int var1, int var2, int var3, int var4);

    public abstract void drawSelection(GameCamera var1, PlayerMob var2, int var3, int var4, int var5, int var6);
}

