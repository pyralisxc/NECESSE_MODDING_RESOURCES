/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modifiers.ModifierList;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.GameRegistry;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.enchants.EquipmentItemEnchant;
import necesse.inventory.enchants.ItemEnchantment;
import necesse.inventory.enchants.ToolDamageEnchantment;
import necesse.inventory.enchants.ToolItemEnchantment;
import necesse.inventory.enchants.ToolItemModifiers;

public class EnchantmentRegistry
extends GameRegistry<ItemEnchantment> {
    public static final EnchantmentRegistry instance = new EnchantmentRegistry();
    public static Set<Integer> equipmentEnchantments = new HashSet<Integer>();
    public static Set<Integer> meleeItemEnchantments = new HashSet<Integer>();
    public static Set<Integer> rangedItemEnchantments = new HashSet<Integer>();
    public static Set<Integer> magicItemEnchantments = new HashSet<Integer>();
    public static Set<Integer> summonItemEnchantments = new HashSet<Integer>();
    public static Set<Integer> toolDamageEnchantments = new HashSet<Integer>();
    public static int keen;
    public static int blunt;
    public static int nimble;
    public static int sluggish;
    public static int quick;
    public static int clumsy;
    public static int sturdy;
    public static int weak;
    public static int precise;
    public static int sloppy;
    public static int bulky;
    public static int puny;
    public static int magical;
    public static int draining;
    public static int tenacious;
    public static int flimsy;
    public static int tough;
    public static int fragile;
    public static int berserk;
    public static int adamant;
    public static int agile;
    public static int harmful;
    public static int grand;
    public static int docile;
    public static int shoddy;
    public static int amateur;
    public static int envious;
    public static int masterful;
    public static int skillful;
    public static int tightened;
    public static int modern;
    public static int trained;
    public static int loose;
    public static int eroding;
    public static int primitive;
    public static int faulty;
    public static int divine;
    public static int wrathful;
    public static int wise;
    public static int adept;
    public static int apprentice;
    public static int decaying;
    public static int novice;
    public static int daft;
    public static int corrupt;
    public static int savage;
    public static int athletic;
    public static int mindful;
    public static int proud;
    public static int aware;
    public static int spoiled;
    public static int sick;
    public static int spiteful;
    public static int naive;
    public static int master;
    public static int shining;
    public static int sharp;
    public static int absurd;
    public static int used;

    public EnchantmentRegistry() {
        super("Enchantment", Short.MAX_VALUE);
    }

    @Override
    public void registerCore() {
        EnchantmentRegistry.registerEnchantment("noenchant", new ItemEnchantment(new ModifierList(), 0));
        keen = EnchantmentRegistry.registerEquipmentEnchantment("keen", new EquipmentItemEnchant(10, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(0.04f))));
        blunt = EnchantmentRegistry.registerEquipmentEnchantment("blunt", new EquipmentItemEnchant(-20, new ModifierValue<Float>(BuffModifiers.ALL_DAMAGE, Float.valueOf(-0.04f))));
        nimble = EnchantmentRegistry.registerEquipmentEnchantment("nimble", new EquipmentItemEnchant(10, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(0.04f))));
        sluggish = EnchantmentRegistry.registerEquipmentEnchantment("sluggish", new EquipmentItemEnchant(-20, new ModifierValue<Float>(BuffModifiers.SPEED, Float.valueOf(-0.04f))));
        quick = EnchantmentRegistry.registerEquipmentEnchantment("quick", new EquipmentItemEnchant(10, new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(0.04f))));
        clumsy = EnchantmentRegistry.registerEquipmentEnchantment("clumsy", new EquipmentItemEnchant(-20, new ModifierValue<Float>(BuffModifiers.ATTACK_SPEED, Float.valueOf(-0.04f))));
        sturdy = EnchantmentRegistry.registerEquipmentEnchantment("sturdy", new EquipmentItemEnchant(10, new ModifierValue<Integer>(BuffModifiers.ARMOR_FLAT, 4)));
        weak = EnchantmentRegistry.registerEquipmentEnchantment("weak", new EquipmentItemEnchant(-20, new ModifierValue<Integer>(BuffModifiers.ARMOR_FLAT, -4)));
        precise = EnchantmentRegistry.registerEquipmentEnchantment("precise", new EquipmentItemEnchant(10, new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(0.04f))));
        sloppy = EnchantmentRegistry.registerEquipmentEnchantment("sloppy", new EquipmentItemEnchant(-20, new ModifierValue<Float>(BuffModifiers.CRIT_CHANCE, Float.valueOf(-0.04f))));
        bulky = EnchantmentRegistry.registerEquipmentEnchantment("bulky", new EquipmentItemEnchant(10, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, 10)));
        puny = EnchantmentRegistry.registerEquipmentEnchantment("puny", new EquipmentItemEnchant(-20, new ModifierValue<Integer>(BuffModifiers.MAX_HEALTH_FLAT, -10)));
        magical = EnchantmentRegistry.registerEquipmentEnchantment("magical", new EquipmentItemEnchant(5, new ModifierValue<Float>(BuffModifiers.COMBAT_MANA_REGEN, Float.valueOf(0.2f))));
        draining = EnchantmentRegistry.registerEquipmentEnchantment("draining", new EquipmentItemEnchant(-20, new ModifierValue<Float>(BuffModifiers.COMBAT_MANA_REGEN, Float.valueOf(-0.2f))));
        tenacious = EnchantmentRegistry.registerEquipmentEnchantment("tenacious", new EquipmentItemEnchant(10, new ModifierValue<Float>(BuffModifiers.RESILIENCE_GAIN, Float.valueOf(0.04f))));
        flimsy = EnchantmentRegistry.registerEquipmentEnchantment("flimsy", new EquipmentItemEnchant(-20, new ModifierValue<Float>(BuffModifiers.RESILIENCE_GAIN, Float.valueOf(-0.04f))));
        tough = EnchantmentRegistry.registerEquipmentEnchantment("tough", new EquipmentItemEnchant(10, new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, 10)));
        fragile = EnchantmentRegistry.registerEquipmentEnchantment("fragile", new EquipmentItemEnchant(-20, new ModifierValue<Integer>(BuffModifiers.MAX_RESILIENCE_FLAT, -10)));
        berserk = EnchantmentRegistry.registerMeleeEnchantment("berserk", new ToolItemEnchantment(15, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.15f)), new ModifierValue<Float>(ToolItemModifiers.CRIT_CHANCE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(0.1f))));
        adamant = EnchantmentRegistry.registerMeleeEnchantment("adamant", new ToolItemEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(0.2f)), new ModifierValue<Float>(ToolItemModifiers.KNOCKBACK, Float.valueOf(-1.0f))));
        agile = EnchantmentRegistry.registerMeleeEnchantment("agile", new ToolItemEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(0.3f))));
        harmful = EnchantmentRegistry.registerMeleeEnchantment("harmful", new ToolItemEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.2f)), new ModifierValue<Float>(ToolItemModifiers.CRIT_CHANCE, Float.valueOf(0.05f))));
        grand = EnchantmentRegistry.registerMeleeEnchantment("grand", new ToolItemEnchantment(0, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.2f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(-0.2f)), new ModifierValue<Float>(ToolItemModifiers.KNOCKBACK, Float.valueOf(0.5f))));
        docile = EnchantmentRegistry.registerMeleeEnchantment("docile", new ToolItemEnchantment(-20, new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(-0.2f))));
        shoddy = EnchantmentRegistry.registerMeleeEnchantment("shoddy", new ToolItemEnchantment(-20, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.15f))));
        amateur = EnchantmentRegistry.registerMeleeEnchantment("amateur", new ToolItemEnchantment(-30, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.15f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(-0.1f))));
        envious = EnchantmentRegistry.registerMeleeEnchantment("envious", new ToolItemEnchantment(-40, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.2f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(-0.1f))));
        masterful = EnchantmentRegistry.registerRangedEnchantment("masterful", new ToolItemEnchantment(20, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.15f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(0.2f)), new ModifierValue<Float>(ToolItemModifiers.RANGE, Float.valueOf(0.15f))));
        skillful = EnchantmentRegistry.registerRangedEnchantment("skillful", new ToolItemEnchantment(15, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.CRIT_CHANCE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(0.15f)), new ModifierValue<Float>(ToolItemModifiers.RANGE, Float.valueOf(0.2f))));
        tightened = EnchantmentRegistry.registerRangedEnchantment("tightened", new ToolItemEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(0.25f))));
        modern = EnchantmentRegistry.registerRangedEnchantment("modern", new ToolItemEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.CRIT_CHANCE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(0.25f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(0.1f))));
        trained = EnchantmentRegistry.registerRangedEnchantment("trained", new ToolItemEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(0.1f))));
        loose = EnchantmentRegistry.registerRangedEnchantment("loose", new ToolItemEnchantment(-20, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.05f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(-0.25f))));
        eroding = EnchantmentRegistry.registerRangedEnchantment("eroding", new ToolItemEnchantment(-30, new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(-0.15f)), new ModifierValue<Float>(ToolItemModifiers.RANGE, Float.valueOf(-0.1f))));
        primitive = EnchantmentRegistry.registerRangedEnchantment("primitive", new ToolItemEnchantment(-30, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.1f)), new ModifierValue<Float>(ToolItemModifiers.RANGE, Float.valueOf(-0.2f))));
        faulty = EnchantmentRegistry.registerRangedEnchantment("faulty", new ToolItemEnchantment(-40, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.15f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(-0.1f))));
        divine = EnchantmentRegistry.registerMagicEnchantment("divine", new ToolItemEnchantment(20, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.15f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.RANGE, Float.valueOf(0.2f)), new ModifierValue<Float>(ToolItemModifiers.MANA_USAGE, Float.valueOf(-0.3f))));
        wrathful = EnchantmentRegistry.registerMagicEnchantment("wrathful", new ToolItemEnchantment(15, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.CRIT_CHANCE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(0.1f))));
        wise = EnchantmentRegistry.registerMagicEnchantment("wise", new ToolItemEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(0.15f)), new ModifierValue<Float>(ToolItemModifiers.RANGE, Float.valueOf(0.25f)), new ModifierValue<Float>(ToolItemModifiers.MANA_USAGE, Float.valueOf(-0.25f))));
        adept = EnchantmentRegistry.registerMagicEnchantment("adept", new ToolItemEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.MANA_USAGE, Float.valueOf(-0.1f))));
        apprentice = EnchantmentRegistry.registerMagicEnchantment("apprentice", new ToolItemEnchantment(5, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.CRIT_CHANCE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(0.1f))));
        decaying = EnchantmentRegistry.registerMagicEnchantment("decaying", new ToolItemEnchantment(-10, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.05f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(-0.1f)), new ModifierValue<Float>(ToolItemModifiers.MANA_USAGE, Float.valueOf(0.1f))));
        novice = EnchantmentRegistry.registerMagicEnchantment("novice", new ToolItemEnchantment(-20, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.1f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(-0.05f)), new ModifierValue<Float>(ToolItemModifiers.MANA_USAGE, Float.valueOf(0.2f))));
        daft = EnchantmentRegistry.registerMagicEnchantment("daft", new ToolItemEnchantment(-20, new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(-0.1f)), new ModifierValue<Float>(ToolItemModifiers.RANGE, Float.valueOf(-0.2f)), new ModifierValue<Float>(ToolItemModifiers.MANA_USAGE, Float.valueOf(0.3f))));
        corrupt = EnchantmentRegistry.registerMagicEnchantment("corrupt", new ToolItemEnchantment(-40, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.15f)), new ModifierValue<Float>(ToolItemModifiers.ATTACK_SPEED, Float.valueOf(-0.1f)), new ModifierValue<Float>(ToolItemModifiers.VELOCITY, Float.valueOf(-0.1f))));
        savage = EnchantmentRegistry.registerSummonEnchantment("savage", new ToolItemEnchantment(20, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.15f)), new ModifierValue<Float>(ToolItemModifiers.SUMMONS_SPEED, Float.valueOf(0.1f))));
        athletic = EnchantmentRegistry.registerSummonEnchantment("athletic", new ToolItemEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.SUMMONS_SPEED, Float.valueOf(0.25f)), new ModifierValue<Float>(ToolItemModifiers.SUMMONS_TARGET_RANGE, Float.valueOf(0.2f))));
        mindful = EnchantmentRegistry.registerSummonEnchantment("mindful", new ToolItemEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.CRIT_CHANCE, Float.valueOf(0.05f))));
        proud = EnchantmentRegistry.registerSummonEnchantment("proud", new ToolItemEnchantment(5, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.SUMMONS_SPEED, Float.valueOf(0.1f))));
        aware = EnchantmentRegistry.registerSummonEnchantment("aware", new ToolItemEnchantment(5, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.CRIT_CHANCE, Float.valueOf(0.05f)), new ModifierValue<Float>(ToolItemModifiers.SUMMONS_TARGET_RANGE, Float.valueOf(0.2f))));
        spoiled = EnchantmentRegistry.registerSummonEnchantment("spoiled", new ToolItemEnchantment(-20, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.1f))));
        sick = EnchantmentRegistry.registerSummonEnchantment("sick", new ToolItemEnchantment(-20, new ModifierValue<Float>(ToolItemModifiers.SUMMONS_SPEED, Float.valueOf(-0.15f)), new ModifierValue<Float>(ToolItemModifiers.SUMMONS_TARGET_RANGE, Float.valueOf(-0.1f))));
        spiteful = EnchantmentRegistry.registerSummonEnchantment("spiteful", new ToolItemEnchantment(-20, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.1f)), new ModifierValue<Float>(ToolItemModifiers.SUMMONS_SPEED, Float.valueOf(-0.05f))));
        naive = EnchantmentRegistry.registerSummonEnchantment("naive", new ToolItemEnchantment(-30, new ModifierValue<Float>(ToolItemModifiers.DAMAGE, Float.valueOf(-0.15f)), new ModifierValue<Float>(ToolItemModifiers.SUMMONS_TARGET_RANGE, Float.valueOf(-0.1f))));
        master = EnchantmentRegistry.registerToolEnchantment("master", new ToolDamageEnchantment(20, new ModifierValue<Float>(ToolItemModifiers.TOOL_DAMAGE, Float.valueOf(0.2f)), new ModifierValue<Float>(ToolItemModifiers.MINING_SPEED, Float.valueOf(0.15f))));
        shining = EnchantmentRegistry.registerToolEnchantment("shining", new ToolDamageEnchantment(10, new ModifierValue<Float>(ToolItemModifiers.TOOL_DAMAGE, Float.valueOf(0.1f)), new ModifierValue<Float>(ToolItemModifiers.MINING_SPEED, Float.valueOf(0.1f))));
        sharp = EnchantmentRegistry.registerToolEnchantment("sharp", new ToolDamageEnchantment(5, new ModifierValue<Float>(ToolItemModifiers.TOOL_DAMAGE, Float.valueOf(0.1f))));
        absurd = EnchantmentRegistry.registerToolEnchantment("absurd", new ToolDamageEnchantment(-20, new ModifierValue<Float>(ToolItemModifiers.MINING_SPEED, Float.valueOf(-0.1f))));
        used = EnchantmentRegistry.registerToolEnchantment("used", new ToolDamageEnchantment(-30, new ModifierValue<Float>(ToolItemModifiers.TOOL_DAMAGE, Float.valueOf(-0.15f))));
    }

    public static int registerEnchantment(String stringID, ItemEnchantment enchantment) {
        if (LoadedMod.isRunningModClientSide()) {
            throw new IllegalStateException("Client/server only mods cannot register enchantments");
        }
        return instance.register(stringID, enchantment);
    }

    public static int registerEquipmentEnchantment(String stringID, EquipmentItemEnchant enchantment) {
        int id = EnchantmentRegistry.registerEnchantment(stringID, enchantment);
        equipmentEnchantments.add(id);
        return id;
    }

    public static int registerMeleeEnchantment(String stringID, ToolItemEnchantment enchantment) {
        int id = EnchantmentRegistry.registerEnchantment(stringID, enchantment);
        meleeItemEnchantments.add(id);
        return id;
    }

    public static int registerRangedEnchantment(String stringID, ToolItemEnchantment enchantment) {
        int id = EnchantmentRegistry.registerEnchantment(stringID, enchantment);
        rangedItemEnchantments.add(id);
        return id;
    }

    public static int registerMagicEnchantment(String stringID, ToolItemEnchantment enchantment) {
        int id = EnchantmentRegistry.registerEnchantment(stringID, enchantment);
        magicItemEnchantments.add(id);
        return id;
    }

    public static int registerSummonEnchantment(String stringID, ToolItemEnchantment enchantment) {
        int id = EnchantmentRegistry.registerEnchantment(stringID, enchantment);
        summonItemEnchantments.add(id);
        return id;
    }

    public static int registerToolEnchantment(String stringID, ToolDamageEnchantment enchantment) {
        int id = EnchantmentRegistry.registerEnchantment(stringID, enchantment);
        toolDamageEnchantments.add(id);
        return id;
    }

    @Override
    protected void onRegister(ItemEnchantment object, int id, String stringID, boolean isReplace) {
    }

    @Override
    protected void onRegistryClose() {
        for (ItemEnchantment element : this.getElements()) {
            element.onEnchantmentRegistryClosed();
        }
        equipmentEnchantments = Collections.unmodifiableSet(equipmentEnchantments);
        meleeItemEnchantments = Collections.unmodifiableSet(meleeItemEnchantments);
        rangedItemEnchantments = Collections.unmodifiableSet(rangedItemEnchantments);
        magicItemEnchantments = Collections.unmodifiableSet(magicItemEnchantments);
        summonItemEnchantments = Collections.unmodifiableSet(summonItemEnchantments);
        toolDamageEnchantments = Collections.unmodifiableSet(toolDamageEnchantments);
    }

    public static ItemEnchantment getEnchantment(int id) {
        try {
            return (ItemEnchantment)instance.getElementRaw(id);
        }
        catch (NoSuchElementException e) {
            return null;
        }
    }

    public static <T extends ItemEnchantment> T getEnchantment(int id, Class<T> expectedClass) {
        ItemEnchantment out = EnchantmentRegistry.getEnchantment(id);
        if (expectedClass.isInstance(out)) {
            return (T)((ItemEnchantment)expectedClass.cast(out));
        }
        return null;
    }

    public static <T extends ItemEnchantment> T getEnchantment(int id, Class<T> expectedClass, T defaultReturn) {
        if (id == 0) {
            return defaultReturn;
        }
        T out = EnchantmentRegistry.getEnchantment(id, expectedClass);
        if (out == null) {
            return defaultReturn;
        }
        return out;
    }

    public static int getEnchantmentID(String stringID) {
        try {
            return instance.getElementIDRaw(stringID);
        }
        catch (NoSuchElementException e) {
            return -1;
        }
    }

    public static String getEnchantmentStringID(int id) {
        return instance.getElementStringID(id);
    }

    public static ItemEnchantment getEnchantment(String stringID) {
        return EnchantmentRegistry.getEnchantment(EnchantmentRegistry.getEnchantmentID(stringID));
    }

    public static <T extends ItemEnchantment> T getEnchantment(String stringID, Class<T> expectedClass) {
        return EnchantmentRegistry.getEnchantment(EnchantmentRegistry.getEnchantmentID(stringID), expectedClass);
    }

    public static List<ItemEnchantment> getEnchantments() {
        return instance.streamElements().collect(Collectors.toList());
    }
}

