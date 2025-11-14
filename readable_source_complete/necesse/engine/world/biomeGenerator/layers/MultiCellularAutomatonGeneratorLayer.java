/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.world.biomeGenerator.layers.AdjacentGeneratorLayer;

public class MultiCellularAutomatonGeneratorLayer
extends AdjacentGeneratorLayer {
    protected int deathTileID;
    protected int deathLimit;
    protected int birthLimit;

    public MultiCellularAutomatonGeneratorLayer(int deathLimit, int birthLimit, int deathTileID) {
        super(0);
        this.deathLimit = deathLimit;
        this.birthLimit = birthLimit;
        this.deathTileID = deathTileID;
    }

    @Override
    protected int sample(int x, int y, int[] adjacent) {
        int totalAlive = 0;
        HashMap<Integer, Integer> alive = new HashMap<Integer, Integer>();
        for (int i : adjacent) {
            if (i == this.deathTileID) continue;
            ++totalAlive;
            alive.compute(i, (k, v) -> v == null ? 1 : v + 1);
        }
        int index = alive.entrySet().stream().sorted(Comparator.comparingInt(e -> -((Integer)e.getValue()).intValue())).map(Map.Entry::getKey).findFirst().orElse(this.deathTileID);
        int center = this.getParent(x, y);
        if (center != this.deathTileID) {
            return totalAlive >= this.deathLimit ? index : this.deathTileID;
        }
        return totalAlive > this.birthLimit ? index : this.deathTileID;
    }
}

