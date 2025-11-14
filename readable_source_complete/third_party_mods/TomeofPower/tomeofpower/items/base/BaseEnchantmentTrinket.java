/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.modifiers.Modifier
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.engine.modifiers.ModifierList
 *  necesse.engine.network.gameNetworkData.GNDItem
 *  necesse.engine.network.gameNetworkData.GNDItemInventory
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.network.packet.PacketOpenContainer
 *  necesse.engine.network.server.ServerClient
 *  necesse.engine.registries.ContainerRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameMath
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.Inventory
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.PlayerInventorySlot
 *  necesse.inventory.container.Container
 *  necesse.inventory.container.ContainerActionResult
 *  necesse.inventory.container.item.ItemInventoryContainer
 *  necesse.inventory.container.slots.ContainerSlot
 *  necesse.inventory.enchants.ItemEnchantment
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.miscItem.EnchantingScrollItem
 *  necesse.inventory.item.miscItem.InternalInventoryItemInterface
 *  necesse.inventory.item.trinketItem.TrinketItem
 */
package tomeofpower.items.base;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierList;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.ItemInventoryContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.EnchantingScrollItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.inventory.item.trinketItem.TrinketItem;
import tomeofpower.registry.TomeOfPowerContainers;
import tomeofpower.util.TomeLogger;

