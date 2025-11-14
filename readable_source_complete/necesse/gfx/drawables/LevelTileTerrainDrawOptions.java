/*
 * Decompiled with CFR 0.152.
 */
package necesse.gfx.drawables;

import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SpecialSharedDrawables;
import necesse.level.gameTile.GameTile;

public class LevelTileTerrainDrawOptions
extends SharedTextureDrawOptions {
    public final SpecialSharedDrawables specialDrawables = new SpecialSharedDrawables();

    public LevelTileTerrainDrawOptions() {
        super(GameTile.generatedTileTexture);
    }

    @Override
    public void draw(int maxDrawsPerCall) {
        super.draw(maxDrawsPerCall);
        this.specialDrawables.draw(maxDrawsPerCall);
    }
}

