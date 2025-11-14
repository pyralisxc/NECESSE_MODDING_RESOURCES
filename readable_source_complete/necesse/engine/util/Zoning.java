/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.PointHashSet;
import necesse.engine.util.PointSetAbstract;
import necesse.engine.util.PointTreeMap;
import necesse.engine.util.PointTreeSet;
import necesse.gfx.Renderer;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.level.maps.Level;

public class Zoning {
    protected PointTreeSet tiles = Zoning.getNewZoneSet();
    protected PointHashSet edgeTiles;
    protected LinkedList<Rectangle> rectangles;
    protected Rectangle bounds;
    public static final Point[] edgeTileOffsets = new Point[]{new Point(0, -1), new Point(1, 0), new Point(0, 1), new Point(-1, 0)};

    public Zoning(boolean trackEdgeTiles) {
        this.edgeTiles = trackEdgeTiles ? new PointHashSet() : null;
    }

    public Zoning() {
        this(false);
    }

    public void addZoneSaveData(String dataName, SaveData save) {
        LinkedList<Rectangle> rectangles = this.getTileRectangles();
        if (!rectangles.isEmpty()) {
            SaveData areas = new SaveData(dataName);
            for (Rectangle rectangle : rectangles) {
                areas.addIntArray("", new int[]{rectangle.x, rectangle.y, rectangle.width, rectangle.height});
            }
            save.addSaveData(areas);
        }
    }

    public Rectangle getLimits() {
        return null;
    }

    private Rectangle limitRectangle(Rectangle rectangle) {
        return Zoning.limitRectangle(rectangle, this.getLimits());
    }

    public static Rectangle limitRectangle(Rectangle rectangle, Rectangle limits) {
        if (limits != null && (limits.width > 0 || limits.height > 0)) {
            Rectangle intersectionRectangle = new Rectangle(rectangle);
            if (limits.width > 0) {
                intersectionRectangle.x = GameMath.limit(intersectionRectangle.x, limits.x, limits.x + limits.width);
                intersectionRectangle.width = GameMath.limit(intersectionRectangle.x + intersectionRectangle.width, limits.x, limits.x + limits.width) - intersectionRectangle.x;
            }
            if (limits.height > 0) {
                intersectionRectangle.y = GameMath.limit(intersectionRectangle.y, limits.y, limits.y + limits.height);
                intersectionRectangle.height = GameMath.limit(intersectionRectangle.y + intersectionRectangle.height, limits.y, limits.y + limits.height) - intersectionRectangle.y;
            }
            return intersectionRectangle;
        }
        return rectangle;
    }

    public boolean limitZoneToTiles(int startTileX, int endTileX, int startTileY, int endTileY) {
        boolean changed = false;
        for (Rectangle tileRectangle : this.getTileRectangles()) {
            int endDeltaY;
            int tileY;
            int endDeltaX;
            int tileX;
            int startDeltaY;
            int tileY2;
            int startDeltaX = startTileX - tileRectangle.x;
            if (startDeltaX > 0) {
                changed = true;
                for (int tileX2 = tileRectangle.x; tileX2 < tileRectangle.x + startDeltaX; ++tileX2) {
                    for (tileY2 = tileRectangle.y; tileY2 < tileRectangle.y + tileRectangle.height; ++tileY2) {
                        this.removeTile(tileX2, tileY2);
                    }
                }
            }
            if ((startDeltaY = startTileY - tileRectangle.y) > 0) {
                changed = true;
                for (tileY2 = tileRectangle.y; tileY2 < tileRectangle.y + startDeltaY; ++tileY2) {
                    for (tileX = tileRectangle.x; tileX < tileRectangle.x + tileRectangle.width; ++tileX) {
                        this.removeTile(tileX, tileY2);
                    }
                }
            }
            if ((endDeltaX = tileRectangle.x + tileRectangle.width - 1 - endTileX) > 0) {
                changed = true;
                for (tileX = tileRectangle.x + tileRectangle.width - 1; tileX >= tileRectangle.x + tileRectangle.width - endDeltaX; --tileX) {
                    for (tileY = tileRectangle.y; tileY < tileRectangle.y + tileRectangle.height; ++tileY) {
                        this.removeTile(tileX, tileY);
                    }
                }
            }
            if ((endDeltaY = tileRectangle.y + tileRectangle.height - 1 - endTileY) <= 0) continue;
            changed = true;
            for (tileY = tileRectangle.y + tileRectangle.height - 1; tileY >= tileRectangle.y + tileRectangle.height - endDeltaY; --tileY) {
                for (int tileX3 = tileRectangle.x; tileX3 < tileRectangle.x + tileRectangle.width; ++tileX3) {
                    this.removeTile(tileX3, tileY);
                }
            }
        }
        return changed;
    }

