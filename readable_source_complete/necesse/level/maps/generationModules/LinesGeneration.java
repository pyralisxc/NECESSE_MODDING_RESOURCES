/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.generationModules;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.HashMapPointEntry;
import necesse.engine.util.PointHashMap;
import necesse.engine.util.PointHashSet;
import necesse.level.maps.Level;
import necesse.level.maps.generationModules.CellAutomaton;

public class LinesGeneration {
    public final int x1;
    public final int y1;
    public final int x2;
    public final int y2;
    public final float width;
    protected Point2D.Float dir;
    protected boolean lineWidthPriority;
    protected final LinesGeneration root;
    protected LinkedList<LinesGeneration> lines = new LinkedList();

    private LinesGeneration(LinesGeneration root, int x1, int y1, int x2, int y2, float width) {
        this.root = root;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.width = width;
    }

    public LinesGeneration(int x1, int y1, int x2, int y2, float width) {
        this(null, x1, y1, x2, y2, width);
    }

    public LinesGeneration(int x, int y, float range) {
        this(null, x, y, x, y, range);
    }

    public LinesGeneration(int x, int y) {
        this(null, x, y, x, y, 0.0f);
    }

    public Point2D.Float getDir() {
        if (this.dir == null) {
            this.dir = GameMath.normalize(this.x1 - this.x2, this.y1 - this.y2);
        }
        return this.dir;
    }

    public float getDirX() {
        return this.getDir().x;
    }

    public float getDirY() {
        return this.getDir().y;
    }

    public LinesGeneration lineWidthPriority(boolean value) {
        this.lineWidthPriority = value;
        return this;
    }

    public LinesGeneration addLine(int x1, int y1, int x2, int y2, float width) {
        if (this.root != null) {
            return this.root.addLine(x1, y1, x2, y2, width);
        }
        LinesGeneration e = new LinesGeneration(this, x1, y1, x2, y2, width);
        this.lines.addLast(e);
        return e;
    }

    public LinesGeneration addLineTo(int x, int y, float width) {
        if (this.root != null) {
            return this.root.addLine(this.x2, this.y2, x, y, width);
        }
        LinesGeneration e = new LinesGeneration(this, this.x2, this.y2, x, y, width);
        this.lines.addLast(e);
        return e;
    }

    public void removeLastLine() {
        if (this.root != null) {
            this.root.removeLastLine();
        } else if (!this.lines.isEmpty()) {
            this.lines.removeLast();
        }
    }

    public LinesGeneration addLineToDelta(int dx, int dy, float width) {
        return this.addLineTo(this.x2 + dx, this.y2 + dy, width);
    }

    public LinesGeneration addPoint(int x, int y, float width) {
        return this.addLine(x, y, x, y, width);
    }

    public LinesGeneration addPointDelta(int dx, int dy, float width) {
        return this.addLine(this.x2 + dx, this.y2 + dy, this.x2 + dx, this.y2 + dy, width);
    }

    public LinesGeneration addRandomArms(GameRandom random, int arms, float minRange, float maxRange, float minWidth, float maxWidth) {
        int angle = random.nextInt(360);
        int anglePerArm = 360 / arms;
        for (int i = 0; i < arms; ++i) {
            float range = random.getFloatBetween(minRange, maxRange);
            float width = random.getFloatBetween(minWidth, maxWidth);
            this.addArm(angle += random.getIntOffset(anglePerArm, anglePerArm / 2), range, width);
        }
        return this;
    }

    public LinesGeneration addArm(float angle, float range, float width) {
        Point2D.Float dir = GameMath.getAngleDir(angle);
        return this.addLineToDelta((int)(dir.x * range), (int)(dir.y * range), width);
    }

