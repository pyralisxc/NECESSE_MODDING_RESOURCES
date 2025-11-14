/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import java.awt.Point;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Stream;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.LightMap;
import necesse.level.maps.light.SourcedGameLight;
import necesse.level.maps.light.SourcedLightArea;

public class LightCompute {
    protected final Level level;
    protected final LightMap map;
    protected final SourcedLightArea area;
    protected boolean computed;
    protected LinkedList<DetachedSourceLight> sources = new LinkedList();
    protected LinkedList<Point> open = new LinkedList();
    protected HashSet<Point> closed = new HashSet();

    public LightCompute(Level level, LightMap map, int startX, int startY, int endX, int endY) {
        this.level = level;
        this.map = map;
        this.area = new SourcedLightArea(startX, startY, endX, endY);
    }

    public void addSource(int x, int y, SourcedGameLight source) {
        if (this.computed) {
            throw new IllegalStateException("Cannot add sources to already computed area");
        }
        this.sources.add(new DetachedSourceLight(x, y, source));
    }

    public void addSource(SourcedGameLight source) {
        this.addSource(source.sourceTileX, source.sourceTileY, source);
    }

    public int compute() {
        this.computed = true;
        this.area.initLights();
        for (DetachedSourceLight ds : this.sources) {
            this.area.lights[this.area.getIndex((int)ds.x, (int)ds.y)] = ds.source;
            this.open.add(new Point(ds.x, ds.y));
        }
        int iterations = 0;
        while (!this.open.isEmpty()) {
            ++iterations;
            Point current = this.open.removeFirst();
            SourcedGameLight currentLight = this.area.lights[this.area.getIndex(current.x, current.y)];
            this.closed.add(new Point(current.x, current.y));
            int mod = Math.max(10, this.level.getObject(current.x, current.y).getLightLevelMod(this.level, current.x, current.y));
            this.handleTile(current.x, current.y - 1, currentLight, mod);
            this.handleTile(current.x - 1, current.y, currentLight, mod);
            this.handleTile(current.x + 1, current.y, currentLight, mod);
            this.handleTile(current.x, current.y + 1, currentLight, mod);
        }
        return iterations;
    }

    public int apply(Collection<Integer> shouldUpdates) {
        for (Point p : this.closed) {
            SourcedGameLight light = this.area.lights[this.area.getIndex(p.x, p.y)];
            this.map.addSourcedLight(p.x, p.y, light, shouldUpdates);
        }
        return this.closed.size();
    }

    private void handleTile(int tileX, int tileY, SourcedGameLight last, int mod) {
        if (this.area.isOutsideArea(tileX, tileY)) {
            return;
        }
        int index = this.area.getIndex(tileX, tileY);
        float level = Math.max(last.light.getLevel() - (float)mod, 0.0f);
        if (level > 0.0f && (this.area.lights[index] == null || this.area.lights[index].light.getLevel() < level)) {
            GameLight nextLight = last.light.copy();
            nextLight.setLevel(level);
            SourcedGameLight next = new SourcedGameLight(last.sourceTileX, last.sourceTileY, nextLight);
            if (this.map.hasNoBetterSameSource(tileX, tileY, next) && this.map.hasNoBetterSameColor(tileX, tileY, next)) {
                this.area.lights[index] = next;
                if (!this.open.contains(new Point(tileX, tileY))) {
                    this.open.add(new Point(tileX, tileY));
                }
            }
        }
    }

    public void printDebug(PrintStream stream) {
        stream.println("Computed: " + this.computed + ", Sources: " + this.sources.size());
        stream.println(this.safeToArrayString(this.sources.stream().map(s -> s.source).limit(100L)));
        stream.println("Open: " + this.open.size() + ", " + this.safeToArrayString(this.open.stream().map(p -> p.x + "x" + p.y)));
        stream.println("Closed: " + this.closed.size() + ", " + this.safeToArrayString(this.closed.stream().map(p -> p.x + "x" + p.y)));
    }

    private String safeToArrayString(Stream<?> stream) {
        try {
            return Arrays.toString(stream.toArray());
        }
        catch (Exception e) {
            return "ERR:" + e;
        }
    }

    protected static class DetachedSourceLight {
        public final int x;
        public final int y;
        public final SourcedGameLight source;

        public DetachedSourceLight(int x, int y, SourcedGameLight source) {
            this.x = x;
            this.y = y;
            this.source = source;
        }
    }
}

