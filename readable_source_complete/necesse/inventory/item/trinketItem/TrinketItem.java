/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;
import necesse.engine.GlobalData;
import necesse.engine.input.Control;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemEnchantment;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuffAbility;
import necesse.entity.mobs.buffs.BuffAbility;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.entity.pickup.ItemPickupEntity;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.ContainerTransferResult;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.enchants.Enchantable;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.Level;

public abstract class TrinketItem
extends Item
implements Enchantable<EquipmentItemEnchant> {
    protected int enchantCost;
    public ArrayList<String> disabledBy = new ArrayList();
    public ArrayList<String> disables = new ArrayList();
    public ArrayList<OneOfLootItems> onRegisterLootTables = new ArrayList();

    public TrinketItem(Item.Rarity rarity, int enchantCost, OneOfLootItems lootTableCategory) {
        super(1);
        this.addToLootTable(lootTableCategory);
        this.setItemCategory("equipment", "trinkets");
        this.setItemCategory(ItemCategory.craftingManager, "equipment", "trinkets");
        this.keyWords.add("trinket");
        this.rarity = rarity;
        this.enchantCost = enchantCost;
        this.worldDrawSize = 32;
        this.incinerationTimeMillis = 30000;
    }

    public TrinketItem addToLootTable(OneOfLootItems ... lootTables) {
        if (this.idData.isSet()) {
            for (OneOfLootItems lootTable : lootTables) {
                if (lootTable == null) continue;
                lootTable.add(new LootItem(this.getStringID()));
            }
        } else {
            for (OneOfLootItems lootTable : lootTables) {
                if (lootTable == null) continue;
                this.onRegisterLootTables.add(lootTable);
            }
        }
        return this;
    }

    @Override
    public void onItemRegistryClosed() {
        super.onItemRegistryClosed();
        for (OneOfLootItems lootTable : this.onRegisterLootTables) {
            lootTable.add(new LootItem(this.getStringID()));
        }
    }

    public abstract TrinketBuff[] getBuffs(InventoryItem var1);

    public TrinketItem addDisabledBy(String ... itemStringIDs) {
        for (String itemStringID : itemStringIDs) {
            if (this.disabledBy.contains(itemStringID)) continue;
            this.disabledBy.add(itemStringID);
        }
        return this;
    }

    public TrinketItem addDisables(String ... itemStringIDs) {
        for (String itemStringID : itemStringIDs) {
            if (this.disables.contains(itemStringID)) continue;
            this.disables.add(itemStringID);
        }
        return this;
    }

    public boolean disabledBy(InventoryItem item) {
        return this.disabledBy.stream().anyMatch(s -> s.equals(item.item.getStringID()));
    }

    public boolean disables(InventoryItem item) {
        return this.disables.stream().anyMatch(s -> s.equals(item.item.getStringID()));
    }

    @Override
    public final ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(this.getPreEnchantmentTooltips(item, perspective, blackboard));
        tooltips.add(this.getEnchantmentTooltips(item));
        tooltips.add(this.getPostEnchantmentTooltips(item, perspective, blackboard));
        return tooltips;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        tooltips.add(Localization.translate("itemtooltip", this.isAbilityTrinket(item) ? "trinketabilityslot" : "trinketslot"));
        for (TrinketBuff buff : this.getBuffs(item)) {
            tooltips.addAll(buff.getTrinketTooltip(this, item, perspective));
        }
        return tooltips;
    }

    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = new ListGameTooltips();
        if (blackboard.getBoolean("isAbilitySlot") && blackboard.getBoolean("equipped") && this.isAbilityTrinket(item)) {
            tooltips.add(Localization.translate("ui", "hotkeytip", "hotkey", "[input=" + Control.TRINKET_ABILITY.id + "]"));
        }
        return tooltips;
    }

    public boolean isAbilityTrinket(InventoryItem item) {
        return Arrays.stream(this.getBuffs(item)).anyMatch(b -> b instanceof BuffAbility || b instanceof ActiveBuffAbility);
    }

    public String getInvalidInSlotError(Container container, ContainerSlot slot, InventoryItem item) {
        return null;
    }

    public void addTrinketAbilityHotkeyTooltip(ListGameTooltips tooltips, InventoryItem item) {
        tooltips.add(Localization.translate("ui", "hotkeytip", "hotkey", "[input=" + Control.TRINKET_ABILITY.id + "]"));
    }

    @Override
    public String getInventoryRightClickControlTip(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        if (slotIndex == container.CLIENT_TRINKET_ABILITY_SLOT || slotIndex >= container.CLIENT_TRINKET_START && slotIndex <= container.CLIENT_TRINKET_END) {
            return Localization.translate("controls", "removetip");
        }
        return Localization.translate("controls", "equiptip");
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            if (this.isAbilityTrinket(item) && slotIndex != container.CLIENT_TRINKET_ABILITY_SLOT) {
                container.getSlot(container.CLIENT_TRINKET_ABILITY_SLOT).swapItems(slot);
                return new ContainerActionResult(-2081175478);
            }
            ArrayList<ContainerSlot> slots = new ArrayList<ContainerSlot>();
            slots.add(container.getSlot(container.CLIENT_TRINKET_ABILITY_SLOT));
            for (int i = container.CLIENT_TRINKET_START; i <= container.CLIENT_TRINKET_END; ++i) {
                slots.add(container.getSlot(i));
            }
            if (slots.stream().anyMatch(s -> s != null && s.getContainerIndex() == slotIndex)) {
                ContainerTransferResult result = container.transferToSlots(slot, Arrays.asList(new SlotIndexRange(container.CLIENT_HOTBAR_START, container.CLIENT_HOTBAR_END), new SlotIndexRange(container.CLIENT_INVENTORY_START, container.CLIENT_INVENTORY_END)));
                return new ContainerActionResult(-455167603, result.error);
            }
            ContainerSlot replaceSlot = null;
            for (ContainerSlot cSlot : slots) {
                if (cSlot.getItemInvalidError(item) != null || cSlot.isClear()) continue;
                InventoryItem slotItem = cSlot.getItem();
                if (slotItem.item instanceof TrinketItem) {
                    TrinketItem slotTrinket = (TrinketItem)slotItem.item;
                    TrinketItem thisTrinket = (TrinketItem)item.item;
                    if (slotTrinket.getID() != thisTrinket.getID() && !slotTrinket.disables(item) && !slotTrinket.disabledBy(item) && !thisTrinket.disables(slotItem) && !thisTrinket.disabledBy(slotItem)) continue;
                    replaceSlot = cSlot;
                    break;
                }
                replaceSlot = cSlot;
                break;
            }
            if (replaceSlot == null) {
                replaceSlot = slots.stream().filter(cs -> cs.getItemInvalidError(item) == null).filter(ContainerSlot::isClear).findFirst().orElseGet(() -> slots.stream().filter(cs -> cs.getItemInvalidError(item) == null).findFirst().orElse(null));
            }
            if (replaceSlot != null) {
                ItemCombineResult result = replaceSlot.swapItems(slot);
                return new ContainerActionResult(417568726 + replaceSlot.getContainerIndex(), result.error);
            }
            return new ContainerActionResult(417568726);
        };
    }

    @Override
    public float getSinkingRate(ItemPickupEntity entity, float currentSinking) {
        return super.getSinkingRate(entity, currentSinking) / 5.0f;
    }

    @Override
    public boolean canCombineItem(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        if (!super.canCombineItem(level, player, me, them, purpose)) {
            return false;
        }
        return this.isSameGNDData(level, me, them, purpose);
    }

    @Override
    public boolean isSameGNDData(Level level, InventoryItem me, InventoryItem them, String purpose) {
        return me.getGndData().sameKeys(them.getGndData(), "enchantment");
    }

    @Override
    public boolean isEnchantable(InventoryItem item) {
        return true;
    }

    @Override
    public void setEnchantment(InventoryItem item, int enchantment) {
        item.getGndData().setItem("enchantment", (GNDItem)new GNDItemEnchantment(enchantment));
    }

    @Override
    public int getEnchantmentID(InventoryItem item) {
        GNDItem enchantment = item.getGndData().getItem("enchantment");
        GNDItemEnchantment enchantmentItem = GNDItemEnchantment.convertEnchantmentID(enchantment);
        item.getGndData().setItem("enchantment", (GNDItem)enchantmentItem);
        return enchantmentItem.getRegistryID();
    }

    @Override
    public void clearEnchantment(InventoryItem item) {
        item.getGndData().setItem("enchantment", null);
    }

    @Override
    public EquipmentItemEnchant getRandomEnchantment(GameRandom random, InventoryItem item) {
        return Enchantable.getRandomEnchantment(random, EnchantmentRegistry.equipmentEnchantments, this.getEnchantmentID(item), EquipmentItemEnchant.class);
    }

    @Override
    public boolean isValidEnchantment(InventoryItem item, ItemEnchantment enchantment) {
        return EnchantmentRegistry.equipmentEnchantments.contains(enchantment.getID());
    }

    @Override
    public int getEnchantCost(InventoryItem item) {
        return this.enchantCost;
    }

    @Override
    public Set<Integer> getValidEnchantmentIDs(InventoryItem item) {
        return EnchantmentRegistry.equipmentEnchantments;
    }

    @Override
    public EquipmentItemEnchant getEnchantment(InventoryItem item) {
        return EnchantmentRegistry.getEnchantment(this.getEnchantmentID(item), EquipmentItemEnchant.class, EquipmentItemEnchant.noEnchant);
    }

    @Override
    public GameTooltips getEnchantmentTooltips(InventoryItem item) {
        if (this.getEnchantmentID(item) > 0) {
            ListGameTooltips tooltips = new ListGameTooltips(this.getEnchantment(item).getTooltips());
            if (GlobalData.debugActive()) {
                tooltips.addFirst("Enchantment id " + this.getEnchantmentID(item));
            }
            return tooltips;
        }
        return new StringTooltips();
    }

    @Override
    public float getBrokerValue(InventoryItem item) {
        return super.getBrokerValue(item) * this.getEnchantment(item).getEnchantCostMod();
    }

    @Override
    public GameMessage getLocalization(InventoryItem item) {
        EquipmentItemEnchant enchant = this.getEnchantment(item);
        if (enchant != null && enchant.getID() > 0) {
            return new LocalMessage("enchantment", "format", "enchantment", enchant.getLocalization(), "item", super.getLocalization(item));
        }
        return super.getLocalization(item);
    }

    @Override
    public InventoryItem getDefaultLootItem(GameRandom random, int amount) {
        InventoryItem item = super.getDefaultLootItem(random, amount);
        if (this.isEnchantable(item) && random.getChance(0.65f)) {
            ((Enchantable)((Object)item.item)).addRandomEnchantment(item, random);
        }
        return item;
    }

    @Override
    public String getTranslatedTypeName() {
        return Localization.translate("item", "trinket");
    }
}

