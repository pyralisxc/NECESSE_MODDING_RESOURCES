/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.Modifier
 *  necesse.engine.modifiers.ModifierContainer
 *  necesse.engine.modifiers.ModifierList
 *  necesse.engine.registries.ItemRegistry
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.inventory.Inventory
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.PlayerEquipmentInventory
 *  necesse.inventory.PlayerInventoryManager
 *  necesse.inventory.enchants.ItemEnchantment
 *  necesse.inventory.item.miscItem.EnchantingScrollItem
 *  necesse.inventory.item.miscItem.InternalInventoryItemInterface
 */
package tomeofpower.buffs;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierContainer;
import necesse.engine.modifiers.ModifierList;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerEquipmentInventory;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.item.miscItem.EnchantingScrollItem;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import tomeofpower.registry.TomeOfPowerContainers;
import tomeofpower.util.TomeLogger;

public class TomeOfPowerBuff
extends TrinketBuff {
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
        InventoryItem trinketItem;
        if (buff.owner != null && (trinketItem = this.findTomeOfPowerInEquipment(buff.owner)) != null && trinketItem.item instanceof InternalInventoryItemInterface) {
            try {
                this.applyEnchantmentEffects(buff, trinketItem);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private InventoryItem findTomeOfPowerInEquipment(Mob owner) {
        if (owner instanceof PlayerMob) {
            PlayerMob player = (PlayerMob)owner;
            PlayerInventoryManager inv = player.getInv();
            PlayerEquipmentInventory trinketInventory = inv.equipment.getSelectedTrinketsInventory();
            for (int i = 0; i < trinketInventory.getSize(); ++i) {
                InventoryItem item = trinketInventory.getItem(i);
                if (item == null || item.item.getID() != ItemRegistry.getItemID((String)"tomeofpower")) continue;
                return item;
            }
        }
        return null;
    }

    /*
     * WARNING - void declaration
     */
    private void applyEnchantmentEffects(ActiveBuff buff, InventoryItem trinketItem) {
        block65: {
            block64: {
                Class<?> aphModsClass;
                HashMap<ItemEnchantment, Integer> enchantmentCounts;
                block63: {
                    Inventory inventory = null;
                    if (trinketItem.item instanceof InternalInventoryItemInterface) {
                        InternalInventoryItemInterface internalInv = (InternalInventoryItemInterface)trinketItem.item;
                        inventory = internalInv.getInternalInventory(trinketItem);
                    }
                    if (inventory == null) {
                        return;
                    }
                    int inventorySize = inventory.getSize();
                    InventoryItem[] inventorySnapshot = new InventoryItem[inventorySize];
                    for (int i = 0; i < inventorySize; ++i) {
                        inventorySnapshot[i] = inventory.getItem(i);
                    }
                    enchantmentCounts = new HashMap<ItemEnchantment, Integer>();
                    for (int i = 0; i < inventorySnapshot.length; ++i) {
                        EnchantingScrollItem enchantingScrollItem;
                        ItemEnchantment enchantment;
                        InventoryItem scrollItem = inventorySnapshot[i];
                        if (scrollItem == null || scrollItem.item == null || !(scrollItem.item instanceof EnchantingScrollItem) || (enchantment = (enchantingScrollItem = (EnchantingScrollItem)scrollItem.item).getEnchantment(scrollItem)) == null) continue;
                        int stackSize = scrollItem.getAmount();
                        enchantmentCounts.put(enchantment, enchantmentCounts.getOrDefault(enchantment, 0) + stackSize);
                    }
                    aphModsClass = null;
                    try {
                        aphModsClass = Class.forName("aphorea.registry.AphModifiers");
                    }
                    catch (ClassNotFoundException scrollItem) {
                        // empty catch block
                    }
                    try {
                        if (aphModsClass == null || buff.owner == null) break block63;
                        try {
                            Field loyalField = aphModsClass.getField("LOYAL");
                            Object object = loyalField.get(null);
                            if (object != null) {
                                boolean foundTrue = false;
                                for (ItemEnchantment ie : enchantmentCounts.keySet()) {
                                    try {
                                        Object v = ie.getModifier((Modifier)object);
                                        if (!(v instanceof Boolean) || !((Boolean)v).booleanValue()) continue;
                                        foundTrue = true;
                                        break;
                                    }
                                    catch (Throwable v) {
                                    }
                                }
                                if (foundTrue) {
                                    TomeLogger.warn("Loyal enchantments detected - these do not work with the Tome of Power trinket");
                                }
                            }
                        }
                        catch (ClassCastException | IllegalAccessException | NoSuchFieldException loyalField) {}
                    }
                    catch (Throwable loyalField) {
                        // empty catch block
                    }
                }
                for (Map.Entry noSuchMethodException : enchantmentCounts.entrySet()) {
                    try {
                        this.applyEnchantmentToBuffer(buff, (ItemEnchantment)noSuchMethodException.getKey(), (Integer)noSuchMethodException.getValue(), aphModsClass);
                    }
                    catch (Exception foundTrue) {}
                }
                try {
                    void var9_25;
                    String ownerId = buff.owner != null ? buff.owner.getStringID() : "null";
                    Object var9_23 = null;
                    Float crit = null;
                    Float projVel = null;
                    String[] manaUsage = null;
                    Float mining = null;
                    Integer armorPen = null;
                    try {
                        Float f = (Float)buff.getModifier(BuffModifiers.ALL_DAMAGE);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    try {
                        crit = (Float)buff.getModifier(BuffModifiers.CRIT_CHANCE);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    try {
                        projVel = (Float)buff.getModifier(BuffModifiers.PROJECTILE_VELOCITY);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    try {
                        manaUsage = (String[])buff.getModifier(BuffModifiers.MANA_USAGE);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    try {
                        mining = (Float)buff.getModifier(BuffModifiers.MINING_SPEED);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    try {
                        armorPen = (Integer)buff.getModifier(BuffModifiers.ARMOR_PEN_FLAT);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    System.out.println(String.format("[TomeOfPowerBuff] Applied summary for owner=%s ALL_DAMAGE=%s CRIT=%s ARMOR_PEN=%s PROJ_VEL=%s MANA=%s MINING=%s", ownerId, var9_25 != null ? String.format("%.4f", var9_25) : "0", crit != null ? String.format("%.4f", crit) : "0", armorPen != null ? armorPen.toString() : "0", projVel != null ? String.format("%.4f", projVel) : "0", manaUsage != null ? String.format("%.4f", new Object[]{manaUsage}) : "0", mining != null ? String.format("%.4f", mining) : "0"));
                    try {
                        Float toolDamage = null;
                        Float attackSpeed = null;
                        Float summonsSpeed = null;
                        Float targetRange = null;
                        try {
                            toolDamage = (Float)buff.getModifier(BuffModifiers.TOOL_DAMAGE);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        try {
                            attackSpeed = (Float)buff.getModifier(BuffModifiers.ATTACK_SPEED);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        try {
                            summonsSpeed = (Float)buff.getModifier(BuffModifiers.SUMMONS_SPEED);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        try {
                            targetRange = (Float)buff.getModifier(BuffModifiers.TARGET_RANGE);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                        System.out.println(String.format("[TomeOfPowerBuff] Applied extras TOOL_DAMAGE=%s ATTACK_SPEED=%s SUMMONS_SPEED=%s TARGET_RANGE=%s", toolDamage != null ? String.format("%.4f", toolDamage) : "0", attackSpeed != null ? String.format("%.4f", attackSpeed) : "0", summonsSpeed != null ? String.format("%.4f", summonsSpeed) : "0", targetRange != null ? String.format("%.4f", targetRange) : "0"));
                        if (buff.owner == null) break block64;
                        try {
                            Method getMod = buff.owner.getClass().getMethod("getModifier", Modifier.class);
                            Object td = getMod.invoke(buff.owner, BuffModifiers.TOOL_DAMAGE);
                            Object at = getMod.invoke(buff.owner, BuffModifiers.ATTACK_SPEED);
                            Object ss = getMod.invoke(buff.owner, BuffModifiers.SUMMONS_SPEED);
                            Object tr = getMod.invoke(buff.owner, BuffModifiers.TARGET_RANGE);
                            System.out.println(String.format("[TomeOfPowerBuff] owner.getModifier: TOOL_DAMAGE=%s ATTACK_SPEED=%s SUMMONS_SPEED=%s TARGET_RANGE=%s", String.valueOf(td), String.valueOf(at), String.valueOf(ss), String.valueOf(tr)));
                        }
                        catch (NoSuchMethodException noSuchMethodException) {
                        }
                    }
                    catch (Exception toolDamage) {}
                }
                catch (Exception ownerId) {
                    // empty catch block
                }
            }
            try {
                if (buff.owner == null) break block65;
                Mob owner = buff.owner;
                try {
                    Method method = owner.getClass().getMethod("updateBuffs", new Class[0]);
                    method.invoke(owner, new Object[0]);
                    System.out.println("[TomeOfPowerBuff] Invoked owner.updateBuffs()");
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
                try {
                    Method method = owner.getClass().getMethod("forceUpdateBuffs", new Class[0]);
                    method.invoke(owner, new Object[0]);
                    System.out.println("[TomeOfPowerBuff] Invoked owner.forceUpdateBuffs()");
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
                try {
                    Field field = owner.getClass().getField("buffManager");
                    Object bm = field.get(owner);
                    if (bm != null) {
                        String[] names;
                        for (String name : names = new String[]{"updateBuffs", "forceUpdateBuffs", "recalculate", "recalculateModifiers", "update"}) {
                            try {
                                Method mm = bm.getClass().getMethod(name, new Class[0]);
                                mm.invoke(bm, new Object[0]);
                                System.out.println("[TomeOfPowerBuff] Invoked buffManager." + name + "()");
                            }
                            catch (NoSuchMethodException noSuchMethodException) {
                                // empty catch block
                            }
                        }
                    }
                }
                catch (IllegalAccessException | NoSuchFieldException reflectiveOperationException) {
                    // empty catch block
                }
                try {
                    Method method = owner.getClass().getMethod("getModifier", Modifier.class);
                    Object val = method.invoke(owner, BuffModifiers.ALL_DAMAGE);
                    System.out.println("[TomeOfPowerBuff] owner.getModifier(ALL_DAMAGE) => " + val);
                }
                catch (NoSuchMethodException noSuchMethodException) {}
            }
            catch (Exception e) {
                System.out.println("[TomeOfPowerBuff] Error invoking owner/buffManager update methods: " + e);
            }
        }
        try {
            String[] buffRefreshNames;
            for (String name : buffRefreshNames = new String[]{"forceManagerUpdate", "forceUpdate", "updateManager", "recalculate", "recalculateModifiers", "update"}) {
                try {
                    Method bm = buff.getClass().getMethod(name, new Class[0]);
                    bm.invoke(buff, new Object[0]);
                    System.out.println("[TomeOfPowerBuff] Invoked buff." + name + "()");
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
        }
        catch (Exception e) {
            System.out.println("[TomeOfPowerBuff] Error invoking ActiveBuff refresh methods: " + e);
        }
    }

    private void applyEnchantmentToBuffer(ActiveBuff buff, ItemEnchantment enchantment, int stackSize, Class<?> aphModsClass) {
        block10: {
            TomeLogger.debug("Applying enchantment to buff for owner=" + (buff.owner != null ? buff.owner.getStringID() : "null") + " stacks=" + stackSize);
            Modifier<?> modifier_active = null;
            try {
                Field listField = ModifierContainer.class.getDeclaredField("list");
                listField.setAccessible(true);
                ModifierList modifierList = (ModifierList)listField.get(enchantment);
                TomeLogger.debug("Processing modifier list: " + modifierList);
                for (Modifier modifier_bad : modifierList) {
                    try {
                        modifier_active = TomeOfPowerContainers.remap(modifier_bad);
                        Object value = enchantment.getModifier(modifier_bad);
                        if (!TomeOfPowerContainers.Modifier_include(modifier_bad).booleanValue() || !TomeOfPowerContainers.Modifier_include(modifier_active).booleanValue() || value == null) continue;
                        Object defaultValue = modifier_active.defaultBuffValue;
                        if (defaultValue == null) {
                            TomeLogger.debug("Null defaultBuffValue for modifier: " + modifier_active.stringID);
                            continue;
                        }
                        Class<?> expectedType = defaultValue.getClass();
                        if (expectedType == Float.class) {
                            float f = ((Number)value).floatValue();
                            if (f == 0.0f) continue;
                            Modifier<?> floatMod = modifier_active;
                            buff.addModifier(floatMod, (Object)Float.valueOf(f * (float)stackSize));
                            TomeLogger.debug("Applied " + modifier_active.stringID + " = " + f * (float)stackSize);
                            continue;
                        }
                        if (expectedType == Integer.class) {
                            int i = ((Number)value).intValue();
                            if (i == 0) continue;
                            Modifier<?> intMod = modifier_active;
                            buff.addModifier(intMod, (Object)(i * stackSize));
                            TomeLogger.debug("Applied " + modifier_active.stringID + " = " + i * stackSize);
                            continue;
                        }
                        if (expectedType == Boolean.class) {
                            Modifier<?> boolMod = modifier_active;
                            buff.addModifier(boolMod, (Object)((Boolean)value));
                            TomeLogger.debug("Applied " + modifier_active.stringID + " = " + value);
                            continue;
                        }
                        TomeLogger.warn("Unknown modifier type: " + expectedType.getSimpleName() + " (" + modifier_active.stringID + ")");
                    }
                    catch (Exception e) {
                        TomeLogger.debug("Error applying modifier " + (modifier_active != null ? modifier_active.stringID : "unknown") + ": " + e.getMessage());
                    }
                }
            }
            catch (Exception e) {
                TomeLogger.error("Error in reflection-based modifier application: " + e.getMessage());
                if (modifier_active == null) break block10;
                TomeLogger.debug("Last modifier attempted: " + modifier_active.stringID);
            }
        }
        if (aphModsClass != null) {
            this.applyAphoreaCompatibilityModifiers(buff, enchantment, stackSize, aphModsClass);
        }
    }

    private void applyAphoreaCompatibilityModifiers(ActiveBuff buff, ItemEnchantment enchantment, int stackSize, Class<?> aphModsClass) {
        String[] aphFields;
        TomeLogger.debug("Applying Aphorea compatibility modifiers");
        for (String fieldName : aphFields = new String[]{"MAGIC_HEALING", "MAGIC_HEALING_FLAT", "MAGIC_HEALING_RECEIVED", "MAGIC_HEALING_RECEIVED_FLAT", "MAGIC_HEALING_GRACE", "TOOL_MAGIC_HEALING", "TOOL_MAGIC_HEALING_RECEIVED", "TOOL_MAGIC_HEALING_GRACE", "TOOL_AREA_RANGE", "LOYAL", "INSPIRATION_DAMAGE", "INSPIRATION_CRIT_CHANCE", "INSPIRATION_CRIT_DAMAGE", "INSPIRATION_EFFECT", "INSPIRATION_ABILITY_SPEED"}) {
            try {
                Field f = aphModsClass.getField(fieldName);
                Object aphModifier = f.get(null);
                if (aphModifier == null) continue;
                Object val = null;
                try {
                    val = enchantment.getModifier((Modifier)aphModifier);
                }
                catch (Throwable t) {
                    continue;
                }
                if (val == null) continue;
                try {
                    Field listField = aphModifier.getClass().getField("list");
                    Object modifierList = listField.get(aphModifier);
                    if (modifierList == BuffModifiers.LIST) {
                        Modifier mod;
                        if (val instanceof Float && ((Float)val).floatValue() != 0.0f) {
                            mod = (Modifier)aphModifier;
                            buff.addModifier(mod, (Object)Float.valueOf(((Float)val).floatValue() * (float)stackSize));
                            TomeLogger.debug("Applied Aphorea BuffModifier " + fieldName + " = " + ((Float)val).floatValue() * (float)stackSize);
                            continue;
                        }
                        if (val instanceof Integer && (Integer)val != 0) {
                            mod = (Modifier)aphModifier;
                            buff.addModifier(mod, (Object)((Integer)val * stackSize));
                            TomeLogger.debug("Applied Aphorea BuffModifier " + fieldName + " = " + (Integer)val * stackSize);
                            continue;
                        }
                        if (!(val instanceof Boolean)) continue;
                        mod = (Modifier)aphModifier;
                        buff.addModifier(mod, (Object)((Boolean)val));
                        TomeLogger.debug("Applied Aphorea BuffModifier " + fieldName + " = " + val);
                        continue;
                    }
                    TomeLogger.debug("Skipped Aphorea field '" + fieldName + "' - not a BuffModifier (belongs to different list)");
                }
                catch (ClassCastException | IllegalAccessException | NoSuchFieldException ex) {
                    TomeLogger.debug("Could not determine modifier list for Aphorea field: " + fieldName);
                }
            }
            catch (IllegalAccessException | NoSuchFieldException f) {
            }
            catch (Exception ex) {
                TomeLogger.debug("Error applying Aphorea field " + fieldName + ": " + ex.getMessage());
            }
        }
    }
}