    public LinesGeneration addMultiArm(GameRandom random, int startAngle, int angleOffset, int maxLength, float armMinLength, float armMaxLength, float armMinWidth, float armMaxWidth, Predicate<LinesGeneration> stopAtArm) {
        float currentLength = 0.0f;
        LinesGeneration currentArm = this;
        while (currentLength < (float)maxLength) {
            startAngle = random.getIntOffset(startAngle, angleOffset);
            float armLength = random.getFloatBetween(armMinLength, armMaxLength);
            float armWidth = random.getFloatBetween(armMinWidth, armMaxWidth);
            currentLength += armLength;
            currentArm = currentArm.addArm(startAngle, armLength, armWidth);
            if (stopAtArm == null || !stopAtArm.test(currentArm)) continue;
            break;
        }
        return currentArm;
    }

    public LinesGeneration addMultiArm(GameRandom random, Level level, int startAngle, int angleOffset, int maxLength, float armMinLength, float armMaxLength, float armMinWidth, float armMaxWidth) {
        return this.addMultiArm(random, startAngle, angleOffset, maxLength, armMinLength, armMaxLength, armMinWidth, armMaxWidth, lg -> !level.isTileWithinBounds(lg.x2, lg.y2));
    }

    public CellAutomaton toCellularAutomaton(BiFunction<Point, PointDistance, Boolean> startAliveFunction, boolean ordered) {
        CellAutomaton cellAutomaton = new CellAutomaton();
        if (ordered) {
            Comparator<HashMapPointEntry> comparator = Comparator.comparingInt(e -> ((Point)e.getKey()).x);
            comparator = comparator.thenComparingInt(e -> ((Point)e.getKey()).y);
            this.getSmoothPoints().streamEntries().sorted(comparator).forEachOrdered(e -> {
                PointDistance dist;
                Point point = (Point)e.getKey();
                if (((Boolean)startAliveFunction.apply(point, dist = (PointDistance)e.getValue())).booleanValue()) {
                    cellAutomaton.setAlive(point.x, point.y);
                }
            });
        } else {
            this.getSmoothPoints().forEach((x, y, dist) -> {
                if (((Boolean)startAliveFunction.apply(new Point(x, y), (PointDistance)dist)).booleanValue()) {
                    cellAutomaton.setAlive(x, y);
                }
            });
        }
        return cellAutomaton;
    }

    public CellAutomaton toCellularAutomaton(GameRandom random) {
        return this.toCellularAutomaton((point, dist) -> {
            if (dist.lineWidth == 0.0) {
                return false;
            }
            double chance = Math.abs(dist.dist / dist.lineWidth - 1.0);
            return random.getChance(chance);
        }, true);
    }

    public CellAutomaton doCellularAutomaton(BiFunction<Point, PointDistance, Boolean> startAliveFunction, boolean ordered, int deathLimit, int birthLimit, int iterations) {
        CellAutomaton cellAutomaton = this.toCellularAutomaton(startAliveFunction, ordered);
        cellAutomaton.doCellularAutomaton(deathLimit, birthLimit, iterations);
        return cellAutomaton;
    }

    public CellAutomaton doCellularAutomaton(GameRandom random, int deathLimit, int birthLimit, int iterations) {
        CellAutomaton cellAutomaton = this.toCellularAutomaton(random);
        cellAutomaton.doCellularAutomaton(deathLimit, birthLimit, iterations);
        return cellAutomaton;
    }

    public CellAutomaton doCellularAutomaton(BiFunction<Point, PointDistance, Boolean> startAliveFunction, boolean ordered) {
        return this.doCellularAutomaton(startAliveFunction, ordered, 4, 3, 4);
    }

    public CellAutomaton doCellularAutomaton(GameRandom random) {
        return this.doCellularAutomaton(random, 4, 3, 4);
    }

    public LinesGeneration getRoot() {
        return this.root == null ? this : this.root;
    }

