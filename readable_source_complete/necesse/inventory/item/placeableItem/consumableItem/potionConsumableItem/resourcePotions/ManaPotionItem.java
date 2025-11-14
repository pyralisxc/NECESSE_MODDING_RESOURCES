/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.resourcePotions;

import java.awt.geom.Line2D;
import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.ComparableSequence;
import necesse.engine.util.GameBlackboard;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobManaChangeEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.ItemUsed;
import necesse.inventory.item.placeableItem.consumableItem.potionConsumableItem.PotionConsumableItem;
import necesse.level.maps.Level;

public class ManaPotionItem
extends PotionConsumableItem {
    public int restoreMana;

    public ManaPotionItem(Item.Rarity rarity, int restoreMana) {
        super(250, "manapotionfatigue", 30);
        this.rarity = rarity;
        this.restoreMana = restoreMana;
        this.obeysBuffPotionPolicy = false;
        this.setItemCategory(ItemCategory.craftingManager, "consumable", "resourcepotions");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "manaflatpot", "mana", (Object)this.restoreMana));
        tooltips.add(Localization.translate("itemtooltip", "manapottip", "key", TypeParsers.getInputParseString(Control.MANA_POT)));
        return tooltips;
    }

    @Override
    public InventoryItem onPlace(Level level, int x, int y, PlayerMob player, int seed, InventoryItem item, GNDItemMap mapContent) {
        InventoryItem out = super.onPlace(level, x, y, player, seed, item, mapContent);
        float manaChange = this.restoreMana;
        player.isManaExhausted = false;
        if (player.buffManager.hasBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION)) {
            player.buffManager.removeBuff(BuffRegistry.Debuffs.MANA_EXHAUSTION, false);
        }
        if (level.isServer()) {
            MobManaChangeEvent event = new MobManaChangeEvent((Mob)player, manaChange);
            level.entityManager.events.add(event);
        }
        return out;
    }

    @Override
    public String canPlace(Level level, int x, int y, PlayerMob player, Line2D playerPositionLine, InventoryItem item, GNDItemMap mapContent) {
        if (player.getMana() >= (float)player.getMaxMana()) {
            return "fullmana";
        }
        return super.canPlace(level, x, y, player, playerPositionLine, item, mapContent);
    }

    @Override
    public boolean canAndShouldPartyConsume(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item, String purpose) {
        if (!super.canAndShouldPartyConsume(level, mob, partyClient, item, purpose)) {
            return false;
        }
        float manaPercent = mob.getMana() / (float)mob.getMaxMana();
        return mob.getMana() <= (float)(mob.getMaxMana() - this.restoreMana) && manaPercent <= 0.75f || manaPercent <= 0.1f;
    }

    @Override
    public InventoryItem onPartyConsume(Level level, HumanMob mob, ServerClient partyClient, InventoryItem item, String purpose) {
        InventoryItem out = super.onPartyConsume(level, mob, partyClient, item, purpose);
        if (level.isServer()) {
            MobManaChangeEvent event = new MobManaChangeEvent((Mob)mob, this.restoreMana);
            level.entityManager.events.add(event);
        }
        return out;
    }

    @Override
    public ComparableSequence<Integer> getPartyPriority(Level level, HumanMob mob, ServerClient partyClient, Inventory inventory, int inventorySlot, InventoryItem item, String purpose) {
        return super.getPartyPriority(level, mob, partyClient, inventory, inventorySlot, item, purpose).beforeBy(-this.restoreMana);
    }

    @Override
    public ComparableSequence<Integer> getInventoryPriority(Level level, PlayerMob player, Inventory inventory, int inventorySlot, InventoryItem item, String purpose) {
        if (purpose.equals("usemanapotion")) {
            return super.getInventoryPriority(level, player, inventory, inventorySlot, item, purpose).beforeBy(-this.restoreMana);
        }
        return super.getInventoryPriority(level, player, inventory, inventorySlot, item, purpose);
    }

    @Override
    public ItemUsed useManaPotion(Level level, PlayerMob player, int seed, InventoryItem item) {
        String error = this.canPlace(level, 0, 0, player, null, item, null);
        if (error == null) {
            return new ItemUsed(true, this.onPlace(level, 0, 0, player, seed, item, null));
        }
        return new ItemUsed(false, this.onAttemptPlace(level, 0, 0, player, item, null, error));
    }

    @Override
    public ItemUsed useBuffPotion(Level level, PlayerMob player, int seed, InventoryItem item) {
        return new ItemUsed(false, item);
    }
}

