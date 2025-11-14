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
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.GameClock;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.HomestoneObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.container.object.HomestoneContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.objectItem.HomestoneObjectItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.light.GameLight;

public class HomestoneObject
extends GameObject {
    public ObjectDamagedTextureArray texture;

    public HomestoneObject() {
        super(new Rectangle(0, 4, 32, 22));
        this.mapColor = new Color(124, 137, 154);
        this.objectHealth = 100;
        this.toolType = ToolType.ALL;
        this.stackSize = 1;
        this.lightLevel = 0;
        this.lightHue = 50.0f;
        this.lightSat = 0.2f;
        this.rarity = Item.Rarity.EPIC;
        this.hoverHitbox = new Rectangle(0, -44, 32, 76);
        this.setItemCategory("objects", "misc");
        this.setCraftingCategory("objects", "misc");
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/homestone");
    }

    @Override
    public String canPlace(Level level, int layerID, int x, int y, int rotation, boolean byPlayer, boolean ignoreOtherLayers) {
        String error = super.canPlace(level, layerID, x, y, rotation, byPlayer, ignoreOtherLayers);
        if (error != null) {
            return error;
        }
        if (!level.getIdentifier().equals(LevelIdentifier.SURFACE_IDENTIFIER)) {
            return "notsurface";
        }
        return null;
    }

    @Override
    public Item generateNewObjectItem() {
        return new HomestoneObjectItem(this);
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new HomestoneObjectEntity(level, x, y, this.texture);
    }

    @Override
    public void onDestroyed(Level level, int layerID, int x, int y, Attacker attacker, ServerClient client, ArrayList<ItemPickupEntity> itemsDropped) {
        Point homestoneTile;
        ServerSettlementData settlement;
        if (level.isServer() && (settlement = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), x, y)) != null && (homestoneTile = settlement.getHomestoneTile()) != null && homestoneTile.x == x && homestoneTile.y == y) {
            settlement.setHomestoneTile(null);
        }
        super.onDestroyed(level, layerID, x, y, attacker, client, itemsDropped);
    }

    @Override
    public void placeObject(Level level, int layerID, int x, int y, int rotation, boolean byPlayer) {
        ServerSettlementData settlement;
        super.placeObject(level, layerID, x, y, rotation, byPlayer);
        if (level.isServer() && (settlement = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), x, y)) != null) {
            settlement.setHomestoneTile(new Point(x, y));
        }
    }

    @Override
    public void addDrawables(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int tileX, int tileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameLight light = level.getLightLevel(tileX, tileY);
        int drawX = camera.getTileDrawX(tileX) - 15;
        int drawY = camera.getTileDrawY(tileY) - 96;
        long fadeTime = 1500L;
        float saturation = Math.abs(light.getFloatLevel() - 1.0f) * 0.2f + 0.1f;
        GameTexture texture = this.texture.getDamagedTexture(this, level, tileX, tileY);
        final SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
        draws.addSprite(0, 0, 64, texture.getHeight()).light(light).pos(drawX, drawY);
        draws.addSprite(1, 0, 64, texture.getHeight()).spelunkerLight(light, true, HomestoneObject.getTileSeed(tileX, tileY), level, fadeTime, saturation, 50).pos(drawX, drawY);
        draws.addSprite(2, 0, 64, texture.getHeight()).spelunkerLight(light, true, HomestoneObject.getTileSeed(tileX, tileY), GameClock.offsetClock(level, fadeTime / 3L), fadeTime, saturation, 50).pos(drawX, drawY);
        draws.addSprite(3, 0, 64, texture.getHeight()).spelunkerLight(light, true, HomestoneObject.getTileSeed(tileX, tileY), GameClock.offsetClock(level, fadeTime / 3L * 2L), fadeTime, saturation, 50).pos(drawX, drawY);
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
        int drawX = camera.getTileDrawX(tileX) - 16;
        int drawY = camera.getTileDrawY(tileY) - 96;
        long fadeTime = 1500L;
        float saturation = 0.1f;
        GameTexture texture = this.texture.getDamagedTexture(0.0f);
        SharedTextureDrawOptions draws = new SharedTextureDrawOptions(texture);
        draws.addSprite(0, 0, 64, texture.getHeight()).alpha(alpha).pos(drawX, drawY);
        draws.addSprite(1, 0, 64, texture.getHeight()).spelunkerLight(new GameLight(150.0f), true, HomestoneObject.getTileSeed(tileX, tileY), level, fadeTime, saturation, 50).alpha(alpha).pos(drawX, drawY);
        draws.addSprite(2, 0, 64, texture.getHeight()).spelunkerLight(new GameLight(150.0f), true, HomestoneObject.getTileSeed(tileX, tileY), GameClock.offsetClock(level, fadeTime / 3L), fadeTime, saturation, 50).alpha(alpha).pos(drawX, drawY);
        draws.addSprite(3, 0, 64, texture.getHeight()).spelunkerLight(new GameLight(150.0f), true, HomestoneObject.getTileSeed(tileX, tileY), GameClock.offsetClock(level, fadeTime / 3L * 2L), fadeTime, saturation, 50).alpha(alpha).pos(drawX, drawY);
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
            ServerSettlementData settlement = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), x, y);
            if (settlement != null) {
                PacketOpenContainer p = PacketOpenContainer.SettlementLevelObject(ContainerRegistry.HOMESTONE_CONTAINER, settlement, x, y, HomestoneContainer.getContainerContent(settlement));
                ContainerRegistry.openAndSendContainer(player.getServerClient(), p);
            } else if (player != null && player.isServerClient()) {
                player.getServerClient().sendChatMessage(new LocalMessage("ui", "homestonenosettlement"));
            }
        }
    }
}

