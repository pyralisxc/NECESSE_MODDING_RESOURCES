/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.ai.path;

import java.awt.Point;
import java.util.ArrayList;
import java.util.stream.Stream;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ai.path.FinalPathPoint;
import necesse.entity.mobs.ai.path.TilePathfinding;
import necesse.gfx.camera.GameCamera;

public class FinalPath {
    private static float TILE_LENGTH = 32.0f;
    private static float TILE_DIAGONAL_LENGTH = (float)Math.sqrt(TILE_LENGTH * TILE_LENGTH * 2.0f);
    private ArrayList<FinalPathPoint> pathArray;
    private float removedPathLength;
    private float pathLength = -1.0f;

    public FinalPath(ArrayList<FinalPathPoint> pathArray) {
        this.pathArray = pathArray;
    }

    public float getCurrentLength() {
        if (this.pathLength == -1.0f) {
            this.pathLength = FinalPath.calculatePathLength(this.pathArray);
        }
        return this.pathLength;
    }

    public float getFullLength() {
        return this.getCurrentLength() + this.removedPathLength;
    }

    public int size() {
        if (this.pathArray != null) {
            return this.pathArray.size();
        }
        return 0;
    }

    public void removeFirst() {
        FinalPathPoint last = this.pathArray.get(0);
        this.pathArray.remove(0);
        if (this.pathArray.size() <= 1) {
            this.pathLength = 0.0f;
        } else {
            FinalPathPoint next = this.pathArray.get(0);
            float pointLength = FinalPath.calculatePathLength(last, next);
            if (this.pathLength > 0.0f) {
                this.pathLength -= pointLength;
            }
            this.removedPathLength += pointLength;
        }
    }

    public FinalPathPoint getFirst() {
        if (this.pathArray == null || this.pathArray.size() == 0) {
            return null;
        }
        return this.pathArray.get(0);
    }

    public FinalPathPoint getLast() {
        if (this.pathArray == null || this.pathArray.size() == 0) {
            return null;
        }
        return this.pathArray.get(this.pathArray.size() - 1);
    }

    public Stream<FinalPathPoint> streamPathPoints() {
        return this.pathArray.stream();
    }

    public void drawPath(Mob mob, GameCamera camera) {
        TilePathfinding.drawPath(mob, this.pathArray, camera);
    }

    public static float calculatePathLength(ArrayList<? extends Point> pathArray) {
        if (pathArray == null || pathArray.size() == 1) {
            return 0.0f;
        }
        float lengthCounter = 0.0f;
        for (int i = 1; i < pathArray.size(); ++i) {
            Point prev = pathArray.get(i - 1);
            Point current = pathArray.get(i);
            lengthCounter += FinalPath.calculatePathLength(prev, current);
        }
        return lengthCounter;
    }

    public static float calculatePathLength(Point p1, Point p2) {
        int yDif;
        int xDif;
        if (xDif == 0) {
            return TILE_LENGTH * (float)yDif;
        }
        if (xDif == yDif) {
            return TILE_DIAGONAL_LENGTH * (float)xDif;
        }
        float counter = 0.0f;
        if (xDif < yDif) {
            for (yDif = Math.abs(p1.y - p2.y); yDif != xDif; --yDif) {
                counter += TILE_LENGTH;
            }
            counter += TILE_DIAGONAL_LENGTH * (float)xDif;
        } else {
            for (xDif = Math.abs(p1.x - p2.x); xDif != yDif; --xDif) {
                counter += TILE_LENGTH;
            }
            counter += TILE_DIAGONAL_LENGTH * (float)yDif;
        }
        return counter;
    }
}

