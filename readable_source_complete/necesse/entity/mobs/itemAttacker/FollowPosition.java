/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.itemAttacker;

import java.awt.Point;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.itemAttacker.FollowPositionGetter;
import necesse.entity.mobs.itemAttacker.FollowerPosition;
import necesse.entity.mobs.mobMovement.MobMovementCircle;
import necesse.entity.mobs.mobMovement.MobMovementCircleRelative;

public class FollowPosition {
    public static FollowPosition WALK_CLOSE = new FollowPosition((m, i, t) -> null, (m, i, t) -> null);
    public static FollowPosition CIRCLE_FAR = new FollowPosition((m, i, t) -> null, (m, i, t) -> FollowPosition.circlingPos(m, i, t, 60, 0.0f, 100, 5));
    public static FollowPosition FLYING_CIRCLE = new FollowPosition((m, i, t) -> null, (m, i, t) -> FollowPosition.circlingPos(m, i, t, 50, 30.0f, 50, 10));
    public static FollowPosition FLYING_CIRCLE_FAST = new FollowPosition((m, i, t) -> null, (m, i, t) -> FollowPosition.circlingPos(m, i, t, 40, 60.0f, 50, 10));
    public static FollowPosition SLIME_CIRCLE_MOVEMENT = new FollowPosition((m, i, t) -> null, (m, i, t) -> FollowPosition.circlingPos(m, i, t, 40, 85.0f, 70, 10));
    public static FollowPosition WIDE_CIRCLE_MOVEMENT = new FollowPosition((m, i, t) -> null, (m, i, t) -> FollowPosition.circlingPos(m, i, t, 40, 85.0f, 250, 10));
    public static FollowPosition FLYING = FollowPosition.newFlying(0, 0, 30, -40);
    public static FollowPosition PYRAMID = FollowPosition.newPyramid(30, 30);
    public static FollowPosition LARGE_PYRAMID = FollowPosition.newPyramid(60, 60);
    public final FollowPositionGetter getDefaultPoint;
    public final FollowPositionGetter getRelativePos;

    public static FollowerPosition circlingPos(Mob target, int index, int totalMobs, int size, float speed, int eachCircleDistance, int firstAddedDistance) {
        int range;
        int circumference;
        int mobsInCircle;
        int circle = 1;
        while ((mobsInCircle = (circumference = (int)(Math.PI * (double)(range = firstAddedDistance + circle * eachCircleDistance) * 2.0)) / size) <= index) {
            ++circle;
            index -= mobsInCircle;
            totalMobs -= mobsInCircle;
        }
        int finalRange = range;
        boolean reversed = circle % 2 == 0;
        float anglePerMob = 360.0f / (float)Math.min(totalMobs, mobsInCircle);
        float angleOffset = anglePerMob * (float)index;
        float rotSpeed = MobMovementCircle.convertToRotSpeed(finalRange, speed);
        Point offset = MobMovementCircle.getOffsetPosition(target, finalRange, rotSpeed, angleOffset, false);
        return new FollowerPosition(offset.x, offset.y, t -> new MobMovementCircleRelative(target, (Mob)t, finalRange, rotSpeed, angleOffset, reversed));
    }

    public static FollowPosition newFlying(int xOffset, int yOffset, int rowSize, int colSize) {
        return new FollowPosition((m, i, t) -> {
            int row = i / 8 + 1;
            int col = i % 8 + 1;
            return new FollowerPosition(xOffset + rowSize * col, yOffset + colSize * row);
        }, (m, i, t) -> {
            int row = i / 8 + 1;
            int col = i % 8 + 1;
            int dir = m.getDir();
            if (dir == 1) {
                return new FollowerPosition(-xOffset - rowSize * col, yOffset + colSize * row);
            }
            if (dir == 3) {
                return new FollowerPosition(xOffset + rowSize * col, yOffset + colSize * row);
            }
            return null;
        });
    }

    public static FollowPosition newPyramid(int rowSize, int colSize) {
        return new FollowPosition((m, i, t) -> null, (m, i, t) -> {
            int indexOffset = 4;
            int rowOffset = 2;
            int row = (int)((-1.0 + Math.sqrt(1 + 8 * (i + indexOffset))) / 2.0) - rowOffset;
            int lastRow = (int)((-1.0 + Math.sqrt(1 + 8 * (t + indexOffset))) / 2.0) - rowOffset;
            int realRow = row + rowOffset;
            int mobsBefore = (realRow * realRow + realRow) / 2 - indexOffset;
            int indexInRow = i - mobsBefore;
            int mobsInRow = 3 + row + row % 2;
            if (lastRow == row) {
                mobsInRow = t - mobsBefore + row % 2;
            }
            int rowPos = rowSize * row;
            int colPos = row == 0 ? (i == 0 ? colSize : -colSize) : colSize * mobsInRow / 2 - indexInRow * colSize - row % 2 * colSize / 2 - colSize / 2;
            int dir = m.getDir();
            if (dir == 0) {
                return new FollowerPosition(colPos, rowPos);
            }
            if (dir == 1) {
                return new FollowerPosition(-rowPos, colPos);
            }
            if (dir == 2) {
                return new FollowerPosition(colPos, -rowPos);
            }
            return new FollowerPosition(rowPos, colPos);
        });
    }

    public FollowPosition(FollowPositionGetter getDefaultPoint, FollowPositionGetter getRelativePos) {
        this.getDefaultPoint = getDefaultPoint;
        this.getRelativePos = getRelativePos;
    }

    public FollowerPosition getRelativePos(Mob target, FollowerPosition currentPos, int index, int totalMobs) {
        FollowerPosition pos = this.getRelativePos.getPosition(target, index, totalMobs);
        if (currentPos == null && pos == null) {
            return this.getDefaultPoint.getPosition(target, index, totalMobs);
        }
        return pos == null ? currentPos : pos;
    }
}

