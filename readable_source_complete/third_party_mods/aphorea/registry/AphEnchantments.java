/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.modLoader.LoadedMod
 *  necesse.engine.modifiers.ModifierValue
 *  necesse.engine.registries.EnchantmentRegistry
 *  necesse.inventory.enchants.EquipmentItemEnchant
 *  necesse.inventory.enchants.ItemEnchantment
 *  necesse.inventory.enchants.ToolItemEnchantment
 *  necesse.inventory.enchants.ToolItemModifiers
 *  necesse.inventory.item.miscItem.EnchantingScrollItem
 *  necesse.inventory.item.miscItem.EnchantingScrollItem$EnchantScrollType
 */
package aphorea.registry;

import aphorea.registry.AphModifiers;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.EnchantmentRegistry;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.miscItem.EnchantingScrollItem;

public class AphEnchantments {
    public static Set<Integer> healingItemEnchantments = new HashSet<Integer>();
    public static Set<Integer> healingEquipmentEnchantments = new HashSet<Integer>();
    public static Set<Integer> areaItemEnchantments = new HashSet<Integer>();
    public static Set<Integer> daggerItemEnchantments = new HashSet<Integer>();
    public static int godly;
    public static int absent;
    public static int auxiliary;
    public static int vain;
    public static int gentle;
    public static int selfish;
    public static int friendly;
    public static int graceful;
    public static int wonderful;
    public static int ecologic;
    public static int exalted;
    public static int cursed;
    public static int booming;
    public static int dimmed;
    public static int loyal;

