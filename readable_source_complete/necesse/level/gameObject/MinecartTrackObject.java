/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.summon.MinecartLine;
import necesse.entity.mobs.summon.MinecartLines;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.MinecartObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class MinecartTrackObject
extends GameObject {
    public ObjectDamagedTextureArray texture;
    public ObjectDamagedTextureArray supportTexture;
    public ObjectDamagedTextureArray bridgeTexture;
    public GameTexture endingTexture;
    protected final GameRandom drawRandom;

    public MinecartTrackObject() {
        this.mapColor = new Color(84, 67, 41);
        this.displayMapTooltip = true;
        this.objectHealth = 50;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.drawRandom = new GameRandom();
        this.canPlaceOnShore = true;
        this.canPlaceOnLiquid = true;
        this.overridesInLiquid = true;
        this.stackSize = 500;
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "misc");
        this.replaceCategories.add("minecarttrack");
        this.canReplaceCategories.add("minecarttrack");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/minecarttrack");
        this.supportTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/minecarttracksupport");
        this.bridgeTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/minecarttrackbridge");
        this.endingTexture = GameTexture.fromFile("objects/minecarttrackending");
    }

    public TrackSprite getSprite(Level level, int tileX, int tileY, int rotation) {
        TrackSprite out = new TrackSprite();
        MinecartLines lines = this.getMinecartLines(level, tileX, tileY, rotation, 0.0f, 0.0f, false);
        if (lines.up != null) {
            MinecartLine upLine;
            out.goingUp();
            MinecartLine minecartLine = upLine = lines.up.nextNegative != null ? lines.up.nextNegative.get() : null;
            if (upLine == null) {
                out.connectedUp = false;
            }
        }
        if (lines.right != null) {
            MinecartLine rightLine;
            out.goingRight();
            MinecartLine minecartLine = rightLine = lines.right.nextPositive != null ? lines.right.nextPositive.get() : null;
            if (rightLine == null) {
                out.connectedRight = false;
            }
        }
        if (lines.down != null) {
            MinecartLine downLine;
            out.goingDown();
            MinecartLine minecartLine = downLine = lines.down.nextPositive != null ? lines.down.nextPositive.get() : null;
            if (downLine == null) {
                out.connectedDown = false;
            }
        }
        if (lines.left != null) {
            MinecartLine leftLine;
            out.goingLeft();
            MinecartLine minecartLine = leftLine = lines.left.nextNegative != null ? lines.left.nextNegative.get() : null;
            if (leftLine == null) {
                out.connectedLeft = false;
            }
        }
        switch (rotation) {
            case 0: {
                if (out.connectedLeft && out.connectedRight) {
                    return out.sprite(4, 0);
                }
                if (out.connectedLeft && out.connectedDown) {
                    return out.sprite(3, 3);
                }
                if (out.connectedRight && out.connectedDown) {
                    return out.sprite(2, 3);
                }
                if (out.connectedLeft) {
                    return out.sprite(3, 1);
                }
                if (out.connectedRight) {
                    return out.sprite(2, 1);
                }
                return out.sprite(1, 0);
            }
            case 1: {
                if (out.connectedUp && out.connectedDown) {
                    return out.sprite(4, 0);
                }
                if (out.connectedUp && out.connectedLeft) {
                    return out.sprite(0, 3);
                }
                if (out.connectedDown && out.connectedLeft) {
                    return out.sprite(0, 2);
                }
                if (out.connectedUp) {
                    return out.sprite(2, 1);
                }
                if (out.connectedDown) {
                    return out.sprite(2, 0);
                }
                return out.sprite(0, 0);
            }
            case 2: {
                if (out.connectedLeft && out.connectedRight) {
                    return out.sprite(4, 0);
                }
                if (out.connectedLeft && out.connectedUp) {
                    return out.sprite(3, 2);
                }
                if (out.connectedRight && out.connectedUp) {
                    return out.sprite(2, 2);
                }
                if (out.connectedLeft) {
                    return out.sprite(3, 0);
                }
                if (out.connectedRight) {
                    return out.sprite(2, 0);
                }
                return out.sprite(1, 0);
            }
            case 3: {
                if (out.connectedUp && out.connectedDown) {
                    return out.sprite(4, 0);
                }
                if (out.connectedUp && out.connectedRight) {
                    return out.sprite(1, 3);
                }
                if (out.connectedDown && out.connectedRight) {
                    return out.sprite(1, 2);
                }
                if (out.connectedUp) {
                    return out.sprite(3, 1);
                }
                if (out.connectedDown) {
                    return out.sprite(3, 0);
                }
                return out.sprite(0, 0);
            }
        }
        return out.sprite(0, 0);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        DrawOptionsList options = new DrawOptionsList();
        TrackSprite sprite = this.getSprite(level, tileX, tileY, rotation);
        GameTexture bridgeTexture = this.bridgeTexture.getDamagedTexture(this, level, tileX, tileY);
        GameTexture supportTexture = this.supportTexture.getDamagedTexture(this, level, tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        if (level.isLiquidTile(tileX, tileY) || level.isShore(tileX, tileY)) {
            if ((level.isLiquidTile(tileX, tileY + 1) || level.isShore(tileX, tileY + 1)) && (!sprite.connectedDown || sprite.connectedLeft || sprite.connectedRight)) {
                TextureDrawOptionsEnd bridgeOptions = bridgeTexture.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(drawX, drawY + 8);
                tileList.add(-100, tm -> bridgeOptions.draw());
            }
            options.add(supportTexture.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(drawX, drawY));
        }
        options.add(texture.initDraw().sprite(sprite.x, sprite.y, 32).light(light).pos(drawX, drawY));
        if (sprite.goingUp && !sprite.connectedUp) {
            options.add(this.endingTexture.initDraw().sprite(0, 0, 32).light(light).pos(drawX, drawY));
        }
        if (sprite.goingRight && !sprite.connectedRight) {
            options.add(this.endingTexture.initDraw().sprite(0, 1, 32).light(light).pos(drawX, drawY));
        }
        if (sprite.goingDown && !sprite.connectedDown) {
            options.add(this.endingTexture.initDraw().sprite(0, 2, 32).light(light).pos(drawX, drawY));
        }
        if (sprite.goingLeft && !sprite.connectedLeft) {
            options.add(this.endingTexture.initDraw().sprite(0, 3, 32).light(light).pos(drawX, drawY));
        }
        tileList.add(tm -> options.draw());
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        TrackSprite sprite = this.getSprite(level, tileX, tileY, rotation);
        GameTexture bridgeTexture = this.bridgeTexture.getDamagedTexture(this, level, tileX, tileY);
        GameTexture supportTexture = this.supportTexture.getDamagedTexture(this, level, tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        if (level.isLiquidTile(tileX, tileY) || level.isShore(tileX, tileY)) {
            if ((level.isLiquidTile(tileX, tileY + 1) || level.isShore(tileX, tileY + 1)) && (!sprite.connectedDown || sprite.connectedLeft || sprite.connectedRight)) {
                bridgeTexture.initDraw().sprite(sprite.x, sprite.y, 32).alpha(alpha).draw(drawX, drawY + 8);
            }
            supportTexture.initDraw().sprite(sprite.x, sprite.y, 32).alpha(alpha).draw(drawX, drawY);
        }
        texture.initDraw().sprite(sprite.x, sprite.y, 32).alpha(alpha).draw(drawX, drawY);
        if (sprite.goingUp && !sprite.connectedUp) {
            this.endingTexture.initDraw().sprite(0, 0, 32).alpha(alpha).draw(drawX, drawY);
        }
        if (sprite.goingRight && !sprite.connectedRight) {
            this.endingTexture.initDraw().sprite(0, 1, 32).alpha(alpha).draw(drawX, drawY);
        }
        if (sprite.goingDown && !sprite.connectedDown) {
            this.endingTexture.initDraw().sprite(0, 2, 32).alpha(alpha).draw(drawX, drawY);
        }
        if (sprite.goingLeft && !sprite.connectedLeft) {
            this.endingTexture.initDraw().sprite(0, 3, 32).alpha(alpha).draw(drawX, drawY);
        }
    }

    public MinecartLines getMinecartLines(Level level, int x, int y, float entityDx, float entityDy, boolean ignoreEntityDirection) {
        byte rotation = level.getObjectRotation(x, y);
        return this.getMinecartLines(level, x, y, rotation, entityDx, entityDy, ignoreEntityDirection);
    }

    public MinecartLines getMinecartLines(Level level, int x, int y, int rotation, float entityDx, float entityDy, boolean ignoreEntityDirection) {
        MinecartLines lines = new MinecartLines(x, y);
        boolean hasUp = level.getObjectID(x, y - 1) == this.getID();
        boolean hasDown = level.getObjectID(x, y + 1) == this.getID();
        boolean hasLeft = level.getObjectID(x - 1, y) == this.getID();
        boolean hasRight = level.getObjectID(x + 1, y) == this.getID();
        float turnThreshold = 0.2f;
        if (rotation == 0 || rotation == 2) {
            byte rightRotation;
            boolean success;
            byte leftRotation;
            boolean straightAcross = false;
            boolean anyConnectionFromSides = false;
            if (hasLeft && ((leftRotation = level.getObjectRotation(x - 1, y)) == 1 || leftRotation == 3)) {
                byte rightRotation2;
                success = true;
                if (hasRight && ((rightRotation2 = level.getObjectRotation(x + 1, y)) == 1 || rightRotation2 == 3)) {
                    success = false;
                }
                if (success) {
                    lines.left = MinecartLine.left(x, y);
                    lines.left.nextNegative = () -> this.getMinecartLines((Level)level, (int)(x - 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).right;
                    anyConnectionFromSides = true;
                } else {
                    straightAcross = true;
                }
            }
            if (hasRight && ((rightRotation = level.getObjectRotation(x + 1, y)) == 1 || rightRotation == 3)) {
                byte leftRotation2;
                success = true;
                if (hasLeft && ((leftRotation2 = level.getObjectRotation(x - 1, y)) == 1 || leftRotation2 == 3)) {
                    success = false;
                }
                if (success) {
                    lines.right = MinecartLine.right(x, y);
                    lines.right.nextPositive = () -> this.getMinecartLines((Level)level, (int)(x + 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).left;
                    anyConnectionFromSides = true;
                } else {
                    straightAcross = true;
                }
            }
            if (hasUp) {
                lines.up = MinecartLine.up(x, y);
                lines.up.nextNegative = () -> this.getMinecartLines((Level)level, (int)x, (int)(y - 1), (float)entityDx, (float)entityDy, (boolean)false).down;
            } else if (rotation == 0 || !anyConnectionFromSides) {
                lines.up = MinecartLine.upEnd(x, y);
                lines.up.nextNegative = null;
            }
            if (hasDown) {
                lines.down = MinecartLine.down(x, y);
                lines.down.nextPositive = () -> this.getMinecartLines((Level)level, (int)x, (int)(y + 1), (float)entityDx, (float)entityDy, (boolean)false).up;
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
                    if (lines.up == null || entityDx > turnThreshold && !ignoreEntityDirection) {
                        lines.down.nextNegative = () -> lines.right;
                    }
                    lines.right.nextNegative = () -> lines.down;
                } else {
                    if ((lines.down == null || entityDx > turnThreshold && !ignoreEntityDirection) && lines.up != null) {
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
            boolean success;
            byte upRotation;
            boolean straightAcross = false;
            boolean anyConnectionFromSides = false;
            if (hasUp && ((upRotation = level.getObjectRotation(x, y - 1)) == 0 || upRotation == 2)) {
                byte downRotation2;
                success = true;
                if (hasDown && ((downRotation2 = level.getObjectRotation(x, y + 1)) == 0 || downRotation2 == 2)) {
                    success = false;
                }
                if (success) {
                    lines.up = MinecartLine.up(x, y);
                    lines.up.nextNegative = () -> this.getMinecartLines((Level)level, (int)x, (int)(y - 1), (float)entityDx, (float)entityDy, (boolean)false).down;
                    anyConnectionFromSides = true;
                } else {
                    straightAcross = true;
                }
            }
            if (hasDown && ((downRotation = level.getObjectRotation(x, y + 1)) == 0 || downRotation == 2)) {
                byte upRotation2;
                success = true;
                if (hasUp && ((upRotation2 = level.getObjectRotation(x, y - 1)) == 0 || upRotation2 == 2)) {
                    success = false;
                }
                if (success) {
                    lines.down = MinecartLine.down(x, y);
                    lines.down.nextPositive = () -> this.getMinecartLines((Level)level, (int)x, (int)(y + 1), (float)entityDx, (float)entityDy, (boolean)false).up;
                    anyConnectionFromSides = true;
                } else {
                    straightAcross = true;
                }
            }
            if (hasLeft) {
                lines.left = MinecartLine.left(x, y);
                lines.left.nextNegative = () -> this.getMinecartLines((Level)level, (int)(x - 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).right;
            } else if (rotation == 3 || !anyConnectionFromSides) {
                lines.left = MinecartLine.leftEnd(x, y);
                lines.left.nextNegative = null;
            }
            if (hasRight) {
                lines.right = MinecartLine.right(x, y);
                lines.right.nextPositive = () -> this.getMinecartLines((Level)level, (int)(x + 1), (int)y, (float)entityDx, (float)entityDy, (boolean)false).left;
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
                    if (lines.left == null || entityDy > turnThreshold) {
                        lines.right.nextNegative = () -> lines.down;
                    }
                    lines.down.nextNegative = () -> lines.right;
                } else {
                    if ((lines.right == null || entityDy > turnThreshold) && lines.left != null) {
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

    @Override
    public Item generateNewObjectItem() {
        return new MinecartObjectItem(this);
    }

    @Override
    public boolean canReplaceRotation(Level level, int layerID, int tileX, int tileY, int currentRotation, int newRotation) {
        boolean isNewHorizontal;
        if (!super.canReplaceRotation(level, layerID, tileX, tileY, currentRotation, newRotation)) {
            return false;
        }
        boolean isCurrentVertical = currentRotation == 0 || currentRotation == 2;
        boolean isCurrentHorizontal = currentRotation == 1 || currentRotation == 3;
        boolean isNewVertical = newRotation == 0 || newRotation == 2;
        boolean bl = isNewHorizontal = newRotation == 1 || newRotation == 3;
        if (isCurrentHorizontal && isNewHorizontal) {
            boolean hasBotTrack;
            boolean hasTopTrack = level.getObjectID(layerID, tileX, tileY - 1) == this.getID();
            boolean bl2 = hasBotTrack = level.getObjectID(layerID, tileX, tileY + 1) == this.getID();
            if (hasTopTrack && hasBotTrack) {
                return false;
            }
            return hasTopTrack || hasBotTrack;
        }
        if (isCurrentVertical && isNewVertical) {
            boolean hasRightTrack;
            boolean hasLeftTrack = level.getObjectID(layerID, tileX - 1, tileY) == this.getID();
            boolean bl3 = hasRightTrack = level.getObjectID(layerID, tileX + 1, tileY) == this.getID();
            if (hasLeftTrack && hasRightTrack) {
                return false;
            }
            return hasLeftTrack || hasRightTrack;
        }
        return true;
    }

    protected static class TrackSprite {
        public int x;
        public int y;
        public boolean goingUp;
        public boolean goingRight;
        public boolean goingDown;
        public boolean goingLeft;
        public boolean connectedUp;
        public boolean connectedRight;
        public boolean connectedDown;
        public boolean connectedLeft;

        protected TrackSprite() {
        }

        public void goingUp() {
            this.goingUp = true;
            this.connectedUp = true;
        }

        public void goingRight() {
            this.goingRight = true;
            this.connectedRight = true;
        }

        public void goingDown() {
            this.goingDown = true;
            this.connectedDown = true;
        }

        public void goingLeft() {
            this.goingLeft = true;
            this.connectedLeft = true;
        }

        public TrackSprite sprite(int spriteX, int spriteY) {
            this.x = spriteX;
            this.y = spriteY;
            return this;
        }
    }
}

