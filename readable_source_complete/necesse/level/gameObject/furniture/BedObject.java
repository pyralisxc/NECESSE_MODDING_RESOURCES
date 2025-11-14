/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.ObjectUserMob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.objectEntity.BedObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptionsList;
import necesse.gfx.drawOptions.human.HumanDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.BedContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.gameObject.RespawnObject;
import necesse.level.gameObject.furniture.Bed2Object;
import necesse.level.gameObject.furniture.FurnitureObject;
import necesse.level.gameObject.furniture.SettlerBedObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.multiTile.MultiTile;

public class BedObject
extends FurnitureObject
implements RespawnObject,
ObjectUsersObject,
SettlerBedObject {
    private final String textureName;
    public ObjectDamagedTextureArray baseTexture;
    public GameTexture[] maskTextures;
    protected int counterID;

    private BedObject(String textureName, ToolType toolType, Color mapColor, String ... category) {
        super(new Rectangle(32, 32));
        this.textureName = textureName;
        this.toolType = toolType;
        this.mapColor = mapColor;
        this.objectHealth = 50;
        this.isLightTransparent = true;
        this.roomProperties.add("bed");
        this.furnitureType = "bed";
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
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
        return new MultiTile(0, 1, 1, 2, rotation, true, this.counterID, this.getID());
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.baseTexture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/" + this.textureName);
        this.maskTextures = new GameTexture[4];
        try {
            GameTexture loadedMask = GameTexture.fromFileRaw("objects/" + this.textureName + "_mask", true);
            for (int i = 0; i < this.maskTextures.length; ++i) {
                GameTexture maskSprite = i == 0 ? new GameTexture(loadedMask, 96, 32, 32, 96) : (i == 1 ? new GameTexture(loadedMask, 0, 64, 64, 64).rotatedClockwise() : (i == 2 ? new GameTexture(loadedMask, 64, 32, 32, 96) : new GameTexture(loadedMask, 0, 0, 64, 64).rotatedAnticlockwise()));
                this.maskTextures[i] = maskSprite;
                this.maskTextures[i].makeFinal();
            }
            loadedMask.makeFinal();
        }
        catch (FileNotFoundException e) {
            for (int i = 0; i < this.maskTextures.length; ++i) {
                this.maskTextures[i] = new GameTexture("objects/" + this.textureName + " mask" + i, 64, 64);
                this.maskTextures[i].makeFinal();
            }
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
    public boolean canUserAttack(Level level, int tileX, int tileY, PlayerInventorySlot slot, PlayerMob player) {
        return false;
    }

    @Override
    public boolean canUserInteract(Level level, int tileX, int tileY, PlayerInventorySlot slot, PlayerMob player) {
        return false;
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
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return new Rectangle(tileX * 32 + 2, tileY * 32 - 32, 28, 54);
            }
            case 1: {
                return new Rectangle(tileX * 32, tileY * 32 - 10, 64, 36);
            }
            case 2: {
                return new Rectangle(tileX * 32 + 2, tileY * 32 - 16, 28, 64);
            }
            case 3: {
                return new Rectangle(tileX * 32 - 32, tileY * 32 - 10, 64, 36);
            }
        }
        return new Rectangle(tileX * 32, tileY * 32, 32, 32);
    }

    @Override
    public Point getUserAppearancePos(Level level, int tileX, int tileY, Mob mob) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return new Point(tileX * 32 + 16, tileY * 32);
            }
            case 1: {
                return new Point(tileX * 32 + 16, tileY * 32 + 16);
            }
            case 2: {
                return new Point(tileX * 32 + 16, tileY * 32 + 32);
            }
            case 3: {
                return new Point(tileX * 32 + 16, tileY * 32 + 16);
            }
        }
        return new Point(tileX * 32 + 16, tileY * 32 + 16);
    }

    @Override
    public void tickUser(Level level, int tileX, int tileY, Mob mob) {
        if (!mob.buffManager.hasBuff(BuffRegistry.SLEEPING) || mob.buffManager.getBuff(BuffRegistry.SLEEPING).getDurationLeft() <= 1000) {
            mob.buffManager.addBuff(new ActiveBuff(BuffRegistry.SLEEPING, mob, 60000, null), level.isServer());
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
    protected SoundSettings getInteractSoundOpen() {
        return null;
    }

    @Override
    public void updateUserToExitPos(Level level, int tileX, int tileY, Mob mob, float exitDirX, float exitDirY) {
        ObjectEntity oe = level.entityManager.getObjectEntity(tileX, tileY);
        if (oe instanceof OEUsers) {
            ((OEUsers)((Object)oe)).updateUserToExitPos(mob, exitDirX, exitDirY);
        }
    }

    public Point getMobPosSleepOffset(Level level, int tileX, int tileY) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return new Point(16, 20);
            }
            case 1: {
                return new Point(12, 16);
            }
            case 2: {
                return new Point(16, 18);
            }
            case 3: {
                return new Point(20, 16);
            }
        }
        return new Point(16, 16);
    }

    public void modifyHumanDrawOptions(Level level, int tileX, int tileY, HumanDrawOptions options) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        options.dir(rotation).sprite(0, (int)rotation);
        options.blinking(true);
        switch (rotation) {
            case 0: {
                options.drawOffset(0, 6);
                options.mask(this.maskTextures[0], 0, 39);
                break;
            }
            case 1: {
                options.rotate(-90.0f, 32, 32).drawOffset(6, 9);
                options.mask(this.maskTextures[1], -6, -14);
                break;
            }
            case 2: {
                options.drawOffset(0, 7);
                options.mask(this.maskTextures[2], 0, 6);
                break;
            }
            case 3: {
                options.rotate(90.0f, 32, 32).drawOffset(-6, 9);
                options.mask(this.maskTextures[3], 6, -14);
            }
        }
    }

    @Override
    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        if (rotation == 0) {
            return new Rectangle(x * 32 + 2, y * 32, 28, 30);
        }
        if (rotation == 1) {
            return new Rectangle(x * 32 + 2, y * 32 + 6, 30, 24);
        }
        if (rotation == 2) {
            return new Rectangle(x * 32 + 2, y * 32 + 6, 28, 26);
        }
        return new Rectangle(x * 32, y * 32 + 6, 30, 24);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        List<ObjectUserMob> users = this.getObjectUsers(level, tileX, tileY);
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        byte rotation = level.getObjectRotation(tileX, tileY);
        GameTexture baseTexture = this.baseTexture.getDamagedTexture(this, level, tileX, tileY);
        final DrawOptionsList options = new DrawOptionsList();
        if (rotation == 0) {
            options.add(baseTexture.initDraw().sprite(3, 3, 32).light(light).pos(drawX, drawY));
        } else if (rotation == 1) {
            options.add(baseTexture.initDraw().sprite(0, 1, 32, 64).light(light).pos(drawX, drawY - 32));
        } else if (rotation == 2) {
            options.add(baseTexture.initDraw().sprite(2, 1, 32).light(light).pos(drawX, drawY - 32));
            options.add(baseTexture.initDraw().sprite(2, 2, 32).light(light).pos(drawX, drawY));
        } else {
            options.add(baseTexture.initDraw().sprite(1, 0, 32, 64).light(light).pos(drawX, drawY - 32));
        }
        for (ObjectUserMob user : users) {
            Point offset = this.getMobPosSleepOffset(level, tileX, tileY);
            options.add(user.getUserDrawOptions(level, tileX * 32 + offset.x, tileY * 32 + offset.y, tickManager, camera, perspective, humanOptions -> {
                if (humanOptions != null) {
                    this.modifyHumanDrawOptions(level, tileX, tileY, (HumanDrawOptions)humanOptions);
                }
            }));
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

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        GameTexture baseTexture = this.baseTexture.getDamagedTexture(0.0f);
        if (rotation == 0) {
            baseTexture.initDraw().sprite(3, 1, 32).alpha(alpha).draw(drawX, drawY - 64);
            baseTexture.initDraw().sprite(3, 2, 32).alpha(alpha).draw(drawX, drawY - 32);
            baseTexture.initDraw().sprite(3, 3, 32).alpha(alpha).draw(drawX, drawY);
        } else if (rotation == 1) {
            baseTexture.initDraw().sprite(0, 1, 64).alpha(alpha).draw(drawX, drawY - 32);
        } else if (rotation == 2) {
            baseTexture.initDraw().sprite(2, 1, 32).alpha(alpha).draw(drawX, drawY - 32);
            baseTexture.initDraw().sprite(2, 2, 32).alpha(alpha).draw(drawX, drawY);
            baseTexture.initDraw().sprite(2, 3, 32).alpha(alpha).draw(drawX, drawY + 32);
        } else {
            baseTexture.initDraw().sprite(0, 0, 64).alpha(alpha).draw(drawX - 32, drawY - 32);
        }
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        if (player.isServerClient()) {
            ServerClient client = player.getServerClient();
            GameMessage setSpawnError = this.getCanSetSpawnError(level, x, y, client);
            if (setSpawnError != null) {
                client.sendChatMessage(setSpawnError);
                return;
            }
            if (level.isSleepPrevented() || level.getWorldEntity().isSleepPrevented()) {
                client.sendChatMessage(new LocalMessage("misc", "sleepprevented"));
                return;
            }
            ObjectEntity objectEntity = level.entityManager.getObjectEntity(x, y);
            if (objectEntity instanceof BedObjectEntity) {
                BedObjectEntity bed = (BedObjectEntity)objectEntity;
                GameMessage usageError = bed.getCanUseError(client.playerMob);
                if (usageError == null) {
                    boolean isCurrentSpawn = this.isCurrentSpawn(level, x, y, client);
                    ContainerRegistry.openAndSendContainer(client, PacketOpenContainer.ObjectEntity(ContainerRegistry.BED_CONTAINER, bed, BedContainer.getContainerContent(client, isCurrentSpawn)));
                } else if (bed.isMobUsing(player)) {
                    bed.stopUser(player);
                } else if (!usageError.isEmpty()) {
                    client.sendChatMessage(usageError);
                }
            }
        }
    }

    @Override
    public boolean canRespawn(Level level, int tileX, int tileY, ServerClient client) {
        return this.getCanSetSpawnError(level, tileX, tileY, client) == null;
    }

    public GameMessage getCanSetSpawnError(Level level, int tileX, int tileY, ServerClient client) {
        GameMessage levelSetSpawnError = level.getSetSpawnError(tileX, tileY, client);
        if (levelSetSpawnError != null) {
            return levelSetSpawnError;
        }
        if (level.isOutside(tileX, tileY)) {
            return new LocalMessage("misc", "spawnhouse");
        }
        return null;
    }

    @Override
    public Point getSpawnOffset(Level level, int tileX, int tileY, ServerClient client) {
        byte rotation = level.getObjectRotation(tileX, tileY);
        ArrayList<Point> points = new ArrayList<Point>();
        points.add(new Point(-1, 0));
        points.add(new Point(1, 0));
        points.add(new Point(-1, -1));
        points.add(new Point(1, -1));
        points.add(new Point(0, 1));
        points.add(new Point(0, -2));
        points.add(new Point(-1, -2));
        points.add(new Point(1, -2));
        points.add(new Point(-1, 1));
        points.add(new Point(1, 1));
        Function<Point, Point> rotationMod = rotation == 0 ? p -> p : (rotation == 1 ? p -> new Point(-p.y, -p.x) : (rotation == 2 ? p -> new Point(-p.x, -p.y) : p -> new Point(p.y, p.x)));
        for (Point point : points) {
            Point rPoint = rotationMod.apply(point);
            if (client.playerMob.collidesWith(level, (tileX + rPoint.x) * 32 + 16, (tileY + rPoint.y) * 32 + 16)) continue;
            return new Point(rPoint.x * 32 + 16, rPoint.y * 32 + 16);
        }
        return new Point(16, 16);
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "bedtip"));
        return tooltips;
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new BedObjectEntity(level, x, y);
    }

    @Override
    public boolean isMasterBedObject(Level level, int tileX, int tileY) {
        return true;
    }

    @Override
    public LevelObject getSettlerBedMasterLevelObject(Level level, int tileX, int tileY) {
        return new LevelObject(level, tileX, tileY);
    }

    @Override
    public Rectangle getSettlerBedTileRectangle(Level level, int tileX, int tileY) {
        return this.getMultiTile(level, 0, tileX, tileY).getTileRectangle(tileX, tileY);
    }

    public static int[] registerBed(String stringID, String textureName, ToolType toolType, Color mapColor, float brokerValue, String ... category) {
        int bdi;
        BedObject buo = new BedObject(textureName, toolType, mapColor, category);
        Bed2Object bdo = new Bed2Object(textureName, toolType, mapColor, category);
        int bui = ObjectRegistry.registerObject(stringID, buo, brokerValue, true);
        buo.counterID = bdi = ObjectRegistry.registerObject(stringID + "2", bdo, 0.0f, false);
        bdo.counterID = bui;
        return new int[]{bui, bdi};
    }

    public static int[] registerBed(String stringID, String textureName, Color mapColor, float brokerValue, String ... category) {
        return BedObject.registerBed(stringID, textureName, ToolType.ALL, mapColor, brokerValue, category);
    }
}

