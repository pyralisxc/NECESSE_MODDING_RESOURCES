/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.multiTile;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import necesse.engine.registries.ObjectRegistry;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.presets.PresetRotation;
import necesse.level.maps.presets.PresetUtils;

public class MultiTile {
    public final int rotation;
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final boolean isMaster;
    public final int[] ids;

    public MultiTile(int x, int y, int width, int height, int rotation, boolean isMaster, int ... ids) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height cannot be 0 or below");
        }
        if (x >= width || y >= height) {
            throw new IllegalArgumentException("X/Y cannot be higher or equal to width/height");
        }
        if (ids.length != width * height) {
            throw new IllegalArgumentException("IDs must have a length equal width * height to contain all multiTile ids");
        }
        this.rotation = rotation;
        this.isMaster = isMaster;
        PresetRotation angle = PresetRotation.toRotationAngle(rotation);
        if (angle == null || width == 1 && height == 1) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.ids = ids;
        } else {
            Point pos = PresetUtils.getRotatedPointInSpace(x, y, width, height, angle);
            this.x = pos.x;
            this.y = pos.y;
            Point dim = PresetUtils.getRotatedPoint(width, height, 0, 0, angle);
            this.width = Math.abs(dim.x);
            this.height = Math.abs(dim.y);
            this.ids = new int[ids.length];
            for (int cX = 0; cX < width; ++cX) {
                for (int cY = 0; cY < height; ++cY) {
                    Point rotatedPos = PresetUtils.getRotatedPointInSpace(cX, cY, width, height, angle);
                    this.ids[this.getIndex((int)rotatedPos.x, (int)rotatedPos.y, (int)this.width)] = ids[this.getIndex(cX, cY, width)];
                }
            }
        }
    }

    public String checkValid() {
        HashSet<Integer> usedIDs = new HashSet<Integer>();
        boolean foundMaster = false;
        for (int id : this.ids) {
            if (id == Integer.MIN_VALUE) continue;
            if (id < 0 || id >= ObjectRegistry.getObjectsCount()) {
                return "Could not find object with id " + id;
            }
            if (usedIDs.contains(id)) {
                return ObjectRegistry.getObject(id).getStringID() + " used multiple times";
            }
            if (ObjectRegistry.getObject(id).isMultiTileMaster()) {
                if (foundMaster) {
                    return "Has multiple master objects";
                }
                foundMaster = true;
            }
            usedIDs.add(id);
        }
        if (!foundMaster) {
            return "Does not have a master object";
        }
        return null;
    }

    protected int getIndex(int x, int y, int width) {
        return x + y * width;
    }

    public Point getCenterLevelPos(int tileX, int tileY) {
        int centerX = (tileX - this.x) * 32 + this.width * 32 / 2;
        int centerY = (tileY - this.y) * 32 + this.height * 32 / 2;
        return new Point(centerX, centerY);
    }

    public int getCenterXOffset() {
        float w = (float)(this.width - 1) / 2.0f - (float)this.x;
        boolean isEven = this.width % 2 == 0;
        return (int)(w + (isEven ? Math.signum(w) : 0.0f));
    }

    public int getCenterYOffset() {
        float h = (float)(this.height - 1) / 2.0f - (float)this.y;
        boolean isEven = this.height % 2 == 0;
        return (int)(h + (isEven ? Math.signum(h) : 0.0f));
    }

    public Point getCenterTileOffset() {
        return new Point(this.getCenterXOffset(), this.getCenterYOffset());
    }

    public Rectangle getTileRectangle(int tileX, int tileY) {
        return new Rectangle(tileX - this.x, tileY - this.y, this.width, this.height);
    }

    public boolean containsTile(int tileX, int tileY, int checkTileX, int checkTileY) {
        return this.getTileRectangle(tileX, tileY).contains(checkTileX, checkTileY);
    }

    public Rectangle getAdjacentTileRectangle(int tileX, int tileY) {
        return new Rectangle(tileX - this.x - 1, tileY - this.y - 1, this.width + 2, this.height + 2);
    }

    public Rectangle getLevelRectangle(int tileX, int tileY) {
        Rectangle r = this.getTileRectangle(tileX, tileY);
        return new Rectangle(r.x * 32, r.y * 32, r.width * 32, r.height * 32);
    }

    public Point getMirrorXPosOffset() {
        return new Point(0, 0);
    }

    public Point getMirrorYPosOffset() {
        return new Point(0, 0);
    }

    public int getXMirrorRotation() {
        if (this.rotation == 1) {
            return 3;
        }
        if (this.rotation == 3) {
            return 1;
        }
        return this.rotation;
    }

    public int getYMirrorRotation() {
        if (this.rotation == 0) {
            return 2;
        }
        if (this.rotation == 2) {
            return 0;
        }
        return this.rotation;
    }

    public Point getPresetRotationOffset(PresetRotation presetRotation) {
        return new Point(0, 0);
    }

    public int getPresetRotation(PresetRotation presetRotation) {
        if (presetRotation == null) {
            return this.rotation;
        }
        return (this.rotation + presetRotation.dirOffset) % 4;
    }

    public ArrayList<CoordinateValue<Integer>> getIDs(int tileX, int tileY) {
        ArrayList<CoordinateValue<Integer>> list = new ArrayList<CoordinateValue<Integer>>();
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                int cX = tileX + x - this.x;
                int cY = tileY + y - this.y;
                int id = this.ids[this.getIndex(x, y, this.width)];
                if (id == Integer.MIN_VALUE) continue;
                list.add(new CoordinateValue<Integer>(cX, cY, id));
            }
        }
        return list;
    }

    public Stream<CoordinateValue<Integer>> streamIDs(int tileX, int tileY) {
        Stream.Builder<CoordinateValue<Integer>> objectBuilder = Stream.builder();
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                int cX = tileX + x - this.x;
                int cY = tileY + y - this.y;
                int id = this.ids[this.getIndex(x, y, this.width)];
                if (id == Integer.MIN_VALUE) continue;
                objectBuilder.add(new CoordinateValue<Integer>(cX, cY, id));
            }
        }
        return objectBuilder.build();
    }

    public Stream<CoordinateValue<GameObject>> streamObjects(int tileX, int tileY) {
        return this.streamIDs(tileX, tileY).map(e -> new CoordinateValue<GameObject>(e.tileX, e.tileY, ObjectRegistry.getObject((Integer)e.value)));
    }

    public IntStream streamIDs() {
        return this.streamIDs(0, 0).mapToInt(e -> (Integer)e.value);
    }

    public Stream<GameObject> streamObjects() {
        return this.streamIDs().mapToObj(ObjectRegistry::getObject);
    }

    public Stream<CoordinateValue<Integer>> streamOtherIDs(int tileX, int tileY) {
        return this.streamIDs(tileX, tileY).filter(e -> e.tileX != tileX || e.tileY != tileY);
    }

    public Stream<CoordinateValue<GameObject>> streamOtherObjects(int tileX, int tileY) {
        return this.streamOtherIDs(tileX, tileY).map(e -> new CoordinateValue<GameObject>(e.tileX, e.tileY, ObjectRegistry.getObject((Integer)e.value)));
    }

    public IntStream streamOtherIDs() {
        return this.streamIDs(0, 0).filter(e -> e.tileX != 0 || e.tileY != 0).mapToInt(e -> (Integer)e.value);
    }

    public Stream<GameObject> streamOtherObjects() {
        return this.streamOtherIDs().mapToObj(ObjectRegistry::getObject);
    }

    public Optional<LevelObject> getMasterLevelObject(Level level, int layerID, int tileX, int tileY) {
        if (this.isMaster) {
            return Optional.of(new LevelObject(level, layerID, tileX, tileY));
        }
        return this.streamObjects(tileX, tileY).filter(e -> ((GameObject)e.value).isMultiTileMaster() && level.getObjectID(layerID, e.tileX, e.tileY) == ((GameObject)e.value).getID()).findFirst().map(e -> new LevelObject(level, layerID, e.tileX, e.tileY));
    }

    public Optional<Point> getMasterTilePos(int tileX, int tileY) {
        if (this.isMaster) {
            return Optional.of(new Point(tileX, tileY));
        }
        return this.streamObjects(tileX, tileY).filter(e -> ((GameObject)e.value).isMultiTileMaster()).findFirst().map(e -> new Point(e.tileX, e.tileY));
    }

    @Deprecated
    public Optional<LevelObject> getMasterLevelObject(Level level, int tileX, int tileY) {
        return this.getMasterLevelObject(level, 0, tileX, tileY);
    }

    public GameObject getMasterObject() {
        return this.streamObjects().filter(GameObject::isMultiTileMaster).findFirst().orElseThrow(() -> new IllegalStateException("Could not find object master"));
    }

    public LinkedList<Point> getAdjacentTiles(int tileX, int tileY, boolean addDiagonal) {
        LinkedList<Point> out = new LinkedList<Point>();
        for (int x = 0; x < this.width; ++x) {
            int cX = tileX + x - this.x;
            out.add(new Point(cX, tileY - 1 - this.y));
            out.add(new Point(cX, tileY + this.height - this.y));
        }
        for (int y = 0; y < this.height; ++y) {
            int cY = tileY + y - this.y;
            out.add(new Point(tileX - 1 - this.x, cY));
            out.add(new Point(tileX + this.width - this.x, cY));
        }
        if (addDiagonal) {
            out.add(new Point(tileX - 1 - this.x, tileY - 1 - this.y));
            out.add(new Point(tileX + this.width - this.x, tileY - 1 - this.y));
            out.add(new Point(tileX - 1 - this.x, tileY + this.height - this.y));
            out.add(new Point(tileX + this.width - this.x, tileY + this.height - this.y));
        }
        return out;
    }

    public static class CoordinateValue<T> {
        public final int tileX;
        public final int tileY;
        public final T value;

        public CoordinateValue(int tileX, int tileY, T value) {
            this.tileX = tileX;
            this.tileY = tileY;
            this.value = value;
        }
    }
}

