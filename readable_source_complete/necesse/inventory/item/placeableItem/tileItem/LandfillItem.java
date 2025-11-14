/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.tileItem;

import java.awt.geom.Line2D;
import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.PlaceItemAttackHandler;
import necesse.entity.mobs.attackHandler.SimplePlaceableItemAttackHandler;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class LandfillItem
extends PlaceableItem {
    public LandfillItem() {
        super(500, true);
        this.setItemCategory(ItemCategory.craftingManager, "tiles");
        this.dropsAsMatDeathPenalty = true;
        this.controllerIsTileBasedPlacing = true;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "landfilltip"));
        return tooltips;
    }

    @Override
    public float getAttackSpeedModifier(InventoryItem item, ItemAttackerMob attackerMob) {
        float superModifier = super.getAttackSpeedModifier(item, attackerMob);
        if (attackerMob != null && attackerMob.isPlayer && ((PlayerMob)attackerMob).hasGodMode()) {
            return superModifier;
        }
        return superModifier * (attackerMob == null ? 1.0f : attackerMob.buffManager.getModifier(BuffModifiers.BUILDING_SPEED).floatValue());
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return Math.max(super.getAttackAnimTime(item, attackerMob), 50);
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        int tileY;
        int tileX = GameMath.getTileCoordinate(x);
        if (!level.isTileWithinBounds(tileX, tileY = GameMath.getTileCoordinate(y))) {
            return "outsidelevel";
        }
        if (level.isProtected(tileX, tileY)) {
            return "protected";
        }
        if (!level.getTile((int)tileX, (int)tileY).isLiquid) {
            return "notliquid";
        }
        if (!this.isInPlaceRange(level, tileX * 32 + 16, tileY * 32 + 16, player, playerPositionLine, item)) {
            return "outofrange";
        }
        return null;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        ItemPlaceEvent event = new ItemPlaceEvent(level, tileX, tileY, player, item);
        GameEvents.triggerEvent(event);
        if (!event.isPrevented()) {
            GameTile tile = TileRegistry.getTile(TileRegistry.dirtID);
            tile.placeTile(level, tileX, tileY, true);
            level.tileLayer.setIsPlayerPlaced(tileX, tileY, true);
            if (level.isClient()) {
                SoundManager.playSound(GameResources.tap, (SoundEffect)SoundEffect.effect(tileX * 32 + 16, tileY * 32 + 16));
            } else {
                level.sendTileUpdatePacket(tileX, tileY);
                player.getServerClient().newStats.tiles_placed.increment(1);
                level.getLevelTile(tileX, tileY).checkAround();
                level.getLevelObject(tileX, tileY).checkAround();
                level.onTilePlaced(tile, tileX, tileY, player.getServerClient());
            }
            if (this.isSingleUse(player)) {
                item.setAmount(item.getAmount() - 1);
            }
        }
        return item;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int cooldown = this.getAttackCooldownTime(item, attackerMob);
        if (cooldown > 0) {
            return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        }
        if (!attackerMob.isPlayer) {
            return item;
        }
        attackerMob.startAttackHandler(new SimplePlaceableItemAttackHandler((PlayerMob)attackerMob, slot, x, y, seed, this, mapContent){

            @Override
            protected void onServerPlaceInvalid(InventoryItem item, PlaceItemAttackHandler.PlacePosition placePosition, Line2D playerPositionLine) {
                Level level = this.attackerMob.getLevel();
                PlayerMob player = (PlayerMob)this.attackerMob;
                ServerClient client = player.getServerClient();
                int tileX = GameMath.getTileCoordinate(placePosition.placeX);
                int tileY = GameMath.getTileCoordinate(placePosition.placeY);
                client.sendPacket(new PacketChangeTile(level, tileX, tileY, level.getTileID(tileX, tileY)));
            }
        });
        return slot.getItem();
    }

    @Override
    public void drawPlacePreview(Level level, int x, int y, GameCamera camera, PlayerMob player, InventoryItem item, PlayerInventorySlot slot) {
        if (this.canPlace(level, x, y, player, null, item, null) == null) {
            float alpha = 0.5f;
            int tileX = GameMath.getTileCoordinate(x);
            int tileY = GameMath.getTileCoordinate(y);
            TileRegistry.getTile(TileRegistry.dirtID).drawPreview(level, tileX, tileY, alpha, player, camera);
        }
    }
}

