/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.entity.mobs.summon.MinecartLine;
import necesse.entity.mobs.summon.MinecartLines;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.MinecartTrackObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;

public class TrapTrackObject
extends MinecartTrackObject {
    public TrapTrackObject() {
        this.setItemCategory("objects", "traps");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/traptrack");
        this.endingTexture = GameTexture.fromFile("objects/traptrackending");
    }

    @Override
    public MinecartLines getMinecartLines(Level level, int x, int y, int rotation, float entityDx, float entityDy, boolean ignoreEntityDirection) {
        MinecartLines lines = new MinecartLines(x, y);
        boolean hasUp = level.getObjectID(x, y - 1) == this.getID();
        boolean hasDown = level.getObjectID(x, y + 1) == this.getID();
        boolean hasLeft = level.getObjectID(x - 1, y) == this.getID();
        boolean hasRight = level.getObjectID(x + 1, y) == this.getID();
        float turnThreshold = 0.2f;
        if (rotation == 0 || rotation == 2) {
            byte rightRotation;
            byte leftRotation;
            boolean straightAcross = false;
            boolean anyConnectionFromSides = false;
            if (hasLeft && hasRight) {
                leftRotation = level.getObjectRotation(x - 1, y);
                byte rightRotation2 = level.getObjectRotation(x + 1, y);
                if (leftRotation == 1 && rightRotation2 == 3) {
                    straightAcross = true;
                }
            }
            if (hasLeft && !straightAcross && (leftRotation = level.getObjectRotation(x - 1, y)) == 1) {
                lines.left = MinecartLine.left(x, y);
                lines.left.nextNegative = () -> this.getMinecartLines((Level)level, (int)(x - 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).right;
                anyConnectionFromSides = true;
            }
            if (hasRight && !straightAcross && (rightRotation = level.getObjectRotation(x + 1, y)) == 3) {
                lines.right = MinecartLine.right(x, y);
                lines.right.nextPositive = () -> this.getMinecartLines((Level)level, (int)(x + 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).left;
                anyConnectionFromSides = true;
            }
            if (hasUp) {
                if (rotation != 2 || !anyConnectionFromSides) {
                    lines.up = MinecartLine.up(x, y);
                    lines.up.nextNegative = () -> this.getMinecartLines((Level)level, (int)x, (int)(y - 1), (float)entityDx, (float)entityDy, (boolean)false).down;
                }
            } else if (rotation == 0 || !anyConnectionFromSides) {
                lines.up = MinecartLine.upEnd(x, y);
                lines.up.nextNegative = null;
            }
            if (hasDown) {
                if (rotation != 0 || !anyConnectionFromSides) {
                    lines.down = MinecartLine.down(x, y);
                    lines.down.nextPositive = () -> this.getMinecartLines((Level)level, (int)x, (int)(y + 1), (float)entityDx, (float)entityDy, (boolean)false).up;
                }
            } else if (rotation == 2 || !anyConnectionFromSides) {
                lines.down = MinecartLine.downEnd(x, y);
                lines.down.nextPositive = null;
            }
            if (lines.up != null && lines.down != null) {
                lines.up.nextPositive = () -> lines.down;
                lines.down.nextNegative = () -> lines.up;
            }
            if (straightAcross) {
                lines.left = MinecartLine.left(x, y);
                lines.left.nextNegative = () -> this.getMinecartLines((Level)level, (int)(x - 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).right;
                lines.right = MinecartLine.right(x, y);
                lines.right.nextPositive = () -> this.getMinecartLines((Level)level, (int)(x + 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).left;
                lines.left.nextPositive = () -> lines.right;
                lines.right.nextNegative = () -> lines.left;
            } else if (lines.right != null) {
                lines.right.nextPositive = () -> this.getMinecartLines((Level)level, (int)(x + 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).left;
                if (rotation == 2) {
                    if (lines.up == null || entityDx > turnThreshold) {
                        lines.down.nextNegative = () -> lines.right;
                    }
                    lines.right.nextNegative = () -> lines.down;
                } else {
                    if ((lines.down == null || entityDx > turnThreshold) && lines.up != null) {
                        lines.up.nextPositive = () -> lines.right;
                    }
                    lines.right.nextNegative = () -> lines.up;
                }
            } else if (lines.left != null) {
                lines.left.nextNegative = () -> this.getMinecartLines((Level)level, (int)(x - 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).right;
                if (rotation == 2) {
                    if (lines.up == null || entityDx < -turnThreshold) {
                        lines.down.nextNegative = () -> lines.left;
                    }
                    lines.left.nextPositive = () -> lines.down;
                } else {
                    if ((lines.down == null || entityDx < -turnThreshold) && lines.up != null) {
                        lines.up.nextPositive = () -> lines.left;
                    }
                    lines.left.nextPositive = () -> lines.up;
                }
            }
        } else {
            byte downRotation;
            byte upRotation;
            boolean straightAcross = false;
            boolean anyConnectionFromSides = false;
            if (hasUp && hasDown) {
                upRotation = level.getObjectRotation(x, y - 1);
                byte downRotation2 = level.getObjectRotation(x, y + 1);
                if (upRotation == 2 && downRotation2 == 0) {
                    straightAcross = true;
                }
            }
            if (hasUp && !straightAcross && (upRotation = level.getObjectRotation(x, y - 1)) == 2) {
                lines.up = MinecartLine.up(x, y);
                lines.up.nextNegative = () -> this.getMinecartLines((Level)level, (int)x, (int)(y - 1), (float)entityDx, (float)entityDy, (boolean)false).down;
                anyConnectionFromSides = true;
            }
            if (hasDown && !straightAcross && (downRotation = level.getObjectRotation(x, y + 1)) == 0) {
                lines.down = MinecartLine.down(x, y);
                lines.down.nextPositive = () -> this.getMinecartLines((Level)level, (int)x, (int)(y + 1), (float)entityDx, (float)entityDy, (boolean)false).up;
                anyConnectionFromSides = true;
            }
            if (hasLeft) {
                if (rotation != 1 || !anyConnectionFromSides) {
                    lines.left = MinecartLine.left(x, y);
                    lines.left.nextNegative = () -> this.getMinecartLines((Level)level, (int)(x - 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).right;
                }
            } else if (rotation == 3 || !anyConnectionFromSides) {
                lines.left = MinecartLine.leftEnd(x, y);
                lines.left.nextNegative = null;
            }
            if (hasRight) {
                if (rotation != 3 || !anyConnectionFromSides) {
                    lines.right = MinecartLine.right(x, y);
                    lines.right.nextPositive = () -> this.getMinecartLines((Level)level, (int)(x + 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).left;
                }
            } else if (rotation == 1 || !anyConnectionFromSides) {
                lines.right = MinecartLine.rightEnd(x, y);
                lines.right.nextPositive = null;
            }
            if (lines.left != null && lines.right != null) {
                lines.left.nextPositive = () -> lines.right;
                lines.right.nextNegative = () -> lines.left;
            }
            if (straightAcross) {
                lines.up = MinecartLine.up(x, y);
                lines.up.nextNegative = () -> this.getMinecartLines((Level)level, (int)x, (int)(y - 1), (float)entityDx, (float)entityDy, (boolean)false).down;
                lines.down = MinecartLine.down(x, y);
                lines.down.nextPositive = () -> this.getMinecartLines((Level)level, (int)x, (int)(y + 1), (float)entityDx, (float)entityDy, (boolean)false).up;
                lines.up.nextPositive = () -> lines.down;
                lines.down.nextNegative = () -> lines.up;
            } else if (lines.down != null) {
                lines.down.nextPositive = () -> this.getMinecartLines((Level)level, (int)x, (int)(y + 1), (float)entityDx, (float)entityDy, (boolean)false).up;
                if (rotation == 1) {
                    if (lines.left == null || entityDy > turnThreshold && !ignoreEntityDirection) {
                        lines.right.nextNegative = () -> lines.down;
                    }
                    lines.down.nextNegative = () -> lines.right;
                } else {
                    if ((lines.right == null || entityDy > turnThreshold && !ignoreEntityDirection) && lines.left != null) {
                        lines.left.nextPositive = () -> lines.down;
                    }
                    lines.down.nextNegative = () -> lines.left;
                }
            } else if (lines.up != null) {
                lines.up.nextNegative = () -> this.getMinecartLines((Level)level, (int)x, (int)(y - 1), (float)entityDx, (float)entityDy, (boolean)false).down;
                if (rotation == 1) {
                    if (lines.left == null || entityDy < -turnThreshold) {
                        lines.right.nextNegative = () -> lines.up;
                    }
                    lines.up.nextPositive = () -> lines.right;
                } else {
                    if ((lines.right == null || entityDy < -turnThreshold) && lines.left != null) {
                        lines.left.nextPositive = () -> lines.up;
                    }
                    lines.up.nextPositive = () -> lines.left;
                }
            }
        }
        return lines;
    }
}