    public boolean limitZoneToRectangle(Rectangle rectangle) {
        return this.limitZoneToTiles(rectangle.x, rectangle.x + rectangle.width - 1, rectangle.y, rectangle.y + rectangle.height - 1);
    }

    public void applyZoneSaveData(String dataName, LoadData save, int tileXOffset, int tileYOffset) throws LoadDataException {
        LoadData areas = save.getFirstLoadDataByName(dataName);
        this.rectangles = new LinkedList();
        if (areas != null) {
            for (LoadData area : areas.getLoadData()) {
                try {
                    int[] array = LoadData.getIntArray(area);
                    Rectangle rectangle = new Rectangle(array[0] + tileXOffset, array[1] + tileYOffset, array[2], array[3]);
                    if (rectangle.isEmpty()) continue;
                    rectangle = this.limitRectangle(rectangle);
                    this.rectangles.add(rectangle);
                }
                catch (Exception e) {
                    throw new LoadDataException("Could not load zone area: " + area.getData());
                }
            }
        }
        this.tiles.clear();
        if (this.edgeTiles != null) {
            this.edgeTiles.clear();
        }
        this.addRectangles(this.rectangles);
    }

    public void writeZonePacket(PacketWriter writer) {
        LinkedList<Rectangle> rectangles = this.getTileRectangles();
        writer.putNextShortUnsigned(rectangles.size());
        for (Rectangle rectangle : rectangles) {
            writer.putNextInt(rectangle.x);
            writer.putNextInt(rectangle.y);
            writer.putNextInt(rectangle.width);
            writer.putNextInt(rectangle.height);
        }
    }

    public void readZonePacket(PacketReader reader) {
        this.rectangles = new LinkedList();
        int size = reader.getNextShortUnsigned();
        for (int i = 0; i < size; ++i) {
            int height;
            int width;
            int y;
            int x = reader.getNextInt();
            Rectangle rectangle = new Rectangle(x, y = reader.getNextInt(), width = reader.getNextInt(), height = reader.getNextInt());
            if (rectangle.isEmpty()) continue;
            rectangle = this.limitRectangle(rectangle);
            this.rectangles.add(rectangle);
        }
        this.tiles.clear();
        if (this.edgeTiles != null) {
            this.edgeTiles.clear();
        }
        this.addRectangles(this.rectangles);
    }

    public boolean containsTile(int x, int y) {
        return this.tiles.contains(x, y);
    }

    public boolean isEdgeTile(int x, int y) {
        return this.edgeTiles.contains(x, y);
    }

    public boolean addTile(int x, int y) {
        if (this.tiles.add(x, y)) {
            if (this.edgeTiles != null) {
                boolean isEdge = false;
                for (Point offset : edgeTileOffsets) {
                    int nextX = x + offset.x;
                    int nextY = y + offset.y;
                    if (this.tiles.contains(nextX, nextY)) {
                        this.updateIsEdge(nextX, nextY);
                    }
                    isEdge = isEdge || !this.tiles.contains(nextX, nextY);
                }
                if (isEdge) {
                    this.edgeTiles.add(x, y);
                }
            }
            this.bounds = null;
            this.rectangles = null;
            return true;
        }
        return false;
    }

    protected boolean updateIsEdge(int x, int y) {
        for (Point offset : edgeTileOffsets) {
            int nextX = x + offset.x;
            int nextY = y + offset.y;
            if (this.tiles.contains(nextX, nextY)) continue;
            this.edgeTiles.add(x, y);
            return true;
        }
        this.edgeTiles.remove(x, y);
        return false;
    }

