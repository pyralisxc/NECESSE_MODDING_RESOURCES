/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import necesse.engine.world.biomeGenerator.layers.AdjacentGeneratorLayer;

public class ClearSinglesGeneratorLayer
extends AdjacentGeneratorLayer {
    public int maxFrequency;

    public ClearSinglesGeneratorLayer(int maxFrequency) {
        super(0);
        this.maxFrequency = maxFrequency;
    }

    @Override
    protected int sample(int x, int y, int[] adjacent) {
        HashMap<Integer, Integer> frequency = new HashMap<Integer, Integer>();
        for (int i : adjacent) {
            frequency.compute(i, (k, v) -> v == null ? 1 : v + 1);
        }
        int center = adjacent[4];
        if (frequency.getOrDefault(center, 0) <= this.maxFrequency) {
            Map.Entry next;
            Iterator iterator = frequency.entrySet().stream().filter(e -> (Integer)e.getKey() != center).sorted(Comparator.comparingInt(e -> -((Integer)e.getValue()).intValue())).iterator();
            ArrayList<Integer> biomeIDs = new ArrayList<Integer>(8);
            Map.Entry first = (Map.Entry)iterator.next();
            int bestFrequency = (Integer)first.getValue();
            biomeIDs.add((Integer)first.getKey());
            while (iterator.hasNext() && (Integer)(next = (Map.Entry)iterator.next()).getValue() == bestFrequency) {
                biomeIDs.add((Integer)next.getKey());
            }
            return (Integer)this.getRandom(x, y).getOneOf(biomeIDs);
        }
        return center;
    }
}

