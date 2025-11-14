/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.objectItem;

import java.awt.geom.Line2D;
import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketLogicGateUpdate;
import necesse.engine.network.packet.PacketPlaceLogicGate;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
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
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.gameLogicGate.entities.LogicGateEntity;
import necesse.level.maps.Level;

public class LogicGateItem
extends PlaceableItem {
    public int gateID;

    public LogicGateItem(GameLogicGate gate, int stackSize, Item.Rarity rarity) {
        super(stackSize, true);
        this.gateID = gate.getID();
        this.rarity = rarity;
        this.dropsAsMatDeathPenalty = true;
        this.setItemCategory("wiring", "logicgates");
        this.setItemCategory(ItemCategory.craftingManager, "wiring");
        this.keyWords.add("logic");
        this.keyWords.add("gate");
        this.keyWords.add("logicgate");
        this.keyWords.add("wire");
    }

    public LogicGateItem(GameLogicGate gate) {
        this(gate, 100, Item.Rarity.NORMAL);
    }

    @Override
    public void loadItemTextures() {
        this.itemTexture = this.getLogicGate().generateItemTexture();
    }

    @Override
    public GameMessage getNewLocalization() {
        return this.getLogicGate().getLocalization();
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(this.getLogicGate().getItemTooltips());
        return tooltips;
    }

    @Override
    public void setupAttackMapContent(GNDItemMap map, Level level, int x, int y, ItemAttackerMob attackerMob, int seed, InventoryItem item) {
        super.setupAttackMapContent(map, level, x, y, attackerMob, seed, item);
        map.setByteUnsigned("lastMobDir", attackerMob.isAttacking ? attackerMob.beforeAttackDir : attackerMob.getDir());
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
        if (player.getPositionPoint().distance(tileX * 32 + 16, tileY * 32 + 16) > (double)this.getPlaceRange(item, player)) {
            return "outofrange";
        }
        return this.getLogicGate().canPlace(level, tileX, tileY);
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        int tileX = GameMath.getTileCoordinate(x);
        int tileY = GameMath.getTileCoordinate(y);
        ItemPlaceEvent event = new ItemPlaceEvent(level, tileX, tileY, player, item);
        GameEvents.triggerEvent(event);
        if (!event.isPrevented()) {
            if (level.isServer()) {
                this.getLogicGate().placeGate(level, tileX, tileY);
                level.getServer().network.sendToClientsWithTile(new PacketPlaceLogicGate(level, player.getServerClient(), this.gateID, tileX, tileY), level, tileX, tileY);
                player.getServerClient().newStats.objects_placed.increment(1);
                level.getTile(tileX, tileY).checkAround(level, tileX, tileY);
                level.getObject(tileX, tileY).checkAround(level, tileX, tileY);
            }
            if (this.isSingleUse(player)) {
                item.setAmount(item.getAmount() - 1);
            }
        }
        return item;
    }

    @Override
    public InventoryItem onAttemptPlace(Level level, int x, int y, PlayerMob player, InventoryItem item, GNDItemMap mapContent, String error) {
        this.getLogicGate().attemptPlace(level, GameMath.getTileCoordinate(x), GameMath.getTileCoordinate(y), player, error);
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

    public GameLogicGate getLogicGate() {
        return LogicGateRegistry.getLogicGate(this.gateID);
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
                int tileY;
                Level level = this.attackerMob.getLevel();
                PlayerMob player = (PlayerMob)this.attackerMob;
                ServerClient client = player.getServerClient();
                int tileX = GameMath.getTileCoordinate(placePosition.placeX);
                LogicGateEntity entity = level.logicLayer.getEntity(tileX, tileY = GameMath.getTileCoordinate(placePosition.placeY));
                int gateID = entity == null ? -1 : entity.getLogicGate().getID();
                client.sendPacket(new PacketLogicGateUpdate(level, tileX, tileY, gateID, entity));
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
            this.getLogicGate().drawPreview(level, tileX, tileY, alpha, player, camera);
        }
    }

    @Override
    public boolean showWires() {
        return true;
    }
}

