/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.BenchObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ChairObjectInterface;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.gameObject.TableObjectInterface;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;
import necesse.level.maps.multiTile.SideMultiTile;

class Bench2Object
extends FurnitureObject
implements ObjectUsersObject,
ChairObjectInterface {
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    protected int counterID;
    public boolean alwaysRenderBehindUser;

    protected Bench2Object(String textureName, ToolType toolType, Color mapColor, String ... category) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.alwaysRenderBehindUser = false;
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "furniture");
            this.setCraftingCategory("objects", "furniture");
        }
    }

    @Override
    public MultiTile getMultiTile(int rotation) {
        return new SideMultiTile(0, 0, 1, 2, rotation, false, this.getID(), this.counterID);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 4, y * 32 + 10, 24, 22);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32, y * 32 + 6, 30, 24);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 4, y * 32, 24, 30);
        }
        return new Rectangle(x * 32 + 2, y * 32 + 6, 30, 24);
    }

    @Override
    protected ObjectHoverHitbox getHoverHitbox(Level level, int layerID, int tileX, int tileY) {
        int rotation = this.getModifiedRotation(level, tileX, tileY);
        if (rotation == 2) {
            return new ObjectHoverHitbox(layerID, tileX, tileY, 0, -16, 32, 48);
        }
        if (rotation == 0) {
            return new ObjectHoverHitbox(layerID, tileX, tileY, 0, -4, 32, 36);
        }
        return new ObjectHoverHitbox(layerID, tileX, tileY, 0, 0, 32, 32, 16);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        TextureDrawOptionsEnd drawOptions;
        GameLight light = level.getLightLevel(tileX, tileY);
        List<ObjectUserMob> users = this.getObjectUsers(level, tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        TextureDrawOptionsEnd drawOptions2 = null;
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        if (rotation == 0) {
            drawOptions = texture.initDraw().sprite(2, 1, 32).light(light).pos(drawX, drawY - 32);
            drawOptions2 = texture.initDraw().sprite(2, 2, 32).light(light).pos(drawX, drawY);
        } else {
            drawOptions = rotation == 1 ? texture.initDraw().sprite(1, 1, 32, 64).light(light).pos(drawX, drawY - 32) : (rotation == 2 ? texture.initDraw().sprite(3, 3, 32).light(light).pos(drawX, drawY) : texture.initDraw().sprite(0, 0, 32, 64).light(light).pos(drawX, drawY - 32));
        }
        final DrawOptionsList options = new DrawOptionsList();
        int modifiedRotation = this.getModifiedRotation(level, tileX, tileY);
        if (this.alwaysRenderBehindUser || modifiedRotation != 0) {
            options.add(drawOptions);
            if (drawOptions2 != null) {
                options.add(drawOptions2);
            }
        }
        for (ObjectUserMob user : users) {
            Point offset = this.getMobPosSitOffset(level, tileX, tileY);
            options.add(user.getUserDrawOptions(level, tileX * 32 + offset.x, tileY * 32 + offset.y, tickManager, camera, perspective, humanOptions -> {
                if (humanOptions != null) {
                    this.modifyHumanDrawOptions(level, tileX, tileY, (HumanDrawOptions)humanOptions);
                }
            }));
        }
        if (!this.alwaysRenderBehindUser && modifiedRotation == 0) {
            options.add(drawOptions);
            if (drawOptions2 != null) {
                options.add(drawOptions2);
            }
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 20;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public int getModifiedRotation(Level level, int tileX, int tileY) {
        int rotation = level.getObjectRotation(tileX, tileY) + 1;
        if (rotation == 4) {
            rotation = 0;
        }
        return rotation;
    }

    public void modifyHumanDrawOptions(Level level, int tileX, int tileY, HumanDrawOptions options) {
        int rotation = this.getModifiedRotation(level, tileX, tileY);
        options.dir(rotation).sprite(6, rotation);
    }

    public Point getMobPosSitOffset(Level level, int tileX, int tileY) {
        int rotation = this.getModifiedRotation(level, tileX, tileY);
        switch (rotation) {
            case 0: {
                return new Point(15, 8);
            }
            case 1: {
                return new Point(22, 20);
            }
            case 2: {
                return new Point(15, 18);
            }
            case 3: {
                return new Point(10, 16);
            }
        }
        return new Point(16, 16);
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        if (player.isServerClient()) {
            ServerClient client = player.getServerClient();
            ObjectEntity objectEntity = level.entityManager.getObjectEntity(x, y);
            if (objectEntity instanceof BenchObjectEntity) {
                BenchObjectEntity bench = (BenchObjectEntity)objectEntity;
                GameMessage usageError = bench.getCanUseError(player);
                if (usageError == null) {
                    bench.startUser(player);
                } else if (bench.isMobUsing(player)) {
                    bench.stopUser(player);
                } else if (!usageError.isEmpty()) {
                    client.sendChatMessage(usageError);
                }
            }
        }
    }

    @Override
    public void stopUsing(Level level, int tileX, int tileY, Mob mob, boolean updatePosition, float exitDirX, float exitDirY) {
        ObjectEntity oe = level.entityManager.getObjectEntity(tileX, tileY);
        if (oe instanceof OEUsers) {
            ((OEUsers)((Object)oe)).stopUser(mob);
        }
        if (updatePosition) {
            this.updateUserToExitPos(level, tileX, tileY, mob, exitDirX, exitDirY);
        }
    }

    @Override
    public void updateUserToExitPos(Level level, int tileX, int tileY, Mob mob, float exitDirX, float exitDirY) {
        ObjectEntity oe = level.entityManager.getObjectEntity(tileX, tileY);
        if (oe instanceof OEUsers) {
            ((OEUsers)((Object)oe)).updateUserToExitPos(mob, exitDirX, exitDirY);
        }
    }

    @Override
    public boolean drawsUser(Level level, int tileX, int tileY, Mob mob) {
        return false;
    }

    @Override
    public boolean preventsUserPushed(Level level, int tileX, int tileY, Mob mob) {
        return true;
    }

    @Override
    public boolean preventsUserLevelInteract(Level level, int tileX, int tileY, Mob mob) {
        return true;
    }

    @Override
    public boolean userCanBeTargetedFromAdjacentTiles(Level level, int tileX, int tileY, Mob mob) {
        return true;
    }

    @Override
    public int getForcedUserDir(Level level, int tileX, int tileY) {
        return (level.getObjectRotation(tileX, tileY) + 1) % 4;
    }

    @Override
    public Rectangle getUserCollisionBox(Level level, int tileX, int tileY, Mob mob, Rectangle defaultCollisionBox) {
        int padding = 10;
        int size = 32 + padding * 2;
        return new Rectangle(tileX * 32 - padding, tileY * 32 - padding, size, size);
    }

    @Override
    public Rectangle getUserHitBox(Level level, int tileX, int tileY, Mob mob, Rectangle defaultHitBox) {
        int padding = 12;
        int size = 32 + padding * 2;
        return new Rectangle(tileX * 32 - padding, tileY * 32 - padding, size, size);
    }

    @Override
    public Rectangle getUserSelectBox(Level level, int tileX, int tileY, Mob mob) {
        Point offset = this.getMobPosSitOffset(level, tileX, tileY);
        return new Rectangle(tileX * 32 + offset.x - 14, tileY * 32 + offset.y - 7 - 34, 28, 48);
    }

    @Override
    public Point getUserAppearancePos(Level level, int tileX, int tileY, Mob mob) {
        Point offset = this.getMobPosSitOffset(level, tileX, tileY);
        return new Point(tileX * 32 + offset.x, tileY * 32 + offset.y);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new BenchObjectEntity(level, x, y);
    }

    @Override
    public boolean facesTable(Level level, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return level.getObject(tileX + 1, tileY) instanceof TableObjectInterface;
            }
            case 1: {
                return level.getObject(tileX, tileY + 1) instanceof TableObjectInterface;
            }
            case 2: {
                return level.getObject(tileX - 1, tileY) instanceof TableObjectInterface;
            }
            case 3: {
                return level.getObject(tileX, tileY - 1) instanceof TableObjectInterface;
            }
        }
        return false;
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return null;
    }
}

