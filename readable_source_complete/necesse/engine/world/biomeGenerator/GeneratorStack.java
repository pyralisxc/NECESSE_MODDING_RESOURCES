/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator;

import java.util.ArrayList;
import necesse.engine.world.biomeGenerator.layers.GeneratorLayer;

public class GeneratorStack {
    private GeneratorLayer parent;
    private final ArrayList<GeneratorLayer> layers = new ArrayList();
    private boolean calculatedStackScales;
    public final int worldSeed;

    public GeneratorStack(int worldSeed) {
        this.worldSeed = worldSeed;
    }

    public GeneratorStack addBranchingStack(GeneratorStack stack) {
        GeneratorLayer lastLayer;
        if (stack.parent != null) {
            throw new IllegalStateException("Stack already has a parent");
        }
        GeneratorLayer generatorLayer = lastLayer = this.layers.isEmpty() ? null : this.layers.get(this.layers.size() - 1);
        if (lastLayer != null) {
            lastLayer.initBranch(stack);
        }
        stack.parent = lastLayer;
        return stack;
    }

    public <T extends GeneratorLayer> T addLayer(T layer) {
        if (!this.layers.isEmpty()) {
            layer.init(this, this.layers.get(this.layers.size() - 1), false, this.worldSeed);
        } else {
            layer.init(this, this.parent, true, this.worldSeed);
        }
        this.layers.add(layer);
        this.calculatedStackScales = false;
        return layer;
    }

    public int getStackSize() {
        return this.layers.size();
    }

    public GeneratorLayer getLayer(int index) {
        return this.layers.get(index);
    }

    public Iterable<GeneratorLayer> getLayers() {
        return this.layers;
    }

    public void calculateStackScalesIfNeeded() {
        if (!this.calculatedStackScales) {
            this.calculateStackScales();
        }
    }

    private void calculateStackScales() {
        int scale = this.parent == null ? 1 : this.parent.getStackScale();
        for (GeneratorLayer layer : this.layers) {
            scale = layer.setStackScale(scale);
        }
        this.calculatedStackScales = true;
    }

    public int get(int x, int y) {
        if (this.layers.isEmpty()) {
            throw new IllegalStateException("No layers in stack");
        }
        return this.layers.get(this.layers.size() - 1).get(x, y);
    }

    public boolean isEmpty() {
        return this.layers.isEmpty();
    }
}

