/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon;

import java.util.function.Consumer;
import necesse.entity.mobs.summon.MinecartLine;

public class MinecartLinePos {
    public final MinecartLine line;
    public final float x;
    public final float y;
    public final float distanceAlong;
    public final int dir;
    public final float distanceRemainingToTravel;

    public MinecartLinePos(MinecartLine line, float x, float y, float distanceAlong, int dir, float distanceRemainingToTravel) {
        this.line = line;
        this.x = x;
        this.y = y;
        this.distanceAlong = distanceAlong;
        this.dir = dir;
        this.distanceRemainingToTravel = distanceRemainingToTravel;
    }

    public MinecartLinePos(MinecartLine line, float x, float y, float distanceAlong, int dir) {
        this(line, x, y, distanceAlong, dir, 0.0f);
    }

    public MinecartLinePos progressLines(int currentDir, float distanceToTravel, Consumer<MinecartLine> forEachLine) {
        return this.line.progressLines(this.line, currentDir, this.distanceAlong, distanceToTravel, forEachLine);
    }
}

