/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity;

import java.util.ArrayList;
import necesse.engine.GameEvents;
import necesse.engine.events.players.ObjectDamageEvent;
import necesse.engine.events.players.TileDamageEvent;
import necesse.engine.network.packet.PacketObjectDamage;
import necesse.engine.network.packet.PacketTileDamage;
import necesse.engine.network.packet.PacketTileDestroyed;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.entity.AbstractDamageResult;
import necesse.entity.ObjectDamageResult;
import necesse.entity.TileDamageResult;
import necesse.entity.TileEntity;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.MobObjectDamagedEvent;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.LevelTile;

public class DamagedObjectEntity
extends TileEntity {
    public static int RECOVER_START_TIME = 10000;
    public static int DAMAGE_RECOVERY_PER_SECOND = 10;
    public int tileDamage = 0;
    public int[] objectDamage = new int[ObjectLayerRegistry.getTotalLayers()];
    public long lastDamageTime;
    public float damageRecoverBuffer;

    public DamagedObjectEntity(Level level, int tileX, int tileY) {
        super(level, tileX, tileY);
    }

    @Override
    public void clientTick() {
        if (this.removed()) {
            return;
        }
        this.tickDamageRecovery();
    }

    @Override
    public void serverTick() {
        if (this.removed()) {
            return;
        }
        this.tickDamageRecovery();
        this.checkTileDamage(null, null, null);
        this.checkObjectDamage(null, null, null);
    }

    public void tickDamageRecovery() {
        if (this.getTimeSinceLastDamage() > (long)RECOVER_START_TIME) {
            this.damageRecoverBuffer += (float)DAMAGE_RECOVERY_PER_SECOND / 20.0f;
            int damageRecover = (int)this.damageRecoverBuffer;
            this.damageRecoverBuffer -= (float)damageRecover;
            this.tileDamage = Math.max(this.tileDamage - damageRecover, 0);
            for (int i = 0; i < this.objectDamage.length; ++i) {
                this.objectDamage[i] = Math.max(this.objectDamage[i] - damageRecover, 0);
            }
            if (this.shouldRemove()) {
                this.remove();
            }
        } else {
            this.damageRecoverBuffer = 0.0f;
        }
    }

    public long getTimeSinceLastDamage() {
        return this.getTime() - this.lastDamageTime;
    }

    public int getObjectDamage(int layerID) {
        return this.objectDamage[layerID];
    }

    public boolean hasAnyObjectDamage() {
        for (int damage : this.objectDamage) {
            if (damage == 0) continue;
            return true;
        }
        return false;
    }

    public boolean shouldRemove() {
        return this.tileDamage == 0 && !this.hasAnyObjectDamage();
    }

    public static void destroyObject(Level level, int layerID, int tileX, int tileY, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        GameObject currentObject = level.getObject(layerID, tileX, tileY);
        if (itemsDropped == null) {
            itemsDropped = new ArrayList();
        }
        currentObject.onDestroyed(level, layerID, tileX, tileY, attacker, client, itemsDropped);
        if (level.isServer()) {
            level.getServer().network.sendToClientsWithTile(new PacketTileDestroyed(level, tileX, tileY, currentObject.getID(), false, layerID), level, tileX, tileY);
        }
        if (level.isServer()) {
            level.onObjectDestroyed(currentObject, layerID, tileX, tileY, attacker, client, itemsDropped);
        }
        level.getLevelTile(tileX, tileY).checkAround();
        level.getLevelObject(tileX, tileY).checkAround();
        level.objectLayer.setIsPlayerPlaced(layerID, tileX, tileY, false);
    }

    public boolean checkObjectDamage(Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        Level level = this.getLevel();
        for (int layer = 0; layer < this.objectDamage.length; ++layer) {
            GameObject currentObject = level.getObject(layer, this.tileX, this.tileY);
            if (this.objectDamage[layer] < currentObject.objectHealth) continue;
            DamagedObjectEntity.destroyObject(level, layer, this.tileX, this.tileY, attacker, client, itemsDropped);
            this.objectDamage[layer] = 0;
            if (this.shouldRemove()) {
                this.remove();
            }
            return true;
        }
        return false;
    }

    public boolean checkTileDamage(Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        Level level = this.getLevel();
        GameTile currentTile = level.getTile(this.tileX, this.tileY);
        if (this.tileDamage >= currentTile.tileHealth) {
            currentTile.onDestroyed(level, this.tileX, this.tileY, attacker, client, itemsDropped == null ? new ArrayList<ItemPickupEntity>() : itemsDropped);
            if (this.isServer()) {
                this.getServer().network.sendToClientsWithTile(new PacketTileDestroyed(level, this.tileX, this.tileY, currentTile.getID(), true, -1), level, this.tileX, this.tileY);
            }
            this.tileDamage = 0;
            if (this.isServer()) {
                level.onTileDestroyed(currentTile, this.tileX, this.tileY, attacker, client, itemsDropped);
            }
            level.getLevelTile(this.tileX, this.tileY).checkAround();
            level.getLevelObject(this.tileX, this.tileY).checkAround();
            if (this.shouldRemove()) {
                this.remove();
            }
            level.tileLayer.setIsPlayerPlaced(this.tileX, this.tileY, false);
            return true;
        }
        return false;
    }

    public void updateTileDamage(int totalDamage, boolean destroyed) {
        this.lastDamageTime = this.getTime();
        int n = this.tileDamage = destroyed ? 0 : totalDamage;
        if (this.shouldRemove()) {
            this.remove();
        }
    }

    public void updateObjectDamage(int objectLayerID, int totalDamage, boolean destroyed) {
        this.lastDamageTime = this.getTime();
        int n = this.objectDamage[objectLayerID] = destroyed ? 0 : totalDamage;
        if (this.shouldRemove()) {
            this.remove();
        }
    }

    public TileDamageResult doTileDamageOverride(int damage) {
        this.tileDamage = Math.max(0, this.tileDamage + damage);
        ArrayList<ItemPickupEntity> itemsDropped = new ArrayList<ItemPickupEntity>();
        boolean destroyed = false;
        LevelTile levelTile = this.getLevel().getLevelTile(this.tileX, this.tileY);
        LevelObject levelObject = this.getLevel().getLevelObject(this.tileX, this.tileY);
        if (!this.isClient()) {
            destroyed = this.checkTileDamage(null, null, itemsDropped);
        }
        return new TileDamageResult(this, levelTile, levelObject, damage, destroyed, itemsDropped, false, 0, 0);
    }

    public ObjectDamageResult doObjectDamageOverride(int objectLayerID, int damage) {
        this.objectDamage[objectLayerID] = Math.max(0, this.objectDamage[objectLayerID] + damage);
        ArrayList<ItemPickupEntity> itemsDropped = new ArrayList<ItemPickupEntity>();
        boolean destroyed = false;
        LevelTile levelTile = this.getLevel().getLevelTile(this.tileX, this.tileY);
        LevelObject levelObject = this.getLevel().getLevelObject(objectLayerID, this.tileX, this.tileY);
        if (!this.isClient()) {
            destroyed = this.checkObjectDamage(null, null, itemsDropped);
        }
        return new ObjectDamageResult(this, levelTile, levelObject, objectLayerID, damage, destroyed, itemsDropped, false, 0, 0);
    }

    public TileDamageResult doTileDamage(int damage, float toolTier, Attacker attacker, ServerClient client) {
        return this.doTileDamage(damage, toolTier, attacker, client, false, 0, 0);
    }

    public TileDamageResult doTileDamage(int damage, float toolTier, Attacker attacker, ServerClient client, boolean showEffects, int mouseX, int mouseY) {
        TileDamageEvent event = new TileDamageEvent(this.getLevel(), this.tileX, this.tileY, damage, toolTier, attacker, client);
        GameEvents.triggerEvent(event);
        if (!event.isPrevented()) {
            this.lastDamageTime = this.getTime();
            GameTile tile = this.getLevel().getTile(this.tileX, this.tileY);
            if (tile.getID() != 0 && tile.canBeMined) {
                boolean damaged;
                if (this.getLevel().isProtected(this.tileX, this.tileY) || toolTier != -1.0f && tile.toolTier > toolTier) {
                    damage = 0;
                }
                if (!(damaged = tile.onDamaged(this.getLevel(), this.tileX, this.tileY, damage, attacker, client, showEffects, mouseX, mouseY))) {
                    damage = 0;
                }
                this.tileDamage += damage;
                ArrayList<ItemPickupEntity> itemsDropped = new ArrayList<ItemPickupEntity>();
                boolean destroyed = false;
                LevelTile levelTile = this.getLevel().getLevelTile(this.tileX, this.tileY);
                LevelObject levelObject = this.getLevel().getLevelObject(this.tileX, this.tileY);
                if (!this.isClient()) {
                    destroyed = this.checkTileDamage(attacker, client, itemsDropped);
                }
                TileDamageResult result = new TileDamageResult(this, levelTile, levelObject, damage, destroyed, itemsDropped, showEffects, mouseX, mouseY);
                if (this.isServer()) {
                    if (client != null) {
                        this.getServer().network.sendToClientsWithTileExcept(new PacketTileDamage(this.getLevel(), this.tileX, this.tileY, tile.getID(), this.tileDamage, damage, destroyed, showEffects, mouseX, mouseY), this.getLevel(), this.tileX, this.tileY, client);
                        client.sendPacket(new PacketTileDamage(this.getLevel(), this.tileX, this.tileY, tile.getID(), this.tileDamage, damage, destroyed, false, mouseX, mouseY));
                    } else {
                        this.getServer().network.sendToClientsWithTile(new PacketTileDamage(this.getLevel(), this.tileX, this.tileY, tile.getID(), this.tileDamage, damage, destroyed, showEffects, mouseX, mouseY), this.getLevel(), this.tileX, this.tileY);
                    }
                    this.getLevel().onTileDamaged(tile, this.tileX, this.tileY, attacker, client, result);
                }
                return result;
            }
        }
        return null;
    }

    public ObjectDamageResult doObjectDamage(int objectLayerID, int damage, float toolTier, Attacker attacker, ServerClient client) {
        return this.doObjectDamage(objectLayerID, damage, toolTier, attacker, client, false, 0, 0);
    }

    public ObjectDamageResult doObjectDamage(int objectLayerID, int damage, float toolTier, Attacker attacker, ServerClient client, boolean showEffects, int mouseX, int mouseY) {
        int startDamage = damage;
        ObjectDamageEvent event = new ObjectDamageEvent(this.getLevel(), objectLayerID, this.tileX, this.tileY, damage, toolTier, attacker, client);
        GameEvents.triggerEvent(event);
        if (!event.isPrevented()) {
            this.lastDamageTime = this.getTime();
            GameObject obj = this.getLevel().getObject(objectLayerID, this.tileX, this.tileY);
            if (obj.getID() != 0 && obj.toolType != ToolType.UNBREAKABLE) {
                ObjectDamageResult result;
                boolean damaged;
                if (!obj.canPlaceOnProtectedLevels && this.getLevel().isProtected(this.tileX, this.tileY) || toolTier != -1.0f && obj.toolTier > toolTier) {
                    damage = 0;
                }
                if (obj.getMultiTile(this.getLevel(), objectLayerID, this.tileX, this.tileY).streamOtherObjects(this.tileX, this.tileY).anyMatch(e -> !((GameObject)e.value).canPlaceOnProtectedLevels && this.getLevel().isProtected(e.tileX, e.tileY))) {
                    damage = 0;
                }
                if (!(damaged = obj.onDamaged(this.getLevel(), objectLayerID, this.tileX, this.tileY, damage, attacker, client, showEffects, mouseX, mouseY))) {
                    damage = 0;
                }
                int n = objectLayerID;
                this.objectDamage[n] = this.objectDamage[n] + damage;
                LevelObject master = obj.isMultiTileMaster() ? null : (LevelObject)obj.getMultiTile(this.getLevel(), objectLayerID, this.tileX, this.tileY).getMasterLevelObject(this.getLevel(), objectLayerID, this.tileX, this.tileY).orElse(null);
                ArrayList<ItemPickupEntity> itemsDropped = new ArrayList<ItemPickupEntity>();
                boolean destroyed = false;
                LevelTile levelTile = this.getLevel().getLevelTile(this.tileX, this.tileY);
                LevelObject levelObject = this.getLevel().getLevelObject(objectLayerID, this.tileX, this.tileY);
                if (master != null && (result = this.getLevel().entityManager.doObjectDamage(objectLayerID, master.tileX, master.tileY, damage, toolTier, attacker, client, false, mouseX, mouseY)) != null) {
                    destroyed = result.destroyed;
                    itemsDropped.addAll(result.itemsDropped);
                }
                if (!this.isClient()) {
                    destroyed = this.checkObjectDamage(attacker, client, itemsDropped) || destroyed;
                }
                result = new ObjectDamageResult(this, levelTile, levelObject, objectLayerID, damage, destroyed, itemsDropped, showEffects, mouseX, mouseY);
                if (this.isServer()) {
                    if (client != null) {
                        this.getServer().network.sendToClientsWithTileExcept(new PacketObjectDamage(this.getLevel(), objectLayerID, this.tileX, this.tileY, obj.getID(), this.objectDamage[objectLayerID], damage, destroyed, showEffects, mouseX, mouseY), this.getLevel(), this.tileX, this.tileY, client);
                        client.sendPacket(new PacketObjectDamage(this.getLevel(), objectLayerID, this.tileX, this.tileY, obj.getID(), this.objectDamage[objectLayerID], damage, destroyed, false, mouseX, mouseY));
                    } else {
                        this.getServer().network.sendToClientsWithTile(new PacketObjectDamage(this.getLevel(), objectLayerID, this.tileX, this.tileY, obj.getID(), this.objectDamage[objectLayerID], damage, destroyed, showEffects, mouseX, mouseY), this.getLevel(), this.tileX, this.tileY);
                    }
                    this.getLevel().onObjectDamaged(obj, objectLayerID, this.tileX, this.tileY, attacker, client, result);
                }
                if (attacker != null) {
                    MobObjectDamagedEvent mobEvent = new MobObjectDamagedEvent(this.getLevel(), startDamage, toolTier, result, attacker);
                    for (Mob mob : attacker.getAttackOwnerChain()) {
                        mob.buffManager.submitMobEvent(mobEvent);
                    }
                }
                return result;
            }
        }
        return null;
    }

    public AbstractDamageResult doToolDamage(int priorityObjectLayerID, int damage, ToolType toolType, float toolTier, Attacker attacker, ServerClient client, boolean showEffects, int mouseX, int mouseY) {
        GameTile tile;
        Level level = this.getLevel();
        if (toolType == ToolType.AXE || toolType == ToolType.PICKAXE || toolType == ToolType.ALL) {
            int objectID;
            if (priorityObjectLayerID != -1 && (objectID = level.getObjectID(priorityObjectLayerID, this.tileX, this.tileY)) != 0) {
                GameObject obj = ObjectRegistry.getObject(objectID);
                if (obj.toolType != ToolType.UNBREAKABLE) {
                    if (!toolType.canDealDamageTo(obj.toolType)) {
                        damage = 0;
                    }
                    return this.doObjectDamage(priorityObjectLayerID, damage, toolTier, attacker, client, showEffects, mouseX, mouseY);
                }
            }
            for (int layer = ObjectLayerRegistry.getTotalLayers() - 1; layer >= 0; --layer) {
                int objectID2 = level.getObjectID(layer, this.tileX, this.tileY);
                if (objectID2 == 0) continue;
                GameObject obj = ObjectRegistry.getObject(objectID2);
                if (obj.toolType == ToolType.UNBREAKABLE) continue;
                if (!toolType.canDealDamageTo(obj.toolType)) {
                    damage = 0;
                }
                return this.doObjectDamage(layer, damage, toolTier, attacker, client, showEffects, mouseX, mouseY);
            }
        }
        if ((toolType == ToolType.SHOVEL || toolType == ToolType.ALL) && (tile = level.getTile(this.tileX, this.tileY)).getID() != 0 && tile.canBeMined) {
            return this.doTileDamage(damage, toolTier, attacker, client, showEffects, mouseX, mouseY);
        }
        return null;
    }
}

