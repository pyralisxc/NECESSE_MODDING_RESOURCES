/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon;

import necesse.engine.util.GameMath;
import necesse.entity.mobs.summon.MinecartLine;
import necesse.entity.mobs.summon.MinecartLinePos;

public class MinecartLines {
    public int tileX;
    public int tileY;
    public MinecartLine up;
    public MinecartLine right;
    public MinecartLine down;
    public MinecartLine left;

    public MinecartLines(int tileX, int tileY) {
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public MinecartLinePos getMinecartPos(float entityX, float entityY, int entityDir) {
        if (entityDir == 0) {
            return this.bestPos(entityX, entityY, entityDir, this.up, this.right, this.left, this.down);
        }
        if (entityDir == 1) {
            return this.bestPos(entityX, entityY, entityDir, this.right, this.up, this.down, this.left);
        }
        if (entityDir == 2) {
            return this.bestPos(entityX, entityY, entityDir, this.down, this.right, this.left, this.up);
        }
        return this.bestPos(entityX, entityY, entityDir, this.left, this.up, this.down, this.right);
    }

    private MinecartLinePos bestPos(float entityX, float entityY, int entityDir, MinecartLine ... linePriority) {
        MinecartLinePos best = null;
        float bestDist = 0.0f;
        for (MinecartLine line : linePriority) {
            if (line == null) continue;
            MinecartLinePos next = this.linePos(line, entityX, entityY, entityDir);
            float nextDist = GameMath.diamondDistance(next.x, next.y, entityX, entityY);
            if (best != null && !(nextDist < bestDist)) continue;
            best = next;
            bestDist = nextDist;
        }
        return best;
    }

    private MinecartLinePos linePos(MinecartLine line, float entityX, float entityY, int entityDir) {
        int dir;
        if (line.dir == 1 || line.dir == 3) {
            int dir2;
            int n = dir2 = entityDir == 1 || entityDir == 0 ? 1 : 3;
            float distanceAlong = entityX <= line.x1 ? 0.0f : (entityX >= line.x2 ? line.distance : entityX - line.x1);
            if (entityDir == 0 || entityDir == 2) {
                if (line.dir == 3 && distanceAlong >= line.distance) {
                    dir2 = line.dir;
                } else if (line.dir == 1 && distanceAlong <= 0.0f) {
                    dir2 = line.dir;
                }
            }
            return new MinecartLinePos(line, GameMath.limit(entityX, line.x1, line.x2), GameMath.limit(entityY, line.y1, line.y2), distanceAlong, dir2);
        }
        int n = dir = entityDir == 1 || entityDir == 0 ? 0 : 2;
        float distanceAlong = entityY <= line.y1 ? 0.0f : (entityY >= line.y2 ? line.distance : entityY - line.y1);
        if (entityDir == 0 || entityDir == 2) {
            if (line.dir == 2 && distanceAlong >= line.distance) {
                dir = line.dir;
            } else if (line.dir == 0 && distanceAlong <= 0.0f) {
                dir = line.dir;
            }
        }
        return new MinecartLinePos(line, GameMath.limit(entityX, line.x1, line.x2), GameMath.limit(entityY, line.y1, line.y2), distanceAlong, dir);
    }
}

