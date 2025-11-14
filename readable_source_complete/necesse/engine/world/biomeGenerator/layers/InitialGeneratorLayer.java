/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public class InitialGeneratorLayer
extends GeneratorLayer {
    protected int waterTileID;
    protected int[] biomeTileIDs;
    protected float chance;

    public InitialGeneratorLayer(int layerSeed, float chance, int waterTileID, int[] biomeTileIDs) {
        super(layerSeed, 0);
        this.chance = chance;
        this.waterTileID = waterTileID;
        this.biomeTileIDs = biomeTileIDs;
    }

    @Override
    protected int sample(int x, int y) {
        GameRandom random = this.getRandom(x, y);
        if (random.getChance(this.chance)) {
            return this.biomeTileIDs[random.nextInt(this.biomeTileIDs.length)];
        }
        return this.waterTileID;
    }
}

