/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.tileItem;

import java.awt.Point;
import java.awt.geom.Line2D;
import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.network.packet.PacketPlaceTile;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.AbstractDamageResult;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.PlaceItemAttackHandler;
import necesse.entity.mobs.attackHandler.SimplePlaceableItemAttackHandler;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.inventory.item.toolItem.ToolDamageItem;
import necesse.inventory.item.toolItem.ToolType;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;

public class TileItem
extends PlaceableItem {
    public int tileID;
    private final boolean isBucket;

    public TileItem(GameTile tile) {
        super(tile.stackSize, true);
        this.tileID = tile.getID();
        this.controllerIsTileBasedPlacing = true;
        this.dropsAsMatDeathPenalty = true;
        this.rarity = tile.rarity;
        this.setItemCategory(tile.itemCategoryTree);
        this.setItemCategory(ItemCategory.craftingManager, tile.craftingCategoryTree);
        this.keyWords.add("tile");
        this.isBucket = tile.isLiquid;
        for (String globalIngredient : tile.itemGlobalIngredients) {
            this.addGlobalIngredient(globalIngredient);
        }
    }

    @Override
    public void loadItemTextures() {
        this.itemTexture = this.getTile().generateItemTexture();
    }

    @Override
    public GameMessage getNewLocalization() {
        return this.getTile().getLocalization();
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(this.getTile().getItemTooltips(item, perspective));
        return tooltips;
    }

    @Override
    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        level.setupTileAndObjectsHashGNDMap(map, tileX, tileY, true);
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
        if (mapContent == null) {
            mapContent = new GNDItemMap();
            this.setupAttackMapContent(mapContent, level, x, y, player, -1, item);
        }
        if (player.isServerClient()) {
            ServerClient client = player.getServerClient();
            level.checkTileAndObjectsHashGNDMap(client, mapContent, tileX, tileY, true);
        }
        Point placeDistancePoint = new Point(tileX * 32 + 16, tileY * 32 + 16);
        if (!this.isInPlaceRange(level, placeDistancePoint.x, placeDistancePoint.y, player, playerPositionLine, item)) {
            return "outofrange";
        }
        String error = this.getTile().canPlace(level, tileX, tileY, true);
        if (error != null && !this.canReplace(level, tileX, tileY, player, playerPositionLine, item, error)) {
            return error;
        }
        return null;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        if (mapContent == null) {
            mapContent = new GNDItemMap();
            this.setupAttackMapContent(mapContent, level, x, y, player, seed, item);
        }
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        GameTile tile = this.getTile();
        boolean isReplace = false;
        if (player != null && tile.canPlace(level, tileX, tileY, true) != null) {
            if (!this.runReplaceDamageTile(level, x, y, tileX, tileY, player, item)) {
                return item;
            }
            if (tile.canPlace(level, tileX, tileY, true) != null) {
                return item;
            }
            isReplace = true;
        }
        ItemPlaceEvent event = new ItemPlaceEvent(level, tileX, tileY, player, item);
        GameEvents.triggerEvent(event);
        if (!event.isPrevented()) {
            boolean success = true;
            if (!level.isClient()) {
                ServerClient client = player == null ? null : player.getServerClient();
                success = this.onPlaceTile(tile, level, tileX, tileY, client, item);
                if (success) {
                    level.tileLayer.setIsPlayerPlaced(tileX, tileY, true);
                    if (level.isServer()) {
                        level.getServer().network.sendToClientsWithTile(new PacketPlaceTile(level, client, this.tileID, tileX, tileY), level, tileX, tileY);
                    }
                    if (client != null) {
                        client.newStats.tiles_placed.increment(1);
                    }
                    level.getLevelTile(tileX, tileY).checkAround();
                    level.getLevelObject(tileX, tileY).checkAround();
                } else {
                    level.entityManager.pickups.add(item.copy(1).getPickupEntity(level, tileX * 32 + 16, tileY * 32 + 16));
                }
            } else {
                tile.placeTile(level, tileX, tileY, true);
            }
            if (this.isBucket && success) {
                InventoryItem bucket = new InventoryItem("bucket");
                if (item.getAmount() <= 1 && this.isSingleUse(player)) {
                    item.setAmount(item.getAmount() - 1);
                    return bucket;
                }
                if (!level.isClient()) {
                    if (player != null) {
                        player.getInv().addItemsDropRemaining(bucket, "addback", player, false, false);
                    } else {
                        level.entityManager.pickups.add(bucket.getPickupEntity(level, tileX * 32 + 16, tileY * 32 + 16));
                    }
                }
                if (this.isSingleUse(player)) {
                    item.setAmount(item.getAmount() - 1);
                }
            } else if (this.isSingleUse(player)) {
                item.setAmount(item.getAmount() - 1);
            }
        }
        return item;
    }

    public boolean onPlaceTile(GameTile tile, Level level, int tileX, int tileY, ServerClient client, InventoryItem item) {
        tile.placeTile(level, tileX, tileY, true);
        if (level.isServer()) {
            level.onTilePlaced(tile, tileX, tileY, client);
        }
        return true;
    }

    public boolean canReplace(Level level, int tileX, int tileY, PlayerMob playerMob, Line2D playerPositionLine, InventoryItem item, String error) {
        return this.getTile().canReplace(level, tileX, tileY) && this.getBestToolDamageItem(level, tileX, tileY, playerMob, playerPositionLine, true) != null;
    }

    @Override
    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, String error) {
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        this.getTile().attemptPlace(level, tileX, tileY, player, error);
        return item;
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

    public GameTile getTile() {
        return TileRegistry.getTile(this.tileID);
    }

    @Override
    public void refreshLight(Level level, float x, float y, InventoryItem item, boolean isHolding) {
        GameTile tile = this.getTile();
        if (tile.getLightLevel() >= 100) {
            level.lightManager.refreshParticleLightFloat(x, y, tile.lightHue, tile.lightSat);
        }
    }

    @Override
    public void drawPlacePreview(Level level, int x, int y, GameCamera camera, PlayerMob player, InventoryItem item, PlayerInventorySlot slot) {
        if (this.canPlace(level, x, y, player, null, item, null) == null) {
            float alpha = 0.5f;
            int tileX = GameMath.getTileCoordinate(x);
            int tileY = GameMath.getTileCoordinate(y);
            this.getTile().drawPreview(level, tileX, tileY, alpha, player, camera);
        }
    }

    protected PlayerInventorySlot getBestToolDamageItem(Level level, int tileX, int tileY, PlayerMob player, Line2D playerPositionLine, boolean checkRange) {
        ToolDamageItem bestToolItem = null;
        InventoryItem bestInventoryItem = null;
        PlayerInventorySlot bestSlot = null;
        for (int i = 0; i < player.getInv().main.getSize(); ++i) {
            InventoryItem slotItem = player.getInv().main.getItem(i);
            if (slotItem == null || !(slotItem.item instanceof ToolDamageItem)) continue;
            ToolDamageItem slotToolItem = (ToolDamageItem)slotItem.item;
            if (bestToolItem != null && bestToolItem.getToolDps(bestInventoryItem, player) >= slotToolItem.getToolDps(slotItem, player) || !slotToolItem.canDamageTile(level, 0, tileX, tileY, player, slotItem) || !slotToolItem.getToolType().canDealDamageTo(ToolType.SHOVEL) || checkRange && !slotToolItem.isTileInRange(level, tileX, tileY, player, playerPositionLine, slotItem)) continue;
            bestToolItem = slotToolItem;
            bestInventoryItem = slotItem;
            bestSlot = new PlayerInventorySlot(player.getInv().main, i);
        }
        return bestSlot;
    }

    protected boolean runReplaceDamageTile(Level level, int levelX, int levelY, int tileX, int tileY, PlayerMob player, InventoryItem placeableItem) {
        float toolTier;
        ToolType toolType;
        ServerClient client = player.isServerClient() ? player.getServerClient() : null;
        int tileID = level.getTileID(tileX, tileY);
        if (tileID == TileRegistry.dirtID || tileID == TileRegistry.emptyID) {
            return false;
        }
        PlayerInventorySlot toolDamageItemSlot = this.getBestToolDamageItem(level, tileX, tileY, player, null, false);
        if (toolDamageItemSlot == null) {
            return false;
        }
        InventoryItem toolInventoryItem = toolDamageItemSlot.getItem(player.getInv());
        ToolDamageItem toolItem = (ToolDamageItem)toolInventoryItem.item;
        float miningSpeedModifier = toolItem.getMiningSpeedModifier(toolInventoryItem, player);
        int hitDamage = (int)((float)toolItem.getToolDps(toolInventoryItem, player) * ((float)this.getAttackAnimTime(placeableItem, player) / 1000.0f) * miningSpeedModifier);
        AbstractDamageResult result = level.entityManager.doToolDamage(-1, tileX, tileY, hitDamage, toolType = toolItem.getToolType(), toolTier = toolItem.getToolTier(toolInventoryItem, player), player, client, true, levelX, levelY);
        return result != null && result.destroyed;
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
}

