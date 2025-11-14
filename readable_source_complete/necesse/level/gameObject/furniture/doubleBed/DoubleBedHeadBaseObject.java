/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.furniture.doubleBed;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.objectEntity.BedObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.BedContainer;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.ObjectUsersObject;
import necesse.level.gameObject.RespawnObject;
import necesse.level.gameObject.furniture.doubleBed.DoubleBedBaseObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;

public class DoubleBedHeadBaseObject
extends DoubleBedBaseObject
implements RespawnObject,
ObjectUsersObject {
    public GameTexture[] maskTextures;

    public DoubleBedHeadBaseObject(String textureName, ToolType toolType, Color mapColor) {
        super(textureName, toolType, mapColor);
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.maskTextures = new GameTexture[4];
        try {
            GameTexture loadedMask = GameTexture.fromFileRaw("objects/" + this.textureName + "_mask", true);
            for (int i = 0; i < this.maskTextures.length; ++i) {
                GameTexture maskSprite = i == 0 ? new GameTexture(loadedMask, 64, 96, 64, 96) : (i == 1 ? new GameTexture(loadedMask, 0, 96, 64, 96).rotatedClockwise() : (i == 2 ? new GameTexture(loadedMask, 64, 0, 64, 96) : new GameTexture(loadedMask, 0, 0, 64, 96).rotatedAnticlockwise()));
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
        byte rotation = level.getObjectRotation(tileX, tileY);
        switch (rotation) {
            case 0: {
                return new Rectangle(tileX, tileY - 1, 1, 2);
            }
            case 1: {
                return new Rectangle(tileX, tileY, 2, 1);
            }
            case 2: {
                return new Rectangle(tileX, tileY, 1, 2);
            }
        }
        return new Rectangle(tileX - 1, tileY, 2, 1);
    }
}