    public boolean removeTile(int x, int y) {
        if (this.tiles.remove(x, y)) {
            if (this.edgeTiles != null) {
                this.edgeTiles.remove(x, y);
                for (Point offset : edgeTileOffsets) {
                    int nextX = x + offset.x;
                    int nextY = y + offset.y;
                    if (!this.tiles.contains(nextX, nextY)) continue;
                    this.edgeTiles.add(nextX, nextY);
                }
            }
            this.bounds = null;
            this.rectangles = null;
            return true;
        }
        return false;
    }

    public PointTreeSet getTiles() {
        return this.tiles;
    }

    public PointHashSet getEdgeTiles() {
        return this.edgeTiles;
    }

    public boolean supportsEdgeTiles() {
        return this.edgeTiles != null;
    }

    public int size() {
        return this.tiles.size();
    }

    public boolean isEmpty() {
        return this.tiles.isEmpty();
    }

    public void clear() {
        this.tiles.clear();
        if (this.edgeTiles != null) {
            this.edgeTiles.clear();
        }
        this.bounds = null;
        this.rectangles = null;
    }

    public static PointHashSet calculateAtDistanceTiles(PointSetAbstract<?> tiles, Predicate<Point> filter, int minRange, int maxRange) {
        minRange = Math.min(minRange, maxRange);
        float minComparator = (float)minRange - 0.5f;
        float maxComparator = (float)maxRange + 0.5f;
        PointHashSet validTiles = new PointHashSet();
        PointHashSet removes = new PointHashSet();
        for (int x = -maxRange; x <= maxRange; ++x) {
            for (int y = -maxRange; y <= maxRange; ++y) {
                double dist = Math.sqrt(x * x + y * y);
                if (!(dist <= (double)maxComparator)) continue;
                boolean remove = dist < (double)minComparator;
                for (Point tile : tiles) {
                    Point offsetTile = new Point(tile.x + x, tile.y + y);
                    if (remove) {
                        removes.add(offsetTile.x, offsetTile.y);
                        continue;
                    }
                    if (!filter.test(offsetTile)) continue;
                    validTiles.add(offsetTile.x, offsetTile.y);
                }
            }
        }
        for (Point p : removes) {
            validTiles.remove(p.x, p.y);
        }
        return validTiles;
    }

    public LinkedList<Rectangle> getTileRectangles() {
        if (this.rectangles == null) {
            this.rectangles = Zoning.toRectangles(this.tiles);
        }
        return this.rectangles;
    }

