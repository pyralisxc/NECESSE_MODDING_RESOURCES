/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.summon;

import java.awt.geom.Line2D;
import java.util.function.Consumer;
import java.util.function.Supplier;
import necesse.entity.mobs.summon.MinecartLinePos;

public class MinecartLine
extends Line2D.Float {
    public final int tileX;
    public final int tileY;
    public final int dir;
    public final float distance;
    public Supplier<MinecartLine> nextPositive;
    public Supplier<MinecartLine> nextNegative;

    protected MinecartLine(int tileX, int tileY, float x1, float y1, float x2, float y2, int dir, float distance) {
        super((float)(tileX * 32) + x1, (float)(tileY * 32) + y1, (float)(tileX * 32) + x2, (float)(tileY * 32) + y2);
        this.tileX = tileX;
        this.tileY = tileY;
        this.dir = dir;
        this.distance = distance;
    }

    public static MinecartLine up(int tileX, int tileY) {
        return new MinecartLine(tileX, tileY, 16.0f, 0.0f, 16.0f, 16.0f, 0, 16.0f);
    }

    public static MinecartLine down(int tileX, int tileY) {
        return new MinecartLine(tileX, tileY, 16.0f, 16.0f, 16.0f, 32.0f, 2, 16.0f);
    }

    public static MinecartLine left(int tileX, int tileY) {
        return new MinecartLine(tileX, tileY, 0.0f, 16.0f, 16.0f, 16.0f, 3, 16.0f);
    }

    public static MinecartLine right(int tileX, int tileY) {
        return new MinecartLine(tileX, tileY, 16.0f, 16.0f, 32.0f, 16.0f, 1, 16.0f);
    }

    public static MinecartLine upEnd(int tileX, int tileY) {
        return new MinecartLine(tileX, tileY, 16.0f, 14.0f, 16.0f, 16.0f, 0, 2.0f);
    }

    public static MinecartLine downEnd(int tileX, int tileY) {
        return new MinecartLine(tileX, tileY, 16.0f, 16.0f, 16.0f, 26.0f, 2, 10.0f);
    }

    public static MinecartLine leftEnd(int tileX, int tileY) {
        return new MinecartLine(tileX, tileY, 10.0f, 16.0f, 16.0f, 16.0f, 3, 6.0f);
    }

    public static MinecartLine rightEnd(int tileX, int tileY) {
        return new MinecartLine(tileX, tileY, 16.0f, 16.0f, 22.0f, 16.0f, 1, 6.0f);
    }

    /*
     * Enabled aggressive block sorting
     */
    public MinecartLinePos progressLines(MinecartLine line, int moveDir, float currentDistanceAlong, float distanceToTravel, Consumer<MinecartLine> forEachLine) {
        block26: {
            while (true) {
                MinecartLine next;
                float missingDistance;
                if (forEachLine != null) {
                    forEachLine.accept(line);
                }
                if (moveDir == 0) {
                    missingDistance = currentDistanceAlong;
                    if (distanceToTravel > missingDistance) {
                        distanceToTravel -= missingDistance;
                        currentDistanceAlong = 0.0f;
                        if (line.nextNegative != null && (next = line.nextNegative.get()) != null) {
                            currentDistanceAlong = next.distance;
                            line = next;
                            if (next.dir == 1) {
                                moveDir = next.dir;
                                currentDistanceAlong = 0.0f;
                                continue;
                            }
                            if (next.dir != 3) continue;
                            moveDir = next.dir;
                            currentDistanceAlong = next.distance;
                            continue;
                        }
                        break block26;
                    } else {
                        if (line.dir != 1 && line.dir != 3) {
                            return new MinecartLinePos(line, line.x1, line.y1 + currentDistanceAlong - distanceToTravel, currentDistanceAlong - distanceToTravel, moveDir);
                        }
                        return new MinecartLinePos(line, line.x1 + currentDistanceAlong - distanceToTravel, line.y1, currentDistanceAlong - distanceToTravel, 1);
                    }
                }
                if (moveDir == 1) {
                    missingDistance = line.distance - currentDistanceAlong;
                    if (distanceToTravel > missingDistance) {
                        distanceToTravel -= missingDistance;
                        currentDistanceAlong = line.distance;
                        if (line.nextPositive != null && (next = line.nextPositive.get()) != null) {
                            currentDistanceAlong = 0.0f;
                            line = next;
                            if (next.dir == 0) {
                                moveDir = next.dir;
                                currentDistanceAlong = next.distance;
                                continue;
                            }
                            if (next.dir != 2) continue;
                            moveDir = next.dir;
                            currentDistanceAlong = 0.0f;
                            continue;
                        }
                        break block26;
                    } else {
                        if (line.dir != 1 && line.dir != 3) {
                            return new MinecartLinePos(line, line.x1, line.y1 + currentDistanceAlong + distanceToTravel, currentDistanceAlong + distanceToTravel, 0);
                        }
                        return new MinecartLinePos(line, line.x1 + currentDistanceAlong + distanceToTravel, line.y1, currentDistanceAlong + distanceToTravel, moveDir);
                    }
                }
                if (moveDir == 2) {
                    missingDistance = line.distance - currentDistanceAlong;
                    if (distanceToTravel > missingDistance) {
                        distanceToTravel -= missingDistance;
                        currentDistanceAlong = line.distance;
                        if (line.nextPositive != null && (next = line.nextPositive.get()) != null) {
                            currentDistanceAlong = 0.0f;
                            line = next;
                            if (next.dir == 1) {
                                moveDir = next.dir;
                                currentDistanceAlong = 0.0f;
                                continue;
                            }
                            if (next.dir != 3) continue;
                            moveDir = next.dir;
                            currentDistanceAlong = next.distance;
                            continue;
                        }
                        break block26;
                    } else {
                        if (line.dir != 1 && line.dir != 3) {
                            return new MinecartLinePos(line, line.x1, line.y1 + currentDistanceAlong + distanceToTravel, currentDistanceAlong + distanceToTravel, moveDir);
                        }
                        return new MinecartLinePos(line, line.x1 + currentDistanceAlong + distanceToTravel, line.y1, currentDistanceAlong + distanceToTravel, 3);
                    }
                }
                missingDistance = currentDistanceAlong;
                if (!(distanceToTravel > missingDistance)) break;
                distanceToTravel -= missingDistance;
                currentDistanceAlong = 0.0f;
                if (line.nextNegative != null && (next = line.nextNegative.get()) != null) {
                    currentDistanceAlong = next.distance;
                    line = next;
                    if (next.dir == 0) {
                        moveDir = next.dir;
                        currentDistanceAlong = next.distance;
                        continue;
                    }
                    if (next.dir != 2) continue;
                    moveDir = next.dir;
                    currentDistanceAlong = 0.0f;
                    continue;
                }
                break block26;
                break;
            }
            if (line.dir != 1 && line.dir != 3) {
                return new MinecartLinePos(line, line.x1, line.y1 + currentDistanceAlong - distanceToTravel, currentDistanceAlong - distanceToTravel, 2);
            }
            return new MinecartLinePos(line, line.x1 + currentDistanceAlong - distanceToTravel, line.y1, currentDistanceAlong - distanceToTravel, moveDir);
        }
        if (line.dir == 0) {
            return new MinecartLinePos(line, line.x1, line.y1 + 0.1f, 0.1f, moveDir, distanceToTravel);
        }
        if (line.dir == 1) {
            return new MinecartLinePos(line, line.x1 + line.distance - 0.1f, line.y1, line.distance - 0.1f, moveDir, distanceToTravel);
        }
        if (line.dir == 2) {
            return new MinecartLinePos(line, line.x1, line.y1 + line.distance - 0.1f, line.distance - 0.1f, moveDir, distanceToTravel);
        }
        return new MinecartLinePos(line, line.x1 + 0.1f, line.y1, 0.1f, moveDir, distanceToTravel);
    }

    /*
     * Enabled aggressive block sorting
     */
    public MinecartLinePos progressLines(MinecartLine line, boolean positiveDirection, float currentDistanceAlong, float distanceToTravel, Consumer<MinecartLine> forEachLine) {
        while (true) {
            MinecartLine next;
            block17: {
                float missingDistance;
                if (forEachLine != null) {
                    forEachLine.accept(line);
                }
                if (positiveDirection) {
                    missingDistance = line.distance - currentDistanceAlong;
                    if (distanceToTravel > missingDistance) {
                        distanceToTravel -= missingDistance;
                        break block17;
                    } else {
                        if (line.dir != 1 && line.dir != 3) {
                            return new MinecartLinePos(line, line.x1, line.y1 + currentDistanceAlong + distanceToTravel, currentDistanceAlong + distanceToTravel, 2);
                        }
                        return new MinecartLinePos(line, line.x1 + currentDistanceAlong + distanceToTravel, line.y1, currentDistanceAlong + distanceToTravel, 1);
                    }
                }
                missingDistance = line.distance;
                if (distanceToTravel > missingDistance) {
                    distanceToTravel -= missingDistance;
                } else {
                    if (line.dir != 1 && line.dir != 3) {
                        return new MinecartLinePos(line, line.x1, line.y1 + currentDistanceAlong - distanceToTravel, currentDistanceAlong - distanceToTravel, 0);
                    }
                    return new MinecartLinePos(line, line.x1 + currentDistanceAlong - distanceToTravel, line.y1, currentDistanceAlong - distanceToTravel, 3);
                }
            }
            if (positiveDirection) {
                if (line.nextPositive == null || (next = line.nextPositive.get()) == null) break;
                if (line.dir == 3 && next.dir == 0 || line.dir == 0 && next.dir == 3) {
                    positiveDirection = false;
                    currentDistanceAlong = next.distance;
                } else {
                    currentDistanceAlong = 0.0f;
                }
                line = next;
                continue;
            }
            if (line.nextNegative == null || (next = line.nextNegative.get()) == null) break;
            if (line.dir == 1 && next.dir == 2 || line.dir == 2 && next.dir == 1) {
                positiveDirection = true;
                currentDistanceAlong = 0.0f;
            } else {
                currentDistanceAlong = next.distance;
            }
            line = next;
        }
        if (positiveDirection) {
            if (line.dir != 1 && line.dir != 3) {
                return new MinecartLinePos(line, line.x1, line.y1 + line.distance, line.distance, 2, distanceToTravel);
            }
            return new MinecartLinePos(line, line.x1 + line.distance, line.y1, line.distance, 1, distanceToTravel);
        }
        if (line.dir != 1 && line.dir != 3) {
            return new MinecartLinePos(line, line.x1, line.y1, 0.0f, 0, distanceToTravel);
        }
        return new MinecartLinePos(line, line.x1, line.y1, 0.0f, 3, distanceToTravel);
    }
}

