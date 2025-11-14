/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.bucketItem;

import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.Comparator;
import java.util.LinkedList;
import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.input.Control;
import necesse.engine.input.Input;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.TileRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.attackHandler.PlaceItemAttackHandler;
import necesse.entity.mobs.attackHandler.SimplePlaceAttackHandler;
import necesse.entity.mobs.attackHandler.SimplePlaceableItemAttackHandler;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.InputTooltip;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.TilePosition;

public class InfiniteWaterBucketItem
extends PlaceableItem
implements ItemInteractAction {
    public InfiniteWaterBucketItem() {
        super(1, false);
        this.controllerIsTileBasedPlacing = true;
        this.setItemCategory("equipment", "tools", "misc");
        this.keyWords.add("liquid");
        this.rarity = Item.Rarity.EPIC;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        level.setupTileAndObjectsHashGNDMap(map, tileX, tileY, false);
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
        if (mapContent != null && player.isServerClient()) {
            level.checkTileAndObjectsHashGNDMap(player.getServerClient(), mapContent, tileX, tileY, false);
        }
        if (!this.isInPlaceRange(level, tileX * 32 + 16, tileY * 32 + 16, player, playerPositionLine, item)) {
            return "outofrange";
        }
        if (level.getTileID(tileX, tileY) == TileRegistry.waterID) {
            return "alreadywater";
        }
        GameTile waterTile = TileRegistry.getTile(TileRegistry.waterID);
        return waterTile.canPlace(level, tileX, tileY, true);
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        ItemPlaceEvent event = new ItemPlaceEvent(level, tileX, tileY, player, item);
        GameEvents.triggerEvent(event);
        if (!event.isPrevented()) {
            GameTile tile = TileRegistry.getTile(TileRegistry.waterID);
            tile.placeTile(level, tileX, tileY, true);
            level.tileLayer.setIsPlayerPlaced(tileX, tileY, true);
            if (level.isClient()) {
                SoundManager.playSound(GameResources.watersplash, (SoundEffect)SoundEffect.effect(tileX * 32 + 16, tileY * 32 + 16));
            } else {
                level.sendTileUpdatePacket(tileX, tileY);
                level.getLevelTile(tileX, tileY).checkAround();
                level.getLevelObject(tileX, tileY).checkAround();
                level.onTilePlaced(tile, tileX, tileY, player.getServerClient());
            }
        }
        return item;
    }

    @Override
    public void drawPlacePreview(Level level, int x, int y, GameCamera camera, PlayerMob player, InventoryItem item, PlayerInventorySlot slot) {
        if (this.canPlace(level, x, y, player, null, item, null) == null) {
            int tileX = GameMath.getTileCoordinate(x);
            int tileY = GameMath.getTileCoordinate(y);
            TileRegistry.getTile(TileRegistry.waterID).drawPreview(level, tileX, tileY, 0.5f, player, camera);
        } else if (!Input.lastInputIsController) {
            int tileY;
            int tileX = GameMath.getTileCoordinate(x);
            if (level.isProtected(tileX, tileY = GameMath.getTileCoordinate(y))) {
                return;
            }
            if (player.getPositionPoint().distance(tileX * 32 + 16, tileY * 32 + 16) > (double)this.getPlaceRange(item, player)) {
                return;
            }
            if (level.getTileID(tileX, tileY) != TileRegistry.waterID) {
                return;
            }
            if (level.liquidManager.getHeight(tileX, tileY) < -3) {
                return;
            }
            TileRegistry.getTile(TileRegistry.dirtID).drawPreview(level, tileX, tileY, 0.5f, player, camera);
        }
    }

    @Override
    public ItemControllerInteract getControllerInteract(final Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        if (beforeObjectInteract && !this.overridesObjectInteract(level, player, item)) {
            return null;
        }
        return tileInteractBoxes.stream().flatMap(r -> {
            LinkedList<TilePosition> tilePositions = new LinkedList<TilePosition>();
            for (int i = 0; i < r.width; ++i) {
                for (int j = 0; j < r.height; ++j) {
                    tilePositions.add(new TilePosition(level, r.x + i, r.y + j));
                }
            }
            return tilePositions.stream();
        }).filter(tp -> {
            int levelX = tp.tileX * 32 + 16;
            int levelY = tp.tileY * 32 + 16;
            return this.canLevelInteract(level, levelX, levelY, player, item);
        }).min(Comparator.comparingDouble(tp -> player.getDistance(tp.tileX * 32 + 16, tp.tileY * 32 + 16))).map(tp -> {
            int levelX = tp.tileX * 32 + 16;
            int levelY = tp.tileY * 32 + 16;
            return new ItemControllerInteract(levelX, levelY, (TilePosition)tp, player, item){
                final /* synthetic */ TilePosition val$tp;
                final /* synthetic */ PlayerMob val$player;
                final /* synthetic */ InventoryItem val$item;
                {
                    this.val$tp = tilePosition;
                    this.val$player = playerMob;
                    this.val$item = inventoryItem;
                    super(levelX, levelY);
                }

                @Override
                public DrawOptions getDrawOptions(GameCamera camera) {
                    if (level.isProtected(this.val$tp.tileX, this.val$tp.tileY)) {
                        return null;
                    }
                    if (this.val$player.getPositionPoint().distance(this.val$tp.tileX * 32 + 16, this.val$tp.tileY * 32 + 16) > (double)InfiniteWaterBucketItem.this.getPlaceRange(this.val$item, this.val$player)) {
                        return null;
                    }
                    if (level.getTileID(this.val$tp.tileX, this.val$tp.tileY) != TileRegistry.waterID) {
                        return null;
                    }
                    if (level.liquidManager.getHeight(this.val$tp.tileX, this.val$tp.tileY) < -3) {
                        return null;
                    }
                    return () -> TileRegistry.getTile(TileRegistry.dirtID).drawPreview(level, tp.tileX, tp.tileY, 0.5f, this.val$player, camera);
                }

                @Override
                public void onCurrentlyFocused(GameCamera camera) {
                }
            };
        }).orElse(null);
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
                level.checkTileAndObjectsHashGNDMap(client, placePosition.attackMapContent, tileX, tileY, false);
            }
        });
        return slot.getItem();
    }

    @Override
    public void setupLevelInteractMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        this.setupAttackMapContent(map, level, x, y, attackerMob, 0, item);
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return true;
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (!attackerMob.isPlayer) {
            return item;
        }
        PlayerMob player = (PlayerMob)attackerMob;
        int cooldown = this.getLevelInteractCooldownTime(item, attackerMob);
        if (cooldown > 0) {
            if (this.canPlaceWater(level, x, y, player, null, item, mapContent) == null) {
                return this.onPlaceWater(level, x, y, player, seed, item, mapContent);
            }
            return item;
        }
        attackerMob.startAttackHandler(new SimplePlaceAttackHandler(player, slot, x, y, seed, mapContent){

            @Override
            public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
                return InfiniteWaterBucketItem.this.canPlaceWater(level, x, y, player, playerPositionLine, item, mapContent);
            }

            @Override
            public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
                return InfiniteWaterBucketItem.this.onPlaceWater(level, x, y, player, seed, item, mapContent);
            }

            @Override
            protected void onServerPlaceInvalid(InventoryItem item, PlaceItemAttackHandler.PlacePosition placePosition, Line2D playerPositionLine) {
                Level level = this.attackerMob.getLevel();
                PlayerMob player = (PlayerMob)this.attackerMob;
                ServerClient client = player.getServerClient();
                int tileX = GameMath.getTileCoordinate(placePosition.placeX);
                int tileY = GameMath.getTileCoordinate(placePosition.placeY);
                level.checkTileAndObjectsHashGNDMap(client, placePosition.attackMapContent, tileX, tileY, false);
            }

            @Override
            protected int getPlaceCooldown() {
                InventoryItem item = this.slot.getItem();
                if (item != null) {
                    return InfiniteWaterBucketItem.this.getAttackHandlerPlaceCooldown(item, this.attackerMob);
                }
                return 200;
            }

            @Override
            protected void showAttackAndSendAttacker(int targetX, int targetY, InventoryItem item) {
                this.attackerMob.showItemLevelInteract(InfiniteWaterBucketItem.this, item, targetX, targetY, this.seed, null);
            }
        }.startFromInteract());
        return slot.getItem();
    }

    public String canPlaceWater(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        int tileY;
        int tileX = GameMath.getTileCoordinate(x);
        if (!level.isTileWithinBounds(tileX, tileY = GameMath.getTileCoordinate(y))) {
            return "outsidelevel";
        }
        if (level.isProtected(tileX, tileY)) {
            return "protected";
        }
        if (mapContent != null && player.isServerClient()) {
            level.checkTileAndObjectsHashGNDMap(player.getServerClient(), mapContent, tileX, tileY, false);
        }
        if (!this.isInPlaceRange(level, x, y, player, playerPositionLine, item)) {
            return "outofrange";
        }
        if (level.getTileID(tileX, tileY) != TileRegistry.waterID) {
            return "notwater";
        }
        if (level.liquidManager.getHeight(tileX, tileY) < -3) {
            return "deepsea";
        }
        return null;
    }

    public InventoryItem onPlaceWater(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        ItemPlaceEvent event = new ItemPlaceEvent(level, tileX, tileY, player, item);
        GameEvents.triggerEvent(event);
        if (!event.isPrevented()) {
            GameTile tile = TileRegistry.getTile(TileRegistry.dirtID);
            tile.placeTile(level, tileX, tileY, true);
            level.tileLayer.setIsPlayerPlaced(tileX, tileY, true);
            if (level.isClient()) {
                SoundManager.playSound(GameResources.waterblob, (SoundEffect)SoundEffect.effect(tileX * 32 + 16, tileY * 32 + 16));
            } else {
                level.sendTileUpdatePacket(tileX, tileY);
                level.getLevelTile(tileX, tileY).checkAround();
                level.getLevelObject(tileX, tileY).checkAround();
                level.onTilePlaced(tile, tileX, tileY, player.getServerClient());
            }
        }
        return item;
    }

    @Override
    public boolean getConstantInteract(InventoryItem item) {
        return true;
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
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "infwaterbuckettip"));
        tooltips.add(new InputTooltip(Control.MOUSE1, Localization.translate("itemtooltip", "infwaterbucketplace")));
        tooltips.add(new InputTooltip(Control.MOUSE2, Localization.translate("itemtooltip", "infwaterbucketpickup")));
        return tooltips;
    }
}