    public Rectangle getTileBounds() {
        if (this.tiles.isEmpty()) {
            return null;
        }
        if (this.bounds == null) {
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            for (Point tile : this.tiles) {
                minX = Math.min(tile.x, minX);
                minY = Math.min(tile.y, minY);
                maxX = Math.max(tile.x, maxX);
                maxY = Math.max(tile.y, maxY);
            }
            this.bounds = new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1);
        }
        return this.bounds;
    }

    public boolean removeDisconnected() {
        return this.removeDisconnected(Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public boolean removeDisconnected(int anchorX, int anchorY) {
        LinkedList<Rectangle> rectangles = this.getTileRectangles();
        LinkedList<Section> sections = new LinkedList<Section>();
        Section anchorSection = null;
        for (Rectangle rectangle : rectangles) {
            Section section;
            boolean containsAnchor = rectangle.contains(anchorX, anchorY);
            ListIterator li = sections.listIterator();
            Section connected = null;
            while (li.hasNext()) {
                section = (Section)li.next();
                if (!section.isConnected(rectangle)) continue;
                if (connected != null) {
                    if (anchorSection == section) {
                        anchorSection = connected;
                    }
                    connected.merge(section);
                    li.remove();
                }
                if (containsAnchor) {
                    anchorSection = section;
                }
                section.add(rectangle);
                connected = section;
            }
            if (connected != null) continue;
            section = new Section();
            if (containsAnchor) {
                anchorSection = section;
            }
            section.add(rectangle);
            sections.add(section);
        }
        if (sections.size() <= 1) {
            return false;
        }
        Section best = anchorSection;
        if (best == null) {
            best = sections.stream().max(Comparator.comparingInt(s -> ((Section)s).totalSize)).orElse(null);
        }
        for (Section section : sections) {
            if (section == best) continue;
            for (Rectangle rectangle : section.rectangles) {
                for (int x = 0; x < rectangle.width; ++x) {
                    for (int y = 0; y < rectangle.height; ++y) {
                        this.removeTile(rectangle.x + x, rectangle.y + y);
                    }
                }
            }
        }
        return true;
    }

    public void invert(Rectangle rectangle) {
        if (rectangle.isEmpty()) {
            return;
        }
        if ((rectangle = this.limitRectangle(rectangle)).isEmpty()) {
            return;
        }
        for (int x = 0; x < rectangle.width; ++x) {
            for (int y = 0; y < rectangle.height; ++y) {
                if (this.containsTile(rectangle.x + x, rectangle.y + y)) {
                    this.removeTile(rectangle.x + x, rectangle.y + y);
                    continue;
                }
                this.addTile(rectangle.x + x, rectangle.y + y);
            }
        }
    }

    public void invert() {
        Rectangle limits = this.getLimits();
        if (limits == null || limits.isEmpty()) {
            throw new UnsupportedOperationException("Cannot invert full zones with no limit");
        }
        this.invert(limits);
    }

    public boolean addRectangle(Rectangle rectangle) {
        boolean changed = false;
        if (rectangle.isEmpty()) {
            return false;
        }
        if ((rectangle = this.limitRectangle(rectangle)).isEmpty()) {
            return false;
        }
        for (int x = 0; x < rectangle.width; ++x) {
            for (int y = 0; y < rectangle.height; ++y) {
                changed = this.addTile(rectangle.x + x, rectangle.y + y) || changed;
            }
        }
        return changed;
    }

    public boolean addRectangles(List<Rectangle> rectangles) {
        boolean changed = false;
        for (Rectangle rectangle : rectangles) {
            changed = this.addRectangle(rectangle) || changed;
        }
        return changed;
    }

    public boolean removeRectangle(Rectangle rectangle) {
        boolean changed = false;
        if (rectangle.isEmpty()) {
            return false;
        }
        if ((rectangle = this.limitRectangle(rectangle)).isEmpty()) {
            return false;
        }
        for (int x = 0; x < rectangle.width; ++x) {
            for (int y = 0; y < rectangle.height; ++y) {
                changed = this.removeTile(rectangle.x + x, rectangle.y + y) || changed;
            }
        }
        return changed;
    }

    public boolean removeRectangles(List<Rectangle> rectangles) {
        boolean changed = false;
        for (Rectangle rectangle : rectangles) {
            changed = this.removeRectangle(rectangle) || changed;
        }
        return changed;
    }

    public static boolean isConnected(Rectangle r1, Rectangle r2) {
        return new Rectangle(r1.x - 1, r1.y, r1.width + 2, r1.height).intersects(r2) || new Rectangle(r1.x, r1.y - 1, r1.width, r1.height + 2).intersects(r2);
    }

    public static PointTreeSet getNewZoneSet() {
        Comparator<Point> comparator = Comparator.comparingInt(p -> p.y);
        comparator = comparator.thenComparingInt(p -> p.x);
        return new PointTreeSet(comparator);
    }

    public static <T> PointTreeMap<T> getNewZoneMap() {
        Comparator<Point> comparator = Comparator.comparingInt(p -> p.y);
        comparator = comparator.thenComparingInt(p -> p.x);
        return new PointTreeMap(comparator);
    }

    public static LinkedList<Rectangle> toRectangles(ToRectangleInterface zoneSet) {
        if (zoneSet.isEmpty()) {
            return new LinkedList<Rectangle>();
        }
        LinkedList<Rectangle> out = new LinkedList<Rectangle>();
        PointTreeSet open = Zoning.getNewZoneSet();
        PointHashSet closed = new PointHashSet();
        Point first = zoneSet.first();
        open.add(first.x, first.y);
        while (!open.isEmpty()) {
            Point start = open.pollFirst();
            if (closed.contains(start.x, start.y)) continue;
            closed.add(start.x, start.y);
            int width = Zoning.fillWidth(start, zoneSet, open, closed);
            int height = Zoning.fillHeight(start, width, zoneSet, open, closed);
            out.add(new Rectangle(start.x, start.y, width, height));
        }
        return out;
    }

    private static int fillWidth(Point start, ToRectangleInterface zoneSet, PointTreeSet open, PointHashSet closed) {
        Point next;
        Point current = start;
        int width = 1;
        while ((next = zoneSet.higher(current.x, current.y)) != null) {
            if (!closed.contains(next.x, next.y) && next.x == current.x + 1 && next.y == start.y) {
                ++width;
                closed.add(next.x, next.y);
                current = next;
                continue;
            }
            if (next.x == start.x && next.y == start.y + 1) break;
            open.add(next.x, next.y);
            break;
        }
        return width;
    }

    private static int fillHeight(Point start, int width, ToRectangleInterface zoneSet, PointTreeSet open, PointHashSet closed) {
        int height;
        block4: {
            Point nextRow;
            block3: {
                height = 1;
                while (true) {
                    int i;
                    nextRow = new Point(start.x, start.y + height);
                    if (!zoneSet.contains(nextRow.x, nextRow.y) || closed.contains(nextRow.x, nextRow.y)) break block3;
                    boolean filledRow = true;
                    for (i = 1; i < width; ++i) {
                        Point next = new Point(nextRow.x + i, nextRow.y);
                        if (zoneSet.contains(next.x, next.y) && !closed.contains(next.x, next.y)) continue;
                        filledRow = false;
                        break;
                    }
                    if (!filledRow) break;
                    ++height;
                    for (i = 0; i < width; ++i) {
                        closed.add(nextRow.x + i, nextRow.y);
                    }
                    Point next = zoneSet.higher(nextRow.x + width - 1, nextRow.y);
                    if (next == null) continue;
                    open.add(next.x, next.y);
                }
                open.add(nextRow.x, nextRow.y);
                break block4;
            }
            if (!zoneSet.contains(nextRow.x, nextRow.y)) break block4;
            open.add(nextRow.x, nextRow.y);
        }
        return height;
    }

    public SharedTextureDrawOptions getDrawOptions(Color edgeColor, Color fillColor, GameCamera camera) {
        Rectangle cameraBounds;
        Rectangle bounds = this.getTileBounds();
        if (bounds != null && (cameraBounds = camera.getBounds()).intersects(bounds.x * 32, bounds.y * 32, bounds.width * 32, bounds.height * 32)) {
            SharedTextureDrawOptions options = new SharedTextureDrawOptions(Renderer.getQuadTexture());
            for (Point tile : this.getTiles()) {
                if (!cameraBounds.intersects(tile.x * 32, tile.y * 32, 32.0, 32.0)) continue;
                boolean[] adjacent = new boolean[Level.adjacentGetters.length];
                for (int i = 0; i < adjacent.length; ++i) {
                    Point offset = Level.adjacentGetters[i];
                    adjacent[i] = this.containsTile(tile.x + offset.x, tile.y + offset.y);
                }
                Zoning.addDrawOptions(options, tile, adjacent, edgeColor, fillColor, camera);
            }
            return options;
        }
        return null;
    }

    public static void addDrawOptions(SharedTextureDrawOptions options, Point tile, boolean[] adj, Color edgeColor, Color fillColor, GameCamera camera) {
        Zoning.addDrawOptions(options, camera.getTileDrawX(tile.x), camera.getTileDrawY(tile.y), adj, edgeColor, fillColor);
    }

    public static void addDrawOptions(SharedTextureDrawOptions options, int drawX, int drawY, boolean[] adj, Color edgeColor, Color fillColor) {
        Zoning.addDrawOptions(options, drawX, drawY, adj, edgeColor, fillColor, 16, 16);
    }

    public static void addDrawOptions(SharedTextureDrawOptions options, int drawX, int drawY, boolean[] adj, Color edgeColor, Color fillColor, int res1, int res2) {
        Color[] topLeft = Zoning.getEdgeColors(adj[3], adj[0], adj[1], edgeColor, fillColor);
        Zoning.addDrawOptions(options, drawX, drawY, 0, res1, res1, topLeft);
        Color[] topRight = Zoning.getEdgeColors(adj[1], adj[2], adj[4], edgeColor, fillColor);
        Zoning.addDrawOptions(options, drawX + res1, drawY, 3, res2, res1, topRight);
        Color[] botRight = Zoning.getEdgeColors(adj[4], adj[7], adj[6], edgeColor, fillColor);
        Zoning.addDrawOptions(options, drawX + res1, drawY + res1, 2, res2, res2, botRight);
        Color[] botLeft = Zoning.getEdgeColors(adj[6], adj[5], adj[3], edgeColor, fillColor);
        Zoning.addDrawOptions(options, drawX, drawY + res1, 1, res1, res2, botLeft);
    }

    public static void addDrawOptions(SharedTextureDrawOptions options, Point tile, int offset, Color[] colors, int xOffset, int yOffset, int width, int height, GameCamera camera) {
        Zoning.addDrawOptions(options, camera.getTileDrawX(tile.x) + xOffset, camera.getTileDrawY(tile.y) + yOffset, offset, width, height, colors);
    }

    public static void addDrawOptions(SharedTextureDrawOptions options, int drawX, int drawY, int offset, int width, int height, Color[] colors) {
        float[] fColors = new float[16];
        for (int i = 0; i < colors.length; ++i) {
            Color color = colors[(i + offset) % colors.length];
            fColors[i * 4] = (float)color.getRed() / 255.0f;
            fColors[i * 4 + 1] = (float)color.getGreen() / 255.0f;
            fColors[i * 4 + 2] = (float)color.getBlue() / 255.0f;
            fColors[i * 4 + 3] = (float)color.getAlpha() / 255.0f;
        }
        options.addFull().size(width, height).advColor(fColors).pos(drawX, drawY);
    }

    public static Color[] getEdgeColors(boolean first, boolean diagonal, boolean last, Color edgeColor, Color fillColor) {
        if (first) {
            if (last) {
                if (diagonal) {
                    return new Color[]{fillColor, fillColor, fillColor, fillColor};
                }
                return new Color[]{edgeColor, fillColor, fillColor, fillColor};
            }
            return new Color[]{edgeColor, edgeColor, fillColor, fillColor};
        }
        if (last) {
            return new Color[]{edgeColor, fillColor, fillColor, edgeColor};
        }
        return new Color[]{edgeColor, edgeColor, fillColor, edgeColor};
    }

    public static SharedTextureDrawOptions getRectangleDrawOptions(Rectangle rectangle, Color edgeColor, Color fillColor, GameCamera camera) {
        SharedTextureDrawOptions options = new SharedTextureDrawOptions(Renderer.getQuadTexture());
        Zoning.addRectangleDrawOptions(options, rectangle, edgeColor, fillColor, camera);
        return options;
    }

    public static SharedTextureDrawOptions getRectangleDrawOptions(Rectangle rectangle, Color edgeColor, Color fillColor, int edgeSize, GameCamera camera) {
        SharedTextureDrawOptions options = new SharedTextureDrawOptions(Renderer.getQuadTexture());
        Zoning.addRectangleDrawOptions(options, rectangle, edgeColor, fillColor, edgeSize, camera);
        return options;
    }

    public static SharedTextureDrawOptions getRectangleDrawOptions(Rectangle rectangle, Color edgeColor, Color fillColor, int edgeSize, int drawX, int drawY) {
        SharedTextureDrawOptions options = new SharedTextureDrawOptions(Renderer.getQuadTexture());
        Zoning.addRectangleDrawOptions(options, rectangle, edgeColor, fillColor, edgeSize, drawX, drawY);
        return options;
    }

    public static void addRectangleDrawOptions(SharedTextureDrawOptions options, Rectangle rectangle, Color edgeColor, Color fillColor, GameCamera camera) {
        Zoning.addRectangleDrawOptions(options, rectangle, edgeColor, fillColor, 16, camera);
    }

    public static void addRectangleDrawOptions(SharedTextureDrawOptions options, Rectangle rectangle, Color edgeColor, Color fillColor, int edgeSize, GameCamera camera) {
        Zoning.addRectangleDrawOptions(options, rectangle, edgeColor, fillColor, edgeSize, -camera.getX(), -camera.getY());
    }

    public static void addRectangleDrawOptions(SharedTextureDrawOptions options, Rectangle rectangle, Color edgeColor, Color fillColor, int edgeSize, int drawX, int drawY) {
        float edgeR = (float)edgeColor.getRed() / 255.0f;
        float edgeG = (float)edgeColor.getGreen() / 255.0f;
        float edgeB = (float)edgeColor.getBlue() / 255.0f;
        float edgeA = (float)edgeColor.getAlpha() / 255.0f;
        float fillR = (float)fillColor.getRed() / 255.0f;
        float fillG = (float)fillColor.getGreen() / 255.0f;
        float fillB = (float)fillColor.getBlue() / 255.0f;
        float fillA = (float)fillColor.getAlpha() / 255.0f;
        int xEdge = Math.min(rectangle.width / 2, edgeSize);
        int yEdge = Math.min(rectangle.height / 2, edgeSize);
        options.addFull().size(rectangle.width - xEdge * 2, rectangle.height - yEdge * 2).color(fillR, fillG, fillB, fillA).pos(drawX + rectangle.x + xEdge, drawY + rectangle.y + yEdge);
        float[] topLeft = new float[]{edgeR, edgeG, edgeB, edgeA, edgeR, edgeG, edgeB, edgeA, fillR, fillG, fillB, fillA, edgeR, edgeG, edgeB, edgeA};
        options.addFull().size(xEdge, yEdge).advColor(topLeft).pos(drawX + rectangle.x, drawY + rectangle.y);
        float[] top = new float[]{edgeR, edgeG, edgeB, edgeA, edgeR, edgeG, edgeB, edgeA, fillR, fillG, fillB, fillA, fillR, fillG, fillB, fillA};
        options.addFull().size(rectangle.width - xEdge * 2, yEdge).advColor(top).pos(drawX + rectangle.x + xEdge, drawY + rectangle.y);
        float[] topRight = new float[]{edgeR, edgeG, edgeB, edgeA, edgeR, edgeG, edgeB, edgeA, edgeR, edgeG, edgeB, edgeA, fillR, fillG, fillB, fillA};
        options.addFull().size(xEdge, yEdge).advColor(topRight).pos(drawX + rectangle.x + rectangle.width - xEdge, drawY + rectangle.y);
        float[] right = new float[]{fillR, fillG, fillB, fillA, edgeR, edgeG, edgeB, edgeA, edgeR, edgeG, edgeB, edgeA, fillR, fillG, fillB, fillA};
        options.addFull().size(xEdge, rectangle.height - yEdge * 2).advColor(right).pos(drawX + rectangle.x + rectangle.width - xEdge, drawY + rectangle.y + yEdge);
        float[] botRight = new float[]{fillR, fillG, fillB, fillA, edgeR, edgeG, edgeB, edgeA, edgeR, edgeG, edgeB, edgeA, edgeR, edgeG, edgeB, edgeA};
        options.addFull().size(xEdge, yEdge).advColor(botRight).pos(drawX + rectangle.x + rectangle.width - xEdge, drawY + rectangle.y + rectangle.height - yEdge);
        float[] bot = new float[]{fillR, fillG, fillB, fillA, fillR, fillG, fillB, fillA, edgeR, edgeG, edgeB, edgeA, edgeR, edgeG, edgeB, edgeA};
        options.addFull().size(rectangle.width - xEdge * 2, yEdge).advColor(bot).pos(drawX + rectangle.x + xEdge, drawY + rectangle.y + rectangle.height - yEdge);
        float[] botLeft = new float[]{edgeR, edgeG, edgeB, edgeA, fillR, fillG, fillB, fillA, edgeR, edgeG, edgeB, edgeA, edgeR, edgeG, edgeB, edgeA};
        options.addFull().size(xEdge, yEdge).advColor(botLeft).pos(drawX + rectangle.x, drawY + rectangle.y + rectangle.height - yEdge);
        float[] left = new float[]{edgeR, edgeG, edgeB, edgeA, fillR, fillG, fillB, fillA, fillR, fillG, fillB, fillA, edgeR, edgeG, edgeB, edgeA};
        options.addFull().size(xEdge, rectangle.height - yEdge * 2).advColor(left).pos(drawX + rectangle.x, drawY + rectangle.y + yEdge);
    }

    public static interface ToRectangleInterface {
        public boolean isEmpty();

        public Point first();

        public boolean contains(int var1, int var2);

        public Point higher(int var1, int var2);
    }

    private static class Section {
        private final LinkedList<Rectangle> rectangles = new LinkedList();
        private int totalSize;

        public boolean isConnected(Rectangle r) {
            for (Rectangle r2 : this.rectangles) {
                if (!Zoning.isConnected(r, r2)) continue;
                return true;
            }
            return false;
        }

        public void add(Rectangle r) {
            this.rectangles.add(r);
            this.totalSize += r.width * r.height;
        }

        public void merge(Section other) {
            this.rectangles.addAll(other.rectangles);
            this.totalSize += other.totalSize;
        }
    }
}