    public static void registerCore() {
        godly = AphEnchantments.registerEnchantment(healingEquipmentEnchantments, "godly", (ItemEnchantment)new EquipmentItemEnchant(20, new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING, (Object)Float.valueOf(0.05f))}));
        absent = AphEnchantments.registerEnchantment(healingEquipmentEnchantments, "absent", (ItemEnchantment)new EquipmentItemEnchant(20, new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING, (Object)Float.valueOf(-0.05f))}));
        auxiliary = AphEnchantments.registerEnchantment(healingEquipmentEnchantments, "auxiliary", (ItemEnchantment)new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING_RECEIVED, (Object)Float.valueOf(0.1f))}));
        vain = AphEnchantments.registerEnchantment(healingEquipmentEnchantments, "vain", (ItemEnchantment)new EquipmentItemEnchant(-20, new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING_RECEIVED, (Object)Float.valueOf(-0.1f))}));
        gentle = AphEnchantments.registerEnchantment(healingEquipmentEnchantments, "gentle", (ItemEnchantment)new EquipmentItemEnchant(0, new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING, (Object)Float.valueOf(0.1f)), new ModifierValue(AphModifiers.MAGIC_HEALING_RECEIVED, (Object)Float.valueOf(-0.2f))}));
        selfish = AphEnchantments.registerEnchantment(healingEquipmentEnchantments, "selfish", (ItemEnchantment)new EquipmentItemEnchant(0, new ModifierValue[]{new ModifierValue(AphModifiers.MAGIC_HEALING, (Object)Float.valueOf(-0.1f)), new ModifierValue(AphModifiers.MAGIC_HEALING_RECEIVED, (Object)Float.valueOf(0.3f))}));
        friendly = AphEnchantments.registerEnchantment(healingItemEnchantments, "friendly", (ItemEnchantment)new ToolItemEnchantment(20, new ModifierValue[]{new ModifierValue(AphModifiers.TOOL_MAGIC_HEALING, (Object)Float.valueOf(0.2f))}));
        graceful = AphEnchantments.registerEnchantment(healingItemEnchantments, "graceful", (ItemEnchantment)new ToolItemEnchantment(20, new ModifierValue[]{new ModifierValue(AphModifiers.TOOL_MAGIC_HEALING_GRACE, (Object)Float.valueOf(0.1f))}));
        wonderful = AphEnchantments.registerEnchantment(healingItemEnchantments, "wonderful", (ItemEnchantment)new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(AphModifiers.TOOL_MAGIC_HEALING, (Object)Float.valueOf(0.1f)), new ModifierValue(ToolItemModifiers.MANA_USAGE, (Object)Float.valueOf(-0.1f))}));
        ecologic = AphEnchantments.registerEnchantment(healingItemEnchantments, "ecologic", (ItemEnchantment)new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(ToolItemModifiers.MANA_USAGE, (Object)Float.valueOf(-0.3f))}));
        exalted = AphEnchantments.registerEnchantment(healingItemEnchantments, "exalted", (ItemEnchantment)new ToolItemEnchantment(-20, new ModifierValue[]{new ModifierValue(ToolItemModifiers.MANA_USAGE, (Object)Float.valueOf(0.2f))}));
        cursed = AphEnchantments.registerEnchantment(healingItemEnchantments, "cursed", (ItemEnchantment)new ToolItemEnchantment(-60, new ModifierValue[]{new ModifierValue(AphModifiers.TOOL_MAGIC_HEALING_RECEIVED, (Object)Float.valueOf(-0.8f)), new ModifierValue(AphModifiers.TOOL_MAGIC_HEALING, (Object)Float.valueOf(0.2f)), new ModifierValue(AphModifiers.TOOL_MAGIC_HEALING_GRACE, (Object)Float.valueOf(0.2f))}));
        booming = AphEnchantments.registerEnchantment(areaItemEnchantments, "booming", (ItemEnchantment)new ToolItemEnchantment(20, new ModifierValue[]{new ModifierValue(AphModifiers.TOOL_AREA_RANGE, (Object)Float.valueOf(0.3f))}));
        dimmed = AphEnchantments.registerEnchantment(areaItemEnchantments, "dimmed", (ItemEnchantment)new ToolItemEnchantment(-20, new ModifierValue[]{new ModifierValue(AphModifiers.TOOL_AREA_RANGE, (Object)Float.valueOf(-0.2f))}));
        loyal = AphEnchantments.registerEnchantment(daggerItemEnchantments, "loyal", (ItemEnchantment)new ToolItemEnchantment(10, new ModifierValue[]{new ModifierValue(AphModifiers.LOYAL, (Object)true)}));
        EnchantingScrollItem.types.add(new EnchantingScrollItem.EnchantScrollType("healingequipment", 200, enchantment -> healingEquipmentEnchantments.contains(enchantment.getID()), random -> (ItemEnchantment)random.getOneOf((Object[])((ItemEnchantment[])healingEquipmentEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new))), enchantment -> new LocalMessage("itemtooltip", "enchantingscrollhealingequipmenttip", "enchantment", enchantment.getLocalization()), enchantment -> new LocalMessage("ui", "enchantscrollequipment")));
        EnchantingScrollItem.types.add(new EnchantingScrollItem.EnchantScrollType("healing", 200, enchantment -> healingItemEnchantments.contains(enchantment.getID()), random -> (ItemEnchantment)random.getOneOf((Object[])((ItemEnchantment[])healingItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new))), enchantment -> new LocalMessage("itemtooltip", "enchantingscrollhealingtip", "enchantment", enchantment.getLocalization()), enchantment -> new LocalMessage("ui", "enchantscrollequipment")));
        EnchantingScrollItem.types.add(new EnchantingScrollItem.EnchantScrollType("area", 200, enchantment -> areaItemEnchantments.contains(enchantment.getID()), random -> (ItemEnchantment)random.getOneOf((Object[])((ItemEnchantment[])areaItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new))), enchantment -> new LocalMessage("itemtooltip", "enchantingscrollareatip", "enchantment", enchantment.getLocalization()), enchantment -> new LocalMessage("ui", "enchantscrollequipment")));
        EnchantingScrollItem.types.add(new EnchantingScrollItem.EnchantScrollType("dagger", 200, enchantment -> daggerItemEnchantments.contains(enchantment.getID()), random -> (ItemEnchantment)random.getOneOf((Object[])((ItemEnchantment[])daggerItemEnchantments.stream().map(EnchantmentRegistry::getEnchantment).filter(Objects::nonNull).filter(e -> e.getEnchantCostMod() >= 1.0f).toArray(ItemEnchantment[]::new))), enchantment -> new LocalMessage("itemtooltip", "enchantingscrolldaggertip", "enchantment", enchantment.getLocalization()), enchantment -> new LocalMessage("ui", "enchantscrollequipment")));
    }

    public static int registerEnchantment(Set<Integer> list, String stringID, ItemEnchantment enchantment) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register enchantments");
        }
        int id = EnchantmentRegistry.registerEnchantment((String)stringID, (ItemEnchantment)enchantment);
        list.add(id);
        return id;
    }
}

