/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.TeleportResult;
import necesse.engine.world.GameClock;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.levelEvent.TeleportEvent;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.WaystoneObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.object.HomestoneUpdateEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.WaystoneObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.lootTable.LootTable;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.CachedSettlementData;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.Waystone;
import necesse.level.maps.light.GameLight;

public class WaystoneObject
extends GameObject {
    public ObjectDamagedTextureArray texture;

    public WaystoneObject() {
        super(new Rectangle(0, 4, 32, 22));
        this.mapColor = new Color(124, 137, 154);
        this.objectHealth = 200;
        this.toolType = ToolType.ALL;
        this.stackSize = 10;
        this.lightLevel = 0;
        this.lightHue = 50.0f;
        this.lightSat = 0.2f;
        this.rarity = Item.Rarity.RARE;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/waystone");
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "waystonetip1"));
        tooltips.add(Localization.translate("itemtooltip", "waystonetip2"));
        return tooltips;
    }

    @Override
    public LootTable getLootTable(Level level, int layerID, int tileX, int tileY) {
        return new LootTable();
    }

    @Override
    public Item generateNewObjectItem() {
        return new WaystoneObjectItem(this);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new WaystoneObjectEntity(level, x, y, this.texture);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        ServerSettlementData settlement;
        WaystoneObjectEntity waystoneEntity;
        if (level.isServer() && (waystoneEntity = this.getCurrentObjectEntity(level, x, y, WaystoneObjectEntity.class)) != null && waystoneEntity.settlementUniqueID != 0 && (settlement = SettlementsWorldData.getSettlementsData(level).getServerData(waystoneEntity.settlementUniqueID)) != null) {
            ArrayList<Waystone> waystones = settlement.getWaystones();
            for (int i = 0; i < waystones.size(); ++i) {
                Waystone waystone = waystones.get(i);
                if (!waystone.matches(level, x, y)) continue;
                waystones.remove(i);
                --i;
                settlement.sendEvent(HomestoneUpdateEvent.class);
            }
        }
        super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) + 1;
        int drawY = camera.getTileDrawY(tileY) - 32;
        long fadeTime = 1500L;
        float saturation = Math.abs(light.getFloatLevel() - 1.0f) * 0.2f + 0.1f;
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
        draws.addSprite(0, 0, 32, texture.getHeight()).light(light).pos(drawX, drawY);
        draws.addSprite(1, 0, 32, texture.getHeight()).spelunkerLight(light, true, WaystoneObject.getTileSeed(tileX, tileY), level, fadeTime, saturation, 50).pos(drawX, drawY);
        draws.addSprite(2, 0, 32, texture.getHeight()).spelunkerLight(light, true, WaystoneObject.getTileSeed(tileX, tileY), GameClock.offsetClock(level, fadeTime / 3L), fadeTime, saturation, 50).pos(drawX, drawY);
        list.add(new LevelSortedDrawable(this, tileX, tileY){

            @Override
            public int getSortY() {
                return 16;
            }

            @Override
            public void draw(TickManager tickManager) {
                draws.draw();
            }
        });
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX) + 1;
        int drawY = camera.getTileDrawY(tileY) - 32;
        long fadeTime = 1500L;
        float saturation = 0.1f;
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
        draws.addSprite(0, 0, 32, texture.getHeight()).alpha(alpha).pos(drawX, drawY);
        draws.addSprite(1, 0, 32, texture.getHeight()).spelunkerLight(new GameLight(150.0f), true, WaystoneObject.getTileSeed(tileX, tileY), level, fadeTime, saturation, 50).alpha(alpha).pos(drawX, drawY);
        draws.addSprite(2, 0, 32, texture.getHeight()).spelunkerLight(new GameLight(150.0f), true, WaystoneObject.getTileSeed(tileX, tileY), GameClock.offsetClock(level, fadeTime / 3L), fadeTime, saturation, 50).alpha(alpha).pos(drawX, drawY);
        draws.draw();
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "usetip");
    }

    @Override
    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServer()) {
            ObjectEntity objectEntity;
            WaystoneObjectEntity waystoneEntity = this.getCurrentObjectEntity(level, x, y, WaystoneObjectEntity.class);
            if (waystoneEntity != null && waystoneEntity.settlementUniqueID != 0) {
                ServerClient client = player.getServerClient();
                CachedSettlementData cache = SettlementsWorldData.getSettlementsData(level).getCachedData(waystoneEntity.settlementUniqueID);
                if (cache != null) {
                    TeleportEvent teleportEvent = new TeleportEvent(client, 0, cache.levelIdentifier, 0.0f, null, targetLevel -> {
                        ServerSettlementData settlement = SettlementsWorldData.getSettlementsData(level).getOrLoadServerData(waystoneEntity.settlementUniqueID);
                        if (settlement != null) {
                            Point homestonePos = settlement.getHomestoneTile();
                            if (homestonePos != null) {
                                if (settlement.getWaystones().stream().anyMatch(w -> w.matches(level, x, y))) {
                                    Point point = Waystone.findTeleportLocation(targetLevel, homestonePos.x, homestonePos.y, player);
                                    client.newStats.waystones_used.increment(1);
                                    return new TeleportResult(true, point);
                                }
                                client.sendChatMessage(new LocalMessage("ui", "waystoneinvalidhome"));
                            } else {
                                client.sendChatMessage(new LocalMessage("ui", "waystoneinvalidhome"));
                            }
                        } else {
                            client.sendChatMessage(new LocalMessage("ui", "waystoneinvalidhome"));
                        }
                        return new TeleportResult(false, null);
                    });
                    client.getLevel().entityManager.events.addHidden(teleportEvent);
                } else {
                    client.sendChatMessage(new LocalMessage("ui", "waystoneinvalidhome"));
                }
            }
            if ((objectEntity = level.entityManager.getObjectEntity(x, y)) instanceof WaystoneObjectEntity) {
                WaystoneObjectEntity waystoneObjectEntity = (WaystoneObjectEntity)objectEntity;
            }
        }
    }
}

