/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem;

import java.awt.geom.Line2D;
import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketChangeObjects;
import necesse.engine.network.packet.PacketObjectEntity;
import necesse.engine.network.server.ServerClient;
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
import necesse.entity.objectEntity.AbstractBeeHiveObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.maps.Level;

public class HoneyBeePlaceableItem
extends PlaceableItem {
    public boolean isQueen;

    public HoneyBeePlaceableItem(boolean isQueen) {
        super(isQueen ? 5 : 20, true);
        this.controllerIsTileBasedPlacing = true;
        this.rarity = Item.Rarity.COMMON;
        this.dropsAsMatDeathPenalty = true;
        this.isQueen = isQueen;
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public float getAttackSpeedModifier(InventoryItem item, ItemAttackerMob attackerMob) {
        if (attackerMob != null && attackerMob.isPlayer && ((PlayerMob)attackerMob).hasGodMode()) {
            return 1.0f;
        }
        return attackerMob == null ? 1.0f : attackerMob.buffManager.getModifier(BuffModifiers.BUILDING_SPEED).floatValue();
    }

    @Override
    public int getAttackAnimTime(InventoryItem item, ItemAttackerMob attackerMob) {
        return Math.max(super.getAttackAnimTime(item, attackerMob), 50);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        if (this.isQueen) {
            tooltips.add(Localization.translate("itemtooltip", "queenbeetip"), 300);
        } else {
            tooltips.add(Localization.translate("itemtooltip", "honeybeetip"), 300);
        }
        return tooltips;
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
        if (!this.isInPlaceRange(level, tileX * 32 + 16, tileY * 32 + 16, player, playerPositionLine, item)) {
            return "outofrange";
        }
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        if (!(objectEntity instanceof AbstractBeeHiveObjectEntity)) {
            return "notapiary";
        }
        AbstractBeeHiveObjectEntity apiary = (AbstractBeeHiveObjectEntity)objectEntity;
        if (this.isQueen) {
            if (apiary.hasQueen()) {
                return "hasqueen";
            }
        } else if (!apiary.canAddWorkerBee()) {
            return "maxbees";
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
            if (level.isServer()) {
                ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
                if (objectEntity instanceof AbstractBeeHiveObjectEntity) {
                    AbstractBeeHiveObjectEntity apiary = (AbstractBeeHiveObjectEntity)objectEntity;
                    if (this.isQueen) {
                        apiary.addQueen();
                    } else {
                        apiary.addWorkerBee();
                    }
                }
            } else {
                SoundManager.playSound(GameResources.pop, (SoundEffect)SoundEffect.effect(tileX * 32 + 16, tileY * 32 + 16).pitch(0.6f).volume(0.7f));
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
                int tileY;
                Level level = this.attackerMob.getLevel();
                PlayerMob player = (PlayerMob)this.attackerMob;
                ServerClient client = player.getServerClient();
                int tileX = GameMath.getTileCoordinate(placePosition.placeX);
                AbstractBeeHiveObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY = GameMath.getTileCoordinate(placePosition.placeY), AbstractBeeHiveObjectEntity.class);
                if (objectEntity != null) {
                    client.sendPacket(new PacketObjectEntity(objectEntity));
                } else {
                    client.sendPacket(new PacketChangeObjects(level, tileX, tileY));
                }
            }
        });
        return slot.getItem();
    }
}