public abstract class BaseEnchantmentTrinket
extends TrinketItem
implements InternalInventoryItemInterface {
    protected int tooltipsNumber = 0;
    protected int inventorySize = 9;
    private static final String[] MODIFIER_CATEGORIES = new String[]{"buffmodifiers", "currentmodifiers", "bufftooltip", "nomodifiers"};
    private static volatile long lastAphTooltipLog = 0L;

    public BaseEnchantmentTrinket(Item.Rarity rarity, int enchantCost, int inventorySize) {
        super(rarity, enchantCost, null);
        this.inventorySize = inventorySize;
    }

    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            PlayerInventorySlot playerSlot = null;
            if (slot.getInventory() == container.getClient().playerMob.getInv().main) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().main, slot.getInventorySlot());
            }
            if (slot.getInventory() == container.getClient().playerMob.getInv().cloud) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().cloud, slot.getInventorySlot());
            }
            if (playerSlot != null) {
                if (container.getClient().isServer()) {
                    ServerClient client = container.getClient().getServerClient();
                    this.openContainer(client, playerSlot);
                }
                return new ContainerActionResult(-1002911334);
            }
            return new ContainerActionResult(208675834, Localization.translate((String)"itemtooltip", (String)"rclickinvopenerror"));
        };
    }

    protected void openContainer(ServerClient client, PlayerInventorySlot inventorySlot) {
        PacketOpenContainer packet = new PacketOpenContainer(TomeOfPowerContainers.TRINKET_INVENTORY_CONTAINER, ItemInventoryContainer.getContainerContent((InternalInventoryItemInterface)this, (PlayerInventorySlot)inventorySlot));
        ContainerRegistry.openAndSendContainer((ServerClient)client, (PacketOpenContainer)packet);
    }

    public int getInternalInventorySize() {
        return this.inventorySize;
    }

    public Inventory getInternalInventory(InventoryItem item) {
        GNDItemInventory inventoryData;
        GNDItemMap gndData = item.getGndData();
        if (gndData == null) {
            gndData = new GNDItemMap();
            item.setGndData(gndData);
        }
        if ((inventoryData = (GNDItemInventory)gndData.getItem("inventory")) != null) {
            Inventory inventory = inventoryData.inventory;
            if (inventory.getSize() != this.getInternalInventorySize()) {
                inventory.changeSize(this.getInternalInventorySize());
            }
            return inventory;
        }
        Inventory newInventory = this.getNewInternalInventory(item);
        GNDItemInventory newInventoryData = new GNDItemInventory(newInventory);
        gndData.setItem("inventory", (GNDItem)newInventoryData);
        return newInventory;
    }

    public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        GNDItemMap gndData = item.getGndData();
        if (gndData == null) {
            gndData = new GNDItemMap();
            item.setGndData(gndData);
        }
        gndData.setItem("inventory", (GNDItem)new GNDItemInventory(inventory));
    }

    public Inventory getNewInternalInventory(InventoryItem item) {
        Inventory inventory = new Inventory(this.inventorySize);
        for (int i = 0; i < this.inventorySize; ++i) {
            inventory.setItem(i, null);
        }
        return inventory;
    }

    public boolean isValidItem(InventoryItem item) {
        return item == null || item.item instanceof EnchantingScrollItem;
    }

    public boolean canChangePouchName() {
        return false;
    }

    public String getInventoryRightClickControlTip(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return Localization.translate((String)"controls", (String)"opentip");
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)this.getTooltipKey()));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"rclickinvopentip"));
        String combinedBonuses = this.getCombinedBonusesDisplay(item);
        if (!combinedBonuses.isEmpty()) {
            String[] bonusLines;
            tooltips.add("\u00a7bCombined Bonuses:");
            for (String bonusLine : bonusLines = combinedBonuses.split("\n")) {
                if (bonusLine.trim().isEmpty()) continue;
                tooltips.add("  " + bonusLine);
            }
        }
        return tooltips;
    }

    private static String translateModifier(String key, String valueStr) {
        for (String category : MODIFIER_CATEGORIES) {
            LocalMessage message = new LocalMessage(category, key, "mod", valueStr);
            String translated = message.translate();
            if (translated == null || translated.contains(category + "." + key)) continue;
            return translated;
        }
        return key + ": " + valueStr;
    }

    public static String getModifierText(String funcName, Object currentValue, Object defaultValue, String localisationKey) {
        if (funcName == null || currentValue == null || defaultValue == null) {
            return "";
        }
        switch (funcName) {
            case "NORMAL_PERC_PARSER": {
                float v = ((Number)currentValue).floatValue() - ((Number)defaultValue).floatValue();
                return BaseEnchantmentTrinket.translateModifier(localisationKey, BaseEnchantmentTrinket.formatSigned(v * 100.0f));
            }
            case "INVERSE_PERC_PARSER": {
                float v = ((Number)currentValue).floatValue() - ((Number)defaultValue).floatValue();
                return BaseEnchantmentTrinket.translateModifier(localisationKey, BaseEnchantmentTrinket.formatSigned(v * 100.0f));
            }
            case "NORMAL_FLAT_FLOAT_PARSER": {
                float v = ((Number)currentValue).floatValue() - ((Number)defaultValue).floatValue();
                return BaseEnchantmentTrinket.translateModifier(localisationKey, BaseEnchantmentTrinket.formatSigned(v));
            }
            case "INVERSE_FLAT_FLOAT_PARSER": {
                float v = ((Number)currentValue).floatValue() - ((Number)defaultValue).floatValue();
                return BaseEnchantmentTrinket.translateModifier(localisationKey, BaseEnchantmentTrinket.formatSigned(-v));
            }
            case "NORMAL_FLAT_INT_PARSER": {
                int v = ((Number)currentValue).intValue() - ((Number)defaultValue).intValue();
                return BaseEnchantmentTrinket.translateModifier(localisationKey, BaseEnchantmentTrinket.formatSigned(v));
            }
            case "INVERSE_FLAT_INT_PARSER": {
                int v = ((Number)currentValue).intValue() - ((Number)defaultValue).intValue();
                return BaseEnchantmentTrinket.translateModifier(localisationKey, BaseEnchantmentTrinket.formatSigned(-v));
            }
            case "BAD_PERCENT_MODIFIER": {
                float v = ((Number)currentValue).floatValue() - ((Number)defaultValue).floatValue();
                return BaseEnchantmentTrinket.translateModifier(localisationKey, "-" + BaseEnchantmentTrinket.formatSigned(v * 100.0f));
            }
            case "LESS_GOOD_PERCENT_MODIFIER": {
                float val = ((Number)currentValue).floatValue();
                return BaseEnchantmentTrinket.translateModifier(localisationKey, BaseEnchantmentTrinket.formatSigned(val * 100.0f));
            }
        }
        return BaseEnchantmentTrinket.translateModifier(localisationKey, String.valueOf(currentValue));
    }

    private static String formatSigned(float v) {
        return (v > 0.0f ? "+" : "") + GameMath.removeDecimalIfZero((float)v);
    }

    private static String formatSigned(int v) {
        return (v > 0 ? "+" : "") + v;
    }

    protected String getCombinedBonusesDisplay(InventoryItem item) {
        String formattedText;
        int i;
        Inventory inventory = this.getInternalInventory(item);
        if (inventory == null) {
            return "";
        }
        HashMap<String, Float> floatBonuses = new HashMap<String, Float>();
        HashMap<String, Integer> intBonuses = new HashMap<String, Integer>();
        int inventorySize = inventory.getSize();
        InventoryItem[] inventorySnapshot = new InventoryItem[inventorySize];
        for (i = 0; i < inventorySize; ++i) {
            inventorySnapshot[i] = inventory.getItem(i);
        }
        for (i = 0; i < inventorySnapshot.length; ++i) {
            EnchantingScrollItem scroll;
            ItemEnchantment enchantment;
            InventoryItem scrollItem = inventorySnapshot[i];
            if (scrollItem == null || scrollItem.item == null || !(scrollItem.item instanceof EnchantingScrollItem) || (enchantment = (scroll = (EnchantingScrollItem)scrollItem.item).getEnchantment(scrollItem)) == null) continue;
            int stackSize = scrollItem.getAmount();
            try {
                Field listField = ModifierContainer.class.getDeclaredField("list");
                listField.setAccessible(true);
                ModifierList modifierList = (ModifierList)listField.get(enchantment);
                if (modifierList == null) {
                    TomeLogger.warn("Null modifier list for enchantment in tooltip: " + enchantment.getClass().getSimpleName());
                    continue;
                }
                TomeLogger.debug("Processing modifier list for tooltip: " + modifierList);
                for (Modifier modifier_bad : modifierList) {
                    Modifier<?> modifier = TomeOfPowerContainers.remap(modifier_bad);
                    if (!TomeOfPowerContainers.Modifier_include(modifier_bad).booleanValue() || !TomeOfPowerContainers.Modifier_include(modifier).booleanValue()) continue;
                    try {
                        Object value = enchantment.getModifier(modifier_bad);
                        if (value == null) continue;
                        Object defaultValue = modifier.defaultBuffValue;
                        if (defaultValue == null) {
                            TomeLogger.debug("Null defaultBuffValue for modifier in tooltip: " + modifier.stringID);
                            continue;
                        }
                        Class<?> expectedType = defaultValue.getClass();
                        if (expectedType == Integer.class) {
                            int ix = ((Number)value).intValue();
                            if (ix == 0) continue;
                            this.addIntModifierToMapReflection(intBonuses, modifier.stringID, value, stackSize);
                            continue;
                        }
                        if (expectedType == Float.class) {
                            float f = ((Number)value).floatValue();
                            if (f == 0.0f) continue;
                            this.addModifierToMapReflection(floatBonuses, modifier.stringID, value, stackSize);
                            continue;
                        }
                        if (expectedType != Boolean.class) continue;
                    }
                    catch (IllegalArgumentException e) {
                        LocalMessage message = new LocalMessage("buffmodifiers", modifier.stringID);
                        TomeLogger.debug("Illegal argument in modifier '" + message.translate() + "' (enchantment: " + enchantment.getClass().getSimpleName() + ", item: " + (item != null ? item.toString() : "unknown") + "): " + e.getMessage());
                    }
                    catch (Exception e) {
                        TomeLogger.debug("Unexpected error processing modifier '" + modifier.stringID + "' (enchantment: " + enchantment.getClass().getSimpleName() + ", item: " + (item != null ? item.toString() : "unknown") + "): " + e.getMessage());
                    }
                }
                continue;
            }
            catch (Exception e) {
                TomeLogger.debug("Error processing enchantment for tooltip: " + e.getMessage());
            }
        }
        try {
            long now = System.currentTimeMillis();
            if (now - lastAphTooltipLog > 10000L) {
                lastAphTooltipLog = now;
                TomeLogger.debug("Attempting to read Aphorea modifiers for tooltip");
            }
            Class<?> aphModsClass = Class.forName("aphorea.registry.AphModifiers");
            String[] aphFields = new String[]{"MAGIC_HEALING", "MAGIC_HEALING_FLAT", "MAGIC_HEALING_RECEIVED", "MAGIC_HEALING_RECEIVED_FLAT", "MAGIC_HEALING_GRACE", "TOOL_MAGIC_HEALING", "TOOL_MAGIC_HEALING_RECEIVED", "TOOL_MAGIC_HEALING_GRACE", "TOOL_AREA_RANGE", "LOYAL", "INSPIRATION_DAMAGE", "INSPIRATION_CRIT_CHANCE", "INSPIRATION_CRIT_DAMAGE", "INSPIRATION_EFFECT", "INSPIRATION_ABILITY_SPEED"};
            for (int i2 = 0; i2 < inventorySnapshot.length; ++i2) {
                EnchantingScrollItem scroll;
                ItemEnchantment enchantment;
                InventoryItem scrollItem = inventorySnapshot[i2];
                if (scrollItem == null || scrollItem.item == null || !(scrollItem.item instanceof EnchantingScrollItem) || (enchantment = (scroll = (EnchantingScrollItem)scrollItem.item).getEnchantment(scrollItem)) == null) continue;
                int stackSize = scrollItem.getAmount();
                for (String fieldName : aphFields) {
                    try {
                        Object val;
                        Field f = aphModsClass.getField(fieldName);
                        Object aphModifier = f.get(null);
                        if (aphModifier == null || (val = this.safeGetModifier(enchantment, aphModifier)) == null) continue;
                        Object displayName = fieldName;
                        if (((String)displayName).startsWith("TOOL_")) {
                            displayName = "APH_" + ((String)displayName).substring(5);
                        }
                        displayName = ((String)displayName).replace('_', ' ').toLowerCase();
                        displayName = Character.toUpperCase(((String)displayName).charAt(0)) + ((String)displayName).substring(1);
                        if (val instanceof Float && ((Float)val).floatValue() != 0.0f) {
                            floatBonuses.put((String)displayName, Float.valueOf(floatBonuses.getOrDefault(displayName, Float.valueOf(0.0f)).floatValue() + ((Float)val).floatValue() * (float)stackSize));
                            continue;
                        }
                        if (val instanceof Integer && (Integer)val != 0) {
                            intBonuses.put((String)displayName, intBonuses.getOrDefault(displayName, 0) + (Integer)val * stackSize);
                            continue;
                        }
                        if (!(val instanceof Boolean) || !((Boolean)val).booleanValue()) continue;
                        intBonuses.put((String)displayName, intBonuses.getOrDefault(displayName, 0) + 1);
                    }
                    catch (IllegalAccessException | NoSuchFieldException reflectiveOperationException) {
                        // empty catch block
                    }
                }
            }
        }
        catch (ClassNotFoundException now) {
            // empty catch block
        }
        StringBuilder display = new StringBuilder();
        TreeMap<String, String> sortedDisplay = new TreeMap<String, String>();
        for (Map.Entry entry : floatBonuses.entrySet()) {
            float value = ((Float)entry.getValue()).floatValue();
            if (value == 0.0f) continue;
            formattedText = BaseEnchantmentTrinket.getModifierText(TomeOfPowerContainers.remap_func((String)entry.getKey()), Float.valueOf(value), Float.valueOf(0.0f), (String)entry.getKey());
            sortedDisplay.put((String)entry.getKey(), formattedText);
        }
        for (Map.Entry entry : intBonuses.entrySet()) {
            int value = (Integer)entry.getValue();
            if (value == 0) continue;
            formattedText = BaseEnchantmentTrinket.getModifierText(TomeOfPowerContainers.remap_func((String)entry.getKey()), value, Float.valueOf(0.0f), (String)entry.getKey());
            sortedDisplay.put((String)entry.getKey(), formattedText);
        }
        for (String formattedText2 : sortedDisplay.values()) {
            if (display.length() > 0) {
                display.append("\n");
            }
            display.append(formattedText2);
        }
        return display.toString();
    }

    private void addModifierToMapReflection(Map<String, Float> bonuses, String name, Object value, int stackSize) {
        if (value instanceof Float && ((Float)value).floatValue() != 0.0f) {
            bonuses.put(name, Float.valueOf(bonuses.getOrDefault(name, Float.valueOf(0.0f)).floatValue() + ((Float)value).floatValue() * (float)stackSize));
        }
    }

    private void addIntModifierToMapReflection(Map<String, Integer> bonuses, String name, Object value, int stackSize) {
        if (value instanceof Integer && (Integer)value != 0) {
            bonuses.put(name, bonuses.getOrDefault(name, 0) + (Integer)value * stackSize);
        }
    }

    private Object safeGetModifier(ItemEnchantment enchantment, Modifier<?> modifier) {
        if (enchantment == null || modifier == null) {
            return null;
        }
        try {
            return enchantment.getModifier(modifier);
        }
        catch (ClassCastException | IllegalArgumentException e) {
            return null;
        }
    }

    private Object safeGetModifier(ItemEnchantment enchantment, Object modifierObj) {
        if (modifierObj instanceof Modifier) {
            return this.safeGetModifier(enchantment, (Modifier)modifierObj);
        }
        return null;
    }

    protected abstract String getTooltipKey();

    public abstract GameMessage getLocalization(InventoryItem var1);

    public abstract TrinketBuff[] getBuffs(InventoryItem var1);
}

