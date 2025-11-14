/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.world.biomeGenerator.layers.AdjacentGeneratorLayer;

public class BiomeRulesGeneratorLayer
extends AdjacentGeneratorLayer {
    public BiomeRulesGeneratorLayer(int layerSeed) {
        super(layerSeed);
    }

    protected boolean hasAnyBiomeID(int[] array, int biomeID) {
        for (int id : array) {
            if (id != biomeID) continue;
            return true;
        }
        return false;
    }

    @Override
    protected int sample(int x, int y, int[] adjacent) {
        int parent = this.getParent(x, y);
        if (parent == BiomeRegistry.DESERT.getID() && this.hasAnyBiomeID(adjacent, BiomeRegistry.SNOW.getID())) {
            return this.getMostFrequent(x, y, adjacent, BiomeRegistry.DESERT.getID(), BiomeRegistry.SWAMP.getID(), BiomeRegistry.SNOW.getID());
        }
        if (parent == BiomeRegistry.SWAMP.getID() && this.hasAnyBiomeID(adjacent, BiomeRegistry.SNOW.getID())) {
            return this.getMostFrequent(x, y, adjacent, BiomeRegistry.SWAMP.getID(), BiomeRegistry.DESERT.getID(), BiomeRegistry.SNOW.getID());
        }
        return parent;
    }

    protected int getMostFrequent(int x, int y, int[] adjacent, int ... excludeBiomeIDs) {
        Map.Entry next;
        HashMap<Integer, Integer> frequency = new HashMap<Integer, Integer>();
        for (int i : adjacent) {
            if (this.hasAnyBiomeID(excludeBiomeIDs, i)) continue;
            frequency.compute(i, (k, v) -> v == null ? 1 : v + 1);
        }
        if (frequency.isEmpty()) {
            return adjacent[4];
        }
        Iterator iterator = frequency.entrySet().stream().sorted(Comparator.comparingInt(e -> -((Integer)e.getValue()).intValue())).iterator();
        ArrayList<Integer> biomeIDs = new ArrayList<Integer>(8);
        Map.Entry first = (Map.Entry)iterator.next();
        int bestFrequency = (Integer)first.getValue();
        biomeIDs.add((Integer)first.getKey());
        while (iterator.hasNext() && (Integer)(next = (Map.Entry)iterator.next()).getValue() == bestFrequency) {
            biomeIDs.add((Integer)next.getKey());
        }
        return (Integer)this.getRandom(x, y).getOneOf(biomeIDs);
    }
}

