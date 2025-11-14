/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.light;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import necesse.level.maps.Level;
import necesse.level.maps.light.FastLightMap;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.light.LightArea;

public class FastLightCompute {
    protected final Level level;
    protected final FastLightMap map;
    protected final LightArea area;
    protected LinkedList<LightPoint> sources = new LinkedList();
    protected LinkedList<Point> open = new LinkedList();
    protected HashSet<Point> closed = new HashSet();

    public FastLightCompute(Level level, FastLightMap map, int startX, int startY, int endX, int endY) {
        this.level = level;
        this.map = map;
        this.area = new LightArea(startX, startY, endX, endY);
    }

    public void addSource(int x, int y, GameLight light) {
        this.sources.add(new LightPoint(x, y, light));
    }

    public int compute() {
        this.area.initLights();
        for (LightPoint source : this.sources) {
            this.area.lights[this.area.getIndex((int)source.x, (int)source.y)] = source.light;
            this.open.add(new Point(source.x, source.y));
        }
        this.sources = null;
        int iterations = 0;
        while (!this.open.isEmpty()) {
            ++iterations;
            Point current = this.open.removeFirst();
            GameLight currentLight = this.area.lights[this.area.getIndex(current.x, current.y)];
            this.closed.add(new Point(current.x, current.y));
            int mod = Math.max(10, this.level.getObject(current.x, current.y).getLightLevelMod(this.level, current.x, current.y));
            this.handleTile(current.x, current.y - 1, currentLight, mod);
            this.handleTile(current.x - 1, current.y, currentLight, mod);
            this.handleTile(current.x + 1, current.y, currentLight, mod);
            this.handleTile(current.x, current.y + 1, currentLight, mod);
        }
        return iterations;
    }

    public int apply(LightArea area) {
        for (Point p : this.closed) {
            area.lights[area.getIndex(p.x, p.y)].combine(this.area.lights[this.area.getIndex(p.x, p.y)]);
        }
        return this.closed.size();
    }

    private void handleTile(int tileX, int tileY, GameLight lastLight, int mod) {
        if (this.area.isOutsideArea(tileX, tileY)) {
            return;
        }
        int index = this.area.getIndex(tileX, tileY);
        float level = Math.max(lastLight.getLevel() - (float)mod, 0.0f);
        if (level > 0.0f && (this.area.lights[index] == null || this.area.lights[index].getLevel() < level)) {
            GameLight nextLight = lastLight.copy();
            nextLight.setLevel(level);
            this.area.lights[index] = nextLight;
            if (!this.open.contains(new Point(tileX, tileY))) {
                this.open.add(new Point(tileX, tileY));
            }
        }
    }

    private static class LightPoint {
        public final int x;
        public final int y;
        public final GameLight light;

        public LightPoint(int x, int y, GameLight light) {
            this.x = x;
            this.y = y;
            this.light = light;
        }
    }
}