    public PointHashMap<PointDistance> getSmoothPoints() {
        LinesGeneration root = this.root == null ? this : this.root;
        PointHashMap<PointDistance> pointDistances = new PointHashMap<PointDistance>();
        this.addSmoothPoints(pointDistances, this.width);
        for (LinesGeneration point : root.lines) {
            point.addSmoothPoints(pointDistances, point.width);
        }
        return pointDistances;
    }

    public PointHashSet getDiamondPoints() {
        LinesGeneration root = this.root == null ? this : this.root;
        PointHashSet points = new PointHashSet();
        this.addDiamondPoints(points, this.width);
        for (LinesGeneration point : root.lines) {
            point.addDiamondPoints(points, point.width);
        }
        return points;
    }

    public Line2D.Float getTileLine() {
        return new Line2D.Float(this.x1, this.y1, this.x2, this.y2);
    }

    public Line2D.Float getPosLine() {
        return new Line2D.Float(this.x1 * 32 + 16, this.y1 * 32 + 16, this.x2 * 32 + 16, this.y2 * 32 + 16);
    }

    public Iterable<LinesGeneration> getLines() {
        return this.lines;
    }

    public boolean isEmpty() {
        return this.lines.isEmpty();
    }

    public void recursiveLines(Function<LinesGeneration, Boolean> handler) {
        for (LinesGeneration line : this.lines) {
            if (!handler.apply(line).booleanValue()) continue;
            line.recursiveLines(handler);
        }
    }

    public static void pathTiles(Line2D.Float tileLine, boolean preferTop, BiConsumer<Point, Point> consumer) {
        LinesGeneration.pathTilesBreak(tileLine, preferTop, (from, next) -> {
            consumer.accept((Point)from, (Point)next);
            return true;
        });
    }

    public static void pathTilesBreak(Line2D.Float tileLine, boolean preferTop, BiPredicate<Point, Point> consumer) {
        Point next;
        HashSet<Point> lastTiles = new HashSet<Point>();
        Point currentTile = new Point((int)Math.floor(tileLine.x1), (int)Math.floor(tileLine.y1));
        Point endTile = new Point((int)Math.floor(tileLine.x2), (int)Math.floor(tileLine.y2));
        Line2D.Float posLine = new Line2D.Float(tileLine.x1 * 32.0f + 16.0f, tileLine.y1 * 32.0f + 16.0f, tileLine.x2 * 32.0f + 16.0f, tileLine.y2 * 32.0f + 16.0f);
        if (!consumer.test(null, currentTile)) {
            return;
        }
        lastTiles.add(currentTile);
        while ((next = LinesGeneration.getNextPathLine(currentTile, lastTiles, posLine, preferTop)) != null) {
            lastTiles.add(next);
            Point lastTile = currentTile;
            if (consumer.test(lastTile, currentTile = next) && (currentTile.x != endTile.x || currentTile.y != endTile.y)) continue;
            break;
        }
    }

