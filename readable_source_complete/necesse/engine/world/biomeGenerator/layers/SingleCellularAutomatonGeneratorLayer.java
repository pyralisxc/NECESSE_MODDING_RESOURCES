/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import necesse.engine.world.biomeGenerator.layers.AdjacentGeneratorLayer;

public class SingleCellularAutomatonGeneratorLayer
extends AdjacentGeneratorLayer {
    protected int aliveTileID;
    protected int deathTileID;
    protected int deathLimit;
    protected int birthLimit;

    public SingleCellularAutomatonGeneratorLayer(int deathLimit, int birthLimit, int aliveTileID, int deathTileID) {
        super(0);
        this.deathLimit = deathLimit;
        this.birthLimit = birthLimit;
        this.aliveTileID = aliveTileID;
        this.deathTileID = deathTileID;
    }

    @Override
    protected int sample(int x, int y, int[] adjacent) {
        int totalAlive = 0;
        for (int i : adjacent) {
            if (i != this.aliveTileID) continue;
            ++totalAlive;
        }
        int center = this.getParent(x, y);
        if (center != this.deathTileID) {
            return totalAlive >= this.deathLimit ? this.aliveTileID : this.deathTileID;
        }
        return totalAlive > this.birthLimit ? this.aliveTileID : this.deathTileID;
    }
}

