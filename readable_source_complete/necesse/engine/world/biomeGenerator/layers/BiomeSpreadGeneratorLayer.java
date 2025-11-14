/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.awt.Point;
import necesse.engine.GameTileRange;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public class BiomeSpreadGeneratorLayer
extends GeneratorLayer {
    protected GameTileRange tileRange;

    public BiomeSpreadGeneratorLayer(int layerSeed, GameTileRange tileRange) {
        super(layerSeed, 0);
        this.tileRange = tileRange;
    }

    @Override
    protected int sample(int x, int y) {
        int[] parents = new int[this.tileRange.size()];
        int currentIndex = 0;
        for (Point tile : this.tileRange.getValidTiles(x, y)) {
            int parent = this.getParentForced(tile.x, tile.y);
            parents[currentIndex++] = parent;
        }
        int index = this.getRandom(x, y).nextInt(parents.length);
        return parents[index];
    }
}