    private static Point getNextPathLine(Point currentTile, HashSet<Point> checkedTiles, Line2D.Float posLine, boolean preferTop) {
        if (LinesGeneration.pathLineIntersects(currentTile.x, currentTile.y - 1, checkedTiles, posLine)) {
            if (posLine.getX1() < posLine.getX2()) {
                checkedTiles.add(new Point(currentTile.x + 1, currentTile.y));
            } else if (posLine.getX1() > posLine.getX2()) {
                checkedTiles.add(new Point(currentTile.x - 1, currentTile.y));
            }
            return new Point(currentTile.x, currentTile.y - 1);
        }
        if (LinesGeneration.pathLineIntersects(currentTile.x + 1, currentTile.y, checkedTiles, posLine)) {
            if (posLine.getY1() < posLine.getY2()) {
                checkedTiles.add(new Point(currentTile.x, currentTile.y + 1));
            } else if (posLine.getY1() > posLine.getY2()) {
                checkedTiles.add(new Point(currentTile.x, currentTile.y - 1));
            }
            return new Point(currentTile.x + 1, currentTile.y);
        }
        if (LinesGeneration.pathLineIntersects(currentTile.x, currentTile.y + 1, checkedTiles, posLine)) {
            if (posLine.getX1() < posLine.getX2()) {
                checkedTiles.add(new Point(currentTile.x + 1, currentTile.y));
            } else if (posLine.getX1() > posLine.getX2()) {
                checkedTiles.add(new Point(currentTile.x - 1, currentTile.y));
            }
            return new Point(currentTile.x, currentTile.y + 1);
        }
        if (LinesGeneration.pathLineIntersects(currentTile.x - 1, currentTile.y, checkedTiles, posLine)) {
            if (posLine.getY1() < posLine.getY2()) {
                checkedTiles.add(new Point(currentTile.x, currentTile.y - 1));
            } else if (posLine.getY1() > posLine.getY2()) {
                checkedTiles.add(new Point(currentTile.x, currentTile.y + 1));
            }
            return new Point(currentTile.x - 1, currentTile.y);
        }
        if (LinesGeneration.pathLineIntersects(currentTile.x + 1, currentTile.y - 1, checkedTiles, posLine)) {
            if (preferTop) {
                checkedTiles.add(new Point(currentTile.x + 1, currentTile.y));
                return new Point(currentTile.x, currentTile.y - 1);
            }
            checkedTiles.add(new Point(currentTile.x, currentTile.y - 1));
            return new Point(currentTile.x + 1, currentTile.y);
        }
        if (LinesGeneration.pathLineIntersects(currentTile.x + 1, currentTile.y + 1, checkedTiles, posLine)) {
            if (preferTop) {
                checkedTiles.add(new Point(currentTile.x, currentTile.y + 1));
                return new Point(currentTile.x + 1, currentTile.y);
            }
            checkedTiles.add(new Point(currentTile.x + 1, currentTile.y));
            return new Point(currentTile.x, currentTile.y + 1);
        }
        if (LinesGeneration.pathLineIntersects(currentTile.x - 1, currentTile.y + 1, checkedTiles, posLine)) {
            if (preferTop) {
                checkedTiles.add(new Point(currentTile.x, currentTile.y + 1));
                return new Point(currentTile.x - 1, currentTile.y);
            }
            checkedTiles.add(new Point(currentTile.x - 1, currentTile.y));
            return new Point(currentTile.x, currentTile.y + 1);
        }
        if (LinesGeneration.pathLineIntersects(currentTile.x - 1, currentTile.y - 1, checkedTiles, posLine)) {
            if (preferTop) {
                checkedTiles.add(new Point(currentTile.x - 1, currentTile.y));
                return new Point(currentTile.x, currentTile.y - 1);
            }
            checkedTiles.add(new Point(currentTile.x, currentTile.y - 1));
            return new Point(currentTile.x - 1, currentTile.y);
        }
        return null;
    }

    private static boolean pathLineIntersects(int nextTileX, int nextTileY, HashSet<Point> checkedTiles, Line2D.Float posLine) {
        return !checkedTiles.contains(new Point(nextTileX, nextTileY)) && posLine.intersects(LinesGeneration.getTileRectangle(nextTileX, nextTileY));
    }

    private static Rectangle getTileRectangle(int tileX, int tileY) {
        return new Rectangle(tileX * 32, tileY * 32, 32, 32);
    }

