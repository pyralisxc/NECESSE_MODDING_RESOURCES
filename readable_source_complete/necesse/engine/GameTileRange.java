/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.Predicate;
import java.util.stream.Stream;
import necesse.engine.util.GameUtils;
import necesse.engine.util.HashMapSet;
import necesse.engine.util.PointHashMap;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.Zoning;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.QuadDrawOptionsList;
import necesse.level.maps.Level;

public class GameTileRange {
    public final int minRange;
    public final int maxRange;
    private Rectangle drawBounds;
    private final PointHashSet validTiles;
    private final HashMapSet<Integer, Point> tilesAtDistance;
    private final Zoning zoning;
    private ArrayList<DrawData> drawData;

    public GameTileRange(int range, Point ... extraOffsets) {
        this(range, false, extraOffsets);
    }

    public GameTileRange(int range, boolean saveDistances, Point ... extraOffsets) {
        this(range, saveDistances, () -> GameUtils.arrayIterator(extraOffsets), (Point p) -> true);
    }

    public GameTileRange(int range, Iterable<Point> extraOffsets, Predicate<Point> filter) {
        this(range, false, extraOffsets, filter);
    }

    public GameTileRange(int range, boolean saveDistances, Iterable<Point> extraOffsets, Predicate<Point> filter) {
        this.minRange = 0;
        this.maxRange = range;
        this.zoning = new Zoning();
        this.validTiles = new PointHashSet();
        this.tilesAtDistance = saveDistances ? new HashMapSet() : null;
        float maxComparator = (float)range + 0.5f;
        PointHashMap<Integer> rangeAdds = new PointHashMap<Integer>();
        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                double dist = Math.sqrt(x * x + y * y);
                if (!(dist <= (double)maxComparator)) continue;
                Point p = new Point(x, y);
                if (filter.test(p)) {
                    this.zoning.addTile(x, y);
                    this.validTiles.add(p.x, p.y);
                    if (saveDistances) {
                        int last = rangeAdds.getOrDefault(p.x, p.y, -1);
                        int roundDist = (int)Math.round(dist);
                        if (last == -1) {
                            this.tilesAtDistance.add(roundDist, p);
                            rangeAdds.put(p.x, p.y, roundDist);
                        } else if (last > roundDist) {
                            this.tilesAtDistance.remove(last, p);
                            this.tilesAtDistance.add(roundDist, p);
                            rangeAdds.put(p.x, p.y, roundDist);
                        }
                    }
                }
                for (Point offset : extraOffsets) {
                    Point op = new Point(x + offset.x, y + offset.y);
                    if (!filter.test(op)) continue;
                    if (!this.validTiles.contains(op.x, op.y)) {
                        this.zoning.addTile(op.x, op.y);
                        this.validTiles.add(op.x, op.y);
                    }
                    if (!saveDistances) continue;
                    int last = rangeAdds.getOrDefault(op.x, op.y, -1);
                    int roundDist = (int)Math.round(dist);
                    if (last == -1) {
                        this.tilesAtDistance.add(roundDist, op);
                        rangeAdds.put(op.x, op.y, roundDist);
                        continue;
                    }
                    if (last <= roundDist) continue;
                    this.tilesAtDistance.remove(last, op);
                    this.tilesAtDistance.add(roundDist, op);
                    rangeAdds.put(op.x, op.y, roundDist);
                }
            }
        }
    }

    public GameTileRange(int minRange, int maxRange, Point ... extraOffsets) {
        this(minRange, maxRange, false, extraOffsets);
    }

    public GameTileRange(int minRange, int maxRange, boolean saveDistances, Point ... extraOffsets) {
        this(minRange, maxRange, saveDistances, () -> GameUtils.arrayIterator(extraOffsets), (Point p) -> true);
    }

    public GameTileRange(int minRange, int maxRange, Iterable<Point> extraOffsets, Predicate<Point> filter) {
        this(minRange, maxRange, false, extraOffsets, filter);
    }

    public GameTileRange(int minRange, int maxRange, boolean saveDistances, Iterable<Point> extraOffsets, Predicate<Point> filter) {
        this.minRange = Math.min(minRange, maxRange);
        this.maxRange = maxRange;
        this.zoning = new Zoning();
        this.validTiles = new PointHashSet();
        this.tilesAtDistance = saveDistances ? new HashMapSet() : null;
        float minComparator = (float)minRange - 0.5f;
        float maxComparator = (float)maxRange + 0.5f;
        PointHashSet removes = new PointHashSet();
        PointHashMap<Integer> rangeAdds = new PointHashMap<Integer>();
        for (int x = -maxRange; x <= maxRange; ++x) {
            for (int y = -maxRange; y <= maxRange; ++y) {
                double dist = Math.sqrt(x * x + y * y);
                if (!(dist <= (double)maxComparator)) continue;
                boolean remove = dist < (double)minComparator;
                int roundDist = (int)Math.round(dist);
                Point p = new Point(x, y);
                if (remove) {
                    if (saveDistances) {
                        rangeAdds.remove(p.x, p.y);
                    }
                    removes.add(p.x, p.y);
                } else if (filter.test(p)) {
                    this.zoning.addTile(x, y);
                    this.validTiles.add(p.x, p.y);
                    if (saveDistances) {
                        int last = rangeAdds.getOrDefault(p.x, p.y, -1);
                        if (last == -1) {
                            this.tilesAtDistance.add(roundDist, p);
                            rangeAdds.put(p.x, p.y, roundDist);
                        } else if (last > roundDist) {
                            this.tilesAtDistance.remove(last, p);
                            this.tilesAtDistance.add(roundDist, p);
                            rangeAdds.put(p.x, p.y, roundDist);
                        }
                    }
                }
                for (Point offset : extraOffsets) {
                    Point op = new Point(x + offset.x, y + offset.y);
                    if (remove) {
                        if (saveDistances) {
                            rangeAdds.remove(op.x, op.y);
                        }
                        removes.add(op.x, op.y);
                        continue;
                    }
                    if (!filter.test(op)) continue;
                    if (!this.validTiles.contains(op.x, op.y)) {
                        this.zoning.addTile(op.x, op.y);
                        this.validTiles.add(op.x, op.y);
                    }
                    if (!saveDistances) continue;
                    int last = rangeAdds.getOrDefault(op.x, op.y, -1);
                    if (last == -1) {
                        this.tilesAtDistance.add(roundDist, op);
                        rangeAdds.put(op.x, op.y, roundDist);
                        continue;
                    }
                    if (last <= roundDist) continue;
                    this.tilesAtDistance.remove(last, op);
                    this.tilesAtDistance.add(roundDist, op);
                    rangeAdds.put(op.x, op.y, roundDist);
                }
            }
        }
        for (Point p : removes) {
            this.zoning.removeTile(p.x, p.y);
            this.validTiles.remove(p.x, p.y);
            if (!saveDistances) continue;
            this.tilesAtDistance.removeValues(p);
        }
    }

    public GameTileRange(int range, Rectangle tileRectangle) {
        this(range, false, tileRectangle);
    }

    public GameTileRange(int range, boolean saveDistances, Rectangle tileRectangle) {
        this.minRange = 0;
        this.maxRange = range;
        this.zoning = new Zoning();
        this.validTiles = new PointHashSet();
        HashMapSet hashMapSet = this.tilesAtDistance = saveDistances ? new HashMapSet() : null;
        if (tileRectangle.width > 0 && tileRectangle.height > 0) {
            HashMap<Point, Integer> rangeAdds = new HashMap<Point, Integer>();
            float maxComparator = (float)range + 0.5f;
            for (int x = -range; x <= range; ++x) {
                for (int y = -range; y <= range; ++y) {
                    double dist = Math.sqrt(x * x + y * y);
                    if (!(dist <= (double)maxComparator)) continue;
                    for (int tX = 0; tX < tileRectangle.width; ++tX) {
                        for (int tY = 0; tY < tileRectangle.height; ++tY) {
                            Point p = new Point(x + tileRectangle.x + tX, y + tileRectangle.y + tY);
                            if (!this.validTiles.contains(p.x, p.y)) {
                                this.zoning.addTile(p.x, p.y);
                                this.validTiles.add(p.x, p.y);
                            }
                            if (!saveDistances) continue;
                            int last = rangeAdds.getOrDefault(p, -1);
                            int roundDist = (int)Math.round(dist);
                            if (last == -1) {
                                this.tilesAtDistance.add(roundDist, p);
                                rangeAdds.put(p, roundDist);
                                continue;
                            }
                            if (last <= roundDist) continue;
                            this.tilesAtDistance.remove(last, p);
                            this.tilesAtDistance.add(roundDist, p);
                            rangeAdds.put(p, roundDist);
                        }
                    }
                }
            }
        }
    }

    public GameTileRange(int minRange, int maxRange, Rectangle tileRectangle) {
        this(minRange, maxRange, false, tileRectangle);
    }

    public GameTileRange(int minRange, int maxRange, boolean saveDistances, Rectangle tileRectangle) {
        this.minRange = Math.min(minRange, maxRange);
        this.maxRange = maxRange;
        this.zoning = new Zoning();
        this.validTiles = new PointHashSet();
        HashMapSet hashMapSet = this.tilesAtDistance = saveDistances ? new HashMapSet() : null;
        if (tileRectangle.width > 0 && tileRectangle.height > 0) {
            float minComparator = (float)minRange - 0.5f;
            float maxComparator = (float)maxRange + 0.5f;
            HashSet<Point> removes = new HashSet<Point>();
            HashMap<Point, Integer> rangeAdds = new HashMap<Point, Integer>();
            for (int x = -maxRange; x <= maxRange; ++x) {
                for (int y = -maxRange; y <= maxRange; ++y) {
                    double dist = Math.sqrt(x * x + y * y);
                    if (!(dist <= (double)maxComparator)) continue;
                    boolean remove = dist < (double)minComparator;
                    for (int tX = 0; tX < tileRectangle.width; ++tX) {
                        for (int tY = 0; tY < tileRectangle.height; ++tY) {
                            Point p = new Point(x + tileRectangle.x + tX, y + tileRectangle.y + tY);
                            int roundDist = (int)Math.round(dist);
                            if (remove) {
                                if (saveDistances) {
                                    rangeAdds.remove(p);
                                }
                                removes.add(p);
                                continue;
                            }
                            if (!this.validTiles.contains(p.x, p.y)) {
                                this.zoning.addTile(p.x, p.y);
                                this.validTiles.add(p.x, p.y);
                            }
                            if (!saveDistances) continue;
                            int last = rangeAdds.getOrDefault(p, -1);
                            if (last == -1) {
                                this.tilesAtDistance.add(roundDist, p);
                                rangeAdds.put(p, roundDist);
                                continue;
                            }
                            if (last <= roundDist) continue;
                            this.tilesAtDistance.remove(last, p);
                            this.tilesAtDistance.add(roundDist, p);
                            rangeAdds.put(p, roundDist);
                        }
                    }
                }
            }
            for (Point p : removes) {
                this.zoning.removeTile(p.x, p.y);
                this.validTiles.remove(p.x, p.y);
                if (!saveDistances) continue;
                this.tilesAtDistance.removeValues(p);
            }
        }
    }

    public int size() {
        return this.validTiles.size();
    }

    public PointHashSet getUnderlyingSet() {
        return this.validTiles;
    }

    private synchronized void generateDrawData() {
        this.drawData = new ArrayList();
        for (Point tile : this.zoning.getTiles()) {
            boolean[] adjacent = new boolean[Level.adjacentGetters.length];
            for (int i = 0; i < adjacent.length; ++i) {
                Point offset = Level.adjacentGetters[i];
                adjacent[i] = this.zoning.containsTile(tile.x + offset.x, tile.y + offset.y);
            }
            this.drawData.add(new DrawData(tile.x * 32, tile.y * 32, adjacent));
        }
        Rectangle tileBounds = this.zoning.getTileBounds();
        this.drawBounds = new Rectangle(tileBounds.x * 32, tileBounds.y * 32, tileBounds.width * 32, tileBounds.height * 32);
    }

    public boolean isWithinRange(Point centerTile, Point targetTile) {
        return this.isWithinRange(centerTile.x, centerTile.y, targetTile.x, targetTile.y);
    }

    public boolean isWithinRange(int centerTileX, int centerTileY, Point targetTile) {
        return this.isWithinRange(centerTileX, centerTileY, targetTile.x, targetTile.y);
    }

    public boolean isWithinRange(Point centerTile, int targetTileX, int targetTileY) {
        return this.isWithinRange(centerTile.x, centerTile.y, targetTileX, targetTileY);
    }

    public boolean isWithinRange(int centerTileX, int centerTileY, int targetTileX, int targetTileY) {
        return this.validTiles.contains(targetTileX - centerTileX, targetTileY - centerTileY);
    }

    public Rectangle getRangeBounds(Point centerTile) {
        return this.getRangeBounds(centerTile.x, centerTile.y);
    }

    public Rectangle getRangeBounds(int centerTileX, int centerTileY) {
        return GameUtils.rangeBounds(centerTileX * 32 + 16, centerTileY * 32 + 16, (this.maxRange + 1) * 32);
    }

    public Iterable<Point> getValidTiles(int centerTileX, int centerTileY) {
        return () -> GameUtils.mapIterator(this.validTiles.iterator(), p -> new Point(centerTileX + p.x, centerTileY + p.y));
    }

    public Stream<Point> streamValidTiles(int centerTileX, int centerTileY) {
        return this.validTiles.stream().map(p -> new Point(centerTileX + p.x, centerTileY + p.y));
    }

    public Stream<Point> streamValuesWithin(int centerTileX, int centerTileY, int minRange, int maxRange) {
        if (this.tilesAtDistance == null) {
            throw new IllegalStateException("This GameTileRange does not contain distance data");
        }
        if ((minRange = Math.max(this.minRange, minRange)) == (maxRange = Math.min(this.maxRange, maxRange))) {
            return this.streamValuesAtRange(centerTileX, centerTileY, minRange);
        }
        LinkedList<HashSet> out = new LinkedList<HashSet>();
        for (int i = minRange; i <= maxRange; ++i) {
            out.add((HashSet)this.tilesAtDistance.get(i));
        }
        return out.stream().flatMap(Collection::stream).map(p -> new Point(centerTileX + p.x, centerTileY + p.y));
    }

    public Iterable<Point> getValuesWithin(int minRange, int maxRange, int centerTileX, int centerTileY) {
        return () -> this.streamValuesWithin(centerTileX, centerTileY, minRange, maxRange).iterator();
    }

    public Stream<Point> streamValuesAtRange(int centerTileX, int centerTileY, int range) {
        if (this.tilesAtDistance == null) {
            throw new IllegalStateException("This GameTileRange does not contain distance data");
        }
        return ((HashSet)this.tilesAtDistance.get(range)).stream().map(p -> new Point(centerTileX + p.x, centerTileY + p.y));
    }

    public Iterable<Point> getValuesAtRange(int centerTileX, int centerTileY, int range) {
        return () -> this.streamValuesAtRange(centerTileX, centerTileY, range).iterator();
    }

    public QuadDrawOptionsList getDebugRangesDrawOptions(int drawCenterX, int drawCenterY, int quadResolution, float alpha) {
        Color[] colors = new Color[]{new Color(255, 0, 0), new Color(0, 255, 0), new Color(0, 0, 255)};
        QuadDrawOptionsList draws = new QuadDrawOptionsList();
        for (int i = this.minRange; i <= this.maxRange; ++i) {
            HashSet points = (HashSet)this.tilesAtDistance.get(i);
            Color color = colors[(i - this.minRange) % colors.length];
            for (Point p : points) {
                int drawX = drawCenterX + p.x * quadResolution;
                int drawY = drawCenterY + p.y * quadResolution;
                draws.add(drawX, drawY, quadResolution, quadResolution, (float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, alpha);
            }
        }
        return draws;
    }

    public void debugPrint() {
        for (int i = this.minRange; i <= this.maxRange; ++i) {
            Object[] ar = ((HashSet)this.tilesAtDistance.get(i)).toArray();
            System.out.println(i + ": " + ar.length + " - " + Arrays.toString(ar));
        }
    }

    public SharedTextureDrawOptions getDrawOptions(Color edgeColor, Color fillColor, int centerTileX, int centerTileY, GameCamera camera) {
        return this.getDrawOptions(edgeColor, fillColor, camera.getTileDrawX(centerTileX), camera.getTileDrawY(centerTileY), camera.getWidth(), camera.getHeight());
    }

    public synchronized SharedTextureDrawOptions getDrawOptions(Color edgeColor, Color fillColor, int centerDrawX, int centerDrawY, int cameraWidth, int cameraHeight) {
        if (this.drawData == null) {
            this.generateDrawData();
        }
        SharedTextureDrawOptions options = new SharedTextureDrawOptions(Renderer.getQuadTexture());
        Rectangle cameraBounds = new Rectangle(cameraWidth, cameraHeight);
        if (cameraBounds.intersects(this.drawBounds.x + centerDrawX, this.drawBounds.y + centerDrawY, this.drawBounds.width, this.drawBounds.height)) {
            for (DrawData drawData : this.drawData) {
                int drawX = drawData.drawOffsetX + centerDrawX;
                int drawY = drawData.drawOffsetY + centerDrawY;
                if (!cameraBounds.intersects(drawX, drawY, 32.0, 32.0)) continue;
                Zoning.addDrawOptions(options, drawX, drawY, drawData.adj, edgeColor, fillColor);
            }
        }
        return options;
    }

    public synchronized SharedTextureDrawOptions getDrawOptions(Color edgeColor, Color fillColor, int centerDrawX, int centerDrawY, int tileResolution, Rectangle cameraBounds) {
        if (this.drawData == null) {
            this.generateDrawData();
        }
        double quadRes = (double)tileResolution / 32.0;
        int res1 = tileResolution / 2;
        int res2 = tileResolution - res1;
        SharedTextureDrawOptions options = new SharedTextureDrawOptions(Renderer.getQuadTexture());
        for (DrawData drawData : this.drawData) {
            int drawX = (int)((double)drawData.drawOffsetX * quadRes) + centerDrawX;
            int drawY = (int)((double)drawData.drawOffsetY * quadRes) + centerDrawY;
            if (cameraBounds != null && !cameraBounds.intersects(drawX, drawY, tileResolution, tileResolution)) continue;
            Zoning.addDrawOptions(options, drawX, drawY, drawData.adj, edgeColor, fillColor, res1, res2);
        }
        return options;
    }

    private static class DrawData {
        public final int drawOffsetX;
        public final int drawOffsetY;
        public final boolean[] adj;

        public DrawData(int drawOffsetX, int drawOffsetY, boolean[] adj) {
            this.drawOffsetX = drawOffsetX;
            this.drawOffsetY = drawOffsetY;
            this.adj = adj;
        }
    }
}

