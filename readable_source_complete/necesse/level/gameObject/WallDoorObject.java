/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.window.WindowManager;
import necesse.entity.ObjectDamageResult;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTextureSection;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.DoorObject;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.ObjectPlaceOption;
import necesse.level.gameObject.WallObject;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.RegionType;

public class WallDoorObject
extends DoorObject {
    protected WallObject wallObject;

    protected WallDoorObject(WallObject wallObject, int counterID, boolean isOpen) {
        super(new Rectangle(), counterID, isOpen);
        this.wallObject = wallObject;
        this.toolTier = wallObject.toolTier;
        this.toolType = wallObject.toolType;
        this.setItemCategory("objects", "wallsanddoors");
        this.setCraftingCategory("objects", "wallsanddoors");
        this.mapColor = wallObject.mapColor;
        this.isWall = true;
        this.replaceCategories.add("door");
        this.canReplaceCategories.add("door");
        this.canReplaceCategories.add("wall");
        this.canReplaceCategories.add("fence");
        this.canReplaceCategories.add("fencegate");
    }

    public boolean shouldMirror(Level level, int tileX, int tileY, int rotation) {
        if (rotation == 0) {
            return level.getObject(tileX + 1, tileY) instanceof WallDoorObject;
        }
        if (rotation == 1) {
            return level.getObject(tileX, tileY - 1) instanceof WallDoorObject;
        }
        if (rotation == 2) {
            return level.getObject(tileX - 1, tileY) instanceof WallDoorObject;
        }
        if (rotation == 3) {
            return level.getObject(tileX, tileY + 1) instanceof WallDoorObject;
        }
        return false;
    }

    private boolean testSolid(Level level, int tileX, int tileY) {
        GameObject object = level.getObject(tileX, tileY);
        return object.isWall || object.isDoor || object.isFence || object.isRock || object.isSolid;
    }

    private boolean isWall(Level level, int tileX, int tileY) {
        GameObject object = level.getObject(tileX, tileY);
        return object.isWall || object.isDoor || object.isFence || object.isRock;
    }

    @Override
    public ArrayList<ObjectPlaceOption> getPlaceOptions(Level level, int levelX, int levelY, PlayerMob playerMob, int playerDir, boolean offsetMultiTile) {
        boolean forcedVertical;
        boolean forcedHorizontal = this.isWall(level, levelX, levelY - 1) || this.isWall(level, levelX, levelY + 1);
        boolean bl = forcedVertical = !forcedHorizontal && (this.isWall(level, levelX - 1, levelY) || this.isWall(level, levelX + 1, levelY));
        if (forcedHorizontal) {
            if (playerDir == 0) {
                return super.getPlaceOptions(level, levelX, levelY, playerMob, 1, offsetMultiTile);
            }
            if (playerDir == 2) {
                return super.getPlaceOptions(level, levelX, levelY, playerMob, 3, offsetMultiTile);
            }
        } else if (forcedVertical) {
            if (playerDir == 1) {
                return super.getPlaceOptions(level, levelX, levelY, playerMob, 0, offsetMultiTile);
            }
            if (playerDir == 3) {
                return super.getPlaceOptions(level, levelX, levelY, playerMob, 2, offsetMultiTile);
            }
        }
        return super.getPlaceOptions(level, levelX, levelY, playerMob, playerDir, offsetMultiTile);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        int sortY;
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameLight light = level.getLightLevelWall(tileX, tileY);
        int rotation = this.rotateTowardsSolid(level, tileX, tileY);
        final SharedTextureDrawOptions options = new SharedTextureDrawOptions(WallObject.generatedWallTexture);
        float alpha = 1.0f;
        if (perspective != null && !Settings.hideUI && !Settings.hideCursor) {
            Rectangle alphaRec = new Rectangle(tileX * 32 - 16, tileY * 32 - 48, 64, 80);
            if (rotation == 0) {
                alphaRec.height -= 26;
            } else if (rotation == 2) {
                alphaRec.y += 28;
                alphaRec.height -= 28;
            }
            if (perspective.getCollision().intersects(alphaRec)) {
                alpha = 0.5f;
            } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                alpha = 0.5f;
            }
        }
        boolean shouldMirror = this.shouldMirror(level, tileX, tileY, rotation);
        GameTextureSection wallTexture = this.wallObject.wallTexture.getDamagedTexture(this, level, tileX, tileY);
        if (rotation == 0) {
            options.add(wallTexture.sprite(3, 0, 32, 128)).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 96);
            if (this.wallObject.outlineTexture != null) {
                options.add(this.wallObject.outlineTexture.sprite(3, 0, 32, 128)).alpha(alpha).light(light).pos(drawX, drawY - 96);
            }
            sortY = 28;
        } else if (rotation == 1) {
            options.add(wallTexture.sprite(5, 0, 32, 128)).alpha(alpha).light(light).pos(drawX, drawY - 96);
            if (this.wallObject.outlineTexture != null) {
                options.add(this.wallObject.outlineTexture.sprite(5, 0, 32, 128)).alpha(alpha).light(light).pos(drawX, drawY - 96);
            }
            sortY = 20;
        } else if (rotation == 2) {
            options.add(wallTexture.sprite(7, 0, 32, 128)).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 96);
            if (this.wallObject.outlineTexture != null) {
                options.add(this.wallObject.outlineTexture.sprite(7, 0, 32, 128)).alpha(alpha).light(light).pos(drawX, drawY - 96);
            }
            sortY = 28;
        } else {
            options.add(wallTexture.sprite(9, 0, 32, 128)).alpha(alpha).light(light).pos(drawX, drawY - 96);
            if (this.wallObject.outlineTexture != null) {
                options.add(this.wallObject.outlineTexture.sprite(9, 0, 32, 128)).alpha(alpha).light(light).pos(drawX, drawY - 96);
            }
            sortY = 20;
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return sortY;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        rotation = this.rotateTowardsSolid(level, tileX, tileY, rotation);
        boolean shouldMirror = this.shouldMirror(level, tileX, tileY, rotation);
        GameTextureSection wallTexture = this.wallObject.wallTexture.getDamagedTexture(0.0f);
        if (rotation == 0) {
            wallTexture.sprite(3, 0, 32, 128).initDraw().mirror(shouldMirror, false).alpha(alpha).draw(drawX, drawY - 96);
            if (this.wallObject.outlineTexture != null) {
                this.wallObject.outlineTexture.sprite(3, 0, 32, 128).initDraw().alpha(alpha).draw(drawX, drawY - 96);
            }
        } else if (rotation == 1) {
            wallTexture.sprite(5, 0, 32, 128).initDraw().alpha(alpha).draw(drawX, drawY - 96);
            if (this.wallObject.outlineTexture != null) {
                this.wallObject.outlineTexture.sprite(5, 0, 32, 128).initDraw().alpha(alpha).draw(drawX, drawY - 96);
            }
        } else if (rotation == 2) {
            wallTexture.sprite(7, 0, 32, 128).initDraw().mirror(shouldMirror, false).alpha(alpha).draw(drawX, drawY - 96);
            if (this.wallObject.outlineTexture != null) {
                this.wallObject.outlineTexture.sprite(7, 0, 32, 128).initDraw().alpha(alpha).draw(drawX, drawY - 96);
            }
        } else {
            wallTexture.sprite(9, 0, 32, 128).initDraw().alpha(alpha).draw(drawX, drawY - 96);
            if (this.wallObject.outlineTexture != null) {
                this.wallObject.outlineTexture.sprite(9, 0, 32, 128).initDraw().alpha(alpha).draw(drawX, drawY - 96);
            }
        }
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if ((rotation = this.rotateTowardsSolid(level, x, y, rotation)) == 0 || rotation == 2) {
            return new Rectangle(x * 32, y * 32 + 28, 32, 12);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 28, y * 32, 4, 32);
        }
        return new Rectangle(x * 32, y * 32, 4, 32);
    }

    @Override
    protected ObjectHoverHitbox getHoverHitbox(Level level, int layerID, int tileX, int tileY) {
        int rotation = this.rotateTowardsSolid(level, tileX, tileY);
        if (rotation == 0 || rotation == 2) {
            return new ObjectHoverHitbox(layerID, tileX, tileY, 0, 0, 32, 32);
        }
        return new ObjectHoverHitbox(layerID, tileX, tileY, 0, -24, 32, 56);
    }

    @Override
    public boolean shouldSnapSmartMining(Level level, int x, int y) {
        return true;
    }

    @Override
    public boolean isSolid(Level level, int x, int y) {
        return !this.isOpen(level, x, y, level.getObjectRotation(x, y));
    }

    @Override
    public int getLightLevelMod(Level level, int x, int y) {
        if (this.isSolid(level, x, y) && !this.isLightTransparent(level, x, y) && !this.isOpen(level, x, y, level.getObjectRotation(x, y))) {
            return 40;
        }
        return 10;
    }

    @Override
    public boolean allowsAmbientLightPassThrough(Level level, int tileX, int tileY) {
        return this.isOpen(level, tileX, tileY, level.getObjectRotation(tileX, tileY));
    }

    @Override
    public boolean stopsTerrainSplatting() {
        return true;
    }

    @Override
    public void playSwitchSound(Level level, int x, int y) {
        if (level.isClient()) {
            SoundManager.playSound(this.isSwitched ? GameResources.doorclose : GameResources.dooropen, (SoundEffect)SoundEffect.effect(x * 32 + 16, y * 32 + 16));
        }
    }

    protected int rotateTowardsSolid(Level level, int tileX, int tileY) {
        return this.rotateTowardsSolid(level, tileX, tileY, level.getObjectRotation(tileX, tileY));
    }

    protected int rotateTowardsSolid(Level level, int tileX, int tileY, int objectRotation) {
        boolean attachesTop = this.testSolid(level, tileX, tileY - 1);
        boolean attachesBot = this.testSolid(level, tileX, tileY + 1);
        boolean attachesLeft = this.testSolid(level, tileX - 1, tileY);
        boolean attachesRight = this.testSolid(level, tileX + 1, tileY);
        int rotation = objectRotation;
        if (attachesTop || attachesBot) {
            switch (objectRotation) {
                case 0: 
                case 1: {
                    rotation = 1;
                    break;
                }
                case 2: 
                case 3: {
                    rotation = 3;
                }
            }
        } else if (attachesLeft || attachesRight) {
            switch (objectRotation) {
                case 0: 
                case 1: {
                    rotation = 0;
                    break;
                }
                case 2: 
                case 3: {
                    rotation = 2;
                }
            }
        }
        return rotation;
    }

    public static int[] registerDoorPair(String stringIDPrefix, WallObject wallObject, float brokerValue, boolean itemObtainable, boolean itemCountInStats) {
        WallDoorObject closed = new WallDoorObject(wallObject, 0, false);
        int closedDoor = ObjectRegistry.registerObject(stringIDPrefix, (GameObject)closed, brokerValue, itemObtainable, itemCountInStats, new String[0]);
        int openDoor = ObjectRegistry.registerObject(stringIDPrefix + "open", new WallDoorOpenObject(wallObject, closedDoor), 0.0f, false);
        WallDoorLockedObject locked = new WallDoorLockedObject(wallObject, closed, 1, false);
        int lockedDoor = ObjectRegistry.registerObject(stringIDPrefix + "locked", locked, brokerValue, false);
        int unlockedDoor = ObjectRegistry.registerObject(stringIDPrefix + "unlocked", new WallDoorUnlockedObject(wallObject, lockedDoor), 0.0f, false);
        closed.counterID = openDoor;
        locked.counterID = unlockedDoor;
        return new int[]{closedDoor, openDoor, lockedDoor, unlockedDoor};
    }

    private static class WallDoorOpenObject
    extends WallDoorObject {
        protected WallDoorOpenObject(WallObject wallObject, int counterID) {
            super(wallObject, counterID, true);
        }

        @Override
        public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
            return ObjectRegistry.getObject(this.counterID).getLootTable(level, layerID, tileX, tileY);
        }

        @Override
        public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
            int sortY;
            int drawX = camera.getTileDrawX(tileX);
            int drawY = camera.getTileDrawY(tileY);
            GameLight light = level.getLightLevelWall(tileX, tileY);
            int rotation = this.rotateTowardsSolid(level, tileX, tileY);
            final SharedTextureDrawOptions options = new SharedTextureDrawOptions(WallObject.generatedWallTexture);
            float alpha = 1.0f;
            if (perspective != null) {
                Rectangle alphaRec = new Rectangle(tileX * 32 - 16, tileY * 32 - 48, 64, 80);
                if (perspective.getCollision().intersects(alphaRec)) {
                    alpha = 0.5f;
                } else if (alphaRec.contains(camera.getX() + WindowManager.getWindow().mousePos().sceneX, camera.getY() + WindowManager.getWindow().mousePos().sceneY)) {
                    alpha = 0.5f;
                }
            }
            boolean shouldMirror = this.shouldMirror(level, tileX, tileY, rotation);
            GameTextureSection wallTexture = this.wallObject.wallTexture.getDamagedTexture(this, level, tileX, tileY);
            if (rotation == 0) {
                options.add(wallTexture.sprite(4, 0, 32, 128)).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 96);
                if (this.wallObject.outlineTexture != null) {
                    options.add(this.wallObject.outlineTexture.sprite(4, 0, 32, 128)).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 96);
                }
                sortY = 20;
            } else if (rotation == 1) {
                if (shouldMirror) {
                    if (level.getObject((int)tileX, (int)(tileY + 1)).isWall && !level.getObject((int)tileX, (int)(tileY + 1)).isDoor) {
                        drawY += 8;
                    }
                    drawY += 26;
                }
                options.add(wallTexture.sprite(6, 0, 32, 128)).alpha(alpha).light(light).pos(drawX, drawY - 96);
                if (this.wallObject.outlineTexture != null) {
                    options.add(this.wallObject.outlineTexture.sprite(6, 0, 32, 128)).alpha(alpha).light(light).pos(drawX, drawY - 96);
                }
                sortY = shouldMirror ? 28 : 4;
            } else if (rotation == 2) {
                options.add(wallTexture.sprite(8, 0, 32, 128)).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 96);
                if (this.wallObject.outlineTexture != null) {
                    options.add(this.wallObject.outlineTexture.sprite(8, 0, 32, 128)).mirror(shouldMirror, false).alpha(alpha).light(light).pos(drawX, drawY - 96);
                }
                sortY = 20;
            } else {
                if (level.getObject((int)tileX, (int)(tileY + 1)).isWall && !level.getObject((int)tileX, (int)(tileY + 1)).isDoor) {
                    drawY += 8;
                }
                if (shouldMirror) {
                    drawY -= 26;
                }
                options.add(wallTexture.sprite(10, 0, 32, 128)).alpha(alpha).light(light).pos(drawX, drawY - 96);
                if (this.wallObject.outlineTexture != null) {
                    options.add(this.wallObject.outlineTexture.sprite(10, 0, 32, 128)).alpha(alpha).light(light).pos(drawX, drawY - 96);
                }
                sortY = shouldMirror ? 4 : 28;
            }
            list.add(new LevelSortedDrawable(this, tileX, tileY){

                @Override
                public int getSortY() {
                    return sortY;
                }

                @Override
                public void draw(TickManager tickManager) {
                    options.draw();
                }
            });
        }

        @Override
        public Rectangle getCollision(Level level, int x, int y, int rotation) {
            rotation = this.rotateTowardsSolid(level, x, y, rotation);
            boolean shouldMirror = this.shouldMirror(level, x, y, rotation);
            if (rotation == 0) {
                if (shouldMirror) {
                    return new Rectangle(x * 32, y * 32, 4, 32);
                }
                return new Rectangle(x * 32 + 28, y * 32, 4, 32);
            }
            if (rotation == 1) {
                if (shouldMirror) {
                    return new Rectangle(x * 32, y * 32 + 28, 32, 4);
                }
                return new Rectangle(x * 32, y * 32, 32, 4);
            }
            if (rotation == 2) {
                if (shouldMirror) {
                    return new Rectangle(x * 32 + 28, y * 32, 4, 32);
                }
                return new Rectangle(x * 32, y * 32, 4, 32);
            }
            if (shouldMirror) {
                return new Rectangle(x * 32, y * 32, 32, 4);
            }
            return new Rectangle(x * 32, y * 32 + 28, 32, 4);
        }

        @Override
        protected ObjectHoverHitbox getHoverHitbox(Level level, int layerID, int tileX, int tileY) {
            int rotation = this.rotateTowardsSolid(level, tileX, tileY);
            if (rotation == 2) {
                return new ObjectHoverHitbox(layerID, tileX, tileY, 0, -16, 32, 48);
            }
            return super.getHoverHitbox(level, layerID, tileX, tileY);
        }
    }

    private static class WallDoorLockedObject
    extends WallDoorObject {
        public WallDoorObject normalDoor;

        protected WallDoorLockedObject(WallObject wallObject, WallDoorObject normalDoor, int counterID, boolean isOpen) {
            super(wallObject, counterID, isOpen);
            this.regionType = RegionType.WALL;
            this.normalDoor = normalDoor;
        }

        @Override
        public boolean isForceClosed(Level level, int tileX, int tileY) {
            return true;
        }

        @Override
        public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
            return this.normalDoor.getLootTable(level, layerID, tileX, tileY);
        }

        @Override
        public boolean canInteract(Level level, int x, int y, PlayerMob player) {
            return false;
        }

        @Override
        public void onPathOpened(Level level, int tileX, int tileY, Attacker attacker) {
            level.entityManager.doObjectDamage(0, tileX, tileY, this.objectHealth, this.toolTier, attacker, null, true, tileX * 32 + 16, tileY * 32 + 16);
        }

        @Override
        public boolean onPathBreakDown(Level level, int tileX, int tileY, int damage, Attacker attacker, int hitX, int hitY) {
            ObjectDamageResult result = level.entityManager.doObjectDamage(0, tileX, tileY, damage, this.toolTier, attacker, null, true, hitX, hitY);
            return result != null && result.destroyed;
        }

        @Override
        public boolean pathCollidesIfOpen(Level level, int tileX, int tileY, CollisionFilter collisionFilter, Rectangle mobCollision) {
            return true;
        }

        @Override
        public boolean shouldShowInItemList() {
            return false;
        }
    }

    private static class WallDoorUnlockedObject
    extends WallDoorOpenObject {
        protected WallDoorUnlockedObject(WallObject wallObject, int counterID) {
            super(wallObject, counterID);
            this.regionType = RegionType.OPEN;
        }

        @Override
        public boolean canInteract(Level level, int x, int y, PlayerMob player) {
            return false;
        }

        @Override
        public void onPathOpened(Level level, int tileX, int tileY, Attacker attacker) {
            level.entityManager.doObjectDamage(0, tileX, tileY, this.objectHealth, this.toolTier, attacker, null, true, tileX * 32 + 16, tileY * 32 + 16);
        }

        @Override
        public boolean onPathBreakDown(Level level, int tileX, int tileY, int damage, Attacker attacker, int hitX, int hitY) {
            ObjectDamageResult result = level.entityManager.doObjectDamage(0, tileX, tileY, damage, this.toolTier, attacker, null, true, hitX, hitY);
            return result != null && result.destroyed;
        }

        @Override
        public boolean pathCollidesIfOpen(Level level, int tileX, int tileY, CollisionFilter collisionFilter, Rectangle mobCollision) {
            return true;
        }

        @Override
        public boolean shouldShowInItemList() {
            return false;
        }
    }
}