    private void addTestPoints(HashSet<Point> points, float lineRange) {
        block16: {
            Point next;
            int pY;
            int pX = (int)Math.floor((float)this.x1 - lineRange);
            while ((float)pX <= (float)this.x1 + lineRange) {
                pY = (int)Math.floor((float)this.y1 - lineRange);
                while ((float)pY <= (float)this.y1 + lineRange) {
                    next = new Point(pX, pY);
                    if (!points.contains(next) && GameMath.diamondDistance(this.x1, this.y1, next.x, next.y) <= lineRange) {
                        points.add(next);
                    }
                    ++pY;
                }
                ++pX;
            }
            pX = (int)Math.floor((float)this.x2 - lineRange);
            while ((float)pX <= (float)this.x2 + lineRange) {
                pY = (int)Math.floor((float)this.y2 - lineRange);
                while ((float)pY <= (float)this.y2 + lineRange) {
                    next = new Point(pX, pY);
                    if (!points.contains(next) && GameMath.diamondDistance(this.x2, this.y2, next.x, next.y) <= lineRange) {
                        points.add(next);
                    }
                    ++pY;
                }
                ++pX;
            }
            int deltaX = this.x1 - this.x2;
            int deltaY = this.y1 - this.y2;
            if (deltaX == 0 && deltaY == 0) break block16;
            int deltaAbsX = Math.abs(deltaX);
            int deltaAbsY = Math.abs(deltaY);
            if (deltaAbsY <= deltaAbsX) {
                Point end;
                Point start;
                if (this.x1 < this.x2) {
                    start = new Point(this.x1, this.y1);
                    end = new Point(this.x2, this.y2);
                } else {
                    start = new Point(this.x2, this.y2);
                    end = new Point(this.x1, this.y1);
                }
                int moveY = end.y - start.y;
                int moveX = end.x - start.x;
                for (int pX2 = start.x + 1; pX2 < end.x; ++pX2) {
                    float currentY = (float)start.y + (float)moveY * ((float)(pX2 - start.x) / (float)moveX);
                    System.out.println("On X " + pX2 + ", " + currentY);
                    int startY = (int)Math.floor(currentY - lineRange);
                    int endY = (int)Math.ceil(currentY + lineRange);
                    for (int pY2 = startY; pY2 <= endY; ++pY2) {
                        System.out.println("Checking Y " + pY2);
                        Point next2 = new Point(pX2, pY2);
                        if (points.contains(next2) || !(Math.abs((float)pY2 - currentY) <= lineRange)) continue;
                        points.add(next2);
                    }
                }
            } else {
                Point end;
                Point start;
                if (this.y1 < this.y2) {
                    start = new Point(this.x1, this.y1);
                    end = new Point(this.x2, this.y2);
                } else {
                    start = new Point(this.x2, this.y2);
                    end = new Point(this.x1, this.y1);
                }
                int moveY = end.y - start.y;
                int moveX = end.x - start.x;
                for (int pY3 = start.y + 1; pY3 < end.y; ++pY3) {
                    float currentX = (float)start.x + (float)moveX * ((float)(pY3 - start.y) / (float)moveY);
                    int startX = (int)Math.floor(currentX - lineRange);
                    int endY = (int)Math.ceil(currentX + lineRange);
                    for (int pX3 = startX; pX3 <= endY; ++pX3) {
                        Point next3 = new Point(pX3, pY3);
                        if (points.contains(next3) || !(Math.abs((float)pX3 - currentX) <= lineRange)) continue;
                        points.add(next3);
                    }
                }
            }
        }
    }

