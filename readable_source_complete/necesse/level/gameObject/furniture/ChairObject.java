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
import necesse.entity.objectEntity.ChairObjectEntity;
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
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.gameObject.TableObjectInterface;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class ChairObject
extends FurnitureObject
implements ObjectUsersObject,
ChairObjectInterface {
    protected String textureName;
    public ObjectDamagedTextureArray texture;
    public boolean alwaysRenderBehindUser;

    public ChairObject(String textureName, ToolType toolType, Color mapColor, String ... category) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.furnitureType = "chair";
        this.alwaysRenderBehindUser = false;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        if (category.length > 0) {
            this.setItemCategory(category);
            this.setCraftingCategory(category);
        } else {
            this.setItemCategory("objects", "furniture");
            this.setCraftingCategory("objects", "furniture");
        }
    }

    public ChairObject(String textureName, Color mapColor, String ... category) {
        this(textureName, ToolType.ALL, mapColor, category);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        List<ObjectUserMob> users = this.getObjectUsers(level, tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameLight light = level.getLightLevel(tileX, tileY);
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        TextureDrawOptionsEnd drawOptions = texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY - texture.getHeight() + 32);
        final DrawOptionsList options = new DrawOptionsList();
        if (this.alwaysRenderBehindUser || rotation != 0) {
            options.add(drawOptions);
        }
        for (ObjectUserMob user : users) {
            Point offset = this.getMobPosSitOffset(level, tileX, tileY);
            options.add(user.getUserDrawOptions(level, tileX * 32 + offset.x, tileY * 32 + offset.y, tickManager, camera, perspective, humanOptions -> {
                if (humanOptions != null) {
                    this.modifyHumanDrawOptions(level, tileX, tileY, (HumanDrawOptions)humanOptions);
                }
            }));
        }
        if (!this.alwaysRenderBehindUser && rotation == 0) {
            options.add(drawOptions);
        }
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                options.draw();
            }
        });
    }

    public void modifyHumanDrawOptions(Level level, int tileX, int tileY, HumanDrawOptions options) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        options.dir(rotation).sprite(6, (int)rotation);
    }

    public Point getMobPosSitOffset(Level level, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return new Point(16, 10);
            }
            case 1: {
                return new Point(19, 10);
            }
            case 2: {
                return new Point(16, 14);
            }
            case 3: {
                return new Point(13, 10);
            }
        }
        return new Point(16, 16);
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        texture.initDraw().sprite(rotation % 4, 0, 32, texture.getHeight()).alpha(alpha).draw(drawX, drawY - texture.getHeight() + 32);
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        return new Rectangle(x * 32 + 8, y * 32 + 6, 16, 10);
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
            if (objectEntity instanceof ChairObjectEntity) {
                ChairObjectEntity chair = (ChairObjectEntity)objectEntity;
                GameMessage usageError = chair.getCanUseError(player);
                if (usageError == null) {
                    chair.startUser(player);
                } else if (chair.isMobUsing(player)) {
                    chair.stopUser(player);
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
        return level.getObjectRotation(tileX, tileY);
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
        return new ChairObjectEntity(level, x, y);
    }

    @Override
    public boolean facesTable(Level level, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return level.getObject(tileX, tileY - 1) instanceof TableObjectInterface;
            }
            case 1: {
                return level.getObject(tileX + 1, tileY) instanceof TableObjectInterface;
            }
            case 2: {
                return level.getObject(tileX, tileY + 1) instanceof TableObjectInterface;
            }
            case 3: {
                return level.getObject(tileX - 1, tileY) instanceof TableObjectInterface;
            }
        }
        return false;
    }

    @Override
    protected SoundSettings getInteractSoundOpen() {
        return null;
    }
}

