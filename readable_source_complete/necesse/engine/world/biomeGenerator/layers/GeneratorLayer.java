/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator.layers;

import java.awt.Color;
import java.util.ArrayList;
import necesse.engine.util.GameRandom;
import necesse.engine.world.biomeGenerator.GeneratorCache;
import necesse.engine.world.biomeGenerator.GeneratorStack;

public abstract class GeneratorLayer {
    private GeneratorStack stack;
    private final ArrayList<Parent> parents = new ArrayList();
    private final ArrayList<GeneratorStack> branchingStacks = new ArrayList();
    private final ArrayList<GeneratorLayer> children = new ArrayList();
    private final GeneratorCache cache = new GeneratorCache(16384);
    public int worldSeed;
    public int layerSeed;
    private final int layerScale;
    private int stackScale;
    public String debugName;

    public GeneratorLayer(int layerSeed, int scale) {
        this.layerSeed = layerSeed;
        if (scale < 0) {
            throw new IllegalStateException("Scale must be 0 or higher");
        }
        this.layerScale = scale;
        this.stackScale = scale;
    }

    public GeneratorLayer setDebugName(String debugName) {
        this.debugName = debugName;
        return this;
    }

    public void init(GeneratorStack stack, GeneratorLayer parent, boolean isParentDifferentBranch, int worldSeed) {
        if (this.stack != null) {
            throw new IllegalStateException("Layer already initialized with a stack");
        }
        this.stack = stack;
        if (this.parents.stream().anyMatch(p -> p.layer == parent)) {
            throw new IllegalStateException("Layer already initialized with this parent");
        }
        this.parents.add(new Parent(parent, isParentDifferentBranch));
        if (parent != null) {
            parent.children.add(this);
        }
        this.worldSeed = worldSeed;
    }

    public void initBranch(GeneratorStack stack) {
        this.branchingStacks.add(stack);
    }

    public Iterable<Parent> getParents() {
        return this.parents;
    }

    public Iterable<GeneratorStack> getBranchingStacks() {
        return this.branchingStacks;
    }

    public Iterable<GeneratorLayer> getChildren() {
        return this.children;
    }

    public int setStackScale(int parentScale) {
        this.stackScale = parentScale << this.layerScale;
        return this.stackScale;
    }

    public int getStackScale() {
        this.stack.calculateStackScalesIfNeeded();
        return this.stackScale;
    }

    public GameRandom getRandom(int x, int y) {
        return new GameRandom(this.worldSeed).nextSeeded(this.layerSeed).nextSeeded(x).nextSeeded(y);
    }

    protected int getParentForced(int x, int y) {
        return this.getParentForced(0, x, y);
    }

    protected int getParentForced(int index, int x, int y) {
        return this.parents.get((int)index).layer.get(x, y);
    }

    protected int getParent(int x, int y) {
        return this.getParent(0, x, y);
    }

    protected int getParent(int index, int x, int y) {
        Parent parent = this.parents.get(index);
        if (parent.isDifferentBranch) {
            return 0;
        }
        return parent.layer.get(x, y);
    }

    public int get(int x, int y) {
        return this.cache.get(x, y, this::sample);
    }

    protected Color getDebugColor(int value) {
        Color color = null;
        for (Parent parent : this.parents) {
            color = parent.layer.getDebugColor(value);
            if (color == null) continue;
            break;
        }
        return color;
    }

    public Color getDebugColor(int x, int y, boolean searchParents) {
        block3: {
            int lastValue = 0;
            GeneratorLayer currentLayer = this;
            while (true) {
                Color color;
                int currentValue;
                if (((currentValue = currentLayer.get(x, y)) != lastValue || currentLayer == this) && (color = this.getDebugColor(currentValue)) != null) {
                    return color;
                }
                if (currentLayer.parents.isEmpty()) {
                    return null;
                }
                if (!searchParents) break block3;
                Parent parent = currentLayer.parents.get(0);
                if (parent.layer == null) break;
                lastValue = currentValue;
                currentLayer = parent.layer;
            }
            return null;
        }
        return null;
    }

    protected abstract int sample(int var1, int var2);

    public static class Parent {
        public final GeneratorLayer layer;
        public final boolean isDifferentBranch;

        public Parent(GeneratorLayer layer, boolean isDifferentBranch) {
            this.layer = layer;
            this.isDifferentBranch = isDifferentBranch;
        }
    }
}

