/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.playerStats;

import java.util.HashMap;
import necesse.engine.playerStats.StatsProvider;

public class GameGlobalStats {
    public final long time_played;
    public final long distance_ran;
    public final long distance_ridden;
    public final long deaths;
    public final long mob_kills;
    public final long boss_kills;
    public final long damage_dealt;
    public final long normal_damage_dealt;
    public final long true_damage_dealt;
    public final long melee_damage_dealt;
    public final long ranged_damage_dealt;
    public final long magic_damage_dealt;
    public final long summon_damage_dealt;
    public final long damage_taken;
    public final long island_travels;
    public final long islands_discovered;
    public final long islands_visited;
    public final long objects_mined;
    public final long objects_placed;
    public final long tiles_mined;
    public final long tiles_placed;
    public final long food_consumed;
    public final long potions_consumed;
    public final long fish_caught;
    public final long quests_completed;
    public final long money_earned;
    public final long items_sold;
    public final long money_spent;
    public final long items_bought;
    public final long items_enchanted;
    public final long items_upgraded;
    public final long items_salvaged;
    public final long ladders_used;
    public final long doors_used;
    public final long plates_triggered;
    public final long levers_flicked;
    public final long homestones_used;
    public final long waystones_used;
    public final long crafted_items;
    public final long crates_broken;
    public final long opened_incursions;
    public final long completed_incursions;
    private final HashMap<String, Long> loadedStats = new HashMap();

    public GameGlobalStats(StatsProvider statsProvider) {
        this.time_played = this.getStat(statsProvider, "time_played");
        this.distance_ran = this.getStat(statsProvider, "distance_ran");
        this.distance_ridden = this.getStat(statsProvider, "distance_ridden");
        this.deaths = this.getStat(statsProvider, "deaths");
        this.mob_kills = this.getStat(statsProvider, "mob_kills");
        this.boss_kills = this.getStat(statsProvider, "boss_kills");
        this.damage_dealt = this.getStat(statsProvider, "damage_dealt");
        this.normal_damage_dealt = this.getStat(statsProvider, "normal_damage_dealt");
        this.true_damage_dealt = this.getStat(statsProvider, "true_damage_dealt");
        this.melee_damage_dealt = this.getStat(statsProvider, "melee_damage_dealt");
        this.ranged_damage_dealt = this.getStat(statsProvider, "ranged_damage_dealt");
        this.magic_damage_dealt = this.getStat(statsProvider, "magic_damage_dealt");
        this.summon_damage_dealt = this.getStat(statsProvider, "summon_damage_dealt");
        this.damage_taken = this.getStat(statsProvider, "damage_taken");
        this.island_travels = this.getStat(statsProvider, "island_travels");
        this.islands_discovered = this.getStat(statsProvider, "islands_discovered");
        this.islands_visited = this.getStat(statsProvider, "islands_visited");
        this.objects_mined = this.getStat(statsProvider, "objects_mined");
        this.objects_placed = this.getStat(statsProvider, "objects_placed");
        this.tiles_mined = this.getStat(statsProvider, "tiles_mined");
        this.tiles_placed = this.getStat(statsProvider, "tiles_placed");
        this.food_consumed = this.getStat(statsProvider, "food_consumed");
        this.potions_consumed = this.getStat(statsProvider, "potions_consumed");
        this.fish_caught = this.getStat(statsProvider, "fish_caught");
        this.quests_completed = this.getStat(statsProvider, "quests_completed");
        this.money_earned = this.getStat(statsProvider, "money_earned");
        this.items_sold = this.getStat(statsProvider, "items_sold");
        this.money_spent = this.getStat(statsProvider, "money_spent");
        this.items_bought = this.getStat(statsProvider, "items_bought");
        this.items_enchanted = this.getStat(statsProvider, "items_enchanted");
        this.items_upgraded = this.getStat(statsProvider, "items_upgraded");
        this.items_salvaged = this.getStat(statsProvider, "items_salvaged");
        this.ladders_used = this.getStat(statsProvider, "ladders_used");
        this.doors_used = this.getStat(statsProvider, "doors_used");
        this.plates_triggered = this.getStat(statsProvider, "plates_triggered");
        this.levers_flicked = this.getStat(statsProvider, "levers_flicked");
        this.homestones_used = this.getStat(statsProvider, "homestones_used");
        this.waystones_used = this.getStat(statsProvider, "waystones_used");
        this.crafted_items = this.getStat(statsProvider, "crafted_items");
        this.crates_broken = this.getStat(statsProvider, "crates_broken");
        this.opened_incursions = this.getStat(statsProvider, "opened_incursions");
        this.completed_incursions = this.getStat(statsProvider, "completed_incursions");
    }

    private long getStat(StatsProvider statsProvider, String apiName) {
        long value = statsProvider == null ? 0L : statsProvider.getGlobalStat(apiName, 0L);
        this.loadedStats.put(apiName, value);
        return value;
    }

    public long getStatByName(String apiName, long defaultValue) {
        return this.loadedStats.getOrDefault(apiName, defaultValue);
    }
}

