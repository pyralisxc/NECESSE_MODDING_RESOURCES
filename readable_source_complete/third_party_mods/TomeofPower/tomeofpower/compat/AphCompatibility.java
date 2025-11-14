/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.Modifier
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.inventory.enchants.ItemEnchantment
 */
package tomeofpower.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.inventory.enchants.ItemEnchantment;
import tomeofpower.util.ModifierMapping;
import tomeofpower.util.TomeLogger;

public final class AphCompatibility {
    private static volatile boolean initialized = false;
    private static volatile boolean aphPresent = false;
    private static final Map<String, Field> aphFields = new HashMap<String, Field>();
    private static final WeakHashMap<Object, Set<String>> reportedUnmapped = new WeakHashMap();
    private static final WeakHashMap<Object, Map<String, Number>> aphStateValues = new WeakHashMap();

    private AphCompatibility() {
    }

    private static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        try {
            Class<?> aphClass = Class.forName("aphorea.registry.AphModifiers");
            for (Field f : aphClass.getFields()) {
                aphFields.put(f.getName(), f);
            }
            aphPresent = !aphFields.isEmpty();
        }
        catch (ClassNotFoundException e) {
            aphPresent = false;
        }
        catch (Throwable t) {
            aphPresent = false;
        }
    }

    public static boolean isAphPresent() {
        AphCompatibility.init();
        return aphPresent;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void applyAphModifiers(ActiveBuff buff, ItemEnchantment enchantment, int stackSize, Object owner) {
        AphCompatibility.init();
        if (!aphPresent || enchantment == null) {
            return;
        }
        for (Map.Entry<String, Field> e : aphFields.entrySet()) {
            String fieldName = e.getKey();
            Field f = e.getValue();
            Object aphModifier = null;
            try {
                aphModifier = f.get(null);
            }
            catch (IllegalAccessException ex) {
                continue;
            }
            if (aphModifier == null) continue;
            Object val = null;
            try {
                val = enchantment.getModifier((Modifier)aphModifier);
            }
            catch (Throwable ex) {
                continue;
            }
            if (val == null) continue;
            boolean appliedDirect = false;
            try {
                if (val instanceof Float) {
                    try {
                        Modifier mf = (Modifier)aphModifier;
                        buff.addModifier(mf, (Object)Float.valueOf(((Float)val).floatValue() * (float)stackSize));
                        appliedDirect = true;
                    }
                    catch (ClassCastException | LinkageError mf) {}
                } else if (val instanceof Integer) {
                    try {
                        Modifier mi = (Modifier)aphModifier;
                        buff.addModifier(mi, (Object)((Integer)val * stackSize));
                        appliedDirect = true;
                    }
                    catch (ClassCastException | LinkageError mi) {}
                } else if (val instanceof Boolean) {
                    try {
                        Modifier mb = (Modifier)aphModifier;
                        buff.addModifier(mb, (Object)((Boolean)val));
                        appliedDirect = true;
                    }
                    catch (ClassCastException | LinkageError mb) {}
                }
            }
            catch (Throwable mb) {
                // empty catch block
            }
            if (appliedDirect) continue;
            try {
                Field listField = aphModifier.getClass().getField("list");
                Object modifierList = listField.get(aphModifier);
                try {
                    Class<?> bmCls = Class.forName("necesse.entity.mobs.buffs.BuffModifiers");
                    Field listConst = bmCls.getField("LIST");
                    Object buffList = listConst.get(null);
                    if (modifierList == buffList) {
                        if (val instanceof Float) {
                            buff.addModifier((Modifier)aphModifier, (Object)Float.valueOf(((Float)val).floatValue() * (float)stackSize));
                            continue;
                        }
                        if (val instanceof Integer) {
                            buff.addModifier((Modifier)aphModifier, (Object)((Integer)val * stackSize));
                            continue;
                        }
                        if (val instanceof Boolean) {
                            buff.addModifier((Modifier)aphModifier, (Object)((Boolean)val));
                            continue;
                        }
                    }
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
            }
            catch (IllegalAccessException | NoSuchFieldException listField) {
                // empty catch block
            }
            try {
                Modifier<?> mapped = null;
                try {
                    mapped = ModifierMapping.mapToolModifierToBuffModifier((Modifier)aphModifier);
                }
                catch (ClassCastException cc) {
                    mapped = null;
                }
                if (mapped != null) {
                    if (val instanceof Float) {
                        buff.addModifier(mapped, (Object)Float.valueOf(((Float)val).floatValue() * (float)stackSize));
                        continue;
                    }
                    if (val instanceof Integer) {
                        buff.addModifier(mapped, (Object)((Integer)val * stackSize));
                        continue;
                    }
                    if (val instanceof Boolean && ((Boolean)val).booleanValue()) {
                        buff.addModifier(mapped, (Object)((Boolean)val));
                        continue;
                    }
                }
            }
            catch (Throwable mapped) {
                // empty catch block
            }
            if (owner == null) continue;
            String enchId = String.valueOf(enchantment.getID());
            boolean alreadyReported = false;
            WeakHashMap<Object, Object> weakHashMap = reportedUnmapped;
            synchronized (weakHashMap) {
                Set<String> s = reportedUnmapped.get(owner);
                if (s == null) {
                    s = new HashSet<String>();
                    reportedUnmapped.put(owner, s);
                }
                if (s.contains(enchId)) {
                    alreadyReported = true;
                } else {
                    s.add(enchId);
                }
            }
            if (val instanceof Float || val instanceof Integer) {
                weakHashMap = aphStateValues;
                synchronized (weakHashMap) {
                    Map<String, Number> m = aphStateValues.get(owner);
                    if (m == null) {
                        m = new HashMap<String, Number>();
                        aphStateValues.put(owner, m);
                    }
                    if (val instanceof Float) {
                        float scaled = ((Float)val).floatValue() * (float)stackSize;
                        prev = m.get(fieldName);
                        float next = prev instanceof Number ? prev.floatValue() + scaled : scaled;
                        m.put(fieldName, Float.valueOf(next));
                    } else if (val instanceof Integer) {
                        int scaled = (Integer)val * stackSize;
                        prev = m.get(fieldName);
                        int next = prev instanceof Number ? prev.intValue() + scaled : scaled;
                        m.put(fieldName, next);
                    }
                    TomeLogger.debug("Stored unmapped Aphorea field " + fieldName + "=" + String.valueOf(m.get(fieldName)));
                    continue;
                }
            }
            if (alreadyReported) continue;
            AphCompatibility.sendPlayerChat(owner, String.format("Enchantment '%s' contains Aphorea-only modifiers (e.g. %s) that Tome of Power cannot apply.", enchantment.getID(), fieldName));
        }
    }

    private static Modifier<?> mapAphFieldNameToBuffModifier(String fieldName) {
        if (fieldName == null) {
            return null;
        }
        String n = fieldName.toUpperCase();
        try {
            Class<?> bmCls = Class.forName("necesse.entity.mobs.buffs.BuffModifiers");
            if (n.contains("CRIT_CHANCE") || n.contains("CRIT")) {
                return (Modifier)bmCls.getField("CRIT_CHANCE").get(null);
            }
            if (n.contains("INSPIRATION_CRIT_DAMAGE") || n.contains("DAMAGE") || n.contains("INSPIRATION_DAMAGE")) {
                return (Modifier)bmCls.getField("ALL_DAMAGE").get(null);
            }
            if (n.contains("ATTACK_SPEED") || n.contains("ABILITY_SPEED")) {
                return (Modifier)bmCls.getField("ATTACK_SPEED").get(null);
            }
            if (n.contains("VELOCITY")) {
                return (Modifier)bmCls.getField("PROJECTILE_VELOCITY").get(null);
            }
            if (n.contains("MANA")) {
                return (Modifier)bmCls.getField("MANA_USAGE").get(null);
            }
            if (n.contains("MINING")) {
                return (Modifier)bmCls.getField("MINING_SPEED").get(null);
            }
            if (n.contains("ARMOR_PEN")) {
                return (Modifier)bmCls.getField("ARMOR_PEN_FLAT").get(null);
            }
            if (n.contains("SUMMONS_SPEED")) {
                return (Modifier)bmCls.getField("SUMMONS_SPEED").get(null);
            }
            if (n.contains("SUMMONS_TARGET_RANGE") || n.contains("TOOL_AREA_RANGE") || n.contains("AREA_RANGE")) {
                return (Modifier)bmCls.getField("SUMMONS_TARGET_RANGE").get(null);
            }
            if (n.contains("TOOL_DAMAGE")) {
                return (Modifier)bmCls.getField("TOOL_DAMAGE").get(null);
            }
            if (n.contains("RANGE")) {
                return (Modifier)bmCls.getField("TARGET_RANGE").get(null);
            }
            try {
                Class<?> aphCls = Class.forName("aphorea.registry.AphModifiers");
                if (n.contains("TOOL_MAGIC_HEALING") || n.equals("MAGIC_HEALING") || n.contains("MAGIC_HEALING")) {
                    return (Modifier)aphCls.getField("MAGIC_HEALING").get(null);
                }
                if (n.contains("TOOL_MAGIC_HEALING_RECEIVED") || n.contains("MAGIC_HEALING_RECEIVED")) {
                    return (Modifier)aphCls.getField("MAGIC_HEALING_RECEIVED").get(null);
                }
                if (n.contains("TOOL_MAGIC_HEALING_GRACE") || n.contains("MAGIC_HEALING_GRACE")) {
                    return (Modifier)aphCls.getField("MAGIC_HEALING_GRACE").get(null);
                }
            }
            catch (ClassNotFoundException classNotFoundException) {}
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void applyStoredToBuff(ActiveBuff buff, Object owner) {
        if (buff == null || owner == null) {
            return;
        }
        WeakHashMap<Object, Map<String, Number>> weakHashMap = aphStateValues;
        synchronized (weakHashMap) {
            Map<String, Number> vals = aphStateValues.get(owner);
            if (vals == null || vals.isEmpty()) {
                return;
            }
            for (Map.Entry<String, Number> e : vals.entrySet()) {
                String field = e.getKey();
                Number v = e.getValue();
                Modifier<?> mapped = AphCompatibility.mapAphFieldNameToBuffModifier(field);
                if (mapped == null) continue;
                try {
                    if (v instanceof Float) {
                        Modifier<?> mf = mapped;
                        buff.addModifier(mf, (Object)((Float)v));
                        continue;
                    }
                    if (!(v instanceof Integer)) continue;
                    Modifier<?> mi = mapped;
                    buff.addModifier(mi, (Object)((Integer)v));
                }
                catch (Throwable throwable) {}
            }
            vals.clear();
        }
    }

    private static void sendPlayerChat(Object owner, String message) {
        if (owner == null) {
            System.out.println("[AphCompatibility][CHAT] " + message);
            return;
        }
        try {
            Class<?> cls = owner.getClass();
            Method m = null;
            try {
                m = cls.getMethod("sendMessage", String.class);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            if (m == null) {
                try {
                    m = cls.getMethod("sendChat", String.class);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (m == null) {
                try {
                    m = cls.getMethod("addMessage", String.class);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    // empty catch block
                }
            }
            if (m != null) {
                m.invoke(owner, message);
                return;
            }
            try {
                Field scField = cls.getField("serverClient");
                Object sc = scField.get(owner);
                if (sc != null) {
                    try {
                        Method send = sc.getClass().getMethod("sendMessage", String.class);
                        send.invoke(sc, message);
                        return;
                    }
                    catch (NoSuchMethodException noSuchMethodException) {}
                }
            }
            catch (IllegalAccessException | NoSuchFieldException reflectiveOperationException) {
                // empty catch block
            }
            System.out.println("[AphCompatibility][CHAT] " + message);
        }
        catch (Throwable t) {
            System.out.println("[AphCompatibility][CHAT-ERROR] " + t);
        }
    }
}

