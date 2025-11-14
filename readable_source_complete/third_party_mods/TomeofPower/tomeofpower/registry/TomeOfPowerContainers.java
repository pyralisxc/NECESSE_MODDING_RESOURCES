/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.Modifier
 *  necesse.engine.network.NetworkClient
 *  necesse.engine.registries.ContainerRegistry
 *  necesse.entity.mobs.buffs.BuffModifiers
 */
package tomeofpower.registry;

import java.util.HashMap;
import java.util.Map;
import necesse.engine.modifiers.Modifier;
import necesse.engine.network.NetworkClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.buffs.BuffModifiers;
import tomeofpower.containers.trinket.TrinketInventoryContainer;
import tomeofpower.containers.trinket.TrinketInventoryContainerForm;

public class TomeOfPowerContainers {
    public static int TRINKET_INVENTORY_CONTAINER;
    private static final Map<String, Modifier<?>> REMAP_TABLE;
    public static final Map<String, String> MODIFIER_TOOLTIP_MAP;
    public static final Map<String, Boolean> MODIFIER_Exclude;

    public static Modifier<?> remap(Modifier<?> modifier) {
        if (modifier == null || modifier.stringID == null) {
            return modifier;
        }
        return REMAP_TABLE.getOrDefault(modifier.stringID, modifier);
    }

    public static Boolean Modifier_include(Modifier<?> modifier) {
        if (modifier == null || modifier.stringID == null) {
            return true;
        }
        return MODIFIER_Exclude.getOrDefault(modifier.stringID, true);
    }

    public static String remap_func(String stringID) {
        if (stringID == null) {
            return stringID;
        }
        return MODIFIER_TOOLTIP_MAP.getOrDefault(stringID, stringID);
    }

    public static void registerCore() {
        TRINKET_INVENTORY_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> {
            TrinketInventoryContainer container = new TrinketInventoryContainer((NetworkClient)client.getClient(), uniqueSeed, packet);
            return new TrinketInventoryContainerForm(client, container);
        }, (serverClient, uniqueSeed, packet, serverObject) -> new TrinketInventoryContainer((NetworkClient)serverClient, uniqueSeed, packet));
    }

    static {
        MODIFIER_Exclude = new HashMap<String, Boolean>();
        MODIFIER_Exclude.put("incomingdamagemod", false);
        MODIFIER_Exclude.put("knockbackin", false);
        MODIFIER_Exclude.put("attackmovementmod", false);
        MODIFIER_TOOLTIP_MAP = new HashMap<String, String>();
        MODIFIER_TOOLTIP_MAP.put("alldamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("meleedamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("rangedamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("magicdamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("summondamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("tooldamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("tooldamageflat", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("critchance", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("meleecritchance", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("rangedcritchance", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("magiccritchance", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("summoncritchance", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("critdamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("meleecritdamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("rangedcritdamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("magiccritdamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("summoncritdamage", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("armorpen", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("armorpenflat", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("knockbackout", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("speed", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("speedflat", "NORMAL_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("slow", "BAD_PERCENT_MODIFIER");
        MODIFIER_TOOLTIP_MAP.put("swimspeed", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("friction", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("acceleration", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("deceleration", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("armor", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("armorflat", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("incomingdamagemod", "LESS_GOOD_PERCENT_MODIFIER");
        MODIFIER_TOOLTIP_MAP.put("maxhealth", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("maxhealthflat", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("maxresilience", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("maxresilienceflat", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("maxmana", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("maxmanaflat", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("basehealthregen", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("basehealthregenflat", "NORMAL_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("combathealthregen", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("combathealthregenflat", "NORMAL_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("resiliencegain", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("resiliencedecay", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("resiliencedecayflat", "INVERSE_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("resilienceregen", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("resilienceregenflat", "NORMAL_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("basemanaregen", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("basemanaregenflat", "NORMAL_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("combatmanaregen", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("combatmanaregenflat", "NORMAL_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("lifeessencegain", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("lifeessenceduration", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("poisondamage", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("poisondamageflat", "INVERSE_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("firedamage", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("firedamageflat", "INVERSE_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("frostdamage", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("frostdamageflat", "INVERSE_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("attackspeed", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("meleeattackspeed", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("rangedattackspeed", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("magicattackspeed", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("summonattackspeed", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("miningspeed", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("buildingspeed", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("tooltier", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("miningrange", "NORMAL_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("buildrange", "NORMAL_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("itempickuprange", "NORMAL_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("arrowusage", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("bulletusage", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("manausage", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("maxsummons", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("summonsspeed", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("summonstargetrange", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("attackmovementmod", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("projectilevelocity", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("throwingvelocity", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("projectilebounces", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("blindness", "BAD_PERCENT_MODIFIER");
        MODIFIER_TOOLTIP_MAP.put("dashstacks", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("dashcooldown", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("dashcooldownflat", "INVERSE_FLAT_FLOAT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("potionduration", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("staminacap", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("staminausage", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("staminaregen", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("traveldistance", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("biomeviewdistance", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("knockbackin", "LESS_GOOD_PERCENT_MODIFIER");
        MODIFIER_TOOLTIP_MAP.put("fishingpower", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("fishinglines", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("waterwalking", "BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("emitlight", "BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("invisibility", "BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("spelunker", "BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("treasurehunter", "BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("bouncy", "NEUTRAL_BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("paralyzed", "INVERSE_BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("untargetable", "BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("intimidated", "INVERSE_BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("manaexhausted", "INVERSE_BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("grounded", "INVERSE_BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("extendedmapdiscovery", "BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("mobspawnrate", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("mobspawncap", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("mobspawnlightthreshold", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("targetrange", "INVERSE_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("chaserrange", "NORMAL_PERC_PARSER");
        MODIFIER_TOOLTIP_MAP.put("canbreakobjects", "BOOL_PARSER");
        MODIFIER_TOOLTIP_MAP.put("maxfoodbuffs", "NORMAL_FLAT_INT_PARSER");
        MODIFIER_TOOLTIP_MAP.put("maxauras", "NORMAL_FLAT_INT_PARSER");
        REMAP_TABLE = new HashMap();
        REMAP_TABLE.put("damage", BuffModifiers.ALL_DAMAGE);
        REMAP_TABLE.put("critchance", BuffModifiers.CRIT_CHANCE);
        REMAP_TABLE.put("knockback", BuffModifiers.KNOCKBACK_OUT);
        REMAP_TABLE.put("armorpen", BuffModifiers.ARMOR_PEN_FLAT);
        REMAP_TABLE.put("resilience", BuffModifiers.RESILIENCE_GAIN);
        REMAP_TABLE.put("range", BuffModifiers.TARGET_RANGE);
        REMAP_TABLE.put("summontargetrange", BuffModifiers.SUMMONS_TARGET_RANGE);
        REMAP_TABLE.put("attackspeed", BuffModifiers.ATTACK_SPEED);
        REMAP_TABLE.put("summonsspeed", BuffModifiers.SUMMONS_SPEED);
        REMAP_TABLE.put("velocity", BuffModifiers.PROJECTILE_VELOCITY);
        REMAP_TABLE.put("manausage", BuffModifiers.MANA_USAGE);
        REMAP_TABLE.put("tooldamage", BuffModifiers.TOOL_DAMAGE);
        REMAP_TABLE.put("miningspeed", BuffModifiers.MINING_SPEED);
    }
}

