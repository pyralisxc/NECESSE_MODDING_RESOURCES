/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.forms.presets.debug.tools;

import java.awt.Color;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameRandom;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.GenerationTools;

public class GenerationTesterGameTool
extends MouseDebugGameTool {
    private boolean setServer;

    public GenerationTesterGameTool(DebugForm parent) {
        super(parent, "Generation tester");
        this.onLeftClick(e -> {
            Level sLevel;
            long seed = GameRandom.globalRandom.nextLong();
            Level cLevel = this.getLevel();
            if (cLevel != null) {
                GenerationTools.placeRandomObjectVeinOnTile(cLevel, new GameRandom(seed), this.getMouseTileX(), this.getMouseTileY(), 8, 14, TileRegistry.getTileID("grasstile"), ObjectRegistry.getObjectID("stonewall"), 0.25f, false);
            }
            if ((sLevel = this.getServerLevel()) != null && this.setServer) {
                GenerationTools.placeRandomObjectVeinOnTile(sLevel, new GameRandom(seed), this.getMouseTileX(), this.getMouseTileY(), 8, 14, TileRegistry.getTileID("grasstile"), ObjectRegistry.getObjectID("stonewall"), 0.25f, false);
            }
            return true;
        }, "Test generation");
        this.onRightClick(e -> {
            parent.client.reloadMap();
            return true;
        }, "Reload client map");
        this.onScroll(e -> {
            this.setServer = !this.setServer;
            parent.client.setMessage("Override server: " + this.setServer, new Color(255, 255, 255));
            return true;
        }, "Toggle server override");
    }

    @Override
    public void init() {
        this.setServer = false;
    }
}