    private void addSmoothPoints(PointHashMap<PointDistance> points, float maxRange) {
        if (this.x1 == this.x2 && this.y1 == this.y2) {
            int startX = (int)Math.floor((float)this.x1 - maxRange);
            int endX = (int)Math.ceil((float)this.x1 + maxRange);
            int startY = (int)Math.floor((float)this.y1 - maxRange);
            int endY = (int)Math.ceil((float)this.y1 + maxRange);
            for (int pX = startX; pX <= endX; ++pX) {
                for (int pY = startY; pY <= endY; ++pY) {
                    double dist = new Point(this.x1, this.y1).distance(pX, pY);
                    if (dist > (double)maxRange) continue;
                    points.compute(pX, pY, (point, lastDist) -> {
                        if (lastDist == null) {
                            return new PointDistance(dist, maxRange, this.lineWidthPriority);
                        }
                        if (dist < lastDist.dist) {
                            return new PointDistance(dist, this.lineWidthPriority && !lastDist.lineWidthPriority ? (double)maxRange : Math.min((double)maxRange, lastDist.lineWidth), lastDist.lineWidthPriority || this.lineWidthPriority);
                        }
                        return lastDist;
                    });
                }
            }
        } else if (Math.abs(this.y1 - this.y2) <= Math.abs(this.x1 - this.x2)) {
            Point end;
            Point start;
            if (this.x1 < this.x2) {
                start = new Point(this.x1, this.y1);
                end = new Point(this.x2, this.y2);
            } else {
                start = new Point(this.x2, this.y2);
                end = new Point(this.x1, this.y1);
            }
            int moveY = end.y - start.y;
            int moveX = end.x - start.x;
            for (int currentX = start.x; currentX <= end.x; ++currentX) {
                float currentY = (float)start.y + (float)moveY * ((float)(currentX - start.x) / (float)moveX);
                int startX = (int)Math.floor((float)currentX - maxRange);
                int endX = (int)Math.ceil((float)currentX + maxRange);
                int startY = (int)Math.floor(currentY - maxRange);
                int endY = (int)Math.ceil(currentY + maxRange);
                for (int pX = startX; pX <= endX; ++pX) {
                    for (int pY = startY; pY <= endY; ++pY) {
                        double dist = this.getDistToPoint(pX, pY);
                        if (dist > (double)maxRange) continue;
                        points.compute(pX, pY, (point, lastDist) -> {
                            if (lastDist == null) {
                                return new PointDistance(dist, maxRange, this.lineWidthPriority);
                            }
                            if (dist < lastDist.dist) {
                                return new PointDistance(dist, this.lineWidthPriority && !lastDist.lineWidthPriority ? (double)maxRange : Math.min((double)maxRange, lastDist.lineWidth), lastDist.lineWidthPriority || this.lineWidthPriority);
                            }
                            return lastDist;
                        });
                    }
                }
            }
        } else {
            Point end;
            Point start;
            if (this.y1 < this.y2) {
                start = new Point(this.x1, this.y1);
                end = new Point(this.x2, this.y2);
            } else {
                start = new Point(this.x2, this.y2);
                end = new Point(this.x1, this.y1);
            }
            int moveY = end.y - start.y;
            int moveX = end.x - start.x;
            for (int currentY = start.y; currentY <= end.y; ++currentY) {
                float currentX = (float)start.x + (float)moveX * ((float)(currentY - start.y) / (float)moveY);
                int startX = (int)Math.floor(currentX - maxRange);
                int endX = (int)Math.ceil(currentX + maxRange);
                int startY = (int)Math.floor((float)currentY - maxRange);
                int endY = (int)Math.ceil((float)currentY + maxRange);
                for (int pX = startX; pX <= endX; ++pX) {
                    for (int pY = startY; pY <= endY; ++pY) {
                        double dist = this.getDistToPoint(pX, pY);
                        if (dist > (double)maxRange) continue;
                        points.compute(pX, pY, (point, lastDist) -> {
                            if (lastDist == null) {
                                return new PointDistance(dist, maxRange, this.lineWidthPriority);
                            }
                            if (dist < lastDist.dist) {
                                return new PointDistance(dist, this.lineWidthPriority && !lastDist.lineWidthPriority ? (double)maxRange : Math.min((double)maxRange, lastDist.lineWidth), lastDist.lineWidthPriority || this.lineWidthPriority);
                            }
                            return lastDist;
                        });
                    }
                }
            }
        }
    }

    private double getDistToPoint(int x, int y) {
        float yy;
        float xx;
        int a = x - this.x1;
        int b = y - this.y1;
        int c = this.x2 - this.x1;
        int d = this.y2 - this.y1;
        int dot = a * c + b * d;
        int lenSq = c * c + d * d;
        float param = -1.0f;
        if (lenSq != 0) {
            param = (float)dot / (float)lenSq;
        }
        if (param < 0.0f) {
            xx = this.x1;
            yy = this.y1;
        } else if (param > 1.0f) {
            xx = this.x2;
            yy = this.y2;
        } else {
            xx = (float)this.x1 + param * (float)c;
            yy = (float)this.y1 + param * (float)d;
        }
        float dx = (float)x - xx;
        float dy = (float)y - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void addDiamondPoints(PointHashSet points, float lineRange) {
        if (this.x1 == this.x2 && this.y1 == this.y2) {
            int startX = (int)Math.floor((float)this.x1 - lineRange);
            int endX = (int)Math.ceil((float)this.x1 + lineRange);
            int startY = (int)Math.floor((float)this.y1 - lineRange);
            int endY = (int)Math.ceil((float)this.y1 + lineRange);
            for (int pX = startX; pX <= endX; ++pX) {
                for (int pY = startY; pY <= endY; ++pY) {
                    if (points.contains(pX, pY) || !((float)(Math.abs(pY - this.x1) + Math.abs(pX - this.y1)) <= lineRange)) continue;
                    points.add(pX, pY);
                }
            }
        } else if (Math.abs(this.y1 - this.y2) <= Math.abs(this.x1 - this.x2)) {
            Point end;
            Point start;
            if (this.x1 < this.x2) {
                start = new Point(this.x1, this.y1);
                end = new Point(this.x2, this.y2);
            } else {
                start = new Point(this.x2, this.y2);
                end = new Point(this.x1, this.y1);
            }
            int moveY = end.y - start.y;
            int moveX = end.x - start.x;
            for (int currentX = start.x; currentX <= end.x; ++currentX) {
                float currentY = (float)start.y + (float)moveY * ((float)(currentX - start.x) / (float)moveX);
                int startX = (int)Math.floor((float)currentX - lineRange);
                int endX = (int)Math.ceil((float)currentX + lineRange);
                int startY = (int)Math.floor(currentY - lineRange);
                int endY = (int)Math.ceil(currentY + lineRange);
                for (int pX = startX; pX <= endX; ++pX) {
                    for (int pY = startY; pY <= endY; ++pY) {
                        if (points.contains(pX, pY) || !(Math.abs((float)pY - currentY) + (float)Math.abs(pX - currentX) <= lineRange)) continue;
                        points.add(pX, pY);
                    }
                }
            }
        } else {
            Point end;
            Point start;
            if (this.y1 < this.y2) {
                start = new Point(this.x1, this.y1);
                end = new Point(this.x2, this.y2);
            } else {
                start = new Point(this.x2, this.y2);
                end = new Point(this.x1, this.y1);
            }
            int moveY = end.y - start.y;
            int moveX = end.x - start.x;
            for (int currentY = start.y; currentY <= end.y; ++currentY) {
                float currentX = (float)start.x + (float)moveX * ((float)(currentY - start.y) / (float)moveY);
                int startX = (int)Math.floor(currentX - lineRange);
                int endX = (int)Math.ceil(currentX + lineRange);
                int startY = (int)Math.floor((float)currentY - lineRange);
                int endY = (int)Math.ceil((float)currentY + lineRange);
                for (int pX = startX; pX <= endX; ++pX) {
                    for (int pY = startY; pY <= endY; ++pY) {
                        if (points.contains(pX, pY) || !((float)Math.abs(pY - currentY) + Math.abs((float)pX - currentX) <= lineRange)) continue;
                        points.add(pX, pY);
                    }
                }
            }
        }
    }

    public static class PointDistance {
        public final double dist;
        public final double lineWidth;
        protected boolean lineWidthPriority;

        protected PointDistance(double dist, double lineWidth, boolean lineWidthPriority) {
            this.dist = dist;
            this.lineWidth = lineWidth;
            this.lineWidthPriority = lineWidthPriority;
        }
    }
}

